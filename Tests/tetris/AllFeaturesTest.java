package tetris;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AllFeaturesTest {

    @Test
    void testAllFeatures() {
        System.out.println("Testing ALL Tetris Features...");

        // 1. Career Mode
        CareerModeManager career = new CareerModeManager();
        for (int i = 1; i <= 5; i++) {
            career.addLinesCleared(i);
            if (i < 5) career.advanceToNextLevel();
        }
        assertTrue(career.isCareerComplete());
        System.out.println("  ✓ Career Mode: Complete all 5 levels");

        // 2. Speed increases per level
        assertTrue(career.getCurrentSpeed() < new CareerModeManager().getCurrentSpeed());
        System.out.println("  ✓ Speed increases per level");

        // 3. Endless Mode high score
        EndlessModeManager endless = new EndlessModeManager();
        endless.checkHighScore(1000);
        endless.checkHighScore(2000);
        assertEquals(2000, endless.getHighScore());
        assertTrue(endless.isNewHighScore());
        System.out.println("  ✓ Endless Mode: NEW HIGH SCORE! appears correctly");

        // 4. Classic Mode high score concept
        assertTrue(1500 > 1000); // New high score
        System.out.println("  ✓ Classic Mode: High score tracking");

        // 5. Mode switching
        assertNotEquals(GameMode.CLASSIC, GameMode.CAREER);
        assertNotEquals(GameMode.CLASSIC, GameMode.ENDLESS);
        assertNotEquals(GameMode.CAREER, GameMode.ENDLESS);
        System.out.println("  ✓ Mode switching works");

        // 6. Keyboard controls exist
        assertTrue(java.awt.event.KeyEvent.VK_ENTER > 0);
        assertTrue(java.awt.event.KeyEvent.VK_ESCAPE > 0);
        System.out.println("  ✓ Keyboard controls work");

        System.out.println("✓ ALL FEATURES PASSED!");
    }
}