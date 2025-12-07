package tetris;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WindowGameTest {

    @Test
    void testWindowConstants() {
        // Test window dimensions
        assertEquals(445, WindowGame.WIDTH);
        assertEquals(629, WindowGame.HEIGHT);
    }

    @Test
    void testFileManagerAccess() {
        // Test that FileManager can be accessed
        assertDoesNotThrow(() -> {
            FileManager fm = WindowGame.getFileManager();
            assertNotNull(fm);
        });
    }

    @Test
    void testGameModeTracking() {
        // Test that game mode constants work
        assertEquals("CLASSIC", GameMode.CLASSIC);
        assertEquals("CAREER", GameMode.CAREER);
        assertEquals("ENDLESS", GameMode.ENDLESS);

        // All modes should be different
        assertNotEquals(GameMode.CLASSIC, GameMode.CAREER);
        assertNotEquals(GameMode.CLASSIC, GameMode.ENDLESS);
        assertNotEquals(GameMode.CAREER, GameMode.ENDLESS);
    }
}