import React, { useState } from 'react';
import './SearchForm.css';

// Scryfall-Suchparameter für das Formular
const SEARCH_FIELDS = [
    { key: 'name', label: 'Kartenname', placeholder: 'z.B. Lightning Bolt' },
    { key: 'type', label: 'Typ', placeholder: 'z.B. creature, instant' },
    { key: 'color', label: 'Farbe', placeholder: 'z.B. r, u, g (rot, blau, grün)' },
    { key: 'set', label: 'Set', placeholder: 'z.B. dom, war, neo' },
    { key: 'artist', label: 'Künstler', placeholder: 'z.B. Rebecca Guay' },
    { key: 'rarity', label: 'Seltenheit', placeholder: 'common, uncommon, rare, mythic' },
    { key: 'cmc', label: 'Manakosten (CMC)', placeholder: 'z.B. 3 oder <=2' },
    { key: 'power', label: 'Stärke', placeholder: 'z.B. 4 oder >=3' },
    { key: 'toughness', label: 'Widerstand', placeholder: 'z.B. 5 oder <=2' },
    { key: 'keywords', label: 'Keywords', placeholder: 'z.B. flying, trample' }
];

const ARRANGEMENT_METHODS = [
    { value: 'DEFAULT', label: 'Standard' },
    { value: 'RANDOM', label: 'Zufällig' },
    { value: 'LINEAR', label: 'Linear (Regenbogen)' },
    { value: 'SNAKE', label: 'Schlange' },
    { value: 'DIAGONAL', label: 'Diagonal' },
    { value: 'HILBERT', label: 'Hilbert-Kurve' },
    { value: 'SOM', label: 'SOM (Self-Organizing Map)' }
];

function SearchForm({ onSearch, isLoading }) {
    // State für alle Suchfelder
    const [searchParams, setSearchParams] = useState({
        name: '',
        type: '',
        color: '',
        set: '',
        artist: '',
        rarity: '',
        cmc: '',
        power: '',
        toughness: '',
        keywords: ''
    });

    // State für Collage-Einstellungen
    const [collageSettings, setCollageSettings] = useState({
        numberOfColumns: 5,
        arrangementMethod: 'DEFAULT',
        borderSize: 2
    });

    // Aktualisiert ein einzelnes Suchfeld
    const handleSearchChange = (key, value) => {
        setSearchParams(prev => ({
            ...prev,
            [key]: value
        }));
    };

    // Aktualisiert Collage-Einstellungen
    const handleSettingChange = (key, value) => {
        setCollageSettings(prev => ({
            ...prev,
            [key]: value
        }));
    };

    // Baut den Scryfall-Query-String aus den Feldern
    const buildQuery = () => {
        const parts = [];

        if (searchParams.name) parts.push(`name:${searchParams.name}`);
        if (searchParams.type) parts.push(`type:${searchParams.type}`);
        if (searchParams.color) parts.push(`color:${searchParams.color}`);
        if (searchParams.set) parts.push(`set:${searchParams.set}`);
        if (searchParams.artist) parts.push(`artist:"${searchParams.artist}"`);
        if (searchParams.rarity) parts.push(`rarity:${searchParams.rarity}`);
        if (searchParams.cmc) parts.push(`cmc${searchParams.cmc}`);
        if (searchParams.power) parts.push(`power${searchParams.power}`);
        if (searchParams.toughness) parts.push(`toughness${searchParams.toughness}`);
        if (searchParams.keywords) parts.push(`keyword:${searchParams.keywords}`);

        return parts.join(' ');
    };

    // Formular absenden
    const handleSubmit = (e) => {
        e.preventDefault();
        const query = buildQuery();

        if (!query.trim()) {
            alert('Bitte mindestens ein Suchkriterium eingeben');
            return;
        }

        onSearch({
            query,
            ...collageSettings
        });
    };

    return (
        <form className="search-form" onSubmit={handleSubmit}>
            <h2>🔍 Kartensuche</h2>

            <div className="search-fields">
                {SEARCH_FIELDS.map(field => (
                    <div className="form-group" key={field.key}>
                        <label htmlFor={field.key}>{field.label}</label>
                        <input
                            type="text"
                            id={field.key}
                            value={searchParams[field.key]}
                            onChange={(e) => handleSearchChange(field.key, e.target.value)}
                            placeholder={field.placeholder}
                        />
                    </div>
                ))}
            </div>

            <h3>⚙️ Collage-Einstellungen</h3>

            <div className="settings-fields">
                <div className="form-group">
                    <label htmlFor="numberOfColumns">Spaltenanzahl</label>
                    <input
                        type="number"
                        id="numberOfColumns"
                        min="1"
                        max="20"
                        value={collageSettings.numberOfColumns}
                        onChange={(e) => handleSettingChange('numberOfColumns', parseInt(e.target.value))}
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="arrangementMethod">Anordnung</label>
                    <select
                        id="arrangementMethod"
                        value={collageSettings.arrangementMethod}
                        onChange={(e) => handleSettingChange('arrangementMethod', e.target.value)}
                    >
                        {ARRANGEMENT_METHODS.map(method => (
                            <option key={method.value} value={method.value}>
                                {method.label}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="form-group">
                    <label htmlFor="borderSize">Rahmenbreite (px)</label>
                    <input
                        type="number"
                        id="borderSize"
                        min="0"
                        max="20"
                        value={collageSettings.borderSize}
                        onChange={(e) => handleSettingChange('borderSize', parseInt(e.target.value))}
                    />
                </div>
            </div>

            <button type="submit" disabled={isLoading} className="search-button">
                {isLoading ? '⏳ Erstelle Collage...' : '🎨 Collage erstellen'}
            </button>
        </form>
    );
}

export default SearchForm;
