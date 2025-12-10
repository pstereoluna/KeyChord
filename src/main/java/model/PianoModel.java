package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Main model class that coordinates all model components.
 * Manages tracks, recording, playback, MIDI sound, and chord generation.
 * Follows MVC pattern - no UI dependencies.
 * 
 * <p><b>Design Principles Applied:</b></p>
 * <ul>
 *   <li><b>Single Responsibility Principle (SRP):</b> Each component (Recorder, Player,
 *       MidiSoundManager, ChordManager, RecordingManager) has a single, well-defined responsibility.
 *       PianoModel coordinates these components but delegates specific tasks to them.</li>
 *   <li><b>Composition over Inheritance:</b> PianoModel composes multiple specialized components
 *       (Recorder, Player, MidiSoundManager, etc.) rather than inheriting from a base class.
 *       This provides flexibility and follows the "has-a" relationship pattern.</li>
 *   <li><b>Adapter Pattern:</b> RecordingEventSource inner class adapts Recording to Player's
 *       EventSource interface, allowing Player to work with Recordings without modifying either
 *       class. This demonstrates the Adapter pattern for interface compatibility.</li>
 *   <li><b>MVC Pattern:</b> This class is part of the Model layer, containing no UI dependencies
 *       and providing a clean interface for Controllers to interact with.</li>
 * </ul>
 * 
 * @author KeyChord
 */
public class PianoModel {
    private final Recorder recorder;
    private final Player player;
    private final MidiSoundManager midiSoundManager;
    private final ChordManager chordManager;
    private final RecordingManager recordingManager;
    
    /**
     * Creates a new PianoModel and initializes all components.
     * 
     * @throws MidiUnavailableException if MIDI system is unavailable
     */
    public PianoModel() throws javax.sound.midi.MidiUnavailableException {
        this.recorder = new Recorder();
        this.player = new Player();
        this.midiSoundManager = MidiSoundManager.getInstance(); // Use singleton
        this.chordManager = new ChordManager();
        this.recordingManager = new RecordingManager();
    }
    
    /**
     * Gets the RecordingManager.
     * 
     * @return the RecordingManager instance
     */
    public RecordingManager getRecordingManager() {
        return recordingManager;
    }
    
    /**
     * Gets the Recorder.
     * 
     * @return the Recorder instance
     */
    public Recorder getRecorder() {
        return recorder;
    }
    
    /**
     * Starts recording. Creates a new recording session.
     */
    public void startRecording() {
        recorder.startRecording();
        recordingManager.startRecording();
    }
    
    /**
     * Stops recording and auto-saves with default name.
     * 
     * @return the saved Recording
     */
    public Recording stopRecording() {
        recorder.stopRecording();
        return recordingManager.stopRecording();
    }
    
    /**
     * Stops recording and saves with the specified name.
     * 
     * @param name the name to save the recording as
     * @return the saved Recording
     */
    public Recording stopRecording(String name) {
        recorder.stopRecording();
        return recordingManager.stopRecording(name);
    }
    
    /**
     * Checks if currently recording.
     * 
     * @return true if recording, false otherwise
     */
    public boolean isRecording() {
        return recorder.isRecording();
    }
    
    /**
     * Plays a single note (note on).
     * 
     * @param midiNote the MIDI note number
     */
    public void playNote(int midiNote) {
        midiSoundManager.playNote(midiNote);
        
        // Record if recording
        if (recorder.isRecording()) {
            NoteEvent event = recorder.recordNoteOn(midiNote);
            if (event != null) {
                Recording current = recordingManager.getCurrentRecording();
                if (current != null) {
                    current.addNoteEvent(event);
                }
            }
        }
    }
    
    /**
     * Stops a single note (note off).
     * 
     * @param midiNote the MIDI note number
     */
    public void stopNote(int midiNote) {
        midiSoundManager.stopNote(midiNote);
        
        // Record if recording
        if (recorder.isRecording()) {
            NoteEvent event = recorder.recordNoteOff(midiNote);
            if (event != null) {
                Recording current = recordingManager.getCurrentRecording();
                if (current != null) {
                    current.addNoteEvent(event);
                }
            }
        }
    }
    
    /**
     * Plays a note without recording (for playback).
     * 
     * @param midiNote the MIDI note number
     */
    public void playNoteWithoutRecording(int midiNote) {
        midiSoundManager.playNote(midiNote);
    }
    
    /**
     * Stops a note without recording (for playback).
     * 
     * @param midiNote the MIDI note number
     */
    public void stopNoteWithoutRecording(int midiNote) {
        midiSoundManager.stopNote(midiNote);
    }
    
    /**
     * Plays a chord (all notes simultaneously).
     * 
     * @param rootNote the root MIDI note for the chord
     */
    public void playChord(int rootNote) {
        List<Integer> chordNotes = chordManager.generateChord(rootNote);
        
        // Play all chord notes with normal velocity (80-100)
        for (Integer note : chordNotes) {
            midiSoundManager.playNote(note, 90);
        }
        
        // Record if recording
        if (recorder.isRecording()) {
            List<NoteEvent> events = recorder.recordChordOn(chordNotes);
            if (!events.isEmpty()) {
                Recording current = recordingManager.getCurrentRecording();
                if (current != null) {
                    current.addNoteEvents(events);
                }
            }
        }
    }
    
    /**
     * Stops a chord (all notes off).
     * 
     * @param rootNote the root MIDI note for the chord
     */
    public void stopChord(int rootNote) {
        List<Integer> chordNotes = chordManager.generateChord(rootNote);
        
        // Stop all chord notes
        for (Integer note : chordNotes) {
            midiSoundManager.stopNote(note);
        }
        
        // Record if recording
        if (recorder.isRecording()) {
            List<NoteEvent> events = new ArrayList<>();
            for (Integer note : chordNotes) {
                NoteEvent event = recorder.recordNoteOff(note);
                if (event != null) {
                    events.add(event);
                }
            }
            if (!events.isEmpty()) {
                Recording current = recordingManager.getCurrentRecording();
                if (current != null) {
                    current.addNoteEvents(events);
                }
            }
        }
    }
    
    /**
     * Starts playback of a recording by name.
     * 
     * @param recordingName the name of the recording to play
     */
    public void startPlayback(String recordingName) {
        Recording recording = recordingManager.getRecording(recordingName);
        if (recording == null) {
            return;
        }
        startPlayback(recording);
    }
    
    /**
     * Starts playback of a Recording.
     * 
     * @param recording the Recording to play
     */
    public void startPlayback(Recording recording) {
        if (recording == null) {
            return;
        }
        player.startPlayback(new RecordingEventSource(recording), new Player.NotePlaybackHandler() {
            @Override
            public void onNoteOn(int midiNote) {
                midiSoundManager.playNote(midiNote);
            }
            
            @Override
            public void onNoteOff(int midiNote) {
                midiSoundManager.stopNote(midiNote);
            }
        });
    }
    
    /**
     * Starts playback with a custom handler.
     * 
     * @param recording the Recording to play
     * @param handler the custom playback handler
     */
    public void startPlaybackWithHandler(Recording recording, Player.NotePlaybackHandler handler) {
        if (recording == null) {
            return;
        }
        player.startPlayback(new RecordingEventSource(recording), handler);
    }
    
    /**
     * Adapter class to convert Recording to Player.EventSource interface.
     * 
     * <p><b>Design Principle: Adapter Pattern</b> - Adapts Recording to Player.EventSource
     * interface, allowing Player to work with Recordings without modifying either class.
     */
    private static class RecordingEventSource implements Player.EventSource {
        private final Recording recording;
        
        RecordingEventSource(Recording recording) {
            this.recording = recording;
        }
        
        @Override
        public List<NoteEvent> getEvents() {
            return recording.getEvents();
        }
    }
    
    /**
     * Stops playback.
     */
    public void stopPlayback() {
        player.stopPlayback();
        midiSoundManager.allNotesOff();
    }
    
    /**
     * Checks if currently playing.
     * 
     * @return true if playing, false otherwise
     */
    public boolean isPlaying() {
        return player.isPlaying();
    }
    
    /**
     * Clears the current recording (if recording is in progress).
     */
    public void clearCurrentRecording() {
        Recording current = recordingManager.getCurrentRecording();
        if (current != null) {
            current.clear();
        }
    }
    
    /**
     * Gets the ChordManager.
     * 
     * @return the ChordManager instance
     */
    public ChordManager getChordManager() {
        return chordManager;
    }
    
    /**
     * Closes the model and releases resources.
     */
    public void close() {
        stopPlayback();
        midiSoundManager.close();
    }
}

