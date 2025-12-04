package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for KeyMappings.
 */
class KeyMappingsTest {
    
    @Test
    void testGetMidiNote_WhiteKeysLowerOctave() {
        // Test white keys in lower octave (C3 to C4)
        assertEquals(48, KeyMappings.getMidiNote('q'), "q should map to C3 (MIDI 48)");
        assertEquals(50, KeyMappings.getMidiNote('w'), "w should map to D3 (MIDI 50)");
        assertEquals(52, KeyMappings.getMidiNote('e'), "e should map to E3 (MIDI 52)");
        assertEquals(53, KeyMappings.getMidiNote('r'), "r should map to F3 (MIDI 53)");
        assertEquals(55, KeyMappings.getMidiNote('t'), "t should map to G3 (MIDI 55)");
        assertEquals(57, KeyMappings.getMidiNote('y'), "y should map to A3 (MIDI 57)");
        assertEquals(59, KeyMappings.getMidiNote('u'), "u should map to B3 (MIDI 59)");
        assertEquals(60, KeyMappings.getMidiNote('i'), "i should map to C4/Middle C (MIDI 60)");
    }
    
    @Test
    void testGetMidiNote_WhiteKeysUpperOctave() {
        // Test white keys in upper octave (C4 to C5)
        assertEquals(60, KeyMappings.getMidiNote('c'), "c should map to C4/Middle C (MIDI 60)");
        assertEquals(62, KeyMappings.getMidiNote('v'), "v should map to D4 (MIDI 62)");
        assertEquals(64, KeyMappings.getMidiNote('b'), "b should map to E4 (MIDI 64)");
        assertEquals(65, KeyMappings.getMidiNote('n'), "n should map to F4 (MIDI 65)");
        assertEquals(67, KeyMappings.getMidiNote('m'), "m should map to G4 (MIDI 67)");
        assertEquals(69, KeyMappings.getMidiNote(','), ", should map to A4 (MIDI 69)");
        assertEquals(71, KeyMappings.getMidiNote('.'), ". should map to B4 (MIDI 71)");
        assertEquals(72, KeyMappings.getMidiNote('/'), "/ should map to C5 (MIDI 72)");
    }
    
    @Test
    void testGetMidiNote_BlackKeysLowerOctave() {
        // Test black keys in lower octave
        assertEquals(49, KeyMappings.getMidiNote('2'), "2 should map to C#3 (MIDI 49)");
        assertEquals(51, KeyMappings.getMidiNote('3'), "3 should map to D#3 (MIDI 51)");
        assertEquals(54, KeyMappings.getMidiNote('5'), "5 should map to F#3 (MIDI 54)");
        assertEquals(56, KeyMappings.getMidiNote('6'), "6 should map to G#3 (MIDI 56)");
        assertEquals(58, KeyMappings.getMidiNote('7'), "7 should map to A#3 (MIDI 58)");
    }
    
    @Test
    void testGetMidiNote_BlackKeysUpperOctave() {
        // Test black keys in upper octave
        assertEquals(61, KeyMappings.getMidiNote('f'), "f should map to C#4 (MIDI 61)");
        assertEquals(63, KeyMappings.getMidiNote('g'), "g should map to D#4 (MIDI 63)");
        assertEquals(66, KeyMappings.getMidiNote('j'), "j should map to F#4 (MIDI 66)");
        assertEquals(68, KeyMappings.getMidiNote('k'), "k should map to G#4 (MIDI 68)");
        assertEquals(70, KeyMappings.getMidiNote('l'), "l should map to A#4 (MIDI 70)");
    }
    
    @Test
    void testIsMapped_ValidKeys() {
        // Test white keys
        assertTrue(KeyMappings.isMapped('q'), "q should be mapped");
        assertTrue(KeyMappings.isMapped('w'), "w should be mapped");
        assertTrue(KeyMappings.isMapped('c'), "c should be mapped");
        assertTrue(KeyMappings.isMapped('v'), "v should be mapped");
        
        // Test black keys
        assertTrue(KeyMappings.isMapped('2'), "2 should be mapped");
        assertTrue(KeyMappings.isMapped('f'), "f should be mapped");
        assertTrue(KeyMappings.isMapped('g'), "g should be mapped");
    }
    
