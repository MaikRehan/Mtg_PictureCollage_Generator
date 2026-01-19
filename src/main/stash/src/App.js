import React, { useState } from 'react';
import SearchForm from './components/SearchForm';
import CollageDisplay from './components/CollageDisplay';
import { createCollage } from './services/api';
import './App.css';

function App() {
    // State für Ladezustand
    const [isLoading, setIsLoading] = useState(false);
    // State für Collage-Ergebnis
    const [collageData, setCollageData] = useState(null);
    // State für Fehler
    const [error, setError] = useState(null);

    // Wird aufgerufen wenn Suche abgeschickt wird
    const handleSearch = async (searchParams) => {
        setIsLoading(true);
        setError(null);
        setCollageData(null);

        try {
            const result = await createCollage(searchParams);
            setCollageData(result);
        } catch (err) {
            console.error('Fehler bei der Collage-Erstellung:', err);
            setError(err.response?.data?.message || 'Ein Fehler ist aufgetreten');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="app">
            <header className="app-header">
                <h1>🃏 MTG Picture Collage Generator</h1>
                <p>Erstelle wunderschöne Collagen aus Magic: The Gathering Kartenkunst</p>
            </header>

            <main className="app-main">
                <aside className="search-panel">
                    <SearchForm onSearch={handleSearch} isLoading={isLoading} />
                </aside>

                <section className="result-panel">
                    <CollageDisplay collageData={collageData} error={error} />
                </section>
            </main>

            <footer className="app-footer">
                <p>Daten von <a href="https://scryfall.com">Scryfall</a></p>
            </footer>
        </div>
    );
}

export default App;
