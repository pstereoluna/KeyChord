package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import javax.sound.midi.MidiUnavailableException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Concurrency tests for thread-safe classes.
 * Tests MidiSoundManager, Recorder, and RecordingManager for thread safety.
 * 
 * @author KeyChord
 */
class ConcurrencyTest {
    private MidiSoundManager midiManager;
    private Recorder recorder;
    private RecordingManager recordingManager;
    
    @BeforeEach
    void setUp() throws MidiUnavailableException {
        midiManager = MidiSoundManager.getInstance();
        recorder = new Recorder();
        recordingManager = new RecordingManager();
    }
    
    @Test
    @RepeatedTest(10)
    @Timeout(10)
    void testConcurrentNotePlayback() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(100);
        AtomicInteger errors = new AtomicInteger(0);
        
        for (int i = 0; i < 100; i++) {
            final int note = 60 + (i % 12); // Cycle through 12 notes
            executor.submit(() -> {
                try {
                    midiManager.playNote(note);
                    Thread.sleep(1); // Small delay
                    midiManager.stopNote(note);
                } catch (Exception e) {
                    errors.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "All tasks should complete");
        assertEquals(0, errors.get(), "No errors should occur during concurrent access");
        
        executor.shutdown();
    }
    
    @Test
    @RepeatedTest(10)
    @Timeout(10)
    void testConcurrentPlayAndStop() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(200);
        AtomicInteger errors = new AtomicInteger(0);
        
        // Mix play and stop operations
        for (int i = 0; i < 100; i++) {
            final int note = 60 + (i % 12);
            executor.submit(() -> {
                try {
                    midiManager.playNote(note);
                } catch (Exception e) {
                    errors.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
            
            executor.submit(() -> {
                try {
                    midiManager.stopNote(note);
                } catch (Exception e) {
                    errors.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "All tasks should complete");
        assertEquals(0, errors.get(), "No errors should occur during interleaved operations");
        
        executor.shutdown();
    }
    
    @Test
    @RepeatedTest(10)
    @Timeout(10)
    void testConcurrentRecording() throws InterruptedException {
        recorder.startRecording();
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(100);
        AtomicInteger eventCount = new AtomicInteger(0);
        AtomicInteger errors = new AtomicInteger(0);
        
        // Multiple threads calling recordNoteOn simultaneously
        for (int i = 0; i < 100; i++) {
            final int note = 60 + (i % 12);
            executor.submit(() -> {
                try {
                    NoteEvent event = recorder.recordNoteOn(note);
                    if (event != null) {
                        eventCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errors.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "All tasks should complete");
        assertEquals(0, errors.get(), "No errors should occur during concurrent recording");
        assertEquals(100, eventCount.get(), "All events should be recorded");
        
        recorder.stopRecording();
        executor.shutdown();
    }
    
    @Test
    @RepeatedTest(10)
    @Timeout(10)
    void testConcurrentRecordingTimestamps() throws InterruptedException {
        recorder.startRecording();
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(50);
        List<Long> timestamps = new CopyOnWriteArrayList<>();
        AtomicInteger errors = new AtomicInteger(0);
        
        // Record events from multiple threads
        for (int i = 0; i < 50; i++) {
            final int note = 60 + (i % 12);
            executor.submit(() -> {
                try {
                    NoteEvent event = recorder.recordNoteOn(note);
                    if (event != null) {
                        timestamps.add(event.getTimestamp());
                    }
                } catch (Exception e) {
                    errors.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "All tasks should complete");
        assertEquals(0, errors.get(), "No errors should occur");
        assertEquals(50, timestamps.size(), "All events should be recorded");
        
        // Verify timestamps are reasonable (not negative, generally increasing)
        for (Long timestamp : timestamps) {
            assertTrue(timestamp >= 0, "Timestamp should not be negative: " + timestamp);
        }
        
        // Verify timestamps are within reasonable range (all recorded within test duration)
        long maxTimestamp = timestamps.stream().mapToLong(Long::longValue).max().orElse(0);
        assertTrue(maxTimestamp < 10000, "Max timestamp should be reasonable: " + maxTimestamp);
        
        recorder.stopRecording();
        executor.shutdown();
    }
    
    @Test
    @RepeatedTest(10)
    @Timeout(10)
    void testConcurrentRecordingManager() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch saveLatch = new CountDownLatch(50);
        CountDownLatch listLatch = new CountDownLatch(50);
        AtomicInteger errors = new AtomicInteger(0);
        
        // Multiple threads saving different recordings
        for (int i = 0; i < 50; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    Recording recording = new Recording("Concurrent Recording " + index);
                    recording.addNoteEvent(new NoteEvent(60, 0, true));
                    recording.addNoteEvent(new NoteEvent(60, 100, false));
                    recordingManager.saveRecording("Concurrent Recording " + index, recording);
                } catch (Exception e) {
                    errors.incrementAndGet();
                } finally {
                    saveLatch.countDown();
                }
            });
        }
        
        // One thread listing while another is saving
        for (int i = 0; i < 50; i++) {
            executor.submit(() -> {
                try {
                    List<String> recordings = recordingManager.listRecordings();
                    assertNotNull(recordings, "List should not be null");
                } catch (Exception e) {
                    errors.incrementAndGet();
                } finally {
                    listLatch.countDown();
                }
            });
        }
        
        assertTrue(saveLatch.await(5, TimeUnit.SECONDS), "All save operations should complete");
        assertTrue(listLatch.await(5, TimeUnit.SECONDS), "All list operations should complete");
        assertEquals(0, errors.get(), "No ConcurrentModificationException should occur");
        
        executor.shutdown();
    }
    
    @Test
    @RepeatedTest(10)
    @Timeout(10)
    void testConcurrentRecordingAndPlayback() throws InterruptedException {
        Recording recording = new Recording("Test");
        recording.addNoteEvent(new NoteEvent(60, 0, true));
        recording.addNoteEvent(new NoteEvent(60, 100, false));
        
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(20);
        AtomicInteger errors = new AtomicInteger(0);
        
        // Mix recording operations with playback operations
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    recorder.startRecording();
                    Thread.sleep(10);
                    recorder.stopRecording();
                } catch (Exception e) {
                    errors.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
            
            executor.submit(() -> {
                try {
                    Player player = new Player();
                    player.startPlayback(recording, new Player.NotePlaybackHandler() {
                        @Override
                        public void onNoteOn(int midiNote) {
                            // Do nothing
                        }
                        
                        @Override
                        public void onNoteOff(int midiNote) {
                            // Do nothing
                        }
                    });
                    Thread.sleep(50);
                    player.stopPlayback();
                } catch (Exception e) {
                    errors.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "All tasks should complete");
        assertEquals(0, errors.get(), "No errors should occur during concurrent operations");
        
        executor.shutdown();
    }
}