    @Test
    void testIsMapped_UnmappedKeys() {
        // Test keys that are NOT mapped
        assertFalse(KeyMappings.isMapped('a'), "a should not be mapped");
        assertFalse(KeyMappings.isMapped('s'), "s should not be mapped");
        assertFalse(KeyMappings.isMapped('h'), "h should not be mapped");
        assertFalse(KeyMappings.isMapped('z'), "z should not be mapped");
        assertFalse(KeyMappings.isMapped('x'), "x should not be mapped");
        assertFalse(KeyMappings.isMapped('d'), "d should not be mapped");
        assertFalse(KeyMappings.isMapped('p'), "p should not be mapped");
    }
    
    @Test
    void testCaseInsensitive() {
        // Test that mappings are case insensitive using valid mapped keys
        assertEquals(KeyMappings.getMidiNote('q'), KeyMappings.getMidiNote('Q'), 
            "Q and q should map to same note");
        assertEquals(KeyMappings.getMidiNote('c'), KeyMappings.getMidiNote('C'), 
            "C and c should map to same note");
        assertEquals(KeyMappings.getMidiNote('f'), KeyMappings.getMidiNote('F'), 
            "F and f should map to same note");
        
        // Test isMapped is also case insensitive
        assertEquals(KeyMappings.isMapped('q'), KeyMappings.isMapped('Q'), 
            "isMapped should be case insensitive");
        assertEquals(KeyMappings.isMapped('c'), KeyMappings.isMapped('C'), 
            "isMapped should be case insensitive");
    }
    
    @Test
    void testGetMiddleC() {
        int middleC = KeyMappings.getMiddleC();
        assertEquals(60, middleC, "Middle C should be MIDI note 60");
    }
    
    @Test
    void testUnmappedKey() {
        // Test that unmapped keys return -1
        assertEquals(-1, KeyMappings.getMidiNote('a'), "Unmapped key 'a' should return -1");
        assertEquals(-1, KeyMappings.getMidiNote('z'), "Unmapped key 'z' should return -1");
        assertEquals(-1, KeyMappings.getMidiNote('x'), "Unmapped key 'x' should return -1");
        assertEquals(-1, KeyMappings.getMidiNote('d'), "Unmapped key 'd' should return -1");
        assertEquals(-1, KeyMappings.getMidiNote('p'), "Unmapped key 'p' should return -1");
    }
    
    @Test
    void testAllMappedKeys() {
        // Verify all keys in KEY_TO_OFFSET are properly mapped
        char[] whiteKeysLower = {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i'};
        char[] whiteKeysUpper = {'c', 'v', 'b', 'n', 'm', ',', '.', '/'};
        char[] blackKeysLower = {'2', '3', '5', '6', '7'};
        char[] blackKeysUpper = {'f', 'g', 'j', 'k', 'l'};
        
        // Test all white keys
        for (char key : whiteKeysLower) {
            assertTrue(KeyMappings.isMapped(key), key + " should be mapped");
            assertTrue(KeyMappings.getMidiNote(key) >= 0, key + " should return valid MIDI note");
        }
        
        for (char key : whiteKeysUpper) {
            assertTrue(KeyMappings.isMapped(key), key + " should be mapped");
            assertTrue(KeyMappings.getMidiNote(key) >= 0, key + " should return valid MIDI note");
        }
        
        // Test all black keys
        for (char key : blackKeysLower) {
            assertTrue(KeyMappings.isMapped(key), key + " should be mapped");
            assertTrue(KeyMappings.getMidiNote(key) >= 0, key + " should return valid MIDI note");
        }
        
        for (char key : blackKeysUpper) {
            assertTrue(KeyMappings.isMapped(key), key + " should be mapped");
            assertTrue(KeyMappings.getMidiNote(key) >= 0, key + " should return valid MIDI note");
        }
    }
}
