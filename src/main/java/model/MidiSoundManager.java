package model;

import javax.sound.midi.*;

/**
 * Manages MIDI sound playback for piano notes.
 * Uses a singleton pattern with SoftSynthesizer for clean piano sound.
 * Thread-safe for concurrent note operations.
 * 
 * @author KeyChord
 */
public class MidiSoundManager {
    private static MidiSoundManager instance;
    private static final Object instanceLock = new Object();
    
    private Synthesizer synthesizer;
    private MidiChannel pianoChannel;
    private static final int PIANO_CHANNEL = 0; // Channel 0, NEVER Channel 9 (percussion)
    private static final int PIANO_PROGRAM = 0; // GM Acoustic Grand Piano
    private static final int DEFAULT_VELOCITY = 90; // Normal piano attack (80-100)
    private final Object lock = new Object();
    private boolean initialized = false;
    
    /**
     * Private constructor for singleton pattern.
     * 
     * @throws MidiUnavailableException if MIDI system is unavailable
     */
    private MidiSoundManager() throws MidiUnavailableException {
        initializeSynthesizer();
    }
    
    /**
     * Gets the singleton instance of MidiSoundManager.
     * 
     * @return the MidiSoundManager instance
     * @throws MidiUnavailableException if MIDI system is unavailable
     */
    public static MidiSoundManager getInstance() throws MidiUnavailableException {
        if (instance == null) {
            synchronized (instanceLock) {
                if (instance == null) {
                    instance = new MidiSoundManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Initializes the MIDI synthesizer with SoftSynthesizer if available.
     * 
     * @throws MidiUnavailableException if MIDI system is unavailable
     */
    private void initializeSynthesizer() throws MidiUnavailableException {
        synchronized (lock) {
            if (initialized) {
                return; // Already initialized
            }
            
            try {
                // Try to get SoftSynthesizer first (best quality)
                Synthesizer synth = null;
                try {
                    // Use reflection to access SoftSynthesizer if available
                    Class<?> softSynthClass = Class.forName("com.sun.media.sound.SoftSynthesizer");
                    synth = (Synthesizer) softSynthClass.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    // Fall back to default synthesizer
                    synth = MidiSystem.getSynthesizer();
                }
                
                synthesizer = synth;
                
                // Open synthesizer only once
                if (!synthesizer.isOpen()) {
                    synthesizer.open();
                }
                
                // Load default soundbank if available
                try {
                    Soundbank defaultSoundbank = synthesizer.getDefaultSoundbank();
                    if (defaultSoundbank != null) {
                        // Check if soundbank is already loaded by checking instruments
                        boolean loaded = false;
                        try {
                            Instrument[] loadedInstruments = synthesizer.getLoadedInstruments();
                            if (loadedInstruments != null && loadedInstruments.length > 0) {
                                loaded = true;
                            }
                        } catch (Exception e) {
                            // Method not available, try loading anyway
                        }
                        
                        if (!loaded) {
                            synthesizer.loadAllInstruments(defaultSoundbank);
                        }
                    }
                } catch (Exception e) {
                    // Soundbank loading failed, continue with default instruments
                    System.err.println("Warning: Could not load default soundbank: " + e.getMessage());
                }
                
                // Get channel 0 (NEVER channel 9 - that's percussion)
                MidiChannel[] channels = synthesizer.getChannels();
                if (channels == null || channels.length <= PIANO_CHANNEL) {
                    throw new MidiUnavailableException("Channel " + PIANO_CHANNEL + " not available");
                }
                
                pianoChannel = channels[PIANO_CHANNEL];
                
                // Set to Acoustic Grand Piano (Program 0)
                pianoChannel.programChange(PIANO_PROGRAM);
                
                initialized = true;
            } catch (MidiUnavailableException e) {
                throw e;
            } catch (Exception e) {
                throw new MidiUnavailableException("Failed to initialize synthesizer: " + e.getMessage());
            }
        }
    }
    
    /**
     * Plays a MIDI note with the default velocity.
     * 
     * @param midiNote the MIDI note number (0-127)
     * @throws IllegalArgumentException if midiNote is out of range
     */
    public void playNote(int midiNote) {
        playNote(midiNote, DEFAULT_VELOCITY);
    }
    
    /**
     * Plays a MIDI note with specified velocity.
     * 
     * @param midiNote the MIDI note number (0-127)
     * @param velocity the velocity (0-127), typically 80-100 for normal piano attack
     * @throws IllegalArgumentException if midiNote or velocity is out of range
     */
    public void playNote(int midiNote, int velocity) {
        if (midiNote < 0 || midiNote > 127) {
            throw new IllegalArgumentException("MIDI note must be between 0 and 127");
        }
        if (velocity < 0 || velocity > 127) {
            throw new IllegalArgumentException("Velocity must be between 0 and 127");
        }
        
        synchronized (lock) {
            if (pianoChannel != null && initialized) {
                pianoChannel.noteOn(midiNote, velocity);
            }
        }
    }
    
    /**
     * Stops a MIDI note (note off).
     * 
     * @param midiNote the MIDI note number (0-127)
     * @throws IllegalArgumentException if midiNote is out of range
     */
    public void stopNote(int midiNote) {
        if (midiNote < 0 || midiNote > 127) {
            throw new IllegalArgumentException("MIDI note must be between 0 and 127");
        }
        
        synchronized (lock) {
            if (pianoChannel != null && initialized) {
                pianoChannel.noteOff(midiNote);
            }
        }
    }
    
    /**
     * Stops all currently playing notes.
     */
    public void allNotesOff() {
        synchronized (lock) {
            if (pianoChannel != null && initialized) {
                pianoChannel.allNotesOff();
            }
        }
    }
    
    /**
     * Gets the current program (instrument) on the piano channel.
     * 
     * @return the program number (should be 0 for Acoustic Grand Piano)
     */
    public int getProgram() {
        synchronized (lock) {
            if (pianoChannel != null && initialized) {
                return pianoChannel.getProgram();
            }
            return -1;
        }
    }
    
    /**
     * Gets the channel number being used (should always be 0).
     * 
     * @return the channel number
     */
    public int getChannel() {
        return PIANO_CHANNEL;
    }
    
    /**
     * Checks if the synthesizer is initialized and open.
     * 
     * @return true if initialized and open, false otherwise
     */
    public boolean isOpen() {
        synchronized (lock) {
            return initialized && synthesizer != null && synthesizer.isOpen();
        }
    }
    
    /**
     * Closes the MIDI synthesizer and releases resources.
     * Should only be called on application shutdown.
     */
    public void close() {
        synchronized (lock) {
            if (synthesizer != null && synthesizer.isOpen()) {
                allNotesOff();
                synthesizer.close();
                initialized = false;
            }
        }
    }
    
    /**
     * Resets the singleton instance (for testing only).
     */
    static void resetInstance() {
        synchronized (instanceLock) {
            if (instance != null) {
                instance.close();
                instance = null;
            }
        }
    }
}
