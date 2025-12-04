package controller;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;

/**
 * Default implementation of DialogService using Swing dialogs.
 * 
 * @author KeyChord
 */
public class DefaultDialogService implements DialogService {
    
    @Override
    public boolean confirmDelete(Component parent, String recordingName) {
        int result = JOptionPane.showConfirmDialog(parent,
            "Are you sure you want to delete '" + recordingName + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
    
    @Override
    public String promptRename(Component parent, String oldName) {
        return JOptionPane.showInputDialog(parent,
            "Enter new name for recording:",
            "Rename Recording",
            JOptionPane.QUESTION_MESSAGE);
    }
    
    @Override
    public File chooseExportFile(Component parent, String defaultName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Recording to MIDI");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "MIDI Files (*.mid)", "mid"));
        fileChooser.setSelectedFile(new File(defaultName + ".mid"));
        
        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Ensure .mid extension
            if (!selectedFile.getName().toLowerCase().endsWith(".mid")) {
                return new File(selectedFile.getParent(), selectedFile.getName() + ".mid");
            } else {
                return selectedFile;
            }
        }
        return null;
    }
    
    @Override
    public void showMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent,
            message,
            title,
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public void showError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent,
            message,
            title,
            JOptionPane.ERROR_MESSAGE);
    }
}

