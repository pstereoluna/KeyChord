package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Player.
 */
class PlayerTest {
    private Player player;
    private TestEventSource testSource;

    // A simple implementation of EventSource for testing purposes
    // Replaces the legacy Track class
    private static class TestEventSource implements Player.EventSource {
        private final List<NoteEvent> events = new ArrayList<>();

        @Override
        public List<NoteEvent> getEvents() {
            return events;
        }

        public void addNoteEvent(NoteEvent event) {
            events.add(event);
        }
    }

    @BeforeEach
    void setUp() {
        player = new Player();
        testSource = new TestEventSource();
    }

    @Test
    void testInitialState() {
        assertFalse(player.isPlaying(), "Should not be playing initially");
    }

    @Test
    void testStartPlaybackWithNullSource() {
        assertThrows(IllegalArgumentException.class, () -> {
            player.startPlayback(null, new TestHandler());
        }, "Should throw exception for null source");
    }

    @Test
    void testStartPlaybackWithNullHandler() {
        assertThrows(IllegalArgumentException.class, () -> {
            player.startPlayback(testSource, null);
        }, "Should throw exception for null handler");
    }

    @Test
    void testPlaybackOrderOfEvents() throws InterruptedException {
        // Add events with different timestamps
        testSource.addNoteEvent(new NoteEvent(60, 0, true));
        testSource.addNoteEvent(new NoteEvent(64, 100, true));
        testSource.addNoteEvent(new NoteEvent(67, 200, true));

        List<Integer> playedNotes = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(3);

        player.startPlayback(testSource, new Player.NotePlaybackHandler() {
            @Override
            public void onNoteOn(int midiNote) {
                playedNotes.add(midiNote);
                latch.countDown();
            }

            @Override
            public void onNoteOff(int midiNote) {
                // Not used in this test
            }
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS), "All events should be played");
        assertEquals(3, playedNotes.size(), "Should play 3 notes");
        assertEquals(60, playedNotes.get(0), "First note should be 60");
        assertEquals(64, playedNotes.get(1), "Second note should be 64");
        assertEquals(67, playedNotes.get(2), "Third note should be 67");
    }

    @Test
    void testMultiNotePlaybackSequences() throws InterruptedException {
        // Add note on and note off events
        testSource.addNoteEvent(new NoteEvent(60, 0, true));
        testSource.addNoteEvent(new NoteEvent(60, 100, false));
        testSource.addNoteEvent(new NoteEvent(64, 200, true));

        List<String> events = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(3);

        player.startPlayback(testSource, new Player.NotePlaybackHandler() {
            @Override
            public void onNoteOn(int midiNote) {
                events.add("ON:" + midiNote);
                latch.countDown();
            }

            @Override
            public void onNoteOff(int midiNote) {
                events.add("OFF:" + midiNote);
                latch.countDown();
            }
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS), "All events should be played");
        assertEquals(3, events.size(), "Should have 3 events");
        assertTrue(events.contains("ON:60"), "Should contain note on 60");
        assertTrue(events.contains("OFF:60"), "Should contain note off 60");
        assertTrue(events.contains("ON:64"), "Should contain note on 64");
    }

    @Test
    void testStopPlayback() throws InterruptedException {
        // Add many events
        for (int i = 0; i < 10; i++) {
            testSource.addNoteEvent(new NoteEvent(60 + i, i * 50, true));
        }

        List<Integer> playedNotes = new ArrayList<>();

        player.startPlayback(testSource, new Player.NotePlaybackHandler() {
            @Override
            public void onNoteOn(int midiNote) {
                playedNotes.add(midiNote);
            }

            @Override
            public void onNoteOff(int midiNote) {
                // Not used
            }
        });

        Thread.sleep(100);
        player.stopPlayback();
        Thread.sleep(200);

        // Should have played some notes but not all
        assertTrue(playedNotes.size() < 10, "Should not play all notes after stop");
    }

    @Test
    void testEmptyTrack() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        player.startPlayback(testSource, new TestHandler());

        Thread.sleep(100);
        assertFalse(player.isPlaying(), "Should not be playing empty track");
        latch.countDown();

        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }
    
    @Test
    @DisplayName("Playback with 0ms delay between events")
    void testPlaybackWithZeroDelay() throws InterruptedException {
        // Add events with 0ms delay
        testSource.addNoteEvent(new NoteEvent(60, 0, true));
        testSource.addNoteEvent(new NoteEvent(64, 0, true));
        testSource.addNoteEvent(new NoteEvent(67, 0, true));
        
        List<Integer> playedNotes = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(3);
        
        player.startPlayback(testSource, new Player.NotePlaybackHandler() {
            @Override
            public void onNoteOn(int midiNote) {
                playedNotes.add(midiNote);
                latch.countDown();
            }
            
            @Override
            public void onNoteOff(int midiNote) {
                // Not used
            }
        });
        
        assertTrue(latch.await(1, TimeUnit.SECONDS), "All events should be played");
        assertEquals(3, playedNotes.size(), "Should play all 3 notes");
    }
    
    @Test
    @DisplayName("Playback interrupted mid-stream")
    void testPlaybackInterrupted() throws InterruptedException {
        // Add many events with delays
        for (int i = 0; i < 20; i++) {
            testSource.addNoteEvent(new NoteEvent(60 + i, i * 100, true));
        }
        
        List<Integer> playedNotes = new ArrayList<>();
        
        player.startPlayback(testSource, new Player.NotePlaybackHandler() {
            @Override
            public void onNoteOn(int midiNote) {
                playedNotes.add(midiNote);
            }
            
            @Override
            public void onNoteOff(int midiNote) {
                // Not used
            }
        });
        
        Thread.sleep(300); // Let some events play
        player.stopPlayback();
        Thread.sleep(500); // Wait for stop to take effect
        
        assertFalse(player.isPlaying(), "Should not be playing after stop");
        assertTrue(playedNotes.size() < 20, "Should not play all notes");
    }
    
    @Test
    @DisplayName("Very long delays between events")
    void testPlaybackWithLongDelays() throws InterruptedException {
        testSource.addNoteEvent(new NoteEvent(60, 0, true));
        testSource.addNoteEvent(new NoteEvent(64, 1000, true)); // 1 second delay
        testSource.addNoteEvent(new NoteEvent(67, 2000, true)); // 2 second delay
        
        List<Integer> playedNotes = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(3);
        
        player.startPlayback(testSource, new Player.NotePlaybackHandler() {
            @Override
            public void onNoteOn(int midiNote) {
                playedNotes.add(midiNote);
                latch.countDown();
            }
            
            @Override
            public void onNoteOff(int midiNote) {
                // Not used
            }
        });
        
        assertTrue(latch.await(3, TimeUnit.SECONDS), "All events should be played with long delays");
        assertEquals(3, playedNotes.size(), "Should play all 3 notes");
    }

    // Helper class for testing
    private static class TestHandler implements Player.NotePlaybackHandler {
        @Override
        public void onNoteOn(int midiNote) {
            // Empty implementation for testing
        }

        @Override
        public void onNoteOff(int midiNote) {
            // Empty implementation for testing
        }
    }
}