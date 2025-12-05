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
import static org.mockito.ArgumentMatchers.any;

/**
 * Unit tests for PlaybackController.
 * Tests playback start/stop functionality.
 * Updated for RecordingManager architecture.
 */
class PlaybackControllerTest {
    @Mock
    private PianoModel model;
    
    @Mock
    private MainWindow view;
    
    @Mock
    private PianoView pianoView;
    
    @Mock
    private view.ControlPanelView controlPanel;
    
    @Mock
    private javax.swing.JButton playButton;
    
    @Mock
    private view.PianoKeyboardPanel keyboardPanel;
    
    @Mock
    private RecordingPanel recordingPanel;
    
    @Mock
    private RecordingManager recordingManager;
    
    @Mock
    private Recording recording;
    
    private PlaybackController controller;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(view.getPianoView()).thenReturn(pianoView);
        when(pianoView.getControlPanel()).thenReturn(controlPanel);
        when(pianoView.getKeyboardPanel()).thenReturn(keyboardPanel);
        when(pianoView.getRecordingPanel()).thenReturn(recordingPanel);
        when(controlPanel.getPlayButton()).thenReturn(playButton);
        when(model.isPlaying()).thenReturn(false);
        when(model.isRecording()).thenReturn(false);
        when(model.getRecordingManager()).thenReturn(recordingManager);
        when(recordingManager.listRecordings()).thenReturn(Arrays.asList("Recording 1"));
        when(recordingManager.getRecording("Recording 1")).thenReturn(recording);
        when(recordingPanel.getSelectedRecording()).thenReturn("Recording 1");
        
        controller = new PlaybackController(model, view);
    }
    
    @Test
    void testKeyPressedEnterStartsPlayback() {
        when(model.isRecording()).thenReturn(false);
        when(model.isPlaying()).thenReturn(false);
        when(recordingManager.listRecordings()).thenReturn(Arrays.asList("Recording 1"));
        when(recordingManager.getRecording("Recording 1")).thenReturn(recording);
        
        KeyEvent keyEvent = new KeyEvent(
            mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_ENTER,
            '\n'
        );
        
        controller.keyPressed(keyEvent);
        
        // ✅ 修复：源码调用的是 startPlaybackWithHandler，不是 startPlayback
        verify(model, atLeastOnce()).startPlaybackWithHandler(any(), any());
    }
    
    @Test
    void testKeyPressedEnterStopsPlayback() {
        when(model.isRecording()).thenReturn(false);
        when(model.isPlaying()).thenReturn(true);
        
        KeyEvent keyEvent = new KeyEvent(
            mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_ENTER,
            '\n'
        );
        
        controller.keyPressed(keyEvent);
        
        verify(model, times(1)).stopPlayback();
    }
    
    @Test
    void testButtonActionStartsPlayback() {
        when(model.isPlaying()).thenReturn(false);
        when(recordingPanel.getSelectedRecording()).thenReturn("Recording 1");
        when(recordingManager.getRecording("Recording 1")).thenReturn(recording);
        
        ActionEvent actionEvent = new ActionEvent(playButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(actionEvent);
        
        verify(model, atLeastOnce()).startPlaybackWithHandler(any(), any());
    }
    
    @Test
    void testButtonActionWithNoSelection() {
        when(model.isPlaying()).thenReturn(false);
        when(recordingPanel.getSelectedRecording()).thenReturn(null);
        
        ActionEvent actionEvent = new ActionEvent(playButton, ActionEvent.ACTION_PERFORMED, "");
        controller.actionPerformed(actionEvent);
        
        // Should not start playback
        verify(model, never()).startPlayback(anyString());
        verify(model, never()).startPlaybackWithHandler(any(), any());
    }
}