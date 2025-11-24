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
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Unit tests for PianoController.
 * Tests keyboard event routing and chord generation.
 * Updated for RecordingManager architecture.
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
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(view.getPianoView()).thenReturn(pianoView);
        when(pianoView.getKeyboardPanel()).thenReturn(keyboardPanel);
        when(pianoView.getControlPanel()).thenReturn(controlPanel);
        when(controlPanel.getChordSelector()).thenReturn(chordSelector);
        when(controlPanel.getSelectedChordMode()).thenReturn("Single Note");
        
        ChordManager chordManager = new ChordManager();
        when(model.getChordManager()).thenReturn(chordManager);
        
        controller = new PianoController(model, view);
    }
    
    @Test
    void testKeyPressedWithMappedKeySingleNote() {
        // Create a mock key event for 'a' key
        KeyEvent keyEvent = new KeyEvent(
            mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_A,
            'a'
        );
        
        when(controlPanel.getSelectedChordMode()).thenReturn("Single Note");
        
        controller.keyPressed(keyEvent);
        
        // Verify that playNote was called (not playChord)
        verify(model, times(1)).playNote(anyInt());
        verify(model, never()).playChord(anyInt());
    }
    
    @Test
    void testKeyPressedWithMappedKeyChordMode() {
        KeyEvent keyEvent = new KeyEvent(
            mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_A,
            'a'
        );
        
        when(controlPanel.getSelectedChordMode()).thenReturn("Major");
        
        controller.keyPressed(keyEvent);
        
        // Verify that playNote was called for each chord note
        verify(model, atLeast(1)).playNote(anyInt());
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
        
        // Space is handled by RecordingController, should not call model methods
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
        
        // Enter is handled by PlaybackController, should not call model methods
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
            KeyEvent.VK_A,
            'a'
        );
        
        when(controlPanel.getSelectedChordMode()).thenReturn("Single Note");
        
        controller.keyReleased(keyEvent);
        
        // Verify that stopNote was called
        verify(model, times(1)).stopNote(anyInt());
    }
    
    @Test
    void testKeyReleasedWithMappedKeyChordMode() {
        KeyEvent keyEvent = new KeyEvent(
            mock(Component.class),
            KeyEvent.KEY_RELEASED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_A,
            'a'
        );
        
        when(controlPanel.getSelectedChordMode()).thenReturn("Major");
        
        controller.keyReleased(keyEvent);
        
        // Verify that stopNote was called for each chord note
        verify(model, atLeast(1)).stopNote(anyInt());
    }
    
    @Test
    void testHandleNotePressedSingleNote() {
        when(controlPanel.getSelectedChordMode()).thenReturn("Single Note");
        
        controller.handleNotePressed(60);
        
        verify(model, times(1)).playNote(60);
        verify(keyboardPanel, times(1)).highlightKey(60);
    }
    
    @Test
    void testHandleNotePressedChordMode() {
        when(controlPanel.getSelectedChordMode()).thenReturn("Major");
        ChordManager chordManager = new ChordManager();
        when(model.getChordManager()).thenReturn(chordManager);
        List<Integer> chordNotes = chordManager.generateChord(60, ChordManager.ChordType.MAJOR);
        
        controller.handleNotePressed(60);
        
        // Should play each note in the chord
        verify(model, times(chordNotes.size())).playNote(anyInt());
        verify(keyboardPanel, times(1)).highlightKeys(anyList());
    }
}
