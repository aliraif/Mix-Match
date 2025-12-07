package tetris;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CareerModeManagerTest {
    private CareerModeManager manager;

    @BeforeEach
    void setUp() {
        manager = new CareerModeManager();
    }

    @Test
    void testCompleteAll5Levels() {
        // Complete level 1
        manager.addLinesCleared(5);
        assertTrue(manager.isLevelComplete());
        manager.advanceToNextLevel();

        // Complete level 2
        manager.addLinesCleared(10);
        assertTrue(manager.isLevelComplete());
        manager.advanceToNextLevel();

        // Complete level 3
        manager.addLinesCleared(15);
        assertTrue(manager.isLevelComplete());
        manager.advanceToNextLevel();

        // Complete level 4
        manager.addLinesCleared(20);
        assertTrue(manager.isLevelComplete());
        manager.advanceToNextLevel();

        // Complete level 5
        manager.addLinesCleared(25);
        assertTrue(manager.isLevelComplete());
        assertTrue(manager.isCareerComplete());
    }

    @Test
    void testSpeedIncreasesPerLevel() {
        // Level 1
        assertEquals(600, manager.getCurrentSpeed());

        // Level 2
        manager.addLinesCleared(1);
        manager.advanceToNextLevel();
        assertEquals(510, manager.getCurrentSpeed());

        // Level 5 (fastest)
        manager.addLinesCleared(2);
        manager.advanceToNextLevel();
        manager.addLinesCleared(3);
        manager.advanceToNextLevel();
        manager.addLinesCleared(4);
        manager.advanceToNextLevel();
        assertEquals(240, manager.getCurrentSpeed());
    }

    @Test
    void testGameOverAndRestartAtCurrentLevel() {
        // Get to level 2
        manager.addLinesCleared(1);
        manager.advanceToNextLevel();

        // Add some progress
        manager.addLinesCleared(1);
        assertEquals(1, manager.getLinesCleared());

        // Simulate game over - restart level
        manager.addLinesCleared(-1); // Reset current level
        assertEquals(0, manager.getLinesCleared());
        assertEquals(2, manager.getCurrentLevel()); // Stay on same level
    }
}