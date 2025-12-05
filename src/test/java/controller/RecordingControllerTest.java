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
    private view.PianoKeyboardPanel keyboardPanel;
    
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
        
        // 设置 view 层级
        when(view.getPianoView()).thenReturn(pianoView);
        when(pianoView.getControlPanel()).thenReturn(controlPanel);
        when(pianoView.getRecordingPanel()).thenReturn(recordingPanel);
        when(pianoView.getKeyboardPanel()).thenReturn(keyboardPanel);
        when(controlPanel.getRecordButton()).thenReturn(recordButton);
        
        // 设置 model - 使用 lenient 避免冲突
        lenient().when(model.isRecording()).thenReturn(false);
        lenient().when(model.getRecordingManager()).thenReturn(recordingManager);
        lenient().when(recordingManager.listRecordings()).thenReturn(Arrays.asList());
        
        controller = new RecordingController(model, view);
    }
    
    @Test
    void testKeyPressedSpaceStartsRecording() {
        // 重置 mock 以确保干净状态
        reset(model);
        when(model.isRecording()).thenReturn(false);
        when(model.getRecordingManager()).thenReturn(recordingManager);
        
        KeyEvent keyEvent = new KeyEvent(
            mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_SPACE,
            ' '
        );
        
        controller.keyPressed(keyEvent);
        
        verify(model, times(1)).startRecording();
    }
    
    @Test
    void testKeyPressedEnterStopsRecording() {
        // 重置 mock 以确保干净状态
        reset(model);
        when(model.isRecording()).thenReturn(true);
        when(model.getRecordingManager()).thenReturn(recordingManager);
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
        reset(model);
        when(model.isRecording()).thenReturn(false);
        when(model.getRecordingManager()).thenReturn(recordingManager);
        
        ActionEvent actionEvent = new ActionEvent(recordButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(actionEvent);
        
        verify(model, times(1)).startRecording();
    }
    
    @Test
    void testButtonActionStopsRecording() {
        reset(model);
        when(model.isRecording()).thenReturn(true);
        when(model.getRecordingManager()).thenReturn(recordingManager);
        when(model.stopRecording()).thenReturn(recording);
        when(recording.getName()).thenReturn("Recording 1");
        when(recordingManager.listRecordings()).thenReturn(Arrays.asList("Recording 1"));
        
        ActionEvent actionEvent = new ActionEvent(recordButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(actionEvent);
        
        verify(model, times(1)).stopRecording();
    }
    
    @Test
    void testStopRecordingUpdatesPanel() {
        reset(model);
        when(model.isRecording()).thenReturn(true);
        when(model.getRecordingManager()).thenReturn(recordingManager);
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
        
        // 异步调用，使用 timeout
        verify(recordingPanel, timeout(500).atLeastOnce()).updateRecordings(anyList());
    }
}