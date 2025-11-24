package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NoteEvent.
 */
class NoteEventTest {
    
    @Test
    void testNoteEventCreation() {
        NoteEvent event = new NoteEvent(60, 1000, true);
        
        assertEquals(60, event.getMidiNote());
        assertEquals(1000, event.getTimestamp());
        assertTrue(event.isNoteOn());
        assertFalse(event.isNoteOff());
    }
    
    @Test
    void testNoteOffEvent() {
        NoteEvent event = new NoteEvent(60, 1000, false);
        
        assertFalse(event.isNoteOn());
        assertTrue(event.isNoteOff());
    }
    
    @Test
    void testInvalidMidiNote() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NoteEvent(-1, 1000, true);
        }, "Should throw exception for negative MIDI note");
        
        assertThrows(IllegalArgumentException.class, () -> {
            new NoteEvent(128, 1000, true);
        }, "Should throw exception for MIDI note > 127");
    }
    
    @Test
    void testInvalidTimestamp() {
        assertThrows(IllegalArgumentException.class, () -> {
            new NoteEvent(60, -1, true);
        }, "Should throw exception for negative timestamp");
    }
    
    @Test
    void testEquals() {
        NoteEvent event1 = new NoteEvent(60, 1000, true);
        NoteEvent event2 = new NoteEvent(60, 1000, true);
        NoteEvent event3 = new NoteEvent(61, 1000, true);
        NoteEvent event4 = new NoteEvent(60, 1001, true);
        NoteEvent event5 = new NoteEvent(60, 1000, false);
        
        assertEquals(event1, event2, "Equal events should be equal");
        assertNotEquals(event1, event3, "Different notes should not be equal");
        assertNotEquals(event1, event4, "Different timestamps should not be equal");
        assertNotEquals(event1, event5, "Different note on/off should not be equal");
    }
    
    @Test
    void testHashCode() {
        NoteEvent event1 = new NoteEvent(60, 1000, true);
        NoteEvent event2 = new NoteEvent(60, 1000, true);
        
        assertEquals(event1.hashCode(), event2.hashCode(),
            "Equal events should have same hash code");
    }
    
    @Test
    void testToString() {
        NoteEvent event = new NoteEvent(60, 1000, true);
        String str = event.toString();
        
        assertTrue(str.contains("60"), "Should contain MIDI note");
        assertTrue(str.contains("1000"), "Should contain timestamp");
        assertTrue(str.contains("ON") || str.contains("OFF"), "Should contain note state");
    }
}

