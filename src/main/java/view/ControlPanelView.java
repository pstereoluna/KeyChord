package view;

import javax.swing.*;
import java.awt.*;

/**
 * Control panel for track selection, recording, and playback controls.
 * 
 * @author KeyChord
 */
public class ControlPanelView extends JPanel {
    private final JLabel statusLabel;
    private final JButton recordButton;
    private final JButton playButton;
    private final JComboBox<String> chordSelector;
    
    /**
     * Creates a new ControlPanelView.
     */
    public ControlPanelView() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Controls"));
        
        // Status label
        statusLabel = new JLabel("Status: Ready");
        add(statusLabel);
        
        add(Box.createHorizontalStrut(20));
        
        // Record button
        recordButton = new JButton("Record (Space)");
        add(recordButton);
        
        // Play button (for playing selected recording from panel)
        playButton = new JButton("Play Selected");
        add(playButton);
        
        add(Box.createHorizontalStrut(20));
        
        // Chord mode selector
        JLabel chordLabel = new JLabel("Chord Mode:");
        add(chordLabel);
        
        String[] chordModes = {"Single Note", "Major", "Minor", "7th", "Dim", "Sus2", "Sus4"};
        chordSelector = new JComboBox<>(chordModes);
        chordSelector.setSelectedIndex(0); // Default to "Single Note"
        add(chordSelector);
        
        // Ensure panel is opaque and has preferred size
        setOpaque(true);
        // Set preferred size with enough height to show all controls including dropdown
        setPreferredSize(new Dimension(700, 100));
        setMinimumSize(new Dimension(600, 100)); // Minimum height to prevent cutoff
    }
    
    /**
     * Gets the selected chord mode.
     * 
     * @return the selected chord mode string, or "Single Note" if none selected
     */
    public String getSelectedChordMode() {
        return (String) chordSelector.getSelectedItem();
    }
    
    /**
     * Gets the chord selector combo box.
     * 
     * @return the chord selector
     */
    public JComboBox<String> getChordSelector() {
        return chordSelector;
    }
    
    /**
     * Updates the status display.
     * 
     * @param status the status text
     */
    public void setStatus(String status) {
        statusLabel.setText("Status: " + status);
    }
    
    /**
     * Gets the record button.
     * 
     * @return the record button
     */
    public JButton getRecordButton() {
        return recordButton;
    }
    
    /**
     * Gets the play button.
     * 
     * @return the play button
     */
    public JButton getPlayButton() {
        return playButton;
    }
    
    /**
     * Updates the record button appearance based on recording state.
     * 
     * @param isRecording true if recording, false otherwise
     */
    public void setRecordingState(boolean isRecording) {
        if (isRecording) {
            recordButton.setText("Recording... (Enter to stop)");
            recordButton.setBackground(Color.RED);
        } else {
            recordButton.setText("Record (Space)");
            recordButton.setBackground(null);
        }
    }
    
    /**
     * Updates the play button appearance based on playing state.
     * 
     * @param isPlaying true if playing, false otherwise
     */
    public void setPlayingState(boolean isPlaying) {
        if (isPlaying) {
            playButton.setText("Playing...");
            playButton.setBackground(Color.GREEN);
        } else {
            playButton.setText("Play (Enter)");
            playButton.setBackground(null);
        }
    }
}

