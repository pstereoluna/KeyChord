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
 * Tests keyboard event routing and chord generation with specific verifications.
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
        // Test with 'c' key which maps to MIDI 60 (C4)
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
        
        // Verify that playNote was called with exact MIDI note 60
        verify(model, times(1)).playNote(60);
        verify(model, never()).playChord(anyInt());
    }
    
    @Test
    void testHandleNotePressedChordMode_Major() {
        when(controlPanel.getSelectedChordMode()).thenReturn("Major");
        when(model.isRecording()).thenReturn(false);
        
        // C major chord: C(60), E(64), G(67)
        controller.handleNotePressed(60);
        
        // Verify exact notes are played without recording
        verify(model, times(1)).playNoteWithoutRecording(60);
        verify(model, times(1)).playNoteWithoutRecording(64);
        verify(model, times(1)).playNoteWithoutRecording(67);
        verify(model, never()).playNote(anyInt()); // Should not use playNote when not recording
        
        // Verify highlightKeys is called with correct list
        verify(keyboardPanel, times(1)).highlightKeys(argThat(list -> 
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
        
        // C major chord: C(60), E(64), G(67)
        controller.handleNotePressed(60);
        
        // When recording, should use playNote (which records) instead of playNoteWithoutRecording
        verify(model, times(1)).playNote(60);
        verify(model, times(1)).playNote(64);
        verify(model, times(1)).playNote(67);
        verify(model, never()).playNoteWithoutRecording(anyInt());
        
        // Verify highlightKeys is called with correct list
        verify(keyboardPanel, times(1)).highlightKeys(argThat(list -> 
            list.size() == 3 && 
            list.contains(60) && 
            list.contains(64) && 
            list.contains(67)
        ));
    }
    
    @Test
    void testHandleNoteReleasedChordMode_Major() {
        when(controlPanel.getSelectedChordMode()).thenReturn("Major");
        when(model.isRecording()).thenReturn(false);
        
        // C major chord: C(60), E(64), G(67)
        controller.handleNoteReleased(60);
        
        // Verify exact notes are stopped without recording
        verify(model, times(1)).stopNoteWithoutRecording(60);
        verify(model, times(1)).stopNoteWithoutRecording(64);
        verify(model, times(1)).stopNoteWithoutRecording(67);
        verify(model, never()).stopNote(anyInt()); // Should not use stopNote when not recording
        
        // Verify unhighlightKeys is called with correct list
        verify(keyboardPanel, times(1)).unhighlightKeys(argThat(list -> 
            list.size() == 3 && 
            list.contains(60) && 
            list.contains(64) && 
            list.contains(67)
        ));
    }
    
    @Test
    void testHandleNoteReleasedChordMode_WhenRecording() {
        when(controlPanel.getSelectedChordMode()).thenReturn("Major");
        when(model.isRecording()).thenReturn(true);
        
        // C major chord: C(60), E(64), G(67)
        controller.handleNoteReleased(60);
        
        // When recording, should use stopNote (which records) instead of stopNoteWithoutRecording
        verify(model, times(1)).stopNote(60);
        verify(model, times(1)).stopNote(64);
        verify(model, times(1)).stopNote(67);
        verify(model, never()).stopNoteWithoutRecording(anyInt());
        
        // Verify unhighlightKeys is called with correct list
        verify(keyboardPanel, times(1)).unhighlightKeys(argThat(list -> 
            list.size() == 3 && 
            list.contains(60) && 
            list.contains(64) && 
            list.contains(67)
        ));
    }
    
    @Test
    void testHandleNotePressedSingleNote() {
        when(controlPanel.getSelectedChordMode()).thenReturn("Single Note");
        
        controller.handleNotePressed(60);
        
        verify(model, times(1)).playNote(60);
        verify(keyboardPanel, times(1)).highlightKey(60);
    }
    
    @Test
    void testHandleNoteReleasedSingleNote() {
        when(controlPanel.getSelectedChordMode()).thenReturn("Single Note");
        
        controller.handleNoteReleased(60);
        
        verify(model, times(1)).stopNote(60);
        verify(keyboardPanel, times(1)).unhighlightKey(60);
    }
    
    @Test
    void testHandleNotePressedChordMode_Minor() {
        when(controlPanel.getSelectedChordMode()).thenReturn("Minor");
        when(model.isRecording()).thenReturn(false);
        
        // C minor chord: C(60), D#(63), G(67)
        controller.handleNotePressed(60);
        
        verify(model, times(1)).playNoteWithoutRecording(60);
        verify(model, times(1)).playNoteWithoutRecording(63);
        verify(model, times(1)).playNoteWithoutRecording(67);
        
        verify(keyboardPanel, times(1)).highlightKeys(argThat(list -> 
            list.size() == 3 && 
            list.contains(60) && 
            list.contains(63) && 
            list.contains(67)
        ));
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
    void testKeyPressedWithUnmappedKey() {
        // 'a' is not mapped in KeyMappings
        KeyEvent keyEvent = new KeyEvent(
            mock(Component.class),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_A,
            'a'
        );
        
        controller.keyPressed(keyEvent);
        
        // Unmapped key should not trigger any model calls
        verify(model, never()).playNote(anyInt());
        verify(model, never()).playChord(anyInt());
    }
    
    @Test
    void testKeyReleasedWithMappedKeySingleNote() {
        // Test with 'c' key which maps to MIDI 60
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
        
        // Verify that stopNote was called with exact MIDI note 60
        verify(model, times(1)).stopNote(60);
    }
}
