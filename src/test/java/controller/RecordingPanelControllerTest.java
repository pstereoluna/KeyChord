package controller;

import model.PianoModel;
import model.RecordingManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import view.PianoView;
import view.RecordingPanel;
import view.ControlPanelView;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;

import static org.mockito.Mockito.*;

/**
 * Unit tests for RecordingPanelController.
 * Tests play, delete, export, and rename operations with mocked DialogService.
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
    private DialogService dialogService;
    
    private RecordingPanelController controller;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(view.getRecordingPanel()).thenReturn(recordingPanel);
        when(view.getControlPanel()).thenReturn(controlPanel);
        when(model.getRecordingManager()).thenReturn(recordingManager);
        when(recordingManager.listRecordings()).thenReturn(Arrays.asList("Recording 1"));
        
        controller = new RecordingPanelController(model, view, dialogService);
    }
    
    @Test
    void testPlayRecording() {
        when(recordingPanel.getSelectedRecording()).thenReturn("Recording 1");
        when(model.isPlaying()).thenReturn(false).thenReturn(true).thenReturn(false);
        
        ActionEvent event = new ActionEvent(recordingPanel.getPlayButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        controller.actionPerformed(event);
        
        verify(model, times(1)).startPlayback("Recording 1");
        verify(controlPanel, times(1)).setStatus("Playing: Recording 1");
        verify(controlPanel, times(1)).setPlayingState(true);
    }
    
    @Test
    void testPlayRecordingNoSelection() {
        when(recordingPanel.getSelectedRecording()).thenReturn(null);
        
        ActionEvent event = new ActionEvent(recordingPanel.getPlayButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        controller.actionPerformed(event);
        
        // Should show message and not start playback
        verify(dialogService, times(1)).showMessage(eq(view), 
            eq("Please select a recording first."), eq("No Selection"));
        verify(model, never()).startPlayback(anyString());
    }
    
    @Test
    void testDeleteRecording_UserConfirms() {
        when(recordingPanel.getSelectedRecording()).thenReturn("Recording 1");
        when(dialogService.confirmDelete(view, "Recording 1")).thenReturn(true);
        when(recordingManager.deleteRecording("Recording 1")).thenReturn(true);
        when(recordingManager.listRecordings()).thenReturn(Arrays.asList());
        
        ActionEvent event = new ActionEvent(recordingPanel.getDeleteButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        controller.actionPerformed(event);
        
        verify(dialogService, times(1)).confirmDelete(view, "Recording 1");
        verify(recordingManager, times(1)).deleteRecording("Recording 1");
        verify(controlPanel, times(1)).setStatus("Deleted: Recording 1");
        verify(recordingPanel, times(1)).updateRecordings(anyList());
    }
    
    @Test
    void testDeleteRecording_UserCancels() {
        when(recordingPanel.getSelectedRecording()).thenReturn("Recording 1");
        when(dialogService.confirmDelete(view, "Recording 1")).thenReturn(false);
        
        ActionEvent event = new ActionEvent(recordingPanel.getDeleteButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        controller.actionPerformed(event);
        
        verify(dialogService, times(1)).confirmDelete(view, "Recording 1");
        verify(recordingManager, never()).deleteRecording(anyString());
        verify(controlPanel, never()).setStatus(anyString());
    }
    
    @Test
    void testExportRecording_UserSelectsFile() throws java.io.IOException {
        when(recordingPanel.getSelectedRecording()).thenReturn("Recording 1");
        File exportFile = new File("test.mid");
        when(dialogService.chooseExportFile(view, "Recording 1")).thenReturn(exportFile);
        
        ActionEvent event = new ActionEvent(recordingPanel.getExportButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        controller.actionPerformed(event);
        
        verify(dialogService, times(1)).chooseExportFile(view, "Recording 1");
        verify(recordingManager, times(1)).exportToMIDI("Recording 1", exportFile);
        verify(controlPanel, times(1)).setStatus(contains("Exported:"));
        verify(dialogService, times(1)).showMessage(eq(view), contains("exported successfully"), eq("Export Success"));
    }
    
    @Test
    void testExportRecording_UserCancels() {
        when(recordingPanel.getSelectedRecording()).thenReturn("Recording 1");
        when(dialogService.chooseExportFile(view, "Recording 1")).thenReturn(null);
        
        ActionEvent event = new ActionEvent(recordingPanel.getExportButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        controller.actionPerformed(event);
        
        verify(dialogService, times(1)).chooseExportFile(view, "Recording 1");
        try {
            verify(recordingManager, never()).exportToMIDI(anyString(), any(File.class));
        } catch (java.io.IOException e) {
            // Should not happen since method is never called
            fail("exportToMIDI should not be called when user cancels");
        }
    }
    
    @Test
    void testExportRecording_ExportFails() throws java.io.IOException {
        when(recordingPanel.getSelectedRecording()).thenReturn("Recording 1");
        File exportFile = new File("test.mid");
        when(dialogService.chooseExportFile(view, "Recording 1")).thenReturn(exportFile);
        doThrow(new java.io.IOException("Export failed")).when(recordingManager).exportToMIDI("Recording 1", exportFile);
        
        ActionEvent event = new ActionEvent(recordingPanel.getExportButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        controller.actionPerformed(event);
        
        verify(recordingManager, times(1)).exportToMIDI("Recording 1", exportFile);
        verify(dialogService, times(1)).showError(eq(view), contains("Failed to export"), eq("Export Error"));
    }
    
    @Test
    void testRenameRecording_UserProvidesNewName() {
        when(recordingPanel.getSelectedRecording()).thenReturn("Old Name");
        when(dialogService.promptRename(view, "Old Name")).thenReturn("New Name");
        when(recordingManager.renameRecording("Old Name", "New Name")).thenReturn(true);
        when(recordingManager.listRecordings()).thenReturn(Arrays.asList("New Name"));
        
        ActionEvent event = new ActionEvent(recordingPanel.getRenameButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        controller.actionPerformed(event);
        
        verify(dialogService, times(1)).promptRename(view, "Old Name");
        verify(recordingManager, times(1)).renameRecording("Old Name", "New Name");
        verify(controlPanel, times(1)).setStatus("Renamed to: New Name");
        verify(recordingPanel, times(1)).updateRecordings(anyList());
    }
    
    @Test
    void testRenameRecording_UserCancels() {
        when(recordingPanel.getSelectedRecording()).thenReturn("Old Name");
        when(dialogService.promptRename(view, "Old Name")).thenReturn(null);
        
        ActionEvent event = new ActionEvent(recordingPanel.getRenameButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        controller.actionPerformed(event);
        
        verify(dialogService, times(1)).promptRename(view, "Old Name");
        verify(recordingManager, never()).renameRecording(anyString(), anyString());
    }
    
    @Test
    void testRenameRecording_UserProvidesEmptyName() {
        when(recordingPanel.getSelectedRecording()).thenReturn("Old Name");
        when(dialogService.promptRename(view, "Old Name")).thenReturn("   "); // Whitespace only
        
        ActionEvent event = new ActionEvent(recordingPanel.getRenameButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        controller.actionPerformed(event);
        
        verify(dialogService, times(1)).promptRename(view, "Old Name");
        verify(recordingManager, never()).renameRecording(anyString(), anyString());
    }
    
    @Test
    void testRenameRecording_UserProvidesSameName() {
        when(recordingPanel.getSelectedRecording()).thenReturn("Old Name");
        when(dialogService.promptRename(view, "Old Name")).thenReturn("Old Name");
        
        ActionEvent event = new ActionEvent(recordingPanel.getRenameButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        controller.actionPerformed(event);
        
        verify(dialogService, times(1)).promptRename(view, "Old Name");
        verify(recordingManager, never()).renameRecording(anyString(), anyString());
    }
    
    @Test
    void testRenameRecording_RenameFails() {
        when(recordingPanel.getSelectedRecording()).thenReturn("Old Name");
        when(dialogService.promptRename(view, "Old Name")).thenReturn("New Name");
        when(recordingManager.renameRecording("Old Name", "New Name"))
            .thenThrow(new IllegalArgumentException("Name already exists"));
        
        ActionEvent event = new ActionEvent(recordingPanel.getRenameButton(), 
            ActionEvent.ACTION_PERFORMED, "");
        
        controller.actionPerformed(event);
        
        verify(recordingManager, times(1)).renameRecording("Old Name", "New Name");
        verify(dialogService, times(1)).showError(eq(view), contains("Cannot rename"), eq("Rename Error"));
    }
    
    @Test
    void testUpdateRecordingList() {
        when(recordingManager.listRecordings()).thenReturn(Arrays.asList("Recording 1", "Recording 2"));
        
        controller.updateRecordingList();
        
        verify(recordingPanel, times(1)).updateRecordings(anyList());
    }
}
