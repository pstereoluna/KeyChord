package controller;

import java.awt.Component;
import java.io.File;

/**
 * Service interface for dialog operations.
 * Allows for dependency injection and testing of dialog-dependent code.
 * 
 * @author KeyChord
 */
public interface DialogService {
    /**
     * Shows a confirmation dialog for deleting a recording.
     * 
     * @param parent the parent component for the dialog
     * @param recordingName the name of the recording to delete
     * @return true if user confirmed deletion, false otherwise
     */
    boolean confirmDelete(Component parent, String recordingName);
    
    /**
     * Shows an input dialog for renaming a recording.
     * 
     * @param parent the parent component for the dialog
     * @param oldName the current name of the recording
     * @return the new name entered by the user, or null if cancelled
     */
    String promptRename(Component parent, String oldName);
    
    /**
     * Shows a file chooser dialog for exporting a recording.
     * 
     * @param parent the parent component for the dialog
     * @param defaultName the default file name
     * @return the selected file, or null if cancelled
     */
    File chooseExportFile(Component parent, String defaultName);
    
    /**
     * Shows an information message dialog.
     * 
     * @param parent the parent component for the dialog
     * @param message the message to display
     * @param title the dialog title
     */
    void showMessage(Component parent, String message, String title);
    
    /**
     * Shows an error message dialog.
     * 
     * @param parent the parent component for the dialog
     * @param message the error message to display
     * @param title the dialog title
     */
    void showError(Component parent, String message, String title);
}

