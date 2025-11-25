package model;

import java.util.List;

/**
 * Plays back NoteEvents from an EventSource with proper timing.
 * Handles scheduling of note on/off events for accurate playback.
 * * @author KeyChord
 */
public class Player {
    private boolean isPlaying;
    private final Object lock = new Object();

    /**
     * Interface for handling note playback events.
     */
    public interface NotePlaybackHandler {
        /**
         * Called when a note should be turned on.
         * * @param midiNote the MIDI note number
         */
        void onNoteOn(int midiNote);

        /**
         * Called when a note should be turned off.
         * * @param midiNote the MIDI note number
         */
        void onNoteOff(int midiNote);
    }

    /**
     * Creates a new Player in the stopped state.
     */
    public Player() {
        this.isPlaying = false;
    }

    /**
     * Interface for objects that provide NoteEvents for playback.
     */
    public interface EventSource {
        List<NoteEvent> getEvents();
    }

    /**
     * Starts playback from an EventSource (Recording, etc.).
     * * @param source the EventSource to play
     * @param handler the handler for note on/off events
     * @throws IllegalArgumentException if source or handler is null
     */
    public void startPlayback(EventSource source, NotePlaybackHandler handler) {
        if (source == null) {
            throw new IllegalArgumentException("EventSource cannot be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }

        synchronized (lock) {
            if (isPlaying) {
                return; // Already playing
            }
            isPlaying = true;
        }

        // Playback happens in a separate thread
        new Thread(() -> playEvents(source.getEvents(), handler)).start();
    }

    /**
     * Stops playback.
     */
    public void stopPlayback() {
        synchronized (lock) {
            isPlaying = false;
        }
    }

    /**
     * Checks if currently playing.
     * * @return true if playing, false otherwise
     */
    public boolean isPlaying() {
        synchronized (lock) {
            return isPlaying;
        }
    }

    /**
     * Plays events by scheduling note events according to their timestamps.
     * * @param events the list of NoteEvents to play
     * @param handler the handler for note events
     */
    private void playEvents(List<NoteEvent> events, NotePlaybackHandler handler) {
        if (events == null || events.isEmpty()) {
            synchronized (lock) {
                isPlaying = false;
            }
            return;
        }

        long baseTime = System.currentTimeMillis();

        for (NoteEvent event : events) {
            // Check if playback was stopped
            synchronized (lock) {
                if (!isPlaying) {
                    return;
                }
            }

            long eventTime = baseTime + event.getTimestamp();
            long currentTime = System.currentTimeMillis();
            long delay = eventTime - currentTime;

            // Wait until it's time to play this event
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    synchronized (lock) {
                        isPlaying = false;
                    }
                    return;
                }
            }

            // Play the event
            if (event.isNoteOn()) {
                handler.onNoteOn(event.getMidiNote());
            } else {
                handler.onNoteOff(event.getMidiNote());
            }
        }

        synchronized (lock) {
            isPlaying = false;
        }
    }
}