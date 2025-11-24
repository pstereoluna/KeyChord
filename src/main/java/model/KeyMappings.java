package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps computer keyboard keys to MIDI note numbers.
 * Provides a standard QWERTY keyboard layout for piano keys.
 * 
 * @author KeyChord
 */
public class KeyMappings {
    // Standard MIDI note for middle C (C4)
    private static final int MIDDLE_C = 60;
    
    // Mapping from keyboard character to semitone offset from middle C
    private static final Map<Character, Integer> KEY_TO_OFFSET = new HashMap<>();
    
    static {
        // White keys (lowercase)
        KEY_TO_OFFSET.put('a', -9);  // C
        KEY_TO_OFFSET.put('s', -7);  // D
        KEY_TO_OFFSET.put('d', -5);  // E
        KEY_TO_OFFSET.put('f', -4);  // F
        KEY_TO_OFFSET.put('g', -2);  // G
        KEY_TO_OFFSET.put('h', 0);   // A (middle C area)
        KEY_TO_OFFSET.put('j', 2);   // B
        KEY_TO_OFFSET.put('k', 3);   // C
        KEY_TO_OFFSET.put('l', 5);   // D
        KEY_TO_OFFSET.put(';', 7);   // E
        KEY_TO_OFFSET.put('\'', 8);  // F
        
        // Black keys (uppercase for sharps)
        KEY_TO_OFFSET.put('w', -8);  // C#
        KEY_TO_OFFSET.put('e', -6);  // D#
        KEY_TO_OFFSET.put('t', -3);  // F#
        KEY_TO_OFFSET.put('y', -1);  // G#
        KEY_TO_OFFSET.put('u', 1);   // A#
        KEY_TO_OFFSET.put('o', 4);   // C#
        KEY_TO_OFFSET.put('p', 6);   // D#
    }
    
    /**
     * Gets the MIDI note number for a given keyboard character.
     * 
     * @param keyChar the keyboard character
     * @return the MIDI note number, or -1 if the key is not mapped
     */
    public static int getMidiNote(char keyChar) {
        Integer offset = KEY_TO_OFFSET.get(Character.toLowerCase(keyChar));
        if (offset == null) {
            return -1;
        }
        int note = MIDDLE_C + offset;
        // Ensure note is within valid MIDI range
        if (note < 0 || note > 127) {
            return -1;
        }
        return note;
    }
    
    /**
     * Checks if a keyboard character is mapped to a MIDI note.
     * 
     * @param keyChar the keyboard character
     * @return true if the key is mapped, false otherwise
     */
    public static boolean isMapped(char keyChar) {
        return KEY_TO_OFFSET.containsKey(Character.toLowerCase(keyChar));
    }
    
    /**
     * Gets the base MIDI note (middle C).
     * 
     * @return the MIDI note number for middle C
     */
    public static int getMiddleC() {
        return MIDDLE_C;
    }
}

