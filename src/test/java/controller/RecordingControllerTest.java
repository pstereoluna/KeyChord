package controller;

import model.PianoModel;
import model.Recording;
import model.RecordingManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import view.MainWindow;
import view.PianoView;
import view.RecordingPanel;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Component;
import java.util.Arrays;

import static org.mockito.Mockito.*;

/**
 * Unit tests for RecordingController.
 * Tests recording start/stop functionality.
 * Updated for RecordingManager architecture.
 */
class RecordingControllerTest {
    @Mock
    private PianoModel model;
    
    @Mock
    private MainWindow view;
    
    @Mock
    private PianoView pianoView;
    
    @Mock
    private view.ControlPanelView controlPanel;
    
    @Mock
    private javax.swing.JButton recordButton;
    
    @Mock
    private RecordingPanel recordingPanel;
    
    @Mock
    private RecordingManager recordingManager;
    
    @Mock
    private Recording recording;
    
    private RecordingController controller;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(view.getPianoView()).thenReturn(pianoView);
        when(pianoView.getControlPanel()).thenReturn(controlPanel);
        when(pianoView.getRecordingPanel()).thenReturn(recordingPanel);
        when(controlPanel.getRecordButton()).thenReturn(recordButton);
        when(model.isRecording()).thenReturn(false);
        when(model.getRecordingManager()).thenReturn(recordingManager);
        when(recordingManager.listRecordings()).thenReturn(Arrays.asList());
        
        controller = new RecordingController(model, view);
    }
    
    @Test
    void testKeyPressedSpaceStartsRecording() {
        KeyEvent keyEvent = new KeyEvent(
            mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_SPACE,
            ' '
        );
        
        when(model.isRecording()).thenReturn(false);
        
        controller.keyPressed(keyEvent);
        
        verify(model, times(1)).startRecording();
    }
    
    @Test
    void testKeyPressedEnterStopsRecording() {
        when(model.isRecording()).thenReturn(true);
        when(model.stopRecording()).thenReturn(recording);
        when(recording.getName()).thenReturn("Recording 1");
        when(recordingManager.listRecordings()).thenReturn(Arrays.asList("Recording 1"));
        
        KeyEvent keyEvent = new KeyEvent(
            mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_ENTER,
            '\n'
        );
        
        controller.keyPressed(keyEvent);
        
        verify(model, times(1)).stopRecording();
    }
    
    @Test
    void testButtonActionStartsRecording() {
        when(model.isRecording()).thenReturn(false);
        
        ActionEvent actionEvent = new ActionEvent(recordButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(actionEvent);
        
        verify(model, times(1)).startRecording();
    }
    
    @Test
    void testButtonActionStopsRecording() {
        when(model.isRecording()).thenReturn(true);
        when(model.stopRecording()).thenReturn(recording);
        when(recording.getName()).thenReturn("Recording 1");
        when(recordingManager.listRecordings()).thenReturn(Arrays.asList("Recording 1"));
        
        ActionEvent actionEvent = new ActionEvent(recordButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(actionEvent);
        
        verify(model, times(1)).stopRecording();
    }
    
    @Test
    void testStopRecordingUpdatesPanel() {
        when(model.isRecording()).thenReturn(true);
        when(model.stopRecording()).thenReturn(recording);
        when(recording.getName()).thenReturn("Recording 1");
        when(recordingManager.listRecordings()).thenReturn(Arrays.asList("Recording 1"));
        
        KeyEvent keyEvent = new KeyEvent(
            mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_ENTER,
            '\n'
        );
        
        controller.keyPressed(keyEvent);
        
        // Verify that recording panel was updated
        verify(recordingPanel, atLeastOnce()).updateRecordings(anyList());
    }
}
