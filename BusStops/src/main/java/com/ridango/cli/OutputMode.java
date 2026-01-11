package com.ridango.cli;

public enum OutputMode {
    ABSOLUTE,
    RELATIVE;

    public static OutputMode outputFormat(String raw) {
        if (raw == null) throw new IllegalArgumentException("!! Output mode is required.");
        return switch (raw.trim().toLowerCase()) {
            case "absolute" -> ABSOLUTE;
            case "relative" -> RELATIVE;
            default -> throw new IllegalArgumentException(
                    "!! Invalid output mode: '" + raw + "'. Allowed values: absolute | relative"
            );
        };
    }
}
