package controller;

import model.PianoModel;
import view.MainWindow;
import view.PianoView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Controller for playback functionality.
 * Handles starting/stopping playback and synchronizing view updates.
 * 
 * @author KeyChord
 */
public class PlaybackController implements KeyListener, ActionListener {
    private final PianoModel model;
    private final MainWindow view;
    private final PianoView pianoView;
    
    /**
     * Creates a new PlaybackController.
     * 
     * @param model the PianoModel
     * @param view the MainWindow
     */
    public PlaybackController(PianoModel model, MainWindow view) {
        this.model = model;
        this.view = view;
        this.pianoView = view.getPianoView();
        
        // Register key listener on multiple components to ensure events are captured
        view.addKeyListener(this);
        pianoView.getKeyboardPanel().addKeyListener(this);
        pianoView.addKeyListener(this);
        
        // Register button listener
        pianoView.getControlPanel().getPlayButton().addActionListener(this);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        // Enter key: Toggle playback (only if not recording)
        // Note: If recording, RecordingController handles Enter to stop recording
        if (keyCode == KeyEvent.VK_ENTER && !model.isRecording()) {
            if (!model.isPlaying()) {
                startPlayback();
            } else {
                stopPlayback();
            }
            e.consume(); // Mark event as consumed
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // Not needed for playback control
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == pianoView.getControlPanel().getPlayButton()) {
            // Play selected recording from panel
            String selectedName = pianoView.getRecordingPanel().getSelectedRecording();
            if (selectedName != null) {
                if (model.isPlaying()) {
                    stopPlayback();
                } else {
                    startPlayback(selectedName);
                }
            } else {
                SwingUtilities.invokeLater(() -> {
                    pianoView.getControlPanel().setStatus("Please select a recording");
                });
            }
        }
    }
    
    /**
     * Starts playback of a recording with visual synchronization.
     * 
     * @param recordingName the name of the recording to play
     */
    private void startPlayback(String recordingName) {
        if (model.isPlaying()) {
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            pianoView.getControlPanel().setPlayingState(true);
            pianoView.getControlPanel().setStatus("Playing: " + recordingName);
        });
        
        // Start playback with a handler that updates both sound and view
        model.Recording recording = model.getRecordingManager().getRecording(recordingName);
        if (recording != null) {
            model.startPlaybackWithHandler(recording, new model.Player.NotePlaybackHandler() {
                @Override
                public void onNoteOn(int midiNote) {
                    model.playNoteWithoutRecording(midiNote);
                    SwingUtilities.invokeLater(() -> {
                        pianoView.getKeyboardPanel().highlightKey(midiNote);
                    });
                }
                
                @Override
                public void onNoteOff(int midiNote) {
                    model.stopNoteWithoutRecording(midiNote);
                    SwingUtilities.invokeLater(() -> {
                        pianoView.getKeyboardPanel().unhighlightKey(midiNote);
                    });
                }
            });
        }
        
        // Monitor playback and update view when finished
        new Thread(() -> {
            while (model.isPlaying()) {
                try {
                    Thread.sleep(50); // Update every 50ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            // Playback finished
            SwingUtilities.invokeLater(() -> {
                pianoView.getControlPanel().setPlayingState(false);
                pianoView.getControlPanel().setStatus("Ready");
                // Clear all highlights
                clearAllHighlights();
            });
        }).start();
    }
    
    /**
     * Starts playback (legacy method for Enter key - plays most recent recording).
     */
    private void startPlayback() {
        // Get most recent recording
        java.util.List<String> recordings = model.getRecordingManager().listRecordings();
        if (recordings.isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                pianoView.getControlPanel().setStatus("No recordings available");
            });
            return;
        }
        startPlayback(recordings.get(recordings.size() - 1));
    }
    
    /**
     * Stops playback.
     */
    private void stopPlayback() {
        model.stopPlayback();
        SwingUtilities.invokeLater(() -> {
            pianoView.getControlPanel().setPlayingState(false);
            pianoView.getControlPanel().setStatus("Ready");
            clearAllHighlights();
        });
    }
    
    /**
     * Clears all key highlights.
     */
    private void clearAllHighlights() {
        // Clear highlights for all visible keys
        for (int note = 48; note <= 72; note++) {
            pianoView.getKeyboardPanel().unhighlightKey(note);
        }
    }
}

