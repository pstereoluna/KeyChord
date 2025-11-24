package model;

/**
 * Represents a single note event with a MIDI note number and timestamp.
 * Used for recording and playback of piano notes.
 * 
 * @author KeyChord
 */
public class NoteEvent {
    private final int midiNote;
    private final long timestamp;
    private final boolean isNoteOn;
    
    /**
     * Creates a new NoteEvent.
     * 
     * @param midiNote the MIDI note number (0-127)
     * @param timestamp the timestamp in milliseconds
     * @param isNoteOn true for note on, false for note off
     */
    public NoteEvent(int midiNote, long timestamp, boolean isNoteOn) {
        if (midiNote < 0 || midiNote > 127) {
            throw new IllegalArgumentException("MIDI note must be between 0 and 127");
        }
        if (timestamp < 0) {
            throw new IllegalArgumentException("Timestamp cannot be negative");
        }
        this.midiNote = midiNote;
        this.timestamp = timestamp;
        this.isNoteOn = isNoteOn;
    }
    
    /**
     * Gets the MIDI note number.
     * 
     * @return the MIDI note number (0-127)
     */
    public int getMidiNote() {
        return midiNote;
    }
    
    /**
     * Gets the timestamp of this event.
     * 
     * @return the timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Checks if this is a note-on event.
     * 
     * @return true if note on, false if note off
     */
    public boolean isNoteOn() {
        return isNoteOn;
    }
    
    /**
     * Checks if this is a note-off event.
     * 
     * @return true if note off, false if note on
     */
    public boolean isNoteOff() {
        return !isNoteOn;
    }
    
    @Override
    public String toString() {
        return String.format("NoteEvent{note=%d, time=%d, %s}", 
            midiNote, timestamp, isNoteOn ? "ON" : "OFF");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NoteEvent noteEvent = (NoteEvent) obj;
        return midiNote == noteEvent.midiNote &&
               timestamp == noteEvent.timestamp &&
               isNoteOn == noteEvent.isNoteOn;
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(midiNote, timestamp, isNoteOn);
    }
}

