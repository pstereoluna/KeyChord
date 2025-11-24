package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Track.
 */
class TrackTest {
    private Track track;
    
    @BeforeEach
    void setUp() {
        track = new Track(1);
    }
    
    @Test
    void testTrackCreation() {
        assertEquals(1, track.getTrackNumber());
        assertTrue(track.isEmpty());
        assertEquals(0, track.getEventCount());
    }
    
    @Test
    void testInvalidTrackNumber() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Track(0);
        }, "Should throw exception for track number < 1");
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Track(10);
        }, "Should throw exception for track number > 9");
    }
    
    @Test
    void testAddNoteEvent() {
        NoteEvent event = new NoteEvent(60, 1000, true);
        track.addNoteEvent(event);
        
        assertFalse(track.isEmpty());
        assertEquals(1, track.getEventCount());
        
        List<NoteEvent> events = track.getEvents();
        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
    }
    
    @Test
    void testAddNullNoteEvent() {
        assertThrows(IllegalArgumentException.class, () -> {
            track.addNoteEvent(null);
        }, "Should throw exception for null event");
    }
    
    @Test
    void testMultipleSimultaneousEvents() {
        // Add multiple events with same timestamp (chord)
        NoteEvent event1 = new NoteEvent(60, 1000, true);
        NoteEvent event2 = new NoteEvent(64, 1000, true);
        NoteEvent event3 = new NoteEvent(67, 1000, true);
        
        track.addNoteEvent(event1);
        track.addNoteEvent(event2);
        track.addNoteEvent(event3);
        
        assertEquals(3, track.getEventCount());
        List<NoteEvent> events = track.getEvents();
        assertEquals(3, events.size());
    }
    
    @Test
    void testAddNoteEvents() {
        NoteEvent event1 = new NoteEvent(60, 1000, true);
        NoteEvent event2 = new NoteEvent(64, 1000, true);
        
        track.addNoteEvents(List.of(event1, event2));
        
        assertEquals(2, track.getEventCount());
    }
    
    @Test
    void testAddNoteEventsWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            track.addNoteEvents(null);
        }, "Should throw exception for null list");
        
        assertThrows(IllegalArgumentException.class, () -> {
            track.addNoteEvents(List.of(new NoteEvent(60, 1000, true), null));
        }, "Should throw exception for list containing null");
    }
    
    @Test
    void testClear() {
        track.addNoteEvent(new NoteEvent(60, 1000, true));
        track.addNoteEvent(new NoteEvent(64, 1000, true));
        
        track.clear();
        
        assertTrue(track.isEmpty());
        assertEquals(0, track.getEventCount());
    }
    
    @Test
    void testGetEventsSorted() {
        NoteEvent event1 = new NoteEvent(60, 2000, true);
        NoteEvent event2 = new NoteEvent(64, 1000, true);
        NoteEvent event3 = new NoteEvent(67, 1500, true);
        
        track.addNoteEvent(event1);
        track.addNoteEvent(event2);
        track.addNoteEvent(event3);
        
        List<NoteEvent> events = track.getEvents();
        assertEquals(1000, events.get(0).getTimestamp(), "Events should be sorted by timestamp");
        assertEquals(1500, events.get(1).getTimestamp());
        assertEquals(2000, events.get(2).getTimestamp());
    }
    
    @Test
    void testGetEventsUnmodifiable() {
        track.addNoteEvent(new NoteEvent(60, 1000, true));
        
        List<NoteEvent> events = track.getEvents();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            events.add(new NoteEvent(64, 1000, true));
        }, "Returned list should be unmodifiable");
    }
}

