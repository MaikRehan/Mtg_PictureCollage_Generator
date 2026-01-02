package com.doci.mtgpicgen.image;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class CollageGenerator {

    private static final int TILE_WIDTH = 626;
    private static final int TILE_HEIGHT = 457;
    private static final int COLUMNS = 5;
    private static final int BORDER = 40;

    public enum ArrangementMethod {
        DIAGONAL,
        HILBERT,
        SOM,
        SNAKE,
        LINEAR
    }

    public BufferedImage generateCollage(List<BufferedImage> artList, ArrangementMethod method) {
        if (artList == null || artList.isEmpty()) {
            return createEmptyImage();
        }

        List<BufferedImage> sortedImages = sortByRainbowColor(artList);
        List<BufferedImage> arrangedImages = arrangeImages(sortedImages, method);

        int rows = calculateRows(arrangedImages.size());
        BufferedImage collage = createCollageCanvas(rows);
        drawImages(collage, arrangedImages);

        return collage;
    }

    public BufferedImage generateCollage(List<BufferedImage> artList, String arrangementMethod) {
        return generateCollage(artList, ArrangementMethod.DIAGONAL);
    }

    private List<BufferedImage> arrangeImages(List<BufferedImage> sortedImages, ArrangementMethod method) {
        return switch (method) {
            case DIAGONAL -> arrangeDiagonally(sortedImages);
            case HILBERT -> arrangeByHilbertCurve(sortedImages);
            case SOM -> arrangeBySOM(sortedImages);
            case SNAKE -> arrangeSnake(sortedImages);
            case LINEAR -> sortedImages;
        };
    }

    // ==================== Sortierung ====================

    private List<BufferedImage> sortByRainbowColor(List<BufferedImage> images) {
        List<BufferedImage> sorted = new ArrayList<>(images);
        sorted.sort(Comparator.comparingDouble(this::calculateHue));
        return sorted;
    }

    private double calculateHue(BufferedImage image) {
        long totalRed = 0, totalGreen = 0, totalBlue = 0;
        int pixelCount = 0;

        for (int y = 0; y < image.getHeight(); y += 10) {
            for (int x = 0; x < image.getWidth(); x += 10) {
                int rgb = image.getRGB(x, y);
                totalRed += (rgb >> 16) & 0xFF;
                totalGreen += (rgb >> 8) & 0xFF;
                totalBlue += rgb & 0xFF;
                pixelCount++;
            }
        }

        if (pixelCount == 0) return 0;

        int avgRed = (int) (totalRed / pixelCount);
        int avgGreen = (int) (totalGreen / pixelCount);
        int avgBlue = (int) (totalBlue / pixelCount);

        float[] hsb = Color.RGBtoHSB(avgRed, avgGreen, avgBlue, null);
        float hue = hsb[0];
        float saturation = hsb[1];
        float brightness = hsb[2];

        if (saturation < 0.15) {
            return 1.0 + brightness;
        }

        return hue;
    }

    // ==================== Diagonal ====================

    private List<BufferedImage> arrangeDiagonally(List<BufferedImage> sortedImages) {
        int rows = calculateRows(sortedImages.size());
        BufferedImage[][] grid = new BufferedImage[rows][COLUMNS];

        int imageIndex = 0;
        for (int diag = 0; diag < rows + COLUMNS - 1 && imageIndex < sortedImages.size(); diag++) {
            int startRow = Math.max(0, diag - COLUMNS + 1);
            int startCol = Math.min(diag, COLUMNS - 1);

            for (int row = startRow, col = startCol;
                 row < rows && col >= 0 && imageIndex < sortedImages.size();
                 row++, col--) {
                grid[row][col] = sortedImages.get(imageIndex++);
            }
        }

        return gridToList(grid, rows);
    }

    // ==================== Hilbert-Kurve ====================

    private List<BufferedImage> arrangeByHilbertCurve(List<BufferedImage> sortedImages) {
        int rows = calculateRows(sortedImages.size());
        int size = Math.max(COLUMNS, rows);
        int order = (int) Math.ceil(Math.log(size) / Math.log(2));

        BufferedImage[][] grid = new BufferedImage[rows][COLUMNS];
        List<Point> hilbertPoints = generateHilbertCurve(order);

        int imageIndex = 0;
        for (Point p : hilbertPoints) {
            if (p.y < rows && p.x < COLUMNS && imageIndex < sortedImages.size()) {
                if (grid[p.y][p.x] == null) {
                    grid[p.y][p.x] = sortedImages.get(imageIndex++);
                }
            }
        }

        return gridToList(grid, rows);
    }

    private List<Point> generateHilbertCurve(int order) {
        List<Point> points = new ArrayList<>();
        int n = (int) Math.pow(2, order);
        for (int i = 0; i < n * n; i++) {
            points.add(hilbertD2XY(n, i));
        }
        return points;
    }

    private Point hilbertD2XY(int n, int d) {
        int x = 0, y = 0;
        for (int s = 1; s < n; s *= 2) {
            int rx = 1 & (d / 2);
            int ry = 1 & (d ^ rx);
            if (ry == 0) {
                if (rx == 1) {
                    x = s - 1 - x;
                    y = s - 1 - y;
                }
                int temp = x;
                x = y;
                y = temp;
            }
            x += s * rx;
            y += s * ry;
            d /= 4;
        }
        return new Point(x, y);
    }

    // ==================== SOM ====================

    private List<BufferedImage> arrangeBySOM(List<BufferedImage> images) {
        int rows = calculateRows(images.size());
        double[][] colorMap = new double[rows][COLUMNS];
        BufferedImage[][] grid = new BufferedImage[rows][COLUMNS];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                colorMap[r][c] = (double) (r + c) / (rows + COLUMNS - 2);
            }
        }

        List<ImageWithHue> imagesWithHue = images.stream()
                .map(img -> new ImageWithHue(img, calculateHue(img)))
                .sorted(Comparator.comparingDouble(ImageWithHue::hue))
                .toList();

        boolean[][] used = new boolean[rows][COLUMNS];

        for (ImageWithHue iwh : imagesWithHue) {
            Point best = findBestMatch(colorMap, used, iwh.hue(), rows);
            if (best != null) {
                grid[best.y][best.x] = iwh.image();
                used[best.y][best.x] = true;
            }
        }

        return gridToList(grid, rows);
    }

    private Point findBestMatch(double[][] colorMap, boolean[][] used, double hue, int rows) {
        double minDist = Double.MAX_VALUE;
        Point best = null;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                if (!used[r][c]) {
                    double dist = Math.abs(colorMap[r][c] - hue);
                    if (dist < minDist) {
                        minDist = dist;
                        best = new Point(c, r);
                    }
                }
            }
        }
        return best;
    }

    private record ImageWithHue(BufferedImage image, double hue) {}

    // ==================== Snake ====================

    private List<BufferedImage> arrangeSnake(List<BufferedImage> sortedImages) {
        int rows = calculateRows(sortedImages.size());
        BufferedImage[][] grid = new BufferedImage[rows][COLUMNS];

        int imageIndex = 0;
        for (int row = 0; row < rows && imageIndex < sortedImages.size(); row++) {
            if (row % 2 == 0) {
                for (int col = 0; col < COLUMNS && imageIndex < sortedImages.size(); col++) {
                    grid[row][col] = sortedImages.get(imageIndex++);
                }
            } else {
                for (int col = COLUMNS - 1; col >= 0 && imageIndex < sortedImages.size(); col--) {
                    grid[row][col] = sortedImages.get(imageIndex++);
                }
            }
        }

        return gridToList(grid, rows);
    }

    // ==================== Hilfsmethoden ====================

    private List<BufferedImage> gridToList(BufferedImage[][] grid, int rows) {
        List<BufferedImage> result = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (grid[row][col] != null) {
                    result.add(grid[row][col]);
                }
            }
        }
        return result;
    }

    private BufferedImage createEmptyImage() {
        return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    }

    private int calculateRows(int imageCount) {
        return (int) Math.ceil((double) imageCount / COLUMNS);
    }

    private BufferedImage createCollageCanvas(int rows) {
        int width = COLUMNS * TILE_WIDTH + (COLUMNS + 1) * BORDER;
        int height = rows * TILE_HEIGHT + (rows + 1) * BORDER;

        BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        fillBackground(canvas);

        return canvas;
    }

    private void fillBackground(BufferedImage image) {
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.dispose();
    }

    private void drawImages(BufferedImage collage, List<BufferedImage> images) {
        Graphics2D g2d = collage.createGraphics();

        for (int i = 0; i < images.size(); i++) {
            Point position = calculatePosition(i);
            g2d.drawImage(images.get(i), position.x, position.y, TILE_WIDTH, TILE_HEIGHT, null);
        }

        g2d.dispose();
    }

    private Point calculatePosition(int index) {
        int col = index % COLUMNS;
        int row = index / COLUMNS;
        int x = BORDER + col * (TILE_WIDTH + BORDER);
        int y = BORDER + row * (TILE_HEIGHT + BORDER);

        return new Point(x, y);
    }
}
