package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RecordingManager.
 * Tests recording management, save/delete/export functionality.
 */
class RecordingManagerTest {
    private RecordingManager manager;
    
    @BeforeEach
    void setUp() {
        manager = new RecordingManager();
    }
    
    @Test
    void testStartRecording() {
        Recording recording = manager.startRecording();
        
        assertNotNull(recording, "Should return a Recording");
        assertNotNull(recording.getName(), "Recording should have a name");
        assertTrue(recording.getName().startsWith("Recording "), "Should have default name pattern");
    }
    
    @Test
    void testStopRecordingAutoSave() {
        manager.startRecording();
        Recording saved = manager.stopRecording();
        
        assertNotNull(saved, "Should return saved Recording");
        
        // Recording should be saved with default name
        List<String> recordings = manager.listRecordings();
        assertFalse(recordings.isEmpty(), "Should have saved recording");
    }
    
    @Test
    void testStopRecordingWithName() {
        manager.startRecording();
        Recording saved = manager.stopRecording("My Recording");
        
        assertNotNull(saved, "Should return saved Recording");
        assertEquals("My Recording", saved.getName(), "Should save with specified name");
        
        Recording retrieved = manager.getRecording("My Recording");
        assertNotNull(retrieved, "Should be able to retrieve saved recording");
        assertEquals(saved.getName(), retrieved.getName(), "Retrieved should match saved");
    }
    
    @Test
    void testGetCurrentRecording() {
        assertNull(manager.getCurrentRecording(), "Should return null when not recording");
        
        Recording current = manager.startRecording();
        assertSame(current, manager.getCurrentRecording(), "Should return current recording");
        
        manager.stopRecording();
        assertNull(manager.getCurrentRecording(), "Should return null after stop");
    }
    
    @Test
    void testSaveRecording() {
        Recording recording = new Recording("Test Recording");
        recording.addNoteEvent(new NoteEvent(60, 0, true));
        
        manager.saveRecording("Test Recording", recording);
        
        Recording retrieved = manager.getRecording("Test Recording");
        assertNotNull(retrieved, "Should be able to retrieve saved recording");
        assertEquals(1, retrieved.getEventCount(), "Should preserve events");
    }
    
    @Test
    void testSaveRecordingWithNullName() {
        Recording recording = new Recording("Test");
        
        assertThrows(IllegalArgumentException.class, () -> {
            manager.saveRecording(null, recording);
        }, "Should throw exception for null name");
    }
    
    @Test
    void testSaveRecordingWithNullRecording() {
        assertThrows(IllegalArgumentException.class, () -> {
            manager.saveRecording("Test", null);
        }, "Should throw exception for null recording");
    }
    
    @Test
    void testDeleteRecording() {
        manager.startRecording();
        manager.stopRecording("To Delete");
        
        assertNotNull(manager.getRecording("To Delete"), "Recording should exist before delete");
        
        boolean deleted = manager.deleteRecording("To Delete");
        assertTrue(deleted, "Should return true when deleting existing recording");
        
        assertNull(manager.getRecording("To Delete"), "Recording should be deleted");
    }
    
    @Test
    void testDeleteNonexistentRecording() {
        boolean deleted = manager.deleteRecording("Nonexistent");
        assertFalse(deleted, "Should return false when deleting nonexistent recording");
    }
    
    @Test
    void testListRecordings() {
        manager.startRecording();
        manager.stopRecording("Recording 1");
        
        manager.startRecording();
        manager.stopRecording("Recording 2");
        
        List<String> recordings = manager.listRecordings();
        assertEquals(2, recordings.size(), "Should list all recordings");
        assertTrue(recordings.contains("Recording 1"));
        assertTrue(recordings.contains("Recording 2"));
    }
    
    @Test
    void testListRecordingsSorted() {
        manager.startRecording();
        manager.stopRecording("Z Recording");
        
        manager.startRecording();
        manager.stopRecording("A Recording");
        
        List<String> recordings = manager.listRecordings();
        assertEquals("A Recording", recordings.get(0), "Should be sorted alphabetically");
        assertEquals("Z Recording", recordings.get(1));
    }
    
    @Test
    void testRenameRecording() {
        manager.startRecording();
        manager.stopRecording("Old Name");
        
        boolean renamed = manager.renameRecording("Old Name", "New Name");
        assertTrue(renamed, "Should return true when renaming existing recording");
        
        assertNull(manager.getRecording("Old Name"), "Old name should not exist");
        assertNotNull(manager.getRecording("New Name"), "New name should exist");
    }
    
    @Test
    void testRenameNonexistentRecording() {
        boolean renamed = manager.renameRecording("Nonexistent", "New Name");
        assertFalse(renamed, "Should return false when renaming nonexistent recording");
    }
    
