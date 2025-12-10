package model;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages multiple recordings with save, delete, and export functionality.
 * Thread-safe for concurrent operations.
 * 
 * <p><b>Design Principles Applied:</b></p>
 * <ul>
 *   <li><b>Thread Safety:</b> Uses synchronized blocks and ConcurrentHashMap to ensure
 *       thread-safe access to shared state. All operations on recordings map are protected
 *       by synchronization to prevent race conditions.</li>
 *   <li><b>Encapsulation:</b> Uses ConcurrentHashMap internally to store recordings, providing
 *       controlled access through public methods. The listRecordings() method returns an
 *       unmodifiable list to prevent external modification.</li>
 *   <li><b>Input Validation:</b> Validates recording names (null/empty checks) and file
 *       parameters before operations, throwing IllegalArgumentException with clear messages
 *       for invalid inputs.</li>
 *   <li><b>Defensive Programming:</b> Checks for null recordings and handles edge cases
 *       (e.g., renaming to same name, exporting non-existent recordings) gracefully.</li>
 * </ul>
 * 
 * @author KeyChord
 */
public class RecordingManager {
    private final Map<String, Recording> recordings;
    private Recording currentRecording;
    private int recordingCounter;
    private final Object lock = new Object();
    
    /**
     * Creates a new RecordingManager.
     */
    public RecordingManager() {
        this.recordings = new ConcurrentHashMap<>();
        this.currentRecording = null;
        this.recordingCounter = 1;
    }
    
    /**
     * Starts a new recording session.
     * Creates a new Recording with a default name if none exists.
     * 
     * @return the current Recording
     */
    public Recording startRecording() {
        synchronized (lock) {
            if (currentRecording == null) {
                String defaultName = "Recording " + recordingCounter;
                currentRecording = new Recording(defaultName);
            }
            return currentRecording;
        }
    }
    
    /**
     * Stops the current recording and saves it with the given name.
     * 
     * @param name the name to save the recording as
     * @return the saved Recording
     * @throws IllegalArgumentException if name is null or empty
     */
    public Recording stopRecording(String name) {
        synchronized (lock) {
            if (currentRecording == null) {
                return null;
            }
            
            String saveName = (name != null && !name.trim().isEmpty()) ? name.trim() : currentRecording.getName();
            
            // If name changed, create new recording with new name
            if (!saveName.equals(currentRecording.getName())) {
                Recording saved = new Recording(saveName, currentRecording.getEvents());
                recordings.put(saveName, saved);
                currentRecording = null;
                return saved;
            } else {
                // Save with existing name
                recordings.put(saveName, currentRecording);
                Recording saved = currentRecording;
                currentRecording = null;
                recordingCounter++;
                return saved;
            }
        }
    }
    
    /**
     * Stops the current recording and auto-saves it with a default name.
     * 
     * @return the saved Recording
     */
    public Recording stopRecording() {
        return stopRecording(null);
    }
    
    /**
     * Gets the current active recording (if recording is in progress).
     * 
     * @return the current Recording, or null if not recording
     */
    public Recording getCurrentRecording() {
        synchronized (lock) {
            return currentRecording;
        }
    }
    
