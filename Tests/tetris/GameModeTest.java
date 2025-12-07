package tetris;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameModeTest {
    @Test
    void testAllModesExist() {
        assertNotNull(GameMode.CLASSIC);
        assertNotNull(GameMode.CAREER);
        assertNotNull(GameMode.ENDLESS);
    }

    @Test
    void testModeSwitching() {
        // All modes are distinct
        assertNotEquals(GameMode.CLASSIC, GameMode.CAREER);
        assertNotEquals(GameMode.CLASSIC, GameMode.ENDLESS);
        assertNotEquals(GameMode.CAREER, GameMode.ENDLESS);
    }
}