    @Test
    void testRenameToExistingName() {
        manager.startRecording();
        manager.stopRecording("Recording 1");
        
        manager.startRecording();
        manager.stopRecording("Recording 2");
        
        assertThrows(IllegalArgumentException.class, () -> {
            manager.renameRecording("Recording 1", "Recording 2");
        }, "Should throw exception when renaming to existing name");
    }
    
    @Test
    void testExportToMIDI() throws IOException {
        // Create a recording with some events
        Recording recording = new Recording("Test Export");
        recording.addNoteEvent(new NoteEvent(60, 0, true));
        recording.addNoteEvent(new NoteEvent(60, 500, false));
        recording.addNoteEvent(new NoteEvent(64, 1000, true));
        
        manager.saveRecording("Test Export", recording);
        
        // Create a temporary file for export
        File tempFile = File.createTempFile("test_export", ".mid");
        tempFile.deleteOnExit();
        
        // Should not throw exception
        assertDoesNotThrow(() -> {
            manager.exportToMIDI("Test Export", tempFile);
        }, "Export should succeed");
        
        assertTrue(tempFile.exists(), "MIDI file should be created");
        assertTrue(tempFile.length() > 0, "MIDI file should not be empty");
    }
    
    @Test
    void testExportNonexistentRecording() {
        File tempFile = new File("nonexistent.mid");
        
        assertThrows(IllegalArgumentException.class, () -> {
            manager.exportToMIDI("Nonexistent", tempFile);
        }, "Should throw exception for nonexistent recording");
    }
    
    @Test
    void testExportWithNullName() {
        File tempFile = new File("test.mid");
        
        assertThrows(IllegalArgumentException.class, () -> {
            manager.exportToMIDI(null, tempFile);
        }, "Should throw exception for null name");
    }
    
    @Test
    void testExportWithNullFile() {
        manager.startRecording();
        manager.stopRecording("Test");
        
        assertThrows(IllegalArgumentException.class, () -> {
            manager.exportToMIDI("Test", null);
        }, "Should throw exception for null file");
    }
    
    @Test
    void testRecordingCounterIncrements() {
        manager.startRecording();
        Recording saved1 = manager.stopRecording();
        
        manager.startRecording();
        Recording saved2 = manager.stopRecording();
        
        // Names should be different (counter increments)
        assertNotEquals(saved1.getName(), saved2.getName(), "Recording names should be different");
    }
    
    @Test
    @DisplayName("Save recording with special characters in name")
    void testSaveRecordingWithSpecialCharacters() {
        Recording recording = new Recording("Test");
        recording.addNoteEvent(new NoteEvent(60, 0, true));
        
        // Test various special characters
        String[] specialNames = {
            "Recording (1)",
            "Recording [2]",
            "Recording-3",
            "Recording_4",
            "Recording@5",
            "Recording#6"
        };
        
        for (String name : specialNames) {
            assertDoesNotThrow(() -> {
                manager.saveRecording(name, new Recording(name));
            }, "Should handle special characters in name: " + name);
            
            assertNotNull(manager.getRecording(name), "Should retrieve recording with special characters: " + name);
        }
    }
    
    @Test
    @DisplayName("Save recording with very long name")
    void testSaveRecordingWithLongName() {
        // Create a very long name (1000+ chars)
        StringBuilder longName = new StringBuilder("Recording ");
        for (int i = 0; i < 1000; i++) {
            longName.append("X");
        }
        
        Recording recording = new Recording(longName.toString());
        recording.addNoteEvent(new NoteEvent(60, 0, true));
        
        assertDoesNotThrow(() -> {
            manager.saveRecording(longName.toString(), recording);
        }, "Should handle very long name");
        
        assertNotNull(manager.getRecording(longName.toString()), "Should retrieve recording with long name");
    }
    
    @Test
    @DisplayName("Rename to same name should throw exception")
    void testRenameToSameName() {
        manager.startRecording();
        manager.stopRecording("Same Name");
        
        assertThrows(IllegalArgumentException.class, () -> {
            manager.renameRecording("Same Name", "Same Name");
        }, "Should throw exception when renaming to same name");
    }
    
    @Test
    @DisplayName("Export empty recording to MIDI")
    void testExportEmptyRecording() throws IOException {
        Recording emptyRecording = new Recording("Empty");
        manager.saveRecording("Empty", emptyRecording);
        
        File tempFile = File.createTempFile("test_empty", ".mid");
        tempFile.deleteOnExit();
        
        // Should not throw exception (empty MIDI file is valid)
        assertDoesNotThrow(() -> {
            manager.exportToMIDI("Empty", tempFile);
        }, "Exporting empty recording should not throw");
        
        assertTrue(tempFile.exists(), "MIDI file should be created even for empty recording");
    }
}

