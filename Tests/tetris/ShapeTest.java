package tetris;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

class ShapeTest {
    private Shape shape;

    @BeforeEach
    void setUp() {
        // Create a simple board mock
        Board board = new Board(null) {
            @Override
            public Color[][] getBoard() {
                return new Color[20][10];
            }
        };

        int[][] coords = {{1, 1, 1}};
        shape = new Shape(coords, board, Color.RED);
    }

    @Test
    void testShapeMovement() {
        // Initial position
        assertEquals(4, shape.getX());
        assertEquals(0, shape.getY());

        // Move
        shape.setX(5);
        shape.setY(2);
        assertEquals(5, shape.getX());
        assertEquals(2, shape.getY());
    }

    @Test
    void testShapeControls() {
        // Test controls don't crash
        assertDoesNotThrow(() -> shape.moveRight());
        assertDoesNotThrow(() -> shape.moveLeft());
        assertDoesNotThrow(() -> shape.speedUp());
        assertDoesNotThrow(() -> shape.speedDown());
        assertDoesNotThrow(() -> shape.rotateShape());
    }
}