package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Records NoteEvents with timestamps.
 * Thread-safe for concurrent recording operations.
 * 
 * <p><b>Design Principles Applied:</b></p>
 * <ul>
 *   <li><b>Single Responsibility Principle (SRP):</b> This class has a single responsibility:
 *       recording NoteEvents with timestamps. It does not handle playback, storage, or UI concerns.</li>
 *   <li><b>Thread Safety:</b> All methods use synchronized blocks to ensure thread-safe access
 *       to shared state (isRecording flag, startTime). This allows concurrent access from
 *       multiple threads without race conditions.</li>
 * </ul>
 * 
 * @author KeyChord
 */
public class Recorder {
    private boolean isRecording;
    private long startTime;
    private final Object lock = new Object();
    
    /**
     * Creates a new Recorder in the stopped state.
     */
    public Recorder() {
        this.isRecording = false;
        this.startTime = 0;
    }
    
    /**
     * Starts recording. Resets the start time.
     */
    public void startRecording() {
        // Design Principle: Thread Safety - synchronized access to shared state
        synchronized (lock) {
            isRecording = true;
            startTime = System.currentTimeMillis();
        }
    }
    
    /**
     * Stops recording.
     */
    public void stopRecording() {
        synchronized (lock) {
            isRecording = false;
        }
    }
    
    /**
     * Checks if currently recording.
     * 
     * @return true if recording, false otherwise
     */
    public boolean isRecording() {
        synchronized (lock) {
            return isRecording;
        }
    }
    
    /**
     * Creates a NoteEvent for a note-on event at the current recording time.
     * 
     * @param midiNote the MIDI note number
     * @return a NoteEvent with the current timestamp, or null if not recording
     */
    public NoteEvent recordNoteOn(int midiNote) {
        synchronized (lock) {
            if (!isRecording) {
                return null;
            }
            long currentTime = System.currentTimeMillis();
            long relativeTime = currentTime - startTime;
            return new NoteEvent(midiNote, relativeTime, true);
        }
    }
    
    /**
     * Creates a NoteEvent for a note-off event at the current recording time.
     * 
     * @param midiNote the MIDI note number
     * @return a NoteEvent with the current timestamp, or null if not recording
     */
    public NoteEvent recordNoteOff(int midiNote) {
        synchronized (lock) {
            if (!isRecording) {
                return null;
            }
            long currentTime = System.currentTimeMillis();
            long relativeTime = currentTime - startTime;
            return new NoteEvent(midiNote, relativeTime, false);
        }
    }
    
    /**
     * Creates multiple NoteEvents for chord notes (all note-on) at the current recording time.
     * 
     * @param midiNotes the MIDI note numbers for the chord
     * @return a list of NoteEvents with the same timestamp, or empty list if not recording
     */
    public List<NoteEvent> recordChordOn(List<Integer> midiNotes) {
        synchronized (lock) {
            if (!isRecording) {
                return new ArrayList<>();
            }
            long currentTime = System.currentTimeMillis();
            long relativeTime = currentTime - startTime;
            List<NoteEvent> events = new ArrayList<>();
            for (Integer note : midiNotes) {
                events.add(new NoteEvent(note, relativeTime, true));
            }
            return events;
        }
    }
    
    /**
     * Gets the current recording timestamp relative to the start time.
     * 
     * @return the relative timestamp in milliseconds, or -1 if not recording
     */
    public long getCurrentTimestamp() {
        synchronized (lock) {
            if (!isRecording) {
                return -1;
            }
            return System.currentTimeMillis() - startTime;
        }
    }
}

