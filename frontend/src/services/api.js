import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/scryfall';

export const createCollage = async (searchParams) => {
    const response = await axios.post(`${API_BASE_URL}/collage`, {
        query: searchParams.query,
        collageWidth: searchParams.collageWidth || 1920,
        collageHeight: searchParams.collageHeight || 1080,
        numberOfColumns: searchParams.numberOfColumns || 5,
        arrangementMethod: searchParams.arrangementMethod || 'DEFAULT',
        borderSize: searchParams.borderSize || 2,
        dropExcessCards: searchParams.dropExcessCards || false
    });
    return response.data;
};
