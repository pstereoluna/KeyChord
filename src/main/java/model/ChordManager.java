package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates chords from a root MIDI note.
 * Supports various chord types with extensible design.
 * Default chord type is a major triad.
 * 
 * @author KeyChord
 */
public class ChordManager {
    
    /**
     * Chord type enumeration for different chord types.
     */
    public enum ChordType {
        MAJOR(new int[]{0, 4, 7}),
        MINOR(new int[]{0, 3, 7}),
        DIMINISHED(new int[]{0, 3, 6}),
        AUGMENTED(new int[]{0, 4, 8}),
        MAJOR_SEVENTH(new int[]{0, 4, 7, 11}),
        MINOR_SEVENTH(new int[]{0, 3, 7, 10}),
        SUS2(new int[]{0, 2, 7}),
        SUS4(new int[]{0, 5, 7});
        
        private final int[] intervals;
        
        ChordType(int[] intervals) {
            this.intervals = intervals.clone();
        }
        
        public int[] getIntervals() {
            return intervals.clone();
        }
    }
    
    private ChordType defaultChordType;
    
    /**
     * Creates a ChordManager with the default major triad chord type.
     */
    public ChordManager() {
        this.defaultChordType = ChordType.MAJOR;
    }
    
    /**
     * Creates a ChordManager with a specified default chord type.
     * 
     * @param defaultChordType the default chord type to use
     */
    public ChordManager(ChordType defaultChordType) {
        this.defaultChordType = defaultChordType;
    }
    
    /**
     * Generates a chord from a root MIDI note using the default chord type.
     * 
     * @param rootNote the root MIDI note (0-127)
     * @return a list of MIDI notes representing the chord
     * @throws IllegalArgumentException if rootNote is out of range
     */
    public List<Integer> generateChord(int rootNote) {
        return generateChord(rootNote, defaultChordType);
    }
    
    /**
     * Generates a chord from a root MIDI note using a specific chord type.
     * 
     * @param rootNote the root MIDI note (0-127)
     * @param chordType the type of chord to generate
     * @return a list of MIDI notes representing the chord
     * @throws IllegalArgumentException if rootNote is out of range
     */
    public List<Integer> generateChord(int rootNote, ChordType chordType) {
        if (rootNote < 0 || rootNote > 127) {
            throw new IllegalArgumentException("Root note must be between 0 and 127");
        }
        
        int[] intervals = chordType.getIntervals();
        List<Integer> chordNotes = new ArrayList<>();
        
        for (int interval : intervals) {
            int note = rootNote + interval;
            // Clamp to valid MIDI range
            if (note >= 0 && note <= 127) {
                chordNotes.add(note);
            }
        }
        
        return chordNotes;
    }
    
    /**
     * Sets the default chord type.
     * 
     * @param chordType the new default chord type
     */
    public void setDefaultChordType(ChordType chordType) {
        this.defaultChordType = chordType;
    }
    
    /**
     * Gets the current default chord type.
     * 
     * @return the default chord type
     */
    public ChordType getDefaultChordType() {
        return defaultChordType;
    }
}

