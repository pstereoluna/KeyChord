package view;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window for KeyChord.
 * Sets up the main frame and initializes the piano view.
 * 
 * @author KeyChord
 */
public class MainWindow extends JFrame {
    private final PianoView pianoView;
    
    /**
     * Creates a new MainWindow.
     */
    public MainWindow() {
        setTitle("KeyChord - Virtual Piano");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Ensure content pane uses BorderLayout
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(Color.WHITE); // Remove gray background
        
        pianoView = new PianoView();
        contentPane.add(pianoView, BorderLayout.CENTER);
        
        // Make frame focusable for keyboard input
        setFocusable(true);
    }
    
    /**
     * Gets the piano view.
     * 
     * @return the PianoView
     */
    public PianoView getPianoView() {
        return pianoView;
    }
    
    /**
     * Shows the window.
     */
    public void showWindow() {
        // Pack the window to size based on component preferred sizes
        pack();
        
        // Set minimum size to ensure both panels are fully visible
        // Height = keyboard panel (250) + control panel (100) + frame decorations (~40)
        setMinimumSize(new Dimension(650, 340));
        
        // Center the window on screen
        setLocationRelativeTo(null);
        
        setVisible(true);
        requestFocus();
    }
}

