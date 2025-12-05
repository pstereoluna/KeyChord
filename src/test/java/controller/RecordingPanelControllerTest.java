package controller;

import model.PianoModel;
import model.RecordingManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import view.PianoView;
import view.RecordingPanel;
import view.ControlPanelView;

import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class RecordingPanelControllerTest {
    @Mock private PianoModel model;
    @Mock private PianoView view;
    @Mock private RecordingPanel recordingPanel;
    @Mock private ControlPanelView controlPanel;
    @Mock private RecordingManager recordingManager;
    @Mock private DialogService dialogService;
    @Mock private JButton playButton;
    @Mock private JButton deleteButton;
    @Mock private JButton exportButton;
    @Mock private JButton renameButton;
    
    private RecordingPanelController controller;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        doReturn(recordingPanel).when(view).getRecordingPanel();
        doReturn(controlPanel).when(view).getControlPanel();
        doReturn(playButton).when(recordingPanel).getPlayButton();
        doReturn(deleteButton).when(recordingPanel).getDeleteButton();
        doReturn(exportButton).when(recordingPanel).getExportButton();
        doReturn(renameButton).when(recordingPanel).getRenameButton();
        doReturn("Recording 1").when(recordingPanel).getSelectedRecording();
        doReturn(recordingManager).when(model).getRecordingManager();
        doReturn(Collections.singletonList("Recording 1")).when(recordingManager).listRecordings();
        
        controller = new RecordingPanelController(model, view, dialogService);
    }
    
    @Test
    void testPlayRecording() {
        doReturn(false).when(model).isPlaying();
        ActionEvent event = new ActionEvent(playButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(event);
        verify(model).startPlayback("Recording 1");
    }
    
    @Test
    void testPlayRecordingNoSelection() {
        doReturn(null).when(recordingPanel).getSelectedRecording();
        ActionEvent event = new ActionEvent(playButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(event);
        verify(dialogService).showMessage(any(), eq("Please select a recording first."), eq("No Selection"));
        verify(model, never()).startPlayback(anyString());
    }
    
    @Test
    void testDeleteRecording_UserConfirms() {
        doReturn(true).when(dialogService).confirmDelete(any(), eq("Recording 1"));
        doReturn(true).when(recordingManager).deleteRecording("Recording 1");
        ActionEvent event = new ActionEvent(deleteButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(event);
        verify(recordingManager).deleteRecording("Recording 1");
    }
    
    @Test
    void testDeleteRecording_UserCancels() {
        doReturn(false).when(dialogService).confirmDelete(any(), eq("Recording 1"));
        ActionEvent event = new ActionEvent(deleteButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(event);
        verify(recordingManager, never()).deleteRecording(anyString());
    }
    
    @Test
    void testExportRecording_UserSelectsFile() throws Exception {
        File exportFile = new File("test.mid");
        doReturn(exportFile).when(dialogService).chooseExportFile(any(), eq("Recording 1"));
        ActionEvent event = new ActionEvent(exportButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(event);
        verify(recordingManager).exportToMIDI("Recording 1", exportFile);
    }
    
    @Test
    void testExportRecording_UserCancels() throws Exception {
        doReturn(null).when(dialogService).chooseExportFile(any(), anyString());
        ActionEvent event = new ActionEvent(exportButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(event);
        verify(recordingManager, never()).exportToMIDI(anyString(), any(File.class));
    }
    
    @Test
    void testExportRecording_ExportFails() throws Exception {
        File exportFile = new File("test.mid");
        doReturn(exportFile).when(dialogService).chooseExportFile(any(), eq("Recording 1"));
        doThrow(new java.io.IOException("fail")).when(recordingManager).exportToMIDI(eq("Recording 1"), any(File.class));
        ActionEvent event = new ActionEvent(exportButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(event);
        verify(dialogService, timeout(500)).showError(any(), contains("Failed to export"), eq("Export Error"));
    }
    
    @Test
    void testRenameRecording_UserProvidesNewName() {
        // ✅ 用 any() 匹配 view 参数，用 Recording 1 保持一致
        doReturn("New Name").when(dialogService).promptRename(any(), eq("Recording 1"));
        doReturn(true).when(recordingManager).renameRecording("Recording 1", "New Name");
        
        ActionEvent event = new ActionEvent(renameButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(event);
        
        verify(recordingManager).renameRecording("Recording 1", "New Name");
    }
    
    @Test
    void testRenameRecording_UserCancels() {
        doReturn(null).when(dialogService).promptRename(any(), eq("Recording 1"));
        ActionEvent event = new ActionEvent(renameButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(event);
        verify(recordingManager, never()).renameRecording(anyString(), anyString());
    }
    
    @Test
    void testRenameRecording_UserProvidesEmptyName() {
        doReturn("   ").when(dialogService).promptRename(any(), eq("Recording 1"));
        ActionEvent event = new ActionEvent(renameButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(event);
        verify(recordingManager, never()).renameRecording(anyString(), anyString());
    }
    
    @Test
    void testRenameRecording_UserProvidesSameName() {
        // 用户输入相同的名字 "Recording 1"
        doReturn("Recording 1").when(dialogService).promptRename(any(), eq("Recording 1"));
        ActionEvent event = new ActionEvent(renameButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(event);
        verify(recordingManager, never()).renameRecording(anyString(), anyString());
    }
    
    @Test
    void testRenameRecording_RenameFails() {
        doReturn("New Name").when(dialogService).promptRename(any(), eq("Recording 1"));
        doThrow(new IllegalArgumentException("exists")).when(recordingManager).renameRecording("Recording 1", "New Name");
        ActionEvent event = new ActionEvent(renameButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(event);
        verify(dialogService, timeout(500)).showError(any(), contains("Cannot rename"), eq("Rename Error"));
    }
    
    @Test
    void testUpdateRecordingList() {
        controller.updateRecordingList();
        verify(recordingPanel, timeout(500).atLeastOnce()).updateRecordings(anyList());
    }
}