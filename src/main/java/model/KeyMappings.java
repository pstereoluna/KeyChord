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

        // White keys (Lower Octave Starts at C3, Offset -12)
        KEY_TO_OFFSET.put('q', -12); // C3
        KEY_TO_OFFSET.put('w', -10); // D3
        KEY_TO_OFFSET.put('e', -8);  // E3
        KEY_TO_OFFSET.put('r', -7);  // F3
        KEY_TO_OFFSET.put('t', -5);  // G3
        KEY_TO_OFFSET.put('y', -3);  // A3
        KEY_TO_OFFSET.put('u', -1);  // B3
        KEY_TO_OFFSET.put('i', 0);   // C4 (Middle C)

        // Black keys
        KEY_TO_OFFSET.put('2', -11); // C#3
        KEY_TO_OFFSET.put('3', -9);  // D#3
        KEY_TO_OFFSET.put('5', -6);  // F#3
        KEY_TO_OFFSET.put('6', -4);  // G#3
        KEY_TO_OFFSET.put('7', -2);  // A#3

        // White keys (Upper Octave Starts at C4/Middle C, Offset 0)
        KEY_TO_OFFSET.put('c', 0);   // C4 (Middle C)
        KEY_TO_OFFSET.put('v', 2);   // D4
        KEY_TO_OFFSET.put('b', 4);   // E4
        KEY_TO_OFFSET.put('n', 5);   // F4
        KEY_TO_OFFSET.put('m', 7);   // G4
        KEY_TO_OFFSET.put(',', 9);   // A4
        KEY_TO_OFFSET.put('.', 11);  // B4
        KEY_TO_OFFSET.put('/', 12);  // C5

        // Black keys
        KEY_TO_OFFSET.put('f', 1);   // C#4
        KEY_TO_OFFSET.put('g', 3);   // D#4
        KEY_TO_OFFSET.put('j', 6);   // F#4
        KEY_TO_OFFSET.put('k', 8);   // G#4
        KEY_TO_OFFSET.put('l', 10);  // A#4

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

