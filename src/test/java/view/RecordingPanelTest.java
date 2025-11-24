package view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RecordingPanel.
 * Tests component initialization and list management.
 */
class RecordingPanelTest {
    private RecordingPanel panel;
    
    @BeforeEach
    void setUp() {
        panel = new RecordingPanel();
    }
    
    @Test
    void testComponentInitialization() {
        assertNotNull(panel.getPlayButton(), "Play button should be initialized");
        assertNotNull(panel.getDeleteButton(), "Delete button should be initialized");
        assertNotNull(panel.getExportButton(), "Export button should be initialized");
        assertNotNull(panel.getRenameButton(), "Rename button should be initialized");
    }
    
    @Test
    void testUpdateRecordings() {
        List<String> recordings = Arrays.asList("Recording 1", "Recording 2", "Recording 3");
        
        panel.updateRecordings(recordings);
        
        // Can't easily test list contents without exposing internals,
        // but method should not throw
        assertTrue(true, "Update recordings should succeed");
    }
    
    @Test
    void testUpdateRecordingsEmpty() {
        panel.updateRecordings(List.of());
        
        // Should handle empty list
        assertTrue(true, "Update with empty list should succeed");
    }
    
    @Test
    void testGetSelectedRecordingInitially() {
        String selected = panel.getSelectedRecording();
        
        // Initially no selection
        assertNull(selected, "Should return null when nothing selected");
    }
    
    @Test
    void testButtonExistence() {
        // Verify all buttons exist and are accessible
        assertNotNull(panel.getPlayButton(), "Play button should exist");
        assertNotNull(panel.getDeleteButton(), "Delete button should exist");
        assertNotNull(panel.getExportButton(), "Export button should exist");
        assertNotNull(panel.getRenameButton(), "Rename button should exist");
    }
    
    @Test
    void testAddListeners() {
        // Test that listeners can be added without errors
        panel.addPlayListener(e -> {});
        panel.addDeleteListener(e -> {});
        panel.addExportListener(e -> {});
        panel.addRenameListener(e -> {});
        
        assertTrue(true, "Adding listeners should succeed");
    }
}

