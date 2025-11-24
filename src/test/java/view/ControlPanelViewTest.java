package view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ControlPanelView.
 * Tests component initialization and state updates.
 * Updated for RecordingManager architecture (removed track selection).
 */
class ControlPanelViewTest {
    private ControlPanelView controlPanel;
    
    @BeforeEach
    void setUp() {
        controlPanel = new ControlPanelView();
    }
    
    @Test
    void testComponentInitialization() {
        assertNotNull(controlPanel.getRecordButton(), "Record button should be initialized");
        assertNotNull(controlPanel.getPlayButton(), "Play button should be initialized");
        assertNotNull(controlPanel.getChordSelector(), "Chord selector should be initialized");
    }
    
    @Test
    void testSetStatus() {
        controlPanel.setStatus("Recording");
        // Method should not throw
        assertTrue(true, "Setting status should succeed");
    }
    
    @Test
    void testSetRecordingState() {
        controlPanel.setRecordingState(true);
        controlPanel.setRecordingState(false);
        // Method should not throw
        assertTrue(true, "Setting recording state should succeed");
    }
    
    @Test
    void testSetPlayingState() {
        controlPanel.setPlayingState(true);
        controlPanel.setPlayingState(false);
        // Method should not throw
        assertTrue(true, "Setting playing state should succeed");
    }
    
    @Test
    void testGetSelectedChordMode() {
        String mode = controlPanel.getSelectedChordMode();
        assertNotNull(mode, "Should return a chord mode");
        assertEquals("Single Note", mode, "Default should be Single Note");
    }
    
    @Test
    void testChordSelectorExists() {
        javax.swing.JComboBox<String> selector = controlPanel.getChordSelector();
        assertNotNull(selector, "Chord selector should exist");
        assertTrue(selector.getItemCount() > 0, "Chord selector should have items");
    }
}
