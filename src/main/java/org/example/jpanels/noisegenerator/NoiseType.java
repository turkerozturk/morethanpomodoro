package org.example.jpanels.noisegenerator;

public enum NoiseType {
    WHITE_NOISE("White Noise"),
    BROWN_NOISE("Brown Noise"),
    PINK_NOISE("Pink Noise"),
    ANOTHER_NOISE("Another Noise");

    private final String displayName;

    NoiseType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
