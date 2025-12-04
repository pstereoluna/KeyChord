import controller.DefaultDialogService;
import controller.PianoController;
import controller.PlaybackController;
import controller.RecordingController;
import controller.RecordingPanelController;
import model.PianoModel;
import view.MainWindow;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;

/**
 * Main application class for KeyChord.
 * Initializes the MVC components and starts the application.
 * 
 * @author KeyChord
 */
public class KeyChordApp {
    /**
     * Main entry point for the application.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Create model
                PianoModel model = new PianoModel();
                
                // Create view
                MainWindow view = new MainWindow();
                
                // Create controllers
                PianoController pianoController = new PianoController(model, view);
                RecordingController recordingController = new RecordingController(model, view);
                PlaybackController playbackController = new PlaybackController(model, view);
                RecordingPanelController recordingPanelController = new RecordingPanelController(
                    model, view.getPianoView(), new DefaultDialogService());
                
                // Show window
                view.showWindow();
                
                // Add shutdown hook to clean up resources
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    model.close();
                }));
                
            } catch (MidiUnavailableException e) {
                JOptionPane.showMessageDialog(null,
                    "MIDI system unavailable. Please check your audio settings.",
                    "MIDI Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Error starting application: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}

