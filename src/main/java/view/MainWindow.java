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
        
        // Add listener to chord selector to return focus after selection
        JComboBox<?> chordSelector = pianoView.getControlPanel().getChordSelector();
        chordSelector.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                // Popup opening - do nothing
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
                // Popup closing (selection made or cancelled) - return focus to keyboard
                SwingUtilities.invokeLater(() -> {
                    pianoView.getKeyboardPanel().requestFocusInWindow();
                });
            }
            
            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
                // Popup cancelled - return focus to keyboard
                SwingUtilities.invokeLater(() -> {
                    pianoView.getKeyboardPanel().requestFocusInWindow();
                });
            }
        });
        
        // Add focus listener to control panel buttons to return focus after click
        for (Component comp : pianoView.getControlPanel().getComponents()) {
            if (comp instanceof JButton) {
                ((JButton) comp).addActionListener(e -> {
                    SwingUtilities.invokeLater(() -> {
                        pianoView.getKeyboardPanel().requestFocusInWindow();
                    });
                });
            }
        }
        
        // Add focus listener to recording panel buttons to return focus after click
        pianoView.getRecordingPanel().getPlayButton().addActionListener(e -> {
            SwingUtilities.invokeLater(() -> pianoView.getKeyboardPanel().requestFocusInWindow());
        });
        pianoView.getRecordingPanel().getDeleteButton().addActionListener(e -> {
            SwingUtilities.invokeLater(() -> pianoView.getKeyboardPanel().requestFocusInWindow());
        });
        pianoView.getRecordingPanel().getExportButton().addActionListener(e -> {
            SwingUtilities.invokeLater(() -> pianoView.getKeyboardPanel().requestFocusInWindow());
        });
        pianoView.getRecordingPanel().getRenameButton().addActionListener(e -> {
            SwingUtilities.invokeLater(() -> pianoView.getKeyboardPanel().requestFocusInWindow());
        });
        
        // Add focus listener to keyboard panel to keep it focused
        pianoView.getKeyboardPanel().addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                Component opposite = e.getOppositeComponent();
                
                // Don't steal focus from control panel components (buttons, dropdowns, etc.)
                // This allows users to interact with the control panel without focus being stolen
                if (opposite != null) {
                    // Check if focus went to control panel or recording panel
                    if (SwingUtilities.isDescendingFrom(opposite, pianoView.getControlPanel()) ||
                        SwingUtilities.isDescendingFrom(opposite, pianoView.getRecordingPanel())) {
                        return; // Let the control/recording panel keep focus
                    }
                    
                    // Check if focus went to a JComboBox popup (which is a separate window)
                    if (opposite instanceof JComponent) {
                        // JComboBox popups are often in a separate lightweight popup
                        Container parent = opposite.getParent();
                        while (parent != null) {
                            if (parent instanceof JPopupMenu || 
                                parent.getClass().getName().contains("Popup")) {
                                return; // Let the popup keep focus
                            }
                            parent = parent.getParent();
                        }
                    }
                }
                
                // For other cases within our window, reclaim focus for keyboard input
                if (opposite == null || 
                    SwingUtilities.isDescendingFrom(opposite, MainWindow.this)) {
                    SwingUtilities.invokeLater(() -> {
                        // Double-check that focus isn't on a control panel component
                        Component currentFocus = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                        if (currentFocus != null && 
                            (SwingUtilities.isDescendingFrom(currentFocus, pianoView.getControlPanel()) ||
                             SwingUtilities.isDescendingFrom(currentFocus, pianoView.getRecordingPanel()))) {
                            return;
                        }
                        pianoView.getKeyboardPanel().requestFocusInWindow();
                    });
                }
            }
        });
    }
}
