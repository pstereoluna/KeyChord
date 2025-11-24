package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ChordManager.
 * Tests chord generation with various chord types.
 */
class ChordManagerTest {
    private ChordManager chordManager;
    
    @BeforeEach
    void setUp() {
        chordManager = new ChordManager();
    }
    
    @Test
    void testMajorChordGeneration() {
        // Test C major chord (C = 60)
        List<Integer> chord = chordManager.generateChord(60);
        
        assertEquals(3, chord.size(), "Major chord should have 3 notes");
        assertTrue(chord.contains(60), "Should contain root note C");
        assertTrue(chord.contains(64), "Should contain E (major 3rd)");
        assertTrue(chord.contains(67), "Should contain G (perfect 5th)");
    }
    
    @Test
    void testIntervalsCorrect() {
        // Test that intervals are correct for major triad
        List<Integer> chord = chordManager.generateChord(60);
        
        assertEquals(60, chord.get(0), "First note should be root");
        assertEquals(64, chord.get(1), "Second note should be major 3rd (+4 semitones)");
        assertEquals(67, chord.get(2), "Third note should be perfect 5th (+7 semitones)");
    }
    
    @Test
    void testMinorChordGeneration() {
        List<Integer> chord = chordManager.generateChord(60, ChordManager.ChordType.MINOR);
        
        assertEquals(3, chord.size(), "Minor chord should have 3 notes");
        assertTrue(chord.contains(60), "Should contain root note C");
        assertTrue(chord.contains(63), "Should contain Eb (minor 3rd)");
        assertTrue(chord.contains(67), "Should contain G (perfect 5th)");
    }
    
    @Test
    void testDiminishedChordGeneration() {
        List<Integer> chord = chordManager.generateChord(60, ChordManager.ChordType.DIMINISHED);
        
        assertEquals(3, chord.size(), "Diminished chord should have 3 notes");
        assertTrue(chord.contains(60), "Should contain root note C");
        assertTrue(chord.contains(63), "Should contain Eb (minor 3rd)");
        assertTrue(chord.contains(66), "Should contain Gb (diminished 5th)");
    }
    
    @Test
    void testMajorSeventhChordGeneration() {
        List<Integer> chord = chordManager.generateChord(60, ChordManager.ChordType.MAJOR_SEVENTH);
        
        assertEquals(4, chord.size(), "Major 7th chord should have 4 notes");
        assertTrue(chord.contains(60), "Should contain root note C");
        assertTrue(chord.contains(64), "Should contain E (major 3rd)");
        assertTrue(chord.contains(67), "Should contain G (perfect 5th)");
        assertTrue(chord.contains(71), "Should contain B (major 7th)");
    }
    
    @Test
    void testChordAtBoundary() {
        // Test chord at upper boundary
        List<Integer> chord = chordManager.generateChord(127);
        assertFalse(chord.isEmpty(), "Should generate at least root note");
        assertTrue(chord.get(0) == 127, "Root should be 127");
        
        // Test chord at lower boundary
        chord = chordManager.generateChord(0);
        assertFalse(chord.isEmpty(), "Should generate at least root note");
        assertTrue(chord.get(0) == 0, "Root should be 0");
    }
    
    @Test
    void testAugmentedChordGeneration() {
        List<Integer> chord = chordManager.generateChord(60, ChordManager.ChordType.AUGMENTED);
        
        assertEquals(3, chord.size(), "Augmented chord should have 3 notes");
        assertTrue(chord.contains(60), "Should contain root note C");
        assertTrue(chord.contains(64), "Should contain E (major 3rd)");
        assertTrue(chord.contains(68), "Should contain G# (augmented 5th)");
    }
    
    @Test
    void testMinorSeventhChordGeneration() {
        List<Integer> chord = chordManager.generateChord(60, ChordManager.ChordType.MINOR_SEVENTH);
        
        assertEquals(4, chord.size(), "Minor 7th chord should have 4 notes");
        assertTrue(chord.contains(60), "Should contain root note C");
        assertTrue(chord.contains(63), "Should contain Eb (minor 3rd)");
        assertTrue(chord.contains(67), "Should contain G (perfect 5th)");
        assertTrue(chord.contains(70), "Should contain Bb (minor 7th)");
    }
    
    @Test
    void testSus2ChordGeneration() {
        List<Integer> chord = chordManager.generateChord(60, ChordManager.ChordType.SUS2);
        
        assertEquals(3, chord.size(), "Sus2 chord should have 3 notes");
        assertTrue(chord.contains(60), "Should contain root note C");
        assertTrue(chord.contains(62), "Should contain D (2nd)");
        assertTrue(chord.contains(67), "Should contain G (perfect 5th)");
    }
    
    @Test
    void testSus4ChordGeneration() {
        List<Integer> chord = chordManager.generateChord(60, ChordManager.ChordType.SUS4);
        
        assertEquals(3, chord.size(), "Sus4 chord should have 3 notes");
        assertTrue(chord.contains(60), "Should contain root note C");
        assertTrue(chord.contains(65), "Should contain F (4th)");
        assertTrue(chord.contains(67), "Should contain G (perfect 5th)");
    }
    
    @Test
    void testInvalidRootNote() {
        assertThrows(IllegalArgumentException.class, () -> {
            chordManager.generateChord(-1);
        }, "Should throw exception for negative root note");
        
        assertThrows(IllegalArgumentException.class, () -> {
            chordManager.generateChord(128);
        }, "Should throw exception for root note > 127");
    }
    
    @Test
    void testDefaultChordType() {
        assertEquals(ChordManager.ChordType.MAJOR, chordManager.getDefaultChordType(),
            "Default chord type should be MAJOR");
    }
    
    @Test
    void testSetDefaultChordType() {
        chordManager.setDefaultChordType(ChordManager.ChordType.MINOR);
        assertEquals(ChordManager.ChordType.MINOR, chordManager.getDefaultChordType(),
            "Default chord type should be updated to MINOR");
        
        // Test that default is used when no type specified
        List<Integer> chord = chordManager.generateChord(60);
        assertTrue(chord.contains(63), "Should use minor chord intervals");
    }
}

