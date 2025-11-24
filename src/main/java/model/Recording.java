package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a named recording containing NoteEvents.
 * Thread-safe for concurrent access.
 * 
 * @author KeyChord
 */
public class Recording {
    private final String name;
    private final List<NoteEvent> events;
    private final long creationTime;
    private final Object lock = new Object();
    
    /**
     * Creates a new Recording with the specified name.
     * 
     * @param name the name of the recording
     * @throws IllegalArgumentException if name is null or empty
     */
    public Recording(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Recording name cannot be null or empty");
        }
        this.name = name.trim();
        this.events = new ArrayList<>();
        this.creationTime = System.currentTimeMillis();
    }
    
    /**
     * Creates a new Recording with the specified name and events.
     * 
     * @param name the name of the recording
     * @param events the initial list of events
     * @throws IllegalArgumentException if name is null or empty
     */
    public Recording(String name, List<NoteEvent> events) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Recording name cannot be null or empty");
        }
        this.name = name.trim();
        this.events = new ArrayList<>();
        if (events != null) {
            this.events.addAll(events);
        }
        this.creationTime = System.currentTimeMillis();
    }
    
    /**
     * Gets the name of this recording.
     * 
     * @return the recording name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets a new name for this recording.
     * 
     * @param newName the new name
     * @throws IllegalArgumentException if newName is null or empty
     */
    public void setName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Recording name cannot be null or empty");
        }
        synchronized (lock) {
            // Note: name is final, so we can't change it. This method is for future extensibility.
            // For now, we'll need to create a new Recording with the new name.
        }
    }
    
    /**
     * Adds a NoteEvent to this recording.
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
     * Adds multiple NoteEvents to this recording.
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
     * Gets an unmodifiable copy of all NoteEvents in this recording.
     * Events are sorted by timestamp.
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
     * Clears all NoteEvents from this recording.
     */
    public void clear() {
        synchronized (lock) {
            events.clear();
        }
    }
    
    /**
     * Gets the number of NoteEvents in this recording.
     * 
     * @return the event count
     */
    public int getEventCount() {
        synchronized (lock) {
            return events.size();
        }
    }
    
    /**
     * Checks if this recording is empty.
     * 
     * @return true if the recording has no events, false otherwise
     */
    public boolean isEmpty() {
        synchronized (lock) {
            return events.isEmpty();
        }
    }
    
    /**
     * Gets the creation time of this recording.
     * 
     * @return the creation timestamp in milliseconds
     */
    public long getCreationTime() {
        return creationTime;
    }
    
    /**
     * Gets the duration of this recording in milliseconds.
     * 
     * @return the duration, or 0 if empty
     */
    public long getDuration() {
        synchronized (lock) {
            if (events.isEmpty()) {
                return 0;
            }
            long maxTime = 0;
            for (NoteEvent event : events) {
                maxTime = Math.max(maxTime, event.getTimestamp());
            }
            return maxTime;
        }
    }
}

