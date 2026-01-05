package com.doci.mtgpicgen.image;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ArrangementMethod {
    DIAGONAL("diagonal"),
    HILBERT("hilbert"),
    SOM("som"),
    SNAKE("snake"),
    LINEAR("linear"),
    RANDOM("random"),
    DEFAULT("default");

    private final String value;

    ArrangementMethod(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ArrangementMethod fromString(String text) {
        if (text == null || text.isBlank()) {
            return DEFAULT;
        }
        for (ArrangementMethod method : ArrangementMethod.values()) {
            if (method.value.equalsIgnoreCase(text)) {
                return method;
            }
        }
        return DEFAULT;
    }
}
