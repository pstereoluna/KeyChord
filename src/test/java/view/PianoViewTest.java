package view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PianoView.
 * Tests component initialization.
 * Updated for RecordingManager architecture (added RecordingPanel).
 */
class PianoViewTest {
    private PianoView pianoView;
    
    @BeforeEach
    void setUp() {
        pianoView = new PianoView();
    }
    
    @Test
    void testComponentInitialization() {
        assertNotNull(pianoView.getKeyboardPanel(), "Keyboard panel should be initialized");
        assertNotNull(pianoView.getControlPanel(), "Control panel should be initialized");
        assertNotNull(pianoView.getRecordingPanel(), "Recording panel should be initialized");
    }
    
    @Test
    void testGetKeyboardPanel() {
        PianoKeyboardPanel panel = pianoView.getKeyboardPanel();
        assertNotNull(panel, "Should return keyboard panel");
        assertSame(panel, pianoView.getKeyboardPanel(), "Should return same instance");
    }
    
    @Test
    void testGetControlPanel() {
        ControlPanelView panel = pianoView.getControlPanel();
        assertNotNull(panel, "Should return control panel");
        assertSame(panel, pianoView.getControlPanel(), "Should return same instance");
    }
    
    @Test
    void testGetRecordingPanel() {
        RecordingPanel panel = pianoView.getRecordingPanel();
        assertNotNull(panel, "Should return recording panel");
        assertSame(panel, pianoView.getRecordingPanel(), "Should return same instance");
    }
}
