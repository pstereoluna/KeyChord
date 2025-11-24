package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PianoModel.
 * Updated for RecordingManager architecture.
 */
class PianoModelTest {
    private PianoModel model;
    
    @BeforeEach
    void setUp() throws javax.sound.midi.MidiUnavailableException {
        model = new PianoModel();
    }
    
    @Test
    void testInitialState() {
        assertFalse(model.isRecording(), "Should not be recording initially");
        assertFalse(model.isPlaying(), "Should not be playing initially");
        assertNotNull(model.getRecordingManager(), "RecordingManager should be initialized");
        assertNotNull(model.getChordManager(), "ChordManager should be initialized");
    }
    
    @Test
    void testGetRecordingManager() {
        RecordingManager manager = model.getRecordingManager();
        assertNotNull(manager, "Should return RecordingManager");
        assertSame(manager, model.getRecordingManager(), "Should return same instance");
    }
    
    @Test
    void testStartStopRecording() {
        model.startRecording();
        assertTrue(model.isRecording(), "Should be recording after start");
        
        Recording saved = model.stopRecording();
        assertFalse(model.isRecording(), "Should not be recording after stop");
        assertNotNull(saved, "Should return saved Recording");
        assertNotNull(saved.getName(), "Recording should have a name");
    }
    
    @Test
    void testStopRecordingWithName() {
        model.startRecording();
        Recording saved = model.stopRecording("My Recording");
        
        assertNotNull(saved, "Should return saved Recording");
        assertEquals("My Recording", saved.getName(), "Recording should have specified name");
        
        Recording retrieved = model.getRecordingManager().getRecording("My Recording");
        assertNotNull(retrieved, "Should be able to retrieve saved recording");
        assertEquals(saved.getName(), retrieved.getName(), "Retrieved recording should match");
    }
    
    @Test
    void testPlayNoteWhileRecording() {
        model.startRecording();
        model.playNote(60);
        
        Recording current = model.getRecordingManager().getCurrentRecording();
        assertNotNull(current, "Should have current recording");
        
        List<NoteEvent> events = current.getEvents();
        assertTrue(events.size() >= 1, "Should record at least one note event");
        
        NoteEvent event = events.get(0);
        assertEquals(60, event.getMidiNote(), "Should record correct MIDI note");
        assertTrue(event.isNoteOn(), "Should be note on event");
    }
    
    @Test
    void testPlayChord() {
        // Play a chord without recording (uses default MAJOR)
        model.playChord(60);
        
        // Should not throw exception
        assertTrue(true, "Playing chord should succeed");
    }
    
    @Test
    void testPlayChordWhileRecording() {
        model.startRecording();
        model.playChord(60);
        
        Recording current = model.getRecordingManager().getCurrentRecording();
        assertNotNull(current, "Should have current recording");
        
        List<NoteEvent> events = current.getEvents();
        assertTrue(events.size() >= 3, "Should record at least 3 notes for chord");
        
        // All events should have same timestamp
        long timestamp = events.get(0).getTimestamp();
        for (NoteEvent event : events) {
            assertEquals(timestamp, event.getTimestamp(), "All chord notes should have same timestamp");
            assertTrue(event.isNoteOn(), "All should be note on events");
        }
    }
    
    @Test
    void testClearCurrentRecording() {
        model.startRecording();
        model.playNote(60);
        
        Recording current = model.getRecordingManager().getCurrentRecording();
        assertNotNull(current, "Should have current recording");
        assertFalse(current.isEmpty(), "Recording should have events");
        
        model.clearCurrentRecording();
        assertTrue(current.isEmpty(), "Recording should be empty after clear");
    }
    
    @Test
    void testGetChordManager() {
        ChordManager chordManager = model.getChordManager();
        assertNotNull(chordManager, "Should return chord manager");
    }
    
    @Test
    void testPlayNoteWithoutRecording() {
        // Should not throw exception
        model.playNoteWithoutRecording(60);
        model.stopNoteWithoutRecording(60);
        assertTrue(true, "Playing note without recording should succeed");
    }
    
    @Test
    void testStartPlaybackWithRecording() {
        // Create and save a recording
        model.startRecording();
        model.playNote(60);
        model.stopRecording("Test Recording");
        
        // Start playback
        model.startPlayback("Test Recording");
        
        // Should not throw exception
        assertTrue(true, "Playback should start");
        
        // Stop playback
        model.stopPlayback();
        assertFalse(model.isPlaying(), "Should not be playing after stop");
    }
    
    @Test
    void testStartPlaybackWithNonexistentRecording() {
        // Should not throw, but playback won't start
        model.startPlayback("Nonexistent");
        
        // Should not be playing
        assertFalse(model.isPlaying(), "Should not be playing nonexistent recording");
    }
}
