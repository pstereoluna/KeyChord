package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Recording.
 * Tests recording creation, event management, and properties.
 */
class RecordingTest {
    private Recording recording;
    
    @BeforeEach
    void setUp() {
        recording = new Recording("Test Recording");
    }
    
    @Test
    void testRecordingCreation() {
        assertEquals("Test Recording", recording.getName());
        assertTrue(recording.isEmpty());
        assertEquals(0, recording.getEventCount());
        assertTrue(recording.getCreationTime() > 0);
    }
    
    @Test
    void testRecordingCreationWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Recording(null);
        }, "Should throw exception for null name");
    }
    
    @Test
    void testRecordingCreationWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Recording("");
        }, "Should throw exception for empty name");
    }
    
    @Test
    void testRecordingCreationWithWhitespaceName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Recording("   ");
        }, "Should throw exception for whitespace-only name");
    }
    
    @Test
    void testRecordingCreationWithEvents() {
        List<NoteEvent> events = List.of(
            new NoteEvent(60, 0, true),
            new NoteEvent(64, 0, true),
            new NoteEvent(67, 0, true)
        );
        
        Recording rec = new Recording("Chord Recording", events);
        assertEquals(3, rec.getEventCount());
        assertFalse(rec.isEmpty());
    }
    
    @Test
    void testAddNoteEvent() {
        NoteEvent event = new NoteEvent(60, 1000, true);
        recording.addNoteEvent(event);
        
        assertFalse(recording.isEmpty());
        assertEquals(1, recording.getEventCount());
        
        List<NoteEvent> events = recording.getEvents();
        assertEquals(1, events.size());
        assertEquals(60, events.get(0).getMidiNote());
    }
    
    @Test
    void testAddNullNoteEvent() {
        assertThrows(IllegalArgumentException.class, () -> {
            recording.addNoteEvent(null);
        }, "Should throw exception for null event");
    }
    
    @Test
    void testAddNoteEvents() {
        List<NoteEvent> events = List.of(
            new NoteEvent(60, 1000, true),
            new NoteEvent(64, 1000, true),
            new NoteEvent(67, 1000, true)
        );
        
        recording.addNoteEvents(events);
        
        assertEquals(3, recording.getEventCount());
    }
    
    @Test
    void testAddNoteEventsWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            recording.addNoteEvents(null);
        }, "Should throw exception for null list");
        
        assertThrows(NullPointerException.class, () -> {
            recording.addNoteEvents(List.of(new NoteEvent(60, 1000, true), null));
        }, "Should throw exception for list containing null");
    }
    
    @Test
    void testClear() {
        recording.addNoteEvent(new NoteEvent(60, 1000, true));
        recording.addNoteEvent(new NoteEvent(64, 1000, true));
        
        recording.clear();
        
        assertTrue(recording.isEmpty());
        assertEquals(0, recording.getEventCount());
    }
    
    @Test
    void testGetEventsSorted() {
        NoteEvent event1 = new NoteEvent(60, 2000, true);
        NoteEvent event2 = new NoteEvent(64, 1000, true);
        NoteEvent event3 = new NoteEvent(67, 1500, true);
        
        recording.addNoteEvent(event1);
        recording.addNoteEvent(event2);
        recording.addNoteEvent(event3);
        
        List<NoteEvent> events = recording.getEvents();
        assertEquals(1000, events.get(0).getTimestamp(), "Events should be sorted by timestamp");
        assertEquals(1500, events.get(1).getTimestamp());
        assertEquals(2000, events.get(2).getTimestamp());
    }
    
    @Test
    void testGetEventsUnmodifiable() {
        recording.addNoteEvent(new NoteEvent(60, 1000, true));
        
        List<NoteEvent> events = recording.getEvents();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            events.add(new NoteEvent(64, 1000, true));
        }, "Returned list should be unmodifiable");
    }
    
    @Test
    void testGetDuration() {
        assertEquals(0, recording.getDuration(), "Empty recording should have 0 duration");
        
        recording.addNoteEvent(new NoteEvent(60, 1000, true));
        recording.addNoteEvent(new NoteEvent(64, 2000, true));
        recording.addNoteEvent(new NoteEvent(67, 1500, true));
        
        assertEquals(2000, recording.getDuration(), "Duration should be max timestamp");
    }
    
    @Test
    void testMultipleEventsSameTimestamp() {
        // Simulate chord recording (multiple notes at same timestamp)
        recording.addNoteEvent(new NoteEvent(60, 1000, true));
        recording.addNoteEvent(new NoteEvent(64, 1000, true));
        recording.addNoteEvent(new NoteEvent(67, 1000, true));
        
        List<NoteEvent> events = recording.getEvents();
        assertEquals(3, events.size());
        
        long timestamp = events.get(0).getTimestamp();
        for (NoteEvent event : events) {
            assertEquals(timestamp, event.getTimestamp(), "All chord notes should have same timestamp");
        }
    }
    
    @Test
    @DisplayName("Recording with 10000+ events should handle performance")
    void testLargeRecording() {
        // Add 10000 events
        for (int i = 0; i < 10000; i++) {
            recording.addNoteEvent(new NoteEvent(60 + (i % 12), i * 10, i % 2 == 0));
        }
        
        assertEquals(10000, recording.getEventCount(), "Should have 10000 events");
        assertFalse(recording.isEmpty(), "Should not be empty");
        
        // Verify getEvents() still works and returns sorted
        List<NoteEvent> events = recording.getEvents();
        assertEquals(10000, events.size(), "Should return all events");
        
        // Verify sorted order
        for (int i = 1; i < events.size(); i++) {
            assertTrue(events.get(i).getTimestamp() >= events.get(i - 1).getTimestamp(),
                "Events should be sorted by timestamp");
        }
    }
    
    @Test
    @DisplayName("Events added out of order should be sorted by getEvents")
    void testOutOfOrderEvents() {
        // Add events in reverse timestamp order
        recording.addNoteEvent(new NoteEvent(60, 3000, true));
        recording.addNoteEvent(new NoteEvent(64, 1000, true));
        recording.addNoteEvent(new NoteEvent(67, 2000, true));
        recording.addNoteEvent(new NoteEvent(60, 500, false));
        
        List<NoteEvent> events = recording.getEvents();
        
        // Verify sorted by timestamp
        assertEquals(500, events.get(0).getTimestamp(), "First event should be earliest");
        assertEquals(1000, events.get(1).getTimestamp());
        assertEquals(2000, events.get(2).getTimestamp());
        assertEquals(3000, events.get(3).getTimestamp(), "Last event should be latest");
    }
}

