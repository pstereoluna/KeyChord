package view;

import javax.swing.JPanel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel that displays the piano keyboard with white and black keys.
 * Manages visual highlighting of pressed keys.
 * 
 * @author KeyChord
 */
public class PianoKeyboardPanel extends JPanel {
    private static final int START_NOTE = 48; // C3
    private static final int END_NOTE = 72;   // C5
    private static final int WHITE_KEY_WIDTH = 40;
    private static final int WHITE_KEY_HEIGHT = 200;
    // Black keys are 60% of white key dimensions
    private static final int BLACK_KEY_WIDTH = (int)(WHITE_KEY_WIDTH * 0.6); // 24px
    private static final int BLACK_KEY_HEIGHT = (int)(WHITE_KEY_HEIGHT * 0.6); // 120px
    
    private final Map<Integer, PianoKeyView> keyViews;
    private int calculatedWidth;
    private int calculatedHeight;
    
    /**
     * Interface for handling key press events.
     */
    public interface KeyPressHandler {
        /**
         * Called when a key is pressed.
         * 
         * @param midiNote the MIDI note number
         */
        void onKeyPressed(int midiNote);
        
        /**
         * Called when a key is released.
         * 
         * @param midiNote the MIDI note number
         */
        void onKeyReleased(int midiNote);
    }
    
    private KeyPressHandler keyPressHandler;
    
    /**
     * Creates a new PianoKeyboardPanel.
     */
    public PianoKeyboardPanel() {
        this.keyViews = new HashMap<>();
        setLayout(null);
        
        setBackground(Color.WHITE); // White background to match piano keys
        setOpaque(true);
        
        initializeKeys();
        
        // Calculate and set panel size based on actual keys
        calculatePanelSize();
        
        // Force layout update
        revalidate();
        repaint();
    }
    
    /**
     * Sets the key press handler for mouse clicks on keys.
     * 
     * @param handler the handler for key press events
     */
    public void setKeyPressHandler(KeyPressHandler handler) {
        this.keyPressHandler = handler;
        // Wire up existing keys
        for (PianoKeyView key : keyViews.values()) {
            wireKeyListener(key);
        }
    }
    
