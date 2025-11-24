package controller;

import model.PianoModel;
import view.PianoView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Controller for RecordingPanel interactions.
 * Handles play, delete, export, and rename operations.
 * 
 * @author KeyChord
 */
public class RecordingPanelController implements ActionListener {
    private final PianoModel model;
    private final PianoView view;
    
    /**
     * Creates a new RecordingPanelController.
     * 
     * @param model the PianoModel
     * @param view the PianoView
     */
    public RecordingPanelController(PianoModel model, PianoView view) {
        this.model = model;
        this.view = view;
        
        // Wire up button listeners
        view.getRecordingPanel().addPlayListener(this);
        view.getRecordingPanel().addDeleteListener(this);
        view.getRecordingPanel().addExportListener(this);
        view.getRecordingPanel().addRenameListener(this);
        
        // Initial update of recording list
        updateRecordingList();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String selectedName = view.getRecordingPanel().getSelectedRecording();
        if (selectedName == null) {
            JOptionPane.showMessageDialog(view,
                "Please select a recording first.",
                "No Selection",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Object source = e.getSource();
        
        if (source == view.getRecordingPanel().getPlayButton()) {
            playRecording(selectedName);
        } else if (source == view.getRecordingPanel().getDeleteButton()) {
            deleteRecording(selectedName);
        } else if (source == view.getRecordingPanel().getExportButton()) {
            exportRecording(selectedName);
        } else if (source == view.getRecordingPanel().getRenameButton()) {
            renameRecording(selectedName);
        }
    }
    
    /**
     * Plays the selected recording.
     * 
     * @param recordingName the name of the recording to play
     */
    private void playRecording(String recordingName) {
        SwingUtilities.invokeLater(() -> {
            view.getControlPanel().setStatus("Playing: " + recordingName);
            view.getControlPanel().setPlayingState(true);
        });
        
        model.startPlayback(recordingName);
        
        // Monitor playback completion
        new Thread(() -> {
            while (model.isPlaying()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            SwingUtilities.invokeLater(() -> {
                view.getControlPanel().setPlayingState(false);
                view.getControlPanel().setStatus("Ready");
            });
        }).start();
    }
    
    /**
     * Deletes the selected recording.
     * 
     * @param recordingName the name of the recording to delete
     */
    private void deleteRecording(String recordingName) {
        int confirm = JOptionPane.showConfirmDialog(view,
            "Are you sure you want to delete '" + recordingName + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = model.getRecordingManager().deleteRecording(recordingName);
            if (deleted) {
                updateRecordingList();
                SwingUtilities.invokeLater(() -> {
                    view.getControlPanel().setStatus("Deleted: " + recordingName);
                });
            }
        }
    }
    
    /**
     * Exports the selected recording to a MIDI file.
     * 
     * @param recordingName the name of the recording to export
     */
    private void exportRecording(String recordingName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Recording to MIDI");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "MIDI Files (*.mid)", "mid"));
        fileChooser.setSelectedFile(new File(recordingName + ".mid"));
        
        int result = fileChooser.showSaveDialog(view);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Ensure .mid extension
            final File file;
            if (!selectedFile.getName().toLowerCase().endsWith(".mid")) {
                file = new File(selectedFile.getParent(), selectedFile.getName() + ".mid");
            } else {
                file = selectedFile;
            }
            
            try {
                model.getRecordingManager().exportToMIDI(recordingName, file);
                SwingUtilities.invokeLater(() -> {
                    view.getControlPanel().setStatus("Exported: " + file.getName());
                    JOptionPane.showMessageDialog(view,
                        "Recording exported successfully to:\n" + file.getAbsolutePath(),
                        "Export Success",
                        JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(view,
                        "Failed to export recording: " + e.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        }
    }
    
    /**
     * Renames the selected recording.
     * 
     * @param oldName the current name of the recording
     */
    private void renameRecording(String oldName) {
        String newName = JOptionPane.showInputDialog(view,
            "Enter new name for recording:",
            "Rename Recording",
            JOptionPane.QUESTION_MESSAGE);
        
        if (newName != null && !newName.trim().isEmpty() && !newName.trim().equals(oldName)) {
            try {
                boolean renamed = model.getRecordingManager().renameRecording(oldName, newName.trim());
                if (renamed) {
                    updateRecordingList();
                    SwingUtilities.invokeLater(() -> {
                        view.getControlPanel().setStatus("Renamed to: " + newName.trim());
                    });
                }
            } catch (IllegalArgumentException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(view,
                        "Cannot rename: " + e.getMessage(),
                        "Rename Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        }
    }
    
    /**
     * Updates the recording list in the panel.
     */
    public void updateRecordingList() {
        SwingUtilities.invokeLater(() -> {
            java.util.List<String> recordings = model.getRecordingManager().listRecordings();
            view.getRecordingPanel().updateRecordings(recordings);
        });
    }
}

