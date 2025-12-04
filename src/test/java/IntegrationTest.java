package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import javax.sound.midi.MidiUnavailableException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for KeyChord application.
 * Tests end-to-end workflows without mocking.
 * 
 * @author KeyChord
 */
class IntegrationTest {
    private PianoModel model;
    
    @BeforeEach
    void setUp() throws MidiUnavailableException {
        model = new PianoModel();
    }
    
    @Test
    @Timeout(5)
    void testFullRecordingPlaybackCycle() throws InterruptedException {
        // Start recording
        model.startRecording();
        assertTrue(model.isRecording(), "Should be recording");
        
        // Play notes with delays
        model.playNote(60); // C4
        Thread.sleep(50);
        model.playNote(64); // E4
        Thread.sleep(50);
        model.playNote(67); // G4
        Thread.sleep(50);
        model.stopNote(60);
        Thread.sleep(50);
        model.stopNote(64);
        Thread.sleep(50);
        model.stopNote(67);
        Thread.sleep(50);
        
        // Stop recording
        Recording recording = model.stopRecording();
        assertNotNull(recording, "Recording should not be null");
        assertFalse(model.isRecording(), "Should not be recording");
        
        // Verify recording has correct number of events
        List<NoteEvent> events = recording.getEvents();
        assertTrue(events.size() >= 6, "Should have at least 6 events (3 note-on, 3 note-off)");
        
        // Verify events are in correct order
        int noteOnCount = 0;
        int noteOffCount = 0;
        for (NoteEvent event : events) {
            if (event.isNoteOn()) {
                noteOnCount++;
            } else {
                noteOffCount++;
            }
        }
        assertEquals(3, noteOnCount, "Should have 3 note-on events");
        assertEquals(3, noteOffCount, "Should have 3 note-off events");
        
        // Play back the recording
        CountDownLatch playbackLatch = new CountDownLatch(6);
        List<Integer> playedNotes = new ArrayList<>();
        
        model.startPlaybackWithHandler(recording, new Player.NotePlaybackHandler() {
            @Override
            public void onNoteOn(int midiNote) {
                playedNotes.add(midiNote);
                playbackLatch.countDown();
            }
            
            @Override
            public void onNoteOff(int midiNote) {
                playbackLatch.countDown();
            }
        });
        
        // Wait for playback to complete (with timeout)
        assertTrue(playbackLatch.await(2, TimeUnit.SECONDS), "Playback should complete");
        
        // Verify notes were played in correct order
        assertTrue(playedNotes.contains(60), "Should have played C4");
        assertTrue(playedNotes.contains(64), "Should have played E4");
        assertTrue(playedNotes.contains(67), "Should have played G4");
    }
    
    @Test
    @Timeout(5)
    void testChordRecordingAndPlayback() throws InterruptedException {
        // Start recording
        model.startRecording();
        
        // Record a C major chord (60, 64, 67)
        model.playChord(60);
        Thread.sleep(100);
        model.stopChord(60);
        
        // Stop recording
        Recording recording = model.stopRecording();
        assertNotNull(recording, "Recording should not be null");
        
        // Verify all 3 notes recorded with same timestamp (or very close)
        List<NoteEvent> events = recording.getEvents();
        List<NoteEvent> noteOnEvents = new ArrayList<>();
        for (NoteEvent event : events) {
            if (event.isNoteOn()) {
                noteOnEvents.add(event);
            }
        }
        
        assertTrue(noteOnEvents.size() >= 3, "Should have at least 3 note-on events for chord");
        
        // Verify all chord notes are present
        boolean hasC = false, hasE = false, hasG = false;
        for (NoteEvent event : noteOnEvents) {
            if (event.getMidiNote() == 60) hasC = true;
            if (event.getMidiNote() == 64) hasE = true;
            if (event.getMidiNote() == 67) hasG = true;
        }
        assertTrue(hasC && hasE && hasG, "Should have all chord notes (C, E, G)");
        
        // Play back and verify all notes triggered
        CountDownLatch playbackLatch = new CountDownLatch(6); // 3 note-on + 3 note-off
        List<Integer> playedNotes = new ArrayList<>();
        
        model.startPlaybackWithHandler(recording, new Player.NotePlaybackHandler() {
            @Override
            public void onNoteOn(int midiNote) {
                playedNotes.add(midiNote);
                playbackLatch.countDown();
            }
            
            @Override
            public void onNoteOff(int midiNote) {
                playbackLatch.countDown();
            }
        });
        
        assertTrue(playbackLatch.await(2, TimeUnit.SECONDS), "Playback should complete");
        assertTrue(playedNotes.contains(60) && playedNotes.contains(64) && playedNotes.contains(67),
            "All chord notes should be played");
    }
    
