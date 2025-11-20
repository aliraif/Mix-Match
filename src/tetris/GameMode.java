package tetris;

/**
 * Class containing game mode constants for Tetris
 */
public class GameMode {
    // Game mode constants
    public static final String CLASSIC = "CLASSIC";
    public static final String CAREER = "CAREER";
    public static final String ENDLESS = "ENDLESS";

    // Display names for UI
    public static String getDisplayName(String mode) {
        switch (mode) {
            case CLASSIC:
                return "Classic Mode";
            case CAREER:
                return "Career Mode";
            case ENDLESS:
                return "Endless Mode";
            default:
                return "Unknown Mode";
        }
    }

    // Descriptions for UI
    public static String getDescription(String mode) {
        switch (mode) {
            case CLASSIC:
                return "Standard Tetris gameplay";
            case CAREER:
                return "Progress through harder levels.";
            case ENDLESS:
                return "Survive as speed rises";
            default:
                return "";
        }
    }
}