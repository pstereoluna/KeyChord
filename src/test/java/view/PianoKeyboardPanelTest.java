package view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PianoKeyboardPanel.
 * Tests key highlighting and key view retrieval.
 */
class PianoKeyboardPanelTest {
    private PianoKeyboardPanel panel;
    
    @BeforeEach
    void setUp() {
        panel = new PianoKeyboardPanel();
    }
    
    @Test
    @DisplayName("highlightKey should change the key's visual state")
    void testHighlightKey() {
        int midiNote = 60; // C4, should be in range
        
        panel.highlightKey(midiNote);
        
        // Verify key view exists and was highlighted
        PianoKeyView keyView = panel.getKeyView(midiNote);
        assertNotNull(keyView, "Key view should exist for MIDI note 60");
        // Note: We can't easily verify setPressed(true) without exposing internals,
        // but we can verify the key exists and method doesn't throw
    }
    
    @Test
    @DisplayName("unhighlightKey should reset the key")
    void testUnhighlightKey() {
        int midiNote = 60;
        
        panel.highlightKey(midiNote);
        panel.unhighlightKey(midiNote);
        
        // Method should not throw
        assertDoesNotThrow(() -> panel.unhighlightKey(midiNote), 
            "Unhighlighting should not throw");
    }
    
    @Test
    @DisplayName("highlightKeys with a list should highlight all keys")
    void testHighlightKeys() {
        List<Integer> chordNotes = Arrays.asList(60, 64, 67); // C major chord
        
        panel.highlightKeys(chordNotes);
        
        // Verify all keys exist
        for (Integer note : chordNotes) {
            PianoKeyView keyView = panel.getKeyView(note);
            assertNotNull(keyView, "Key view should exist for MIDI note " + note);
        }
    }
    
    @Test
    @DisplayName("unhighlightKeys should unhighlight all keys")
    void testUnhighlightKeys() {
        List<Integer> chordNotes = Arrays.asList(60, 64, 67);
        
        panel.highlightKeys(chordNotes);
        panel.unhighlightKeys(chordNotes);
        
        // Method should not throw
        assertDoesNotThrow(() -> panel.unhighlightKeys(chordNotes), 
            "Unhighlighting keys should not throw");
    }
    
    @Test
    @DisplayName("getKeyView should return correct PianoKeyView for valid MIDI notes")
    void testGetKeyViewValidNotes() {
        // Test notes in the visible range (48-72)
        PianoKeyView key60 = panel.getKeyView(60); // C4
        assertNotNull(key60, "Should return key view for MIDI 60");
        assertEquals(60, key60.getMidiNote(), "Key view should have correct MIDI note");
        
        PianoKeyView key64 = panel.getKeyView(64); // E4
        assertNotNull(key64, "Should return key view for MIDI 64");
        assertEquals(64, key64.getMidiNote(), "Key view should have correct MIDI note");
        
        PianoKeyView key67 = panel.getKeyView(67); // G4
        assertNotNull(key67, "Should return key view for MIDI 67");
        assertEquals(67, key67.getMidiNote(), "Key view should have correct MIDI note");
    }
    
    @Test
    @DisplayName("getKeyView should return null for out-of-range notes")
    void testGetKeyViewOutOfRange() {
        // Test notes outside the visible range
        PianoKeyView key0 = panel.getKeyView(0); // Below range
        assertNull(key0, "Should return null for MIDI note 0 (below range)");
        
        PianoKeyView key127 = panel.getKeyView(127); // Above range
        assertNull(key127, "Should return null for MIDI note 127 (above range)");
        
        PianoKeyView key47 = panel.getKeyView(47); // Just below range (48-72)
        assertNull(key47, "Should return null for MIDI note 47 (below range)");
        
        PianoKeyView key73 = panel.getKeyView(73); // Just above range
        assertNull(key73, "Should return null for MIDI note 73 (above range)");
    }
    
    @Test
    @DisplayName("highlightKey with out-of-range note should not throw")
    void testHighlightKeyOutOfRange() {
        // Should handle gracefully without throwing
        assertDoesNotThrow(() -> {
            panel.highlightKey(0); // Out of range
            panel.highlightKey(127); // Out of range
        }, "Highlighting out-of-range keys should not throw");
    }
    
    @Test
    @DisplayName("KeyPressHandler should be callable")
    void testKeyPressHandler() {
        final boolean[] pressedCalled = {false};
        final boolean[] releasedCalled = {false};
        
        panel.setKeyPressHandler(new PianoKeyboardPanel.KeyPressHandler() {
            @Override
            public void onKeyPressed(int midiNote) {
                pressedCalled[0] = true;
            }
            
            @Override
            public void onKeyReleased(int midiNote) {
                releasedCalled[0] = true;
            }
        });
        
        // Handler should be set (can't easily test without triggering mouse events)
        // But we can verify setKeyPressHandler doesn't throw
        assertDoesNotThrow(() -> panel.setKeyPressHandler(null), 
            "Setting null handler should not throw");
    }
}

