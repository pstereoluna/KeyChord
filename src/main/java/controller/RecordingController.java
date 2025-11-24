package controller;

import model.PianoModel;
import model.Recording;
import view.MainWindow;
import view.PianoView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Controller for recording functionality.
 * Handles starting/stopping recording and updating the view.
 * 
 * @author KeyChord
 */
public class RecordingController implements KeyListener, ActionListener {
    private final PianoModel model;
    private final MainWindow view;
    private final PianoView pianoView;
    
    /**
     * Creates a new RecordingController.
     * 
     * @param model the PianoModel
     * @param view the MainWindow
     */
    public RecordingController(PianoModel model, MainWindow view) {
        this.model = model;
        this.view = view;
        this.pianoView = view.getPianoView();
        
        // Register key listener
        view.addKeyListener(this);
        
        // Register button listener
        pianoView.getControlPanel().getRecordButton().addActionListener(this);
        
        // Initial update of recording panel
        updateRecordingPanel();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!model.isRecording()) {
                startRecording();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (model.isRecording()) {
                stopRecording();
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // Not needed for recording control
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == pianoView.getControlPanel().getRecordButton()) {
            if (model.isRecording()) {
                stopRecording();
            } else {
                startRecording();
            }
        }
    }
    
    /**
     * Starts recording.
     */
    private void startRecording() {
        model.startRecording();
        SwingUtilities.invokeLater(() -> {
            pianoView.getControlPanel().setRecordingState(true);
            pianoView.getControlPanel().setStatus("Recording");
        });
    }
    
    /**
     * Stops recording and auto-saves with default name.
     */
    private void stopRecording() {
        Recording saved = model.stopRecording();
        SwingUtilities.invokeLater(() -> {
            pianoView.getControlPanel().setRecordingState(false);
            if (saved != null) {
                pianoView.getControlPanel().setStatus("Saved: " + saved.getName());
                // Update recording panel
                updateRecordingPanel();
            } else {
                pianoView.getControlPanel().setStatus("Ready");
            }
        });
    }
    
    /**
     * Updates the recording panel with current list of recordings.
     */
    private void updateRecordingPanel() {
        SwingUtilities.invokeLater(() -> {
            java.util.List<String> recordings = model.getRecordingManager().listRecordings();
            pianoView.getRecordingPanel().updateRecordings(recordings);
        });
    }
}

