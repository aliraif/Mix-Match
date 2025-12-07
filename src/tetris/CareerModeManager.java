package tetris;

/**
 * Manages Career Mode progression, levels, and goals
 */
public class CareerModeManager {
    private int currentLevel;
    private int linesCleared;
    private int linesRequiredForLevel;
    private int totalLinesCleared;
    private boolean levelComplete;
    private boolean careerComplete;

    // Level requirements (lines to clear per level)
    private static final int[] LINES_PER_LEVEL = {5, 10, 15, 20, 25};

    // Speed settings (delay in milliseconds)
    private static final int[] SPEED_PER_LEVEL = {600, 510, 420, 330, 240};

    public CareerModeManager() {
        reset();
    }

    public void reset() {
        currentLevel = 1;
        linesCleared = 0;
        totalLinesCleared = 0;
        linesRequiredForLevel = LINES_PER_LEVEL[0];
        levelComplete = false;
        careerComplete = false;
    }

    public void addLinesCleared(int lines) {
        linesCleared += lines;
        totalLinesCleared += lines;

        // Check if level is complete
        if (linesCleared >= linesRequiredForLevel) {
            levelComplete = true;

            // Check if career is complete (all 5 levels done)
            if (currentLevel >= 5) {
                careerComplete = true;
            }
        }
    }

    public void advanceToNextLevel() {
        if (currentLevel < 5) {
            currentLevel++;
            linesCleared = 0;
            linesRequiredForLevel = LINES_PER_LEVEL[currentLevel - 1];
            levelComplete = false;
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getLinesCleared() {
        return linesCleared;
    }

    public int getLinesRequired() {
        return linesRequiredForLevel;
    }

    public int getTotalLinesCleared() {
        return totalLinesCleared;
    }

    public boolean isLevelComplete() {
        return levelComplete;
    }

    public boolean isCareerComplete() {
        return careerComplete;
    }

    public int getCurrentSpeed() {
        return SPEED_PER_LEVEL[currentLevel - 1];
    }

    public String getProgressText() {
        return linesCleared + " / " + linesRequiredForLevel;
    }
}