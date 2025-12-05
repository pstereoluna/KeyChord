package controller;

import model.PianoModel;
import model.ChordManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import view.MainWindow;
import view.PianoView;

import java.awt.event.KeyEvent;
import java.awt.Component;

import static org.mockito.Mockito.*;

/**
 * Unit tests for PianoController.
 * Tests keyboard event routing and chord generation.
 * Note: View highlighting is async (SwingUtilities.invokeLater), so we focus on Model calls.
 */
class PianoControllerTest {
    @Mock
    private PianoModel model;
    
    @Mock
    private MainWindow view;
    
    @Mock
    private PianoView pianoView;
    
    @Mock
    private view.PianoKeyboardPanel keyboardPanel;
    
    @Mock
    private view.ControlPanelView controlPanel;
    
    @Mock
    private javax.swing.JComboBox<String> chordSelector;
    
    private PianoController controller;
    private ChordManager chordManager;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(view.getPianoView()).thenReturn(pianoView);
        when(pianoView.getKeyboardPanel()).thenReturn(keyboardPanel);
        when(pianoView.getControlPanel()).thenReturn(controlPanel);
        when(controlPanel.getChordSelector()).thenReturn(chordSelector);
        when(controlPanel.getSelectedChordMode()).thenReturn("Single Note");
        
        chordManager = new ChordManager();
        when(model.getChordManager()).thenReturn(chordManager);
        
        controller = new PianoController(model, view);
    }
    
    @Test
    void testKeyPressedWithMappedKeySingleNote() {
        KeyEvent keyEvent = new KeyEvent(
            mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_C,
            'c'
        );
        
        when(controlPanel.getSelectedChordMode()).thenReturn("Single Note");
        
        controller.keyPressed(keyEvent);
        
        verify(model, times(1)).playNote(60);
        verify(model, never()).playChord(anyInt());
    }
    
    @Test
    void testHandleNotePressedChordMode_Major() {
        when(controlPanel.getSelectedChordMode()).thenReturn("Major");
        when(model.isRecording()).thenReturn(false);
        
        controller.handleNotePressed(60);
        
        // Verify Model calls (synchronous)
        verify(model, times(1)).playNoteWithoutRecording(60);
        verify(model, times(1)).playNoteWithoutRecording(64);
        verify(model, times(1)).playNoteWithoutRecording(67);
        verify(model, never()).playNote(anyInt());
        
        // View highlighting is async, use timeout or skip
        verify(keyboardPanel, timeout(500)).highlightKeys(argThat(list -> 
            list.size() == 3 && 
            list.contains(60) && 
            list.contains(64) && 
            list.contains(67)
        ));
    }
    
    @Test
    void testHandleNotePressedChordMode_WhenRecording() {
        when(controlPanel.getSelectedChordMode()).thenReturn("Major");
        when(model.isRecording()).thenReturn(true);
        
        controller.handleNotePressed(60);
        
        verify(model, times(1)).playNote(60);
        verify(model, times(1)).playNote(64);
        verify(model, times(1)).playNote(67);
        verify(model, never()).playNoteWithoutRecording(anyInt());
    }
    
    @Test
    void testHandleNoteReleasedChordMode_Major() {
        when(controlPanel.getSelectedChordMode()).thenReturn("Major");
        when(model.isRecording()).thenReturn(false);
        
        controller.handleNoteReleased(60);
        
        verify(model, times(1)).stopNoteWithoutRecording(60);
        verify(model, times(1)).stopNoteWithoutRecording(64);
        verify(model, times(1)).stopNoteWithoutRecording(67);
        verify(model, never()).stopNote(anyInt());
    }
    
    @Test
    void testHandleNoteReleasedChordMode_WhenRecording() {
        when(controlPanel.getSelectedChordMode()).thenReturn("Major");
        when(model.isRecording()).thenReturn(true);
        
        controller.handleNoteReleased(60);
        
        verify(model, times(1)).stopNote(60);
        verify(model, times(1)).stopNote(64);
        verify(model, times(1)).stopNote(67);
        verify(model, never()).stopNoteWithoutRecording(anyInt());
    }
    
    @Test
    void testHandleNotePressedSingleNote() {
        when(controlPanel.getSelectedChordMode()).thenReturn("Single Note");
        
        controller.handleNotePressed(60);
        
        // ✅ 只验证 Model 调用（同步）
        verify(model, times(1)).playNote(60);
        // View highlighting is async - use timeout
        verify(keyboardPanel, timeout(500)).highlightKey(60);
    }
    
    @Test
    void testHandleNoteReleasedSingleNote() {
        when(controlPanel.getSelectedChordMode()).thenReturn("Single Note");
        
        controller.handleNoteReleased(60);
        
        verify(model, times(1)).stopNote(60);
        verify(keyboardPanel, timeout(500)).unhighlightKey(60);
    }
    
    @Test
    void testHandleNotePressedChordMode_Minor() {
        when(controlPanel.getSelectedChordMode()).thenReturn("Minor");
        when(model.isRecording()).thenReturn(false);
        
        controller.handleNotePressed(60);
        
        verify(model, times(1)).playNoteWithoutRecording(60);
        verify(model, times(1)).playNoteWithoutRecording(63);
        verify(model, times(1)).playNoteWithoutRecording(67);
    }
    
    @Test
    void testKeyPressedWithSpace() {
        KeyEvent keyEvent = new KeyEvent(
            mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_SPACE,
            ' '
        );
        
        controller.keyPressed(keyEvent);
        
        verify(model, never()).playNote(anyInt());
        verify(model, never()).playChord(anyInt());
    }
    
    @Test
    void testKeyPressedWithEnter() {
        KeyEvent keyEvent = new KeyEvent(
            mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_ENTER,
            '\n'
        );
        
        controller.keyPressed(keyEvent);
        
        verify(model, never()).playNote(anyInt());
        verify(model, never()).playChord(anyInt());
    }
    
    @Test
    void testKeyPressedWithUnmappedKey() {
        KeyEvent keyEvent = new KeyEvent(
            mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_A,
            'a'
        );
        
        controller.keyPressed(keyEvent);
        
        verify(model, never()).playNote(anyInt());
        verify(model, never()).playChord(anyInt());
    }
    
    @Test
    void testKeyReleasedWithMappedKeySingleNote() {
        KeyEvent keyEvent = new KeyEvent(
            mock(Component.class),
            KeyEvent.KEY_RELEASED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_C,
            'c'
        );
        
        when(controlPanel.getSelectedChordMode()).thenReturn("Single Note");
        
        controller.keyReleased(keyEvent);
        
        verify(model, times(1)).stopNote(60);
    }
}
