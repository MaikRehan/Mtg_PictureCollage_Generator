package com.doci.mtgpicgen.image;

import com.doci.mtgpicgen.image.imagedto.CardArtClientResponse;
import com.doci.mtgpicgen.image.imagedto.ImageServiceRequest;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.doci.mtgpicgen.image.ArrangementMethod.DEFAULT;

@Component
public class ImageGenerator {

    // Fallback-Werte falls keine Dimensionen übergeben werden
    private static final int DEFAULT_TILE_WIDTH = 626;
    private static final int DEFAULT_TILE_HEIGHT = 457;

    public BufferedImage generateCollage(CardArtClientResponse cardArtResponse, ImageServiceRequest request) {
        List<BufferedImage> artList = cardArtResponse.getImages();

        if (artList == null || artList.isEmpty()) {
            return createEmptyImage();
        }

        // Dimensionen aus Response verwenden. Standardwert wenn response keine Dimensionen hat.
        int tileWidth = cardArtResponse.getTargetWidth() > 0 ? cardArtResponse.getTargetWidth() : DEFAULT_TILE_WIDTH;
        int tileHeight = cardArtResponse.getTargetHeight() > 0 ? cardArtResponse.getTargetHeight() : DEFAULT_TILE_HEIGHT;


        ArrangementMethod arrangementMethod = DEFAULT;
        if (request.getArrangementMethod() != null) arrangementMethod = request.getArrangementMethod();

        int numberOfColumns = request.getCollageNumberOfColumns();
        if (numberOfColumns <= 0) numberOfColumns = 5;

        List<BufferedImage> arrangedImages = arrangeImages(artList, arrangementMethod, numberOfColumns);

        int rows = calculateRows(arrangedImages.size(), numberOfColumns);
        BufferedImage collage = createCollageCanvas(rows, numberOfColumns, request.getBorderSize(), tileWidth, tileHeight);
        drawImages(collage, arrangedImages, numberOfColumns, request.getBorderSize(), tileWidth, tileHeight);

        return collage;
    }

    private List<BufferedImage> arrangeImages(List<BufferedImage> images, ArrangementMethod method, int numberOfColumns) {
        return switch (method) {
            case DIAGONAL -> arrangeDiagonally(sortByRainbowColor(images), numberOfColumns);
            case HILBERT -> arrangeByHilbertCurve(sortByRainbowColor(images), numberOfColumns);
            case SOM -> arrangeBySOM(images, numberOfColumns);
            case SNAKE -> arrangeSnake(sortByRainbowColor(images), numberOfColumns);
            case LINEAR -> sortByRainbowColor(images);
            case DEFAULT -> arrangeDefault(images);
            case RANDOM -> arrangeRandom(images);
        };
    }

    // ==================== Arrangement-Methoden (unverändert) ====================

    private List<BufferedImage> arrangeDefault(List<BufferedImage> images) {
        return new ArrayList<>(images);
    }

    private List<BufferedImage> arrangeRandom(List<BufferedImage> images) {
        List<BufferedImage> shuffled = new ArrayList<>(images);
        Collections.shuffle(shuffled);
        return shuffled;
    }

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

    private List<BufferedImage> arrangeDiagonally(List<BufferedImage> sortedImages, int numberOfColumns) {
        int rows = calculateRows(sortedImages.size(), numberOfColumns);
        BufferedImage[][] grid = new BufferedImage[rows][numberOfColumns];

        int imageIndex = 0;
        for (int diag = 0; diag < rows + numberOfColumns - 1 && imageIndex < sortedImages.size(); diag++) {
            int startRow = Math.max(0, diag - numberOfColumns + 1);
            int startCol = Math.min(diag, numberOfColumns - 1);

            for (int row = startRow, col = startCol;
                 row < rows && col >= 0 && imageIndex < sortedImages.size();
                 row++, col--) {
                grid[row][col] = sortedImages.get(imageIndex++);
            }
        }

        return gridToList(grid, rows, numberOfColumns);
    }

    private List<BufferedImage> arrangeByHilbertCurve(List<BufferedImage> sortedImages, int numberOfColumns) {
        int rows = calculateRows(sortedImages.size(), numberOfColumns);
        int size = Math.max(numberOfColumns, rows);
        int order = (int) Math.ceil(Math.log(size) / Math.log(2));

        BufferedImage[][] grid = new BufferedImage[rows][numberOfColumns];
        List<Point> hilbertPoints = generateHilbertCurve(order);

        int imageIndex = 0;
        for (Point p : hilbertPoints) {
            if (p.y < rows && p.x < numberOfColumns && imageIndex < sortedImages.size()) {
                if (grid[p.y][p.x] == null) {
                    grid[p.y][p.x] = sortedImages.get(imageIndex++);
                }
            }
        }

        return gridToList(grid, rows, numberOfColumns);
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

    private List<BufferedImage> arrangeBySOM(List<BufferedImage> images, int numberOfColumns) {
        int rows = calculateRows(images.size(), numberOfColumns);
        double[][] colorMap = new double[rows][numberOfColumns];
        BufferedImage[][] grid = new BufferedImage[rows][numberOfColumns];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < numberOfColumns; c++) {
                colorMap[r][c] = (double) (r + c) / (rows + numberOfColumns - 2);
            }
        }

        List<ImageWithHue> imagesWithHue = images.stream()
                .map(img -> new ImageWithHue(img, calculateHue(img)))
                .sorted(Comparator.comparingDouble(ImageWithHue::hue))
                .toList();

        boolean[][] used = new boolean[rows][numberOfColumns];

        for (ImageWithHue iwh : imagesWithHue) {
            Point best = findBestMatch(colorMap, used, iwh.hue(), rows, numberOfColumns);
            if (best != null) {
                grid[best.y][best.x] = iwh.image();
                used[best.y][best.x] = true;
            }
        }

        return gridToList(grid, rows, numberOfColumns);
    }

    private Point findBestMatch(double[][] colorMap, boolean[][] used, double hue, int rows, int numberOfColumns) {
        double minDist = Double.MAX_VALUE;
        Point best = null;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < numberOfColumns; c++) {
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

    private List<BufferedImage> arrangeSnake(List<BufferedImage> sortedImages, int numberOfColumns) {
        int rows = calculateRows(sortedImages.size(), numberOfColumns);
        BufferedImage[][] grid = new BufferedImage[rows][numberOfColumns];

        int imageIndex = 0;
        for (int row = 0; row < rows && imageIndex < sortedImages.size(); row++) {
            if (row % 2 == 0) {
                for (int col = 0; col < numberOfColumns && imageIndex < sortedImages.size(); col++) {
                    grid[row][col] = sortedImages.get(imageIndex++);
                }
            } else {
                for (int col = numberOfColumns - 1; col >= 0 && imageIndex < sortedImages.size(); col--) {
                    grid[row][col] = sortedImages.get(imageIndex++);
                }
            }
        }

        return gridToList(grid, rows, numberOfColumns);
    }

    // ==================== Hilfsmethoden (angepasst) ====================

    private List<BufferedImage> gridToList(BufferedImage[][] grid, int rows, int numberOfColumns) {
        List<BufferedImage> result = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < numberOfColumns; col++) {
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

    private int calculateRows(int imageCount, int numberOfColumns) {
        return (int) Math.ceil((double) imageCount / numberOfColumns);
    }

    private BufferedImage createCollageCanvas(int rows, int numberOfColumns, int borderSize, int tileWidth, int tileHeight) {
        int width = numberOfColumns * tileWidth + (numberOfColumns + 1) * borderSize;
        int height = rows * tileHeight + (rows + 1) * borderSize;

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

    private void drawImages(BufferedImage collage, List<BufferedImage> images, int numberOfColumns, int borderSize, int tileWidth, int tileHeight) {
        Graphics2D g2d = collage.createGraphics();

        for (int i = 0; i < images.size(); i++) {
            Point position = calculatePosition(i, numberOfColumns, borderSize, tileWidth, tileHeight);
            g2d.drawImage(images.get(i), position.x, position.y, tileWidth, tileHeight, null);
        }

        g2d.dispose();
    }

    private Point calculatePosition(int index, int numberOfColumns, int borderSize, int tileWidth, int tileHeight) {
        int col = index % numberOfColumns;
        int row = index / numberOfColumns;
        int x = borderSize + col * (tileWidth + borderSize);
        int y = borderSize + row * (tileHeight + borderSize);

        return new Point(x, y);
    }
}
