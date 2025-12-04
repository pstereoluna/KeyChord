package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Recorder.
 */
class RecorderTest {
    private Recorder recorder;
    
    @BeforeEach
    void setUp() {
        recorder = new Recorder();
    }
    
    @Test
    void testInitialState() {
        assertFalse(recorder.isRecording(), "Should not be recording initially");
        assertEquals(-1, recorder.getCurrentTimestamp(), "Should return -1 when not recording");
    }
    
    @Test
    void testStartRecording() {
        recorder.startRecording();
        
        assertTrue(recorder.isRecording(), "Should be recording after start");
        assertTrue(recorder.getCurrentTimestamp() >= 0, "Timestamp should be non-negative");
    }
    
    @Test
    void testStopRecording() {
        recorder.startRecording();
        recorder.stopRecording();
        
        assertFalse(recorder.isRecording(), "Should not be recording after stop");
        assertEquals(-1, recorder.getCurrentTimestamp(), "Should return -1 when not recording");
    }
    
    @Test
    void testRecordNoteOn() throws InterruptedException {
        recorder.startRecording();
        Thread.sleep(10); // Small delay to ensure timestamp > 0
        
        NoteEvent event = recorder.recordNoteOn(60);
        
        assertNotNull(event, "Should return event when recording");
        assertEquals(60, event.getMidiNote());
        assertTrue(event.isNoteOn());
        assertTrue(event.getTimestamp() >= 0, "Timestamp should be non-negative");
    }
    
    @Test
    void testRecordNoteOnWhenNotRecording() {
        NoteEvent event = recorder.recordNoteOn(60);
        
        assertNull(event, "Should return null when not recording");
    }
    
    @Test
    void testRecordNoteOff() throws InterruptedException {
        recorder.startRecording();
        Thread.sleep(10);
        
        NoteEvent event = recorder.recordNoteOff(60);
        
        assertNotNull(event, "Should return event when recording");
        assertEquals(60, event.getMidiNote());
        assertTrue(event.isNoteOff());
    }
    
    @Test
    void testRecordChordOn() throws InterruptedException {
        recorder.startRecording();
        Thread.sleep(10);
        
        List<NoteEvent> events = recorder.recordChordOn(List.of(60, 64, 67));
        
        assertNotNull(events, "Should return events when recording");
        assertEquals(3, events.size(), "Should return 3 events for 3 notes");
        
        long timestamp = events.get(0).getTimestamp();
        for (NoteEvent event : events) {
            assertEquals(timestamp, event.getTimestamp(), "All events should have same timestamp");
            assertTrue(event.isNoteOn(), "All events should be note on");
        }
    }
    
    @Test
    void testRecordChordOnWhenNotRecording() {
        List<NoteEvent> events = recorder.recordChordOn(List.of(60, 64, 67));
        
        assertNotNull(events, "Should return list (empty) when not recording");
        assertTrue(events.isEmpty(), "Should return empty list when not recording");
    }
    
    @Test
    void testRecordingTimestamps() throws InterruptedException {
        recorder.startRecording();
        Thread.sleep(50);
        
        NoteEvent event1 = recorder.recordNoteOn(60);
        Thread.sleep(50);
        NoteEvent event2 = recorder.recordNoteOn(64);
        
        assertTrue(event2.getTimestamp() > event1.getTimestamp(),
            "Later event should have larger timestamp");
    }
    
    @Test
    @DisplayName("Start recording twice without stopping should reset")
    void testStartRecordingTwice() throws InterruptedException {
        recorder.startRecording();
        Thread.sleep(10);
        long firstTimestamp = recorder.getCurrentTimestamp();
        
        // Start again without stopping
        recorder.startRecording();
        Thread.sleep(10);
        long secondTimestamp = recorder.getCurrentTimestamp();
        
        // Second start should reset the timestamp
        assertTrue(secondTimestamp < firstTimestamp + 20, 
            "Second start should reset timestamp (within 20ms tolerance)");
    }
    
    @Test
    @DisplayName("Stop recording without starting should handle gracefully")
    void testStopWithoutStart() {
        // Should not throw exception
        assertDoesNotThrow(() -> {
            recorder.stopRecording();
        }, "Stopping without starting should not throw");
        
        assertFalse(recorder.isRecording(), "Should not be recording");
    }
    
    @Test
    @DisplayName("Record when not recording should return null gracefully")
    void testRecordWhenNotRecording() {
        NoteEvent event = recorder.recordNoteOn(60);
        assertNull(event, "Should return null when not recording");
        
        NoteEvent eventOff = recorder.recordNoteOff(60);
        assertNull(eventOff, "Should return null when not recording");
        
        List<NoteEvent> chordEvents = recorder.recordChordOn(List.of(60, 64, 67));
        assertNotNull(chordEvents, "Should return list (not null)");
        assertTrue(chordEvents.isEmpty(), "Should return empty list when not recording");
    }
}