    /**
     * Wires up action and mouse listeners for a key.
     * 
     * @param key the key to wire up
     */
    private void wireKeyListener(PianoKeyView key) {
        // Remove existing listeners
        for (java.awt.event.ActionListener al : key.getActionListeners()) {
            key.removeActionListener(al);
        }
        
        // Add new listener
        if (keyPressHandler != null) {
            key.addActionListener(e -> {
                keyPressHandler.onKeyPressed(key.getMidiNote());
            });
            
            // Also handle mouse press/release for better responsiveness
            key.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    keyPressHandler.onKeyPressed(key.getMidiNote());
                }
                
                @Override
                public void mouseReleased(java.awt.event.MouseEvent e) {
                    keyPressHandler.onKeyReleased(key.getMidiNote());
                }
            });
        }
    }
    
    /**
     * Calculates the panel size based on the number of white keys.
     */
    private void calculatePanelSize() {
        int whiteKeyCount = 0;
        for (int note = START_NOTE; note <= END_NOTE; note++) {
            int noteInOctave = note % 12;
            if (noteInOctave == 0 || noteInOctave == 2 || noteInOctave == 4 ||
                noteInOctave == 5 || noteInOctave == 7 || noteInOctave == 9 || noteInOctave == 11) {
                whiteKeyCount++;
            }
        }
        
        calculatedWidth = whiteKeyCount * WHITE_KEY_WIDTH;
        // Fixed height to prevent taking too much vertical space
        // Height = white key height + small padding for black keys
        calculatedHeight = 200; // Fixed preferred height
        
        Dimension size = new Dimension(calculatedWidth, calculatedHeight);
        setPreferredSize(size);
        setMinimumSize(new Dimension(calculatedWidth, WHITE_KEY_HEIGHT)); // Minimum = white key height
        setMaximumSize(new Dimension(Integer.MAX_VALUE, calculatedHeight)); // Max = preferred height
    }
    
    @Override
    public Dimension getPreferredSize() {
        if (calculatedWidth > 0 && calculatedHeight > 0) {
            return new Dimension(calculatedWidth, calculatedHeight);
        }
        return super.getPreferredSize();
    }
    
    /**
     * Initializes all piano keys in the visible range.
     */
    private void initializeKeys() {
        // Map to store white keys by their note for relative positioning
        Map<Integer, PianoKeyView> whiteKeys = new HashMap<>();
        int whiteKeyX = 0;
        
        // First pass: create all white keys (add to back)
        for (int note = START_NOTE; note <= END_NOTE; note++) {
            int noteInOctave = note % 12;
            // White keys: C, D, E, F, G, A, B (0, 2, 4, 5, 7, 9, 11)
            if (noteInOctave == 0 || noteInOctave == 2 || noteInOctave == 4 ||
                noteInOctave == 5 || noteInOctave == 7 || noteInOctave == 9 || noteInOctave == 11) {
                PianoKeyView key = new PianoKeyView(note, false);
                key.setBounds(whiteKeyX, 0, WHITE_KEY_WIDTH, WHITE_KEY_HEIGHT);
                key.setVisible(true);
                add(key); // Add to back (white keys are background layer)
                wireKeyListener(key);
                keyViews.put(note, key);
                whiteKeys.put(note, key);
                whiteKeyX += WHITE_KEY_WIDTH;
            }
        }
        
        // Second pass: create black keys positioned over white keys
        // Black key pattern: positions [1, 2, 4, 5, 6] relative to white keys in an octave
        // White keys in octave: C(0), D(2), E(4), F(5), G(7), A(9), B(11)
        // Black keys: C#(1), D#(3), F#(6), G#(8), A#(10)
        java.util.List<PianoKeyView> blackKeys = new java.util.ArrayList<>();
        
        // Track white key positions for relative black key placement
        java.util.List<PianoKeyView> whiteKeyList = new java.util.ArrayList<>();
        for (int note = START_NOTE; note <= END_NOTE; note++) {
            int noteInOctave = note % 12;
            if (noteInOctave == 0 || noteInOctave == 2 || noteInOctave == 4 ||
                noteInOctave == 5 || noteInOctave == 7 || noteInOctave == 9 || noteInOctave == 11) {
                whiteKeyList.add(whiteKeys.get(note));
            }
        }
        
        // Create black keys using pattern [1, 2, 4, 5, 6] relative to white keys
        int whiteKeyIndex = 0;
        for (int note = START_NOTE; note <= END_NOTE; note++) {
            int noteInOctave = note % 12;
            
            // White keys: C(0), D(2), E(4), F(5), G(7), A(9), B(11)
            if (noteInOctave == 0 || noteInOctave == 2 || noteInOctave == 4 ||
                noteInOctave == 5 || noteInOctave == 7 || noteInOctave == 9 || noteInOctave == 11) {
                
                PianoKeyView currentWhiteKey = whiteKeyList.get(whiteKeyIndex);
                int whiteKeyXPos = currentWhiteKey.getX();
                int whiteKeyWidth = currentWhiteKey.getWidth();
                
                // Pattern: black keys at positions 1, 2, 4, 5, 6 (0-indexed: 0, 1, 3, 4, 5)
                // Position 1 (C#): after C (index 0) - between C and D
                if (noteInOctave == 0 && note + 1 <= END_NOTE && whiteKeyIndex < whiteKeyList.size() - 1) {
                    int blackX = whiteKeyXPos + whiteKeyWidth - BLACK_KEY_WIDTH / 2;
                    PianoKeyView blackKey = createBlackKey(note + 1, blackX);
                    blackKeys.add(blackKey);
                }
                // Position 2 (D#): after D (index 1) - between D and E
                if (noteInOctave == 2 && note + 1 <= END_NOTE && whiteKeyIndex < whiteKeyList.size() - 1) {
                    int blackX = whiteKeyXPos + whiteKeyWidth - BLACK_KEY_WIDTH / 2;
                    PianoKeyView blackKey = createBlackKey(note + 1, blackX);
                    blackKeys.add(blackKey);
                }
                // Position 4 (F#): after F (index 3) - between F and G
                if (noteInOctave == 5 && note + 1 <= END_NOTE && whiteKeyIndex < whiteKeyList.size() - 1) {
                    int blackX = whiteKeyXPos + whiteKeyWidth - BLACK_KEY_WIDTH / 2;
                    PianoKeyView blackKey = createBlackKey(note + 1, blackX);
                    blackKeys.add(blackKey);
                }
                // Position 5 (G#): after G (index 4) - between G and A
                if (noteInOctave == 7 && note + 1 <= END_NOTE && whiteKeyIndex < whiteKeyList.size() - 1) {
                    int blackX = whiteKeyXPos + whiteKeyWidth - BLACK_KEY_WIDTH / 2;
                    PianoKeyView blackKey = createBlackKey(note + 1, blackX);
                    blackKeys.add(blackKey);
                }
                // Position 6 (A#): after A (index 5) - between A and B
                if (noteInOctave == 9 && note + 1 <= END_NOTE && whiteKeyIndex < whiteKeyList.size() - 1) {
                    int blackX = whiteKeyXPos + whiteKeyWidth - BLACK_KEY_WIDTH / 2;
                    PianoKeyView blackKey = createBlackKey(note + 1, blackX);
                    blackKeys.add(blackKey);
                }
                
                whiteKeyIndex++;
            }
        }
        
        // Ensure all black keys are moved to front (z-order 0)
        // This prevents any clipping or disappearing issues
        for (PianoKeyView blackKey : blackKeys) {
            setComponentZOrder(blackKey, 0);
        }
    }
    
    /**
     * Creates a black key at the specified position.
     * 
     * @param note the MIDI note number
     * @param x the x position
     * @return the created PianoKeyView
     */
    private PianoKeyView createBlackKey(int note, int x) {
        PianoKeyView key = new PianoKeyView(note, true);
        key.setBounds(x, 0, BLACK_KEY_WIDTH, BLACK_KEY_HEIGHT);
        key.setVisible(true);
        add(key, 0); // Add at index 0 (front) to ensure black keys are above white keys
        wireKeyListener(key);
        keyViews.put(note, key);
        return key;
    }
    
    /**
     * Highlights a key as pressed.
     * 
     * @param midiNote the MIDI note number to highlight
     */
    public void highlightKey(int midiNote) {
        PianoKeyView key = keyViews.get(midiNote);
        if (key != null) {
            key.setPressed(true);
        }
    }
    
    /**
     * Removes highlight from a key.
     * 
     * @param midiNote the MIDI note number to unhighlight
     */
    public void unhighlightKey(int midiNote) {
        PianoKeyView key = keyViews.get(midiNote);
        if (key != null) {
            key.setPressed(false);
        }
    }
    
    /**
     * Highlights multiple keys (for chords).
     * 
     * @param midiNotes the MIDI note numbers to highlight
     */
    public void highlightKeys(java.util.List<Integer> midiNotes) {
        for (Integer note : midiNotes) {
            highlightKey(note);
        }
    }
    
    /**
     * Removes highlight from multiple keys.
     * 
     * @param midiNotes the MIDI note numbers to unhighlight
     */
    public void unhighlightKeys(java.util.List<Integer> midiNotes) {
        for (Integer note : midiNotes) {
            unhighlightKey(note);
        }
    }
    
    /**
     * Gets a key view by MIDI note number.
     * 
     * @param midiNote the MIDI note number
     * @return the PianoKeyView, or null if not found
     */
    public PianoKeyView getKeyView(int midiNote) {
        return keyViews.get(midiNote);
    }
}

