package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for KeyMappings.
 */
class KeyMappingsTest {
    
    @Test
    void testGetMidiNote() {
        // Test some white keys
        int noteA = KeyMappings.getMidiNote('a');
        assertTrue(noteA >= 0 && noteA <= 127, "Should return valid MIDI note");
        
        int noteH = KeyMappings.getMidiNote('h');
        assertTrue(noteH >= 0 && noteH <= 127, "Should return valid MIDI note");
    }
    
    @Test
    void testIsMapped() {
        assertTrue(KeyMappings.isMapped('a'), "Should be mapped");
        assertTrue(KeyMappings.isMapped('s'), "Should be mapped");
        assertTrue(KeyMappings.isMapped('w'), "Should be mapped (black key)");
        assertFalse(KeyMappings.isMapped('z'), "Should not be mapped");
        assertFalse(KeyMappings.isMapped('x'), "Should not be mapped");
    }
    
    @Test
    void testCaseInsensitive() {
        int noteLower = KeyMappings.getMidiNote('a');
        int noteUpper = KeyMappings.getMidiNote('A');
        
        assertEquals(noteLower, noteUpper, "Should be case insensitive");
    }
    
    @Test
    void testGetMiddleC() {
        int middleC = KeyMappings.getMiddleC();
        assertEquals(60, middleC, "Middle C should be MIDI note 60");
    }
    
    @Test
    void testUnmappedKey() {
        int note = KeyMappings.getMidiNote('z');
        assertEquals(-1, note, "Unmapped key should return -1");
    }
}

