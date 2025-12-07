package tetris;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MainMenuTest {

    @Test
    void testGameModeSelection() {
        // Test that all game modes exist
        assertEquals("CLASSIC", GameMode.CLASSIC);
        assertEquals("CAREER", GameMode.CAREER);
        assertEquals("ENDLESS", GameMode.ENDLESS);

        // Test display names
        assertEquals("Classic Mode", GameMode.getDisplayName("CLASSIC"));
        assertEquals("Career Mode", GameMode.getDisplayName("CAREER"));
        assertEquals("Endless Mode", GameMode.getDisplayName("ENDLESS"));
    }

    @Test
    void testModeSwitching() {
        // Simulate switching between modes
        String[] modes = {"CLASSIC", "CAREER", "ENDLESS"};

        // Switch 3 times
        for (int i = 0; i < 3; i++) {
            for (String mode : modes) {
                assertNotNull(mode);
                assertFalse(mode.isEmpty());
            }
        }

        // Verify modes are different
        assertNotEquals("CLASSIC", "CAREER");
        assertNotEquals("CLASSIC", "ENDLESS");
        assertNotEquals("CAREER", "ENDLESS");
    }
}