package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.sound.midi.MidiUnavailableException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MidiSoundManager.
 * Tests singleton pattern, channel configuration, and note playback.
 */
class MidiSoundManagerTest {
    
    @BeforeEach
    void setUp() {
        // Reset singleton before each test
        MidiSoundManager.resetInstance();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        try {
            MidiSoundManager instance = MidiSoundManager.getInstance();
            instance.close();
        } catch (Exception e) {
            // Ignore cleanup errors
        }
        MidiSoundManager.resetInstance();
    }
    
    @Test
    void testSingletonInstance() throws MidiUnavailableException {
        MidiSoundManager instance1 = MidiSoundManager.getInstance();
        MidiSoundManager instance2 = MidiSoundManager.getInstance();
        
        assertSame(instance1, instance2, "Should return the same singleton instance");
    }
    
    @Test
    void testChannelIsZero() throws MidiUnavailableException {
        MidiSoundManager manager = MidiSoundManager.getInstance();
        
        assertEquals(0, manager.getChannel(), "Channel should be 0, not 9 (percussion)");
        assertNotEquals(9, manager.getChannel(), "Channel must NOT be 9 (percussion channel)");
    }
    
    @Test
    void testProgramIsSetToPiano() throws MidiUnavailableException {
        MidiSoundManager manager = MidiSoundManager.getInstance();
        
        int program = manager.getProgram();
        assertEquals(0, program, "Program should be 0 (Acoustic Grand Piano)");
    }
    
    @Test
    void testPlayNoteDoesNotThrow() throws MidiUnavailableException {
        MidiSoundManager manager = MidiSoundManager.getInstance();
        
        // Should not throw exception
        assertDoesNotThrow(() -> {
            manager.playNote(60); // Middle C
        });
    }
    
    @Test
    void testStopNoteDoesNotThrow() throws MidiUnavailableException {
        MidiSoundManager manager = MidiSoundManager.getInstance();
        
        // Should not throw exception
        assertDoesNotThrow(() -> {
            manager.playNote(60);
            manager.stopNote(60);
        });
    }
    
    @Test
    void testPlayNoteWithVelocity() throws MidiUnavailableException {
        MidiSoundManager manager = MidiSoundManager.getInstance();
        
        // Test with different velocities (80-100 range for normal piano)
        assertDoesNotThrow(() -> {
            manager.playNote(60, 80);
            manager.playNote(64, 90);
            manager.playNote(67, 100);
        });
    }
    
    @Test
    void testInvalidMidiNote() throws MidiUnavailableException {
        MidiSoundManager manager = MidiSoundManager.getInstance();
        
        assertThrows(IllegalArgumentException.class, () -> {
            manager.playNote(-1);
        }, "Should throw exception for negative MIDI note");
        
        assertThrows(IllegalArgumentException.class, () -> {
            manager.playNote(128);
        }, "Should throw exception for MIDI note > 127");
    }
    
    @Test
    void testInvalidVelocity() throws MidiUnavailableException {
        MidiSoundManager manager = MidiSoundManager.getInstance();
        
        assertThrows(IllegalArgumentException.class, () -> {
            manager.playNote(60, -1);
        }, "Should throw exception for negative velocity");
        
        assertThrows(IllegalArgumentException.class, () -> {
            manager.playNote(60, 128);
        }, "Should throw exception for velocity > 127");
    }
    
    @Test
    void testIsOpen() throws MidiUnavailableException {
        MidiSoundManager manager = MidiSoundManager.getInstance();
        
        assertTrue(manager.isOpen(), "Synthesizer should be open after initialization");
    }
    
    @Test
    void testAllNotesOff() throws MidiUnavailableException {
        MidiSoundManager manager = MidiSoundManager.getInstance();
        
        // Play multiple notes
        manager.playNote(60);
        manager.playNote(64);
        manager.playNote(67);
        
        // Should not throw
        assertDoesNotThrow(() -> {
            manager.allNotesOff();
        });
    }
    
    @Test
    void testPolyphony() throws MidiUnavailableException {
        MidiSoundManager manager = MidiSoundManager.getInstance();
        
        // Play multiple notes simultaneously (polyphony test)
        assertDoesNotThrow(() -> {
            manager.playNote(60); // C
            manager.playNote(64); // E
            manager.playNote(67); // G
            manager.playNote(72); // C (octave higher)
            
            // All should play simultaneously
            manager.allNotesOff();
        });
    }
}

