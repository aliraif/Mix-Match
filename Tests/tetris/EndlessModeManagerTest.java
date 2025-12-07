package tetris;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EndlessModeManagerTest {
    private EndlessModeManager manager;

    @BeforeEach
    void setUp() {
        manager = new EndlessModeManager();
    }

    @Test
    void testTimerAndSpeed() {
        // Timer should start and give formatted time
        String time = manager.getFormattedTime();
        assertNotNull(time);
        assertTrue(time.contains(":"));

        // Initial speed
        assertEquals(600, manager.getCurrentSpeed());
    }

    @Test
    void testHighScoreTracking() {
        // First score becomes high score
        manager.checkHighScore(100);
        assertEquals(100, manager.getHighScore());
        assertTrue(manager.isNewHighScore());

        // Higher score updates
        manager.checkHighScore(200);
        assertEquals(200, manager.getHighScore());
        assertTrue(manager.isNewHighScore());

        // Lower score doesn't update
        manager.checkHighScore(150);
        assertEquals(200, manager.getHighScore());
        assertFalse(manager.isNewHighScore());
    }
}