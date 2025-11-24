package controller;

import model.PianoModel;
import model.Recording;
import model.RecordingManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import view.PianoView;
import view.RecordingPanel;
import view.ControlPanelView;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import static org.mockito.Mockito.*;

/**
 * Unit tests for RecordingPanelController.
 * Tests play, delete, export, and rename operations.
 */
class RecordingPanelControllerTest {
    @Mock
    private PianoModel model;
    
    @Mock
    private PianoView view;
    
    @Mock
    private RecordingPanel recordingPanel;
    
    @Mock
    private ControlPanelView controlPanel;
    
    @Mock
    private RecordingManager recordingManager;
    
    @Mock
    private Recording recording;
    
    private RecordingPanelController controller;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(view.getRecordingPanel()).thenReturn(recordingPanel);
        when(view.getControlPanel()).thenReturn(controlPanel);
        when(model.getRecordingManager()).thenReturn(recordingManager);
        when(recordingManager.listRecordings()).thenReturn(Arrays.asList("Recording 1"));
        
        controller = new RecordingPanelController(model, view);
    }
    
    @Test
    void testPlayRecording() {
        when(recordingPanel.getSelectedRecording()).thenReturn("Recording 1");
        when(recordingManager.getRecording("Recording 1")).thenReturn(recording);
        when(model.isPlaying()).thenReturn(false).thenReturn(true).thenReturn(false);
        
        ActionEvent event = new ActionEvent(recordingPanel.getPlayButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        controller.actionPerformed(event);
        
        verify(model, times(1)).startPlayback("Recording 1");
    }
    
    @Test
    void testPlayRecordingNoSelection() {
        when(recordingPanel.getSelectedRecording()).thenReturn(null);
        
        ActionEvent event = new ActionEvent(recordingPanel.getPlayButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        controller.actionPerformed(event);
        
        // Should not start playback
        verify(model, never()).startPlayback(anyString());
    }
    
    @Test
    void testDeleteRecording() {
        when(recordingPanel.getSelectedRecording()).thenReturn("Recording 1");
        when(recordingManager.deleteRecording("Recording 1")).thenReturn(true);
        when(recordingManager.listRecordings()).thenReturn(Arrays.asList());
        
        ActionEvent event = new ActionEvent(recordingPanel.getDeleteButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        // Mock JOptionPane to return YES_OPTION
        // Note: This is a simplified test - in practice, you'd need to handle the dialog
        
        controller.actionPerformed(event);
        
        // Verify delete was called (if user confirmed)
        // Note: Actual implementation shows dialog, so this test is limited
    }
    
    @Test
    void testExportRecording() {
        when(recordingPanel.getSelectedRecording()).thenReturn("Recording 1");
        when(recordingManager.getRecording("Recording 1")).thenReturn(recording);
        
        ActionEvent event = new ActionEvent(recordingPanel.getExportButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        // Mock JFileChooser to return APPROVE_OPTION
        // Note: This is a simplified test - in practice, you'd need to handle the file chooser
        
        controller.actionPerformed(event);
        
        // Verify export logic (if file chooser approved)
        // Note: Actual implementation shows file chooser, so this test is limited
    }
    
    @Test
    void testRenameRecording() {
        when(recordingPanel.getSelectedRecording()).thenReturn("Old Name");
        when(recordingManager.renameRecording("Old Name", "New Name")).thenReturn(true);
        when(recordingManager.listRecordings()).thenReturn(Arrays.asList("New Name"));
        
        ActionEvent event = new ActionEvent(recordingPanel.getRenameButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        // Mock JOptionPane.showInputDialog to return "New Name"
        // Note: This is a simplified test - in practice, you'd need to handle the dialog
        
        controller.actionPerformed(event);
        
        // Verify rename logic (if user provided new name)
        // Note: Actual implementation shows input dialog, so this test is limited
    }
    
    @Test
    void testUpdateRecordingList() {
        when(recordingManager.listRecordings()).thenReturn(Arrays.asList("Recording 1", "Recording 2"));
        
        controller.updateRecordingList();
        
        verify(recordingPanel, times(1)).updateRecordings(anyList());
    }
}