    /**
     * Saves a recording with the specified name.
     * 
     * @param name the name to save the recording as
     * @param recording the Recording to save
     * @throws IllegalArgumentException if name or recording is null
     */
    public void saveRecording(String name, Recording recording) {
        // Design Principle: Input Validation - validate parameters before operations
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Recording name cannot be null or empty");
        }
        if (recording == null) {
            throw new IllegalArgumentException("Recording cannot be null");
        }
        synchronized (lock) {
            recordings.put(name.trim(), recording);
        }
    }
    
    /**
     * Deletes a recording by name.
     * 
     * @param name the name of the recording to delete
     * @return true if the recording was deleted, false if it didn't exist
     */
    public boolean deleteRecording(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        synchronized (lock) {
            Recording removed = recordings.remove(name.trim());
            return removed != null;
        }
    }
    
    /**
     * Gets a recording by name.
     * 
     * @param name the name of the recording
     * @return the Recording, or null if not found
     */
    public Recording getRecording(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        synchronized (lock) {
            return recordings.get(name.trim());
        }
    }
    
    /**
     * Gets a list of all recording names.
     * 
     * @return a list of recording names, sorted with natural ordering for "Recording X" format
     */
    public List<String> listRecordings() {
        // Design Principle: Thread Safety - synchronized access to shared state
        synchronized (lock) {
            List<String> names = new ArrayList<>(recordings.keySet());
            // Sort with natural ordering: "Recording X" format sorted by number, others alphabetically
            Collections.sort(names, (name1, name2) -> {
                // Try to extract numbers from "Recording X" format
                Integer num1 = extractRecordingNumber(name1);
                Integer num2 = extractRecordingNumber(name2);
                
                if (num1 != null && num2 != null) {
                    // Both are "Recording X" format, sort by number
                    return Integer.compare(num1, num2);
                } else if (num1 != null) {
                    // name1 is "Recording X", name2 is not - put "Recording X" first
                    return -1;
                } else if (num2 != null) {
                    // name2 is "Recording X", name1 is not - put "Recording X" first
                    return 1;
                } else {
                    // Neither is "Recording X" format, sort alphabetically
                    return name1.compareTo(name2);
                }
            });
            // Design Principle: Encapsulation - return unmodifiable list to prevent external modification
            return Collections.unmodifiableList(names);
        }
    }
    
    /**
     * Extracts the number from a "Recording X" format string.
     * 
     * @param name the recording name
     * @return the number if the name matches "Recording X" format, null otherwise
     */
    private Integer extractRecordingNumber(String name) {
        if (name == null || !name.startsWith("Recording ")) {
            return null;
        }
        try {
            String numberPart = name.substring("Recording ".length()).trim();
            return Integer.parseInt(numberPart);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Exports a recording to a Standard MIDI File (.mid).
     * 
     * @param name the name of the recording to export
     * @param file the file to write to
     * @throws IOException if file writing fails
     * @throws IllegalArgumentException if recording not found or file is null
     */
    public void exportToMIDI(String name, File file) throws IOException {
        // Design Principle: Input Validation - validate all parameters before processing
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Recording name cannot be null or empty");
        }
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        
        Recording recording = getRecording(name);
        if (recording == null) {
            throw new IllegalArgumentException("Recording not found: " + name);
        }
        
        try {
            // Create MIDI sequence with PPQ resolution
            Sequence sequence = new Sequence(Sequence.PPQ, 480);
            javax.sound.midi.Track midiTrack = sequence.createTrack();
            
            List<NoteEvent> events = recording.getEvents();
            
            // Convert NoteEvents to MIDI events
            for (NoteEvent event : events) {
                // Convert milliseconds to ticks (assuming 120 BPM = 500ms per beat)
                // 480 ticks per quarter note, 120 BPM = 500ms per quarter note
                // So 1ms = 480/500 = 0.96 ticks
                long ticks = (long)(event.getTimestamp() * 0.96);
                
                int midiNote = event.getMidiNote();
                int velocity = 100; // Default velocity
                
                if (event.isNoteOn()) {
                    ShortMessage noteOn = new ShortMessage();
                    noteOn.setMessage(ShortMessage.NOTE_ON, 0, midiNote, velocity);
                    midiTrack.add(new MidiEvent(noteOn, ticks));
                } else {
                    ShortMessage noteOff = new ShortMessage();
                    noteOff.setMessage(ShortMessage.NOTE_OFF, 0, midiNote, 0);
                    midiTrack.add(new MidiEvent(noteOff, ticks));
                }
            }
            
            // Write to file
            MidiSystem.write(sequence, 1, file);
            
        } catch (InvalidMidiDataException e) {
            throw new IOException("Failed to create MIDI data: " + e.getMessage(), e);
        }
    }
    
    /**
     * Renames a recording.
     * 
     * @param oldName the current name
     * @param newName the new name
     * @return true if renamed successfully, false if old recording not found
     * @throws IllegalArgumentException if new name is null, empty, or already exists
     */
    public boolean renameRecording(String oldName, String newName) {
        if (oldName == null || oldName.trim().isEmpty()) {
            return false;
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("New name cannot be null or empty");
        }
        
        synchronized (lock) {
            Recording recording = recordings.get(oldName.trim());
            if (recording == null) {
                return false;
            }
            
            String trimmedNewName = newName.trim();
            if (recordings.containsKey(trimmedNewName)) {
                throw new IllegalArgumentException("Recording with name '" + trimmedNewName + "' already exists");
            }
            
            // Create new recording with new name and same events
            Recording renamed = new Recording(trimmedNewName, recording.getEvents());
            recordings.put(trimmedNewName, renamed);
            recordings.remove(oldName.trim());
            
            return true;
        }
    }
}

