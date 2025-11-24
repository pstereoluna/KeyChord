package controller;

import model.ChordManager;
import model.KeyMappings;
import model.PianoModel;
import view.MainWindow;
import view.PianoView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

/**
 * Main controller for piano key input and chord generation.
 * Handles keyboard events and coordinates between model and view.
 * 
 * @author KeyChord
 */
public class PianoController implements KeyListener, ActionListener {
    private final PianoModel model;
    private final MainWindow view;
    private final PianoView pianoView;
    
    /**
     * Creates a new PianoController.
     * 
     * @param model the PianoModel
     * @param view the MainWindow
     */
    public PianoController(PianoModel model, MainWindow view) {
        this.model = model;
        this.view = view;
        this.pianoView = view.getPianoView();
        
        // Register this controller as a key listener
        view.addKeyListener(this);
        pianoView.getKeyboardPanel().setFocusable(true);
        
        // Register for chord mode selector changes
        pianoView.getControlPanel().getChordSelector().addActionListener(this);
        
        // Wire up mouse click handlers for piano keys
        pianoView.getKeyboardPanel().setKeyPressHandler(new view.PianoKeyboardPanel.KeyPressHandler() {
            @Override
            public void onKeyPressed(int midiNote) {
                handleNotePressed(midiNote);
            }
            
            @Override
            public void onKeyReleased(int midiNote) {
                handleNoteReleased(midiNote);
            }
        });
    }
    
    /**
     * Handles a note being pressed (mouse click or keyboard).
     * 
     * @param midiNote the MIDI note number
     */
    public void handleNotePressed(int midiNote) {
        String chordMode = pianoView.getControlPanel().getSelectedChordMode();
        
        if ("Single Note".equals(chordMode)) {
            // Play single note
            model.playNote(midiNote);
            SwingUtilities.invokeLater(() -> {
                pianoView.getKeyboardPanel().highlightKey(midiNote);
            });
        } else {
            // Play chord based on selected mode
            ChordManager.ChordType chordType = mapChordModeToType(chordMode);
            List<Integer> chordNotes = model.getChordManager().generateChord(midiNote, chordType);
            
            // Play all chord notes (model will handle recording if active)
            for (Integer note : chordNotes) {
                if (model.isRecording()) {
                    model.playNote(note); // This will record
                } else {
                    model.playNoteWithoutRecording(note); // Just play, don't record
                }
            }
            
            // Update view to highlight chord keys
            SwingUtilities.invokeLater(() -> {
                pianoView.getKeyboardPanel().highlightKeys(chordNotes);
            });
        }
    }
    
    /**
     * Handles a note being released (mouse release or keyboard release).
     * 
     * @param midiNote the MIDI note number
     */
    public void handleNoteReleased(int midiNote) {
        String chordMode = pianoView.getControlPanel().getSelectedChordMode();
        
        if ("Single Note".equals(chordMode)) {
            // Stop single note
            model.stopNote(midiNote);
            SwingUtilities.invokeLater(() -> {
                pianoView.getKeyboardPanel().unhighlightKey(midiNote);
            });
        } else {
            // Stop chord based on selected mode
            ChordManager.ChordType chordType = mapChordModeToType(chordMode);
            List<Integer> chordNotes = model.getChordManager().generateChord(midiNote, chordType);
            
            // Stop all chord notes (model will handle recording if active)
            for (Integer note : chordNotes) {
                if (model.isRecording()) {
                    model.stopNote(note); // This will record
                } else {
                    model.stopNoteWithoutRecording(note); // Just stop, don't record
                }
            }
            
            // Update view to unhighlight chord keys
            SwingUtilities.invokeLater(() -> {
                pianoView.getKeyboardPanel().unhighlightKeys(chordNotes);
            });
        }
    }
    
    /**
     * Maps chord mode string to ChordType enum.
     * 
     * @param chordMode the chord mode string
     * @return the corresponding ChordType
     */
    private ChordManager.ChordType mapChordModeToType(String chordMode) {
        switch (chordMode) {
            case "Major":
                return ChordManager.ChordType.MAJOR;
            case "Minor":
                return ChordManager.ChordType.MINOR;
            case "7th":
                return ChordManager.ChordType.MAJOR_SEVENTH;
            case "Dim":
                return ChordManager.ChordType.DIMINISHED;
            case "Sus2":
                return ChordManager.ChordType.SUS2;
            case "Sus4":
                return ChordManager.ChordType.SUS4;
            default:
                return ChordManager.ChordType.MAJOR;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle chord mode selector changes
        if (e.getSource() == pianoView.getControlPanel().getChordSelector()) {
            // Chord mode changed - no action needed, will be used on next key press
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        char keyChar = e.getKeyChar();
        int keyCode = e.getKeyCode();
        
        // Handle special keys (Space, Enter, number keys)
        if (keyCode == KeyEvent.VK_SPACE) {
            // Recording is handled by RecordingController
            return;
        }
        
        if (keyCode == KeyEvent.VK_ENTER) {
            // Playback is handled by PlaybackController
            return;
        }
        
        // Track selection removed - now using RecordingManager
        
        // Handle piano keys
        if (KeyMappings.isMapped(keyChar)) {
            int rootNote = KeyMappings.getMidiNote(keyChar);
            if (rootNote >= 0) {
                handleNotePressed(rootNote);
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        char keyChar = e.getKeyChar();
        int keyCode = e.getKeyCode();
        
        // Handle special keys
        if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_ENTER) {
            return;
        }
        
        if (keyCode >= KeyEvent.VK_1 && keyCode <= KeyEvent.VK_9) {
            return;
        }
        
        // Handle piano keys - stop note/chord
        if (KeyMappings.isMapped(keyChar)) {
            int rootNote = KeyMappings.getMidiNote(keyChar);
            if (rootNote >= 0) {
                handleNoteReleased(rootNote);
            }
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used, but required by KeyListener interface
    }
}

