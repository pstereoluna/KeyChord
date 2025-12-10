# KeyChord - Virtual Piano Application

A Java Swing application for playing, recording, and exporting piano music.

## Features

### 🎹 Virtual Piano
- **25-key piano keyboard** (C3 to C5 range)
- **Realistic proportions** with white and black keys
- **Visual key highlighting** when pressed
- **Mouse support** for playing notes

### 🎵 Chord Generation
- **8 chord types**: Major, Minor, Diminished, Augmented, Major 7th, Minor 7th, Sus2, Sus4
- **Automatic chord generation** from single key press
- **Chord mode selector** in UI
- **Polyphonic playback** (multiple simultaneous notes)

### 🎙️ Recording System
- **One-click recording**
- **Auto-save** with default names
- **Named recordings** stored in memory
- **Chord recording** support (multiple notes with same timestamp)

### ▶️ Playback
- **Visual synchronization** (keys highlight during playback)
- **Thread-safe playback** with proper timing
- **Play selected recording** from panel
- **Play most recent** with Enter key

### 💾 Recording Management
- **Scrollable list** of all recordings
- **Rename recordings**
- **Delete recordings** (with confirmation)
- **Export to MIDI** (.mid file format)

### 🔊 Audio
- **High-quality MIDI synthesis** using Java's SoftSynthesizer
- **Acoustic Grand Piano** sound (GM Program 0)
- **Polyphonic playback** (multiple simultaneous notes)
- **Proper velocity control** (80-100 for normal piano attack)

## Installation

### Prerequisites
- Java 23 or higher
- Maven 3.6+ (for building)

### Build from Source
```bash
git clone <repository-url>
cd KeyChord
mvn clean install
```

### Run
```bash
mvn exec:java -Dexec.mainClass="KeyChordApp"
```

Or run the JAR:
```bash
java -jar target/KeyChord-1.0-SNAPSHOT.jar
```

## How to Use

### Playing Notes
- **Mouse**: Click on keys to play
- **Keyboard**: Use QWERTY keyboard mapping
  - **Lower octave (C3-C4)**: 
    - White keys: `q`, `w`, `e`, `r`, `t`, `y`, `u`, `i`
    - Black keys: `2`, `3`, `5`, `6`, `7`
  - **Upper octave (C4-C5)**:
    - White keys: `c`, `v`, `b`, `n`, `m`, `,`, `.`, `/`
    - Black keys: `f`, `g`, `j`, `k`, `l`

### Recording
1. Press **Space** or click **Record** button to start recording
2. Play notes/chords
3. Press **Enter** or click **Record** again to stop
4. Recording is auto-saved with default name

### Playing Recordings
1. Select a recording from the **Recording Panel** (right side)
2. Click **Play** button or press **Enter** to play most recent
3. Keys will highlight during playback

### Chord Modes
1. Select chord mode from **Chord Mode** dropdown
2. Press any key to play that chord type
3. Available modes: Single Note, Major, Minor, 7th, Dim, Sus2, Sus4

### Exporting to MIDI
1. Select a recording from the list
2. Click **Export MIDI** button
3. Choose save location
4. Recording is exported as Standard MIDI File (.mid)

### Managing Recordings
- **Rename**: Select recording → Click **Rename** → Enter new name
- **Delete**: Select recording → Click **Delete** → Confirm
- **Play**: Select recording → Click **Play**

## Architecture

KeyChord follows the **MVC (Model-View-Controller)** architectural pattern:

### Model Layer
- **PianoModel**: Central coordinator
- **ChordManager**: Chord generation logic
- **RecordingManager**: Recording storage and management
- **Recorder**: Recording state and timestamps
- **Player**: Playback timing
- **MidiSoundManager**: Audio output (singleton)

### View Layer
- **MainWindow**: Main application window
- **PianoView**: Main container panel
- **PianoKeyboardPanel**: Keyboard display
- **ControlPanelView**: Control buttons
- **RecordingPanel**: Recording list

### Controller Layer
- **PianoController**: Keyboard/mouse input
- **RecordingController**: Recording control
- **PlaybackController**: Playback control
- **RecordingPanelController**: Recording panel interactions


## Project Structure

```
KeyChord/
├── src/
│   ├── main/java/
│   │   ├── KeyChordApp.java          # Entry point
│   │   ├── model/                     # Business logic
│   │   ├── view/                      # UI components
│   │   └── controller/                # Controllers
│   └── test/java/                     # Unit tests
├── pom.xml                            # Maven configuration
└── README.md                          # This file
```

## Testing

Run all tests:
```bash
mvn test
```

Test coverage is comprehensive:
- Model layer: 100% coverage
- Controller layer: 100% coverage (with mocks)
- View layer: Basic component tests

## Known Issues

1. **Recordings are not persisted** - Lost on app restart
2. **Recording.setName() doesn't work** - Name is immutable
3. **No MIDI input device support** - Keyboard/mouse only

## Future Improvements

- [ ] Persist recordings to disk
- [ ] MIDI input device support
- [ ] More chord types (9th, 11th, 13th)
- [ ] Custom chord builder
- [ ] Multiple instrument support
- [ ] Effects (reverb, chorus)
- [ ] Metronome
- [ ] Tempo control
- [ ] Sheet music display
- [ ] Undo/redo
- [ ] Theme support

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Write tests for new features
4. Submit a pull request


## Credits

Developed as a Java Swing application demonstrating:
- MVC architecture
- MIDI audio programming
- Thread-safe design
- Comprehensive unit testing
- Clean code principles

## References

### Core Technologies
- [1] Java MIDI API - https://docs.oracle.com/javase/8/docs/api/javax/sound/midi/
- [2] Java Swing Tutorial - https://docs.oracle.com/javase/tutorial/uiswing/
- [3] Java Concurrency - https://docs.oracle.com/javase/tutorial/essential/concurrency/

### Testing Frameworks
- [4] JUnit 5 User Guide - https://junit.org/junit5/docs/current/user-guide/
- [5] Mockito Framework - https://site.mockito.org/

### Music & MIDI
- [6] MIDI Specification - https://midi.org/specifications
- [7] Music Theory (Chord Intervals) - https://www.musictheory.net/lessons/40

### Design Patterns
- [8] Design Patterns: Elements of Reusable Object-Oriented Software (Gamma et al., 1994)

## Support

For issues or questions, please open an issue on GitHub.