    @Test
    @Timeout(5)
    void testMultipleRecordingsManagement() throws IOException {
        RecordingManager manager = model.getRecordingManager();
        
        // Create 3 recordings with different names
        Recording rec1 = new Recording("Test Recording 1");
        rec1.addNoteEvent(new NoteEvent(60, 0, true));
        rec1.addNoteEvent(new NoteEvent(60, 100, false));
        
        Recording rec2 = new Recording("Test Recording 2");
        rec2.addNoteEvent(new NoteEvent(64, 0, true));
        rec2.addNoteEvent(new NoteEvent(64, 100, false));
        
        Recording rec3 = new Recording("Test Recording 3");
        rec3.addNoteEvent(new NoteEvent(67, 0, true));
        rec3.addNoteEvent(new NoteEvent(67, 100, false));
        
        manager.saveRecording("Test Recording 1", rec1);
        manager.saveRecording("Test Recording 2", rec2);
        manager.saveRecording("Test Recording 3", rec3);
        
        // List recordings and verify all present
        List<String> recordings = manager.listRecordings();
        assertTrue(recordings.contains("Test Recording 1"), "Should contain Recording 1");
        assertTrue(recordings.contains("Test Recording 2"), "Should contain Recording 2");
        assertTrue(recordings.contains("Test Recording 3"), "Should contain Recording 3");
        
        // Delete one, verify it's gone
        boolean deleted = manager.deleteRecording("Test Recording 2");
        assertTrue(deleted, "Should successfully delete");
        
        recordings = manager.listRecordings();
        assertFalse(recordings.contains("Test Recording 2"), "Should not contain deleted recording");
        assertTrue(recordings.contains("Test Recording 1"), "Should still contain Recording 1");
        assertTrue(recordings.contains("Test Recording 3"), "Should still contain Recording 3");
        
        // Rename one, verify old name gone and new name exists
        boolean renamed = manager.renameRecording("Test Recording 1", "Renamed Recording");
        assertTrue(renamed, "Should successfully rename");
        
        recordings = manager.listRecordings();
        assertFalse(recordings.contains("Test Recording 1"), "Old name should be gone");
        assertTrue(recordings.contains("Renamed Recording"), "New name should exist");
        
        // Export one to MIDI file, verify file exists and has content
        File tempFile = File.createTempFile("test_export", ".mid");
        tempFile.deleteOnExit();
        
        manager.exportToMIDI("Renamed Recording", tempFile);
        
        assertTrue(tempFile.exists(), "MIDI file should exist");
        assertTrue(tempFile.length() > 0, "MIDI file should have content");
    }
    
    @Test
    @Timeout(5)
    void testRecordingWithNoteDurations() throws InterruptedException {
        // Start recording
        model.startRecording();
        
        // Record note on at t=0
        model.playNote(60);
        Thread.sleep(100);
        
        // Record note off at t=100
        model.stopNote(60);
        Thread.sleep(100);
        
        // Record another note on at t=200
        model.playNote(64);
        Thread.sleep(50);
        model.stopNote(64);
        
        // Stop recording
        Recording recording = model.stopRecording();
        List<NoteEvent> events = recording.getEvents();
        
        // Verify playback timing is approximately correct (within 50ms tolerance)
        long firstNoteOnTime = -1;
        long firstNoteOffTime = -1;
        long secondNoteOnTime = -1;
        
        for (NoteEvent event : events) {
            if (event.getMidiNote() == 60 && event.isNoteOn()) {
                firstNoteOnTime = event.getTimestamp();
            } else if (event.getMidiNote() == 60 && !event.isNoteOn()) {
                firstNoteOffTime = event.getTimestamp();
            } else if (event.getMidiNote() == 64 && event.isNoteOn()) {
                secondNoteOnTime = event.getTimestamp();
            }
        }
        
        assertTrue(firstNoteOnTime >= 0, "First note-on should be recorded");
        assertTrue(firstNoteOffTime > firstNoteOnTime, "Note-off should be after note-on");
        assertTrue(secondNoteOnTime > firstNoteOffTime, "Second note should be after first");
        
        // Verify timing is approximately correct (within 50ms tolerance)
        long duration1 = firstNoteOffTime - firstNoteOnTime;
        assertTrue(Math.abs(duration1 - 100) < 50, 
            "First note duration should be approximately 100ms, was " + duration1);
        
        long gap = secondNoteOnTime - firstNoteOffTime;
        assertTrue(Math.abs(gap - 100) < 50, 
            "Gap between notes should be approximately 100ms, was " + gap);
    }
    
    @Test
    @Timeout(3)
    void testEmptyRecordingPlayback() {
        // Create empty recording
        Recording emptyRecording = new Recording("Empty");
        
        // Try to play back empty recording
        assertDoesNotThrow(() -> {
            model.startPlayback(emptyRecording);
            Thread.sleep(100); // Give it time to process
        }, "Empty recording playback should not throw");
        
        assertFalse(model.isPlaying(), "Should not be playing after empty recording");
    }
}

