package tetris;

/**
 * Manages Endless Mode: progressive speed, time tracking, and high score
 */
public class EndlessModeManager {
    public long startTime;
    private long pausedTime;
    public long totalPausedDuration;
    private boolean isPaused;

    private int currentSpeedLevel;
    public int linesCleared;
    private int highScore;
    private boolean isNewHighScore;

    // Speed settings
    private static final int START_SPEED = 600;
    private static final int MAX_SPEED = 50;
    private static final double SPEED_MULTIPLIER = 0.90; // 10% faster each level
    private static final long SPEED_INCREASE_INTERVAL = 30000; // 30 seconds in milliseconds

    public EndlessModeManager() {
        reset();
        highScore = 0;
    }

    public void reset() {
        startTime = System.currentTimeMillis();
        pausedTime = 0;
        totalPausedDuration = 0;
        isPaused = false;
        currentSpeedLevel = 1;
        linesCleared = 0;
        isNewHighScore = false;
    }

    public void pause() {
        if (!isPaused) {
            pausedTime = System.currentTimeMillis();
            isPaused = true;
        }
    }

    public void resume() {
        if (isPaused) {
            totalPausedDuration += System.currentTimeMillis() - pausedTime;
            isPaused = false;
        }
    }

    public void addLinesCleared(int lines) {
        linesCleared += lines;
    }

    public void checkHighScore(int finalScore) {
        if (finalScore > highScore) {
            highScore = finalScore;
            isNewHighScore = true;
        } else {
            isNewHighScore = false;
        }
    }

    public long getElapsedTime() {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - startTime - totalPausedDuration;

        // If currently paused, don't count the current pause duration
        if (isPaused) {
            elapsed -= (currentTime - pausedTime);
        }

        return elapsed;
    }

    public String getFormattedTime() {
        long elapsed = getElapsedTime();
        long seconds = (elapsed / 1000) % 60;
        long minutes = (elapsed / 1000) / 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public int getCurrentSpeed() {
        // Calculate speed level based on elapsed time
        long elapsed = getElapsedTime();
        int calculatedLevel = (int) (elapsed / SPEED_INCREASE_INTERVAL) + 1;

        // Update current speed level if it changed
        if (calculatedLevel > currentSpeedLevel) {
            currentSpeedLevel = calculatedLevel;
        }

        // Calculate speed with 5% reduction per level
        int speed = START_SPEED;
        for (int i = 1; i < currentSpeedLevel; i++) {
            speed = (int) (speed * SPEED_MULTIPLIER);
        }

        // Enforce maximum speed limit
        return Math.max(speed, MAX_SPEED);
    }

    public int getCurrentSpeedLevel() {
        // Update speed level based on time
        long elapsed = getElapsedTime();
        currentSpeedLevel = (int) (elapsed / SPEED_INCREASE_INTERVAL) + 1;
        return currentSpeedLevel;
    }

    public int getLinesCleared() {
        return linesCleared;
    }

    public int getHighScore() {
        return highScore;
    }

    public boolean isNewHighScore() {
        return isNewHighScore;
    }
}