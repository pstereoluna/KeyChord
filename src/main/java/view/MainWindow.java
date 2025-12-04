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
        // JFrame needs special setup to receive keyboard events
        setFocusable(true);
        setFocusableWindowState(true);
        
        // Enable key events on the frame
        enableEvents(java.awt.AWTEvent.KEY_EVENT_MASK);
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
        
        // Use SwingUtilities to ensure focus is set after window is fully visible
        SwingUtilities.invokeLater(() -> {
            // Try multiple components to ensure keyboard events are captured
            pianoView.getKeyboardPanel().requestFocusInWindow();
            if (!pianoView.getKeyboardPanel().isFocusOwner()) {
                pianoView.requestFocusInWindow();
            }
            if (!pianoView.isFocusOwner()) {
                requestFocusInWindow();
            }
        });
        
        // Add window focus listener to ensure keyboard panel gets focus when window gains focus
        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    pianoView.getKeyboardPanel().requestFocusInWindow();
                });
            }
        });
        
        // Add focus listener to keyboard panel to keep it focused
        pianoView.getKeyboardPanel().addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                // If focus is lost to another component in our window, try to get it back
                if (e.getOppositeComponent() == null || 
                    SwingUtilities.isDescendingFrom(e.getOppositeComponent(), MainWindow.this)) {
                    SwingUtilities.invokeLater(() -> {
                        pianoView.getKeyboardPanel().requestFocusInWindow();
                    });
                }
            }
        });
    }
}

