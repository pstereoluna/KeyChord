package view;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;

/**
 * Represents a single piano key in the view.
 * Handles visual state (pressed/unpressed) and displays the key.
 * 
 * @author KeyChord
 */
public class PianoKeyView extends JButton {
    private final int midiNote;
    private final boolean isBlackKey;
    private static final Color WHITE_KEY_COLOR = Color.WHITE;
    private static final Color BLACK_KEY_COLOR = Color.BLACK;
    private static final Color WHITE_KEY_PRESSED = new Color(255, 255, 200);
    private static final Color BLACK_KEY_PRESSED = new Color(150, 150, 0);
    
    /**
     * Creates a new PianoKeyView.
     * 
     * @param midiNote the MIDI note number this key represents
     * @param isBlackKey true if this is a black key, false if white
     */
    public PianoKeyView(int midiNote, boolean isBlackKey) {
        this.midiNote = midiNote;
        this.isBlackKey = isBlackKey;
        
        setFocusPainted(false);
        setContentAreaFilled(true);
        setOpaque(true);
        setVisible(true);
        
        // Prevent focus for black keys to avoid visual issues
        if (isBlackKey) {
            setFocusable(false);
            setBorderPainted(false); // Black keys have no border
            setBackground(BLACK_KEY_COLOR);
            setForeground(Color.WHITE);
            // Black keys are 60% of white key dimensions (40*0.6=24, 200*0.6=120)
            Dimension blackSize = new Dimension(24, 120);
            setPreferredSize(blackSize);
            setMinimumSize(blackSize);
            setMaximumSize(blackSize);
        } else {
            setFocusable(true);
            setBorderPainted(true); // White keys need visible borders
            setBackground(WHITE_KEY_COLOR);
            setForeground(Color.BLACK);
            Dimension whiteSize = new Dimension(40, 200);
            setPreferredSize(whiteSize);
            setMinimumSize(whiteSize);
            setMaximumSize(whiteSize);
            // Add thin 1px black border for white keys
            // This creates clear separation between white keys like a real piano
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        }
        
        // Ensure button doesn't clip when pressed
        setRolloverEnabled(false);
        
        updateAppearance();
    }
    
    /**
     * Gets the MIDI note number for this key.
     * 
     * @return the MIDI note number
     */
    public int getMidiNote() {
        return midiNote;
    }
    
    /**
     * Checks if this is a black key.
     * 
     * @return true if black key, false if white key
     */
    public boolean isBlackKey() {
        return isBlackKey;
    }
    
    /**
     * Sets the pressed state of this key.
     * 
     * @param pressed true if pressed, false otherwise
     */
    public void setPressed(boolean pressed) {
        updateAppearance(pressed);
    }
    
    /**
     * Updates the visual appearance based on pressed state.
     * 
     * @param pressed true if pressed, false otherwise
     */
    private void updateAppearance(boolean pressed) {
        if (pressed) {
            if (isBlackKey) {
                setBackground(BLACK_KEY_PRESSED);
            } else {
                setBackground(WHITE_KEY_PRESSED);
            }
        } else {
            if (isBlackKey) {
                setBackground(BLACK_KEY_COLOR);
            } else {
                setBackground(WHITE_KEY_COLOR);
            }
        }
        
        // For black keys, ensure they stay on top when pressed
        if (isBlackKey && getParent() != null) {
            getParent().setComponentZOrder(this, 0);
        }
        
        // Force repaint and ensure bounds are maintained
        repaint();
        // Ensure the button maintains its size when pressed
        if (getParent() != null) {
            getParent().repaint();
        }
    }
    
    /**
     * Updates the visual appearance to the default (unpressed) state.
     */
    private void updateAppearance() {
        updateAppearance(false);
    }
}

