package tetris;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void testBoardCreation() {
        // Test that Board can be instantiated without crashing
        assertDoesNotThrow(() -> {
            // Create a simple WindowGame mock
            WindowGame windowGame = new WindowGame() {
                @Override
                public void startTetris(String mode) {
                    // Do nothing
                }

                @Override
                public void returnToMenu() {
                    // Do nothing
                }

                public static FileManager getFileManager() {
                    return FileManager.getInstance();
                }
            };

            Board board = new Board(windowGame);
            assertNotNull(board);
        });
    }

    @Test
    void testGameModeFunctionality() {
        // Just test that game modes work (this is what Board uses)
        assertEquals("CLASSIC", GameMode.CLASSIC);
        assertEquals("CAREER", GameMode.CAREER);
        assertEquals("ENDLESS", GameMode.ENDLESS);
    }
}