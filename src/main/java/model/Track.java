package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single track that stores NoteEvents for recording and playback.
 * Thread-safe for concurrent recording and playback operations.
 * 
 * @author KeyChord
 */
public class Track implements Player.EventSource {
    private final int trackNumber;
    private final List<NoteEvent> events;
    private final Object lock = new Object();
    
    /**
     * Creates a new track with the specified track number.
     * 
     * @param trackNumber the track number (1-9)
     * @throws IllegalArgumentException if trackNumber is out of range
     */
    public Track(int trackNumber) {
        if (trackNumber < 1 || trackNumber > 9) {
            throw new IllegalArgumentException("Track number must be between 1 and 9");
        }
        this.trackNumber = trackNumber;
        this.events = new ArrayList<>();
    }
    
    /**
     * Adds a NoteEvent to this track.
     * 
     * @param event the NoteEvent to add
     * @throws IllegalArgumentException if event is null
     */
    public void addNoteEvent(NoteEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("NoteEvent cannot be null");
        }
        synchronized (lock) {
            events.add(event);
        }
    }
    
    /**
     * Adds multiple NoteEvents to this track.
     * Useful for adding chord notes with the same timestamp.
     * 
     * @param events the NoteEvents to add
     * @throws IllegalArgumentException if events is null or contains null
     */
    public void addNoteEvents(List<NoteEvent> events) {
        if (events == null) {
            throw new IllegalArgumentException("Events list cannot be null");
        }
        synchronized (lock) {
            for (NoteEvent event : events) {
                if (event == null) {
                    throw new IllegalArgumentException("Events list cannot contain null");
                }
                this.events.add(event);
            }
        }
    }
    
    /**
     * Clears all NoteEvents from this track.
     */
    public void clear() {
        synchronized (lock) {
            events.clear();
        }
    }
    
    /**
     * Gets an unmodifiable copy of all NoteEvents in this track.
     * 
     * @return a list of NoteEvents sorted by timestamp
     */
    public List<NoteEvent> getEvents() {
        synchronized (lock) {
            List<NoteEvent> copy = new ArrayList<>(events);
            // Sort by timestamp to ensure chronological order
            copy.sort((e1, e2) -> {
                int timeCompare = Long.compare(e1.getTimestamp(), e2.getTimestamp());
                if (timeCompare != 0) {
                    return timeCompare;
                }
                // If same timestamp, sort by note number
                return Integer.compare(e1.getMidiNote(), e2.getMidiNote());
            });
            return Collections.unmodifiableList(copy);
        }
    }
    
    /**
     * Gets the track number.
     * 
     * @return the track number (1-9)
     */
    public int getTrackNumber() {
        return trackNumber;
    }
    
    /**
     * Gets the number of NoteEvents in this track.
     * 
     * @return the event count
     */
    public int getEventCount() {
        synchronized (lock) {
            return events.size();
        }
    }
    
    /**
     * Checks if this track is empty.
     * 
     * @return true if the track has no events, false otherwise
     */
    public boolean isEmpty() {
        synchronized (lock) {
            return events.isEmpty();
        }
    }
}

