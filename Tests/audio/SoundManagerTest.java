package audio;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SoundManagerTest {

    @Test
    void testSoundEffectsWork() {
        // Test that sound methods don't crash
        assertDoesNotThrow(() -> SoundManager.play("sfx/drop.wav"));
        assertDoesNotThrow(() -> SoundManager.play("sfx/move.wav"));
        assertDoesNotThrow(() -> SoundManager.play("sfx/rotate.wav"));
    }

    @Test
    void testMuteWorks() {
        // Test mute/unmute
        assertDoesNotThrow(SoundManager::cutAllSound);
        assertDoesNotThrow(SoundManager::uncutAllSound);
    }
}