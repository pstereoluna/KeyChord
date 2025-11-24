package view;

import javax.swing.*;
import java.awt.*;

/**
 * Main view component that combines the piano keyboard and control panel.
 * 
 * @author KeyChord
 */
public class PianoView extends JPanel {
    private final PianoKeyboardPanel keyboardPanel;
    private final ControlPanelView controlPanel;
    private final RecordingPanel recordingPanel;
    
    /**
     * Creates a new PianoView.
     */
    public PianoView() {
        setLayout(new BorderLayout());
        
        keyboardPanel = new PianoKeyboardPanel();
        controlPanel = new ControlPanelView();
        recordingPanel = new RecordingPanel();
        
        // Ensure components are added to correct regions
        add(keyboardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(recordingPanel, BorderLayout.EAST);
        
        // Ensure panel is opaque and visible
        setOpaque(true);
        
        // Force layout update after adding components
        revalidate();
        repaint();
    }
    
    /**
     * Gets the piano keyboard panel.
     * 
     * @return the PianoKeyboardPanel
     */
    public PianoKeyboardPanel getKeyboardPanel() {
        return keyboardPanel;
    }
    
    /**
     * Gets the control panel.
     * 
     * @return the ControlPanelView
     */
    public ControlPanelView getControlPanel() {
        return controlPanel;
    }
    
    /**
     * Gets the recording panel.
     * 
     * @return the RecordingPanel
     */
    public RecordingPanel getRecordingPanel() {
        return recordingPanel;
    }
}

