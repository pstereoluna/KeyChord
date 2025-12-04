# KeyChord Final Project Presentation
## Virtual Piano with Recording and Playback

---

## SLIDE 1: Title Page

**KeyChord: A Virtual Piano Application**
*Demonstrating MVC Architecture, SOLID Principles, and Design Patterns*

Jiaxin Jia, Xiaoyuan Lu  
CS5004 Final Project

---

**Speaker Notes:**
"Today we're going to show you KeyChord, a virtual piano app we built. We'll walk you through how we used MVC architecture, SOLID principles, and some design patterns, and show you how they work together in the code."

---

## SLIDE 2: Motivation

**Why Build a Virtual Piano?**

- **Learning OOD Principles**: Apply MVC, SOLID, and design patterns in practice
- **Real-World Application**: Create something functional and useful
- **Testing Challenge**: Test musical software with timing and audio
- **Separation of Concerns**: Strict MVC with zero cross-layer dependencies

**Key Goal**: Build maintainable, testable, extensible code

---

**Speaker Notes:**
"We wanted to build something that demonstrates professional software engineering practices. A virtual piano is perfect because it has clear separation between audio logic, UI, and user input - ideal for MVC architecture."

---

## SLIDE 3: Features Overview

**KeyChord Features**

- ✅ **Play Notes**: Keyboard or mouse input
- ✅ **8 Chord Types**: Major, Minor, Diminished, Augmented, Major 7th, Minor 7th, Sus2, Sus4
- ✅ **Recording**: Capture performances with timestamps
- ✅ **Playback**: Visual key synchronization
- ✅ **Multiple Recordings**: Save, rename, delete
- ✅ **MIDI Export**: Standard MIDI file format

---

**Speaker Notes:**
"KeyChord is a fully functional virtual piano. You can play individual notes or chords, record your performance, play it back with visual feedback, and export to MIDI files for use in other software."

---

## SLIDE 4: Architecture Overview

**MVC Architecture**

```
┌─────────────────────────────────────────┐
│         KeyChordApp (Entry Point)       │
└─────────────────────────────────────────┘
                    │
    ┌───────────────┼───────────────┐
    │               │               │
    ▼               ▼               ▼
┌─────────┐    ┌─────────┐    ┌─────────┐
│  MODEL  │◄───│  VIEW   │───►│CONTROLLER│
│ (Logic) │    │  (UI)   │    │(Mediator)│
└─────────┘    └─────────┘    └─────────┘
    │               │               │
    │               │               │
    └───────────────┴───────────────┘
            Zero Cross-Layer Dependencies
```

**Key Principle**: Model has NO Swing dependencies

---

**Speaker Notes:**
"KeyChord follows strict MVC architecture. The Model layer contains all business logic with zero Swing dependencies - this means we can test the entire Model in a headless environment. The View is pure UI, and Controllers mediate between them."

---

## SLIDE 5: Model Layer Architecture

**Model Components**

```
PianoModel (Coordinator)
    │
    ├──► Recorder (timestamps)
    ├──► Player (playback timing)
    ├──► ChordManager (chord generation)
    ├──► RecordingManager (storage)
    └──► MidiSoundManager (singleton, audio)
            │
            └──► Recording
                    │
                    └──► List<NoteEvent>
```

**Design Patterns Used:**
- Singleton: MidiSoundManager
- Strategy: ChordType enum
- Adapter: RecordingEventSource

---

**Speaker Notes:**
"PianoModel coordinates all model components. Notice how each component has a single responsibility - Recorder handles timestamps, Player handles playback timing, ChordManager generates chords. This is the Single Responsibility Principle in action."

---

## SLIDE 6: View Layer Hierarchy

**View Component Structure**

```
MainWindow (JFrame)
    │
    └──► PianoView (JPanel)
            │
            ├──► PianoKeyboardPanel
            │       │
            │       └──► Map<Integer, PianoKeyView>
            │               (One JButton per key)
            │
            ├──► ControlPanelView
            │       (Chord mode selector)
            │
            └──► RecordingPanel
                    (Recording list, buttons)
```

**Key Design**: Composition over Inheritance

---

**Speaker Notes:**
"The View layer uses composition - PianoView composes multiple panels rather than inheriting functionality. Each panel has a clear responsibility, making the UI easy to test and modify."

---

## SLIDE 7: Controller Layer

**Controller Responsibilities**

```
PianoController
    ├──► Handles keyboard/mouse input
    ├──► Coordinates Model and View
    └──► Updates UI based on Model state

RecordingController
    └──► Manages recording start/stop (Space key)

PlaybackController
    └──► Manages playback start/stop (Enter key)

RecordingPanelController
    └──► Handles recording management UI
```

**Principle**: Controllers delegate to Model, don't contain business logic

---

**Speaker Notes:**
"Each controller has a single, focused responsibility. They delegate all business logic to the Model - this is delegation over inheritance. The controllers are thin - they just coordinate between Model and View."

---

## SLIDE 8: Key Implementation: Chord Generation

**Strategy Pattern in ChordManager**

```java
public enum ChordType {
    MAJOR(new int[]{0, 4, 7}),
    MINOR(new int[]{0, 3, 7}),
    DIMINISHED(new int[]{0, 3, 6}),
    // ... 5 more chord types
}

public List<Integer> generateChord(int rootNote, ChordType type) {
    int[] intervals = type.getIntervals();
    List<Integer> chordNotes = new ArrayList<>();
    for (int interval : intervals) {
        chordNotes.add(rootNote + interval);
    }
    return chordNotes;
}
```

**Open/Closed Principle**: Add new chord types without modifying existing code

---

**Speaker Notes:**
"This demonstrates the Strategy pattern and Open/Closed Principle. Each chord type is an enum with its interval pattern. To add a new chord type, we just add a new enum value - no need to modify the generateChord method."

---

## SLIDE 9: Key Implementation: Recording Data Structure

**Immutable NoteEvent**

```java
public class NoteEvent {
    private final int midiNote;      // 0-127
    private final long timestamp;    // milliseconds
    private final boolean isNoteOn;  // true = note on, false = note off
    
    // Immutable - all fields are final
    // Thread-safe by design
}
```

**Recording Structure**

```java
public class Recording {
    private final String name;
    private final List<NoteEvent> events;  // Thread-safe list
    private final Object lock;              // Synchronization
    
    public void addNoteEvent(NoteEvent event) {
        synchronized (lock) {
            events.add(event);
        }
    }
}
```

**Thread-Safe Design**: Synchronized access for concurrent recording

---

**Speaker Notes:**
"NoteEvent is immutable - all fields are final. This makes it thread-safe by design. Recording uses synchronized blocks to ensure thread-safe access when recording and playing back simultaneously."

---

## SLIDE 10: Key Implementation: Playback Timing

**Player Class - Timing Loop**

```java
private void playEvents(List<NoteEvent> events, 
                       NotePlaybackHandler handler) {
    long baseTime = System.currentTimeMillis();
    
    for (NoteEvent event : events) {
        // Calculate delay until this event
        long eventTime = baseTime + event.getTimestamp();
        long delay = eventTime - System.currentTimeMillis();
        
        // Wait until it's time
        if (delay > 0) {
            Thread.sleep(delay);
        }
        
        // Play the event
        if (event.isNoteOn()) {
            handler.onNoteOn(event.getMidiNote());
        } else {
            handler.onNoteOff(event.getMidiNote());
        }
    }
}
```

**Key Design**: Separate thread for playback, handler interface

---

**Speaker Notes:**
"The Player runs in a separate thread to avoid blocking the UI. It calculates the delay for each event based on timestamps and sleeps until it's time to play. The handler interface allows us to customize what happens when notes play - we use it to both play audio and highlight keys."

---

## SLIDE 11: Key Implementation: UI Rendering

**Key Highlighting During Playback**

```java
// In PianoController
public void startPlayback(Recording recording) {
    model.startPlaybackWithHandler(recording, 
        new Player.NotePlaybackHandler() {
            @Override
            public void onNoteOn(int midiNote) {
                model.playNoteWithoutRecording(midiNote);
                SwingUtilities.invokeLater(() -> {
                    view.getKeyboardPanel()
                        .highlightKey(midiNote);
                });
            }
            
            @Override
            public void onNoteOff(int midiNote) {
                model.stopNoteWithoutRecording(midiNote);
                SwingUtilities.invokeLater(() -> {
                    view.getKeyboardPanel()
                        .unhighlightKey(midiNote);
                });
            }
        });
}
```

**Thread Safety**: SwingUtilities.invokeLater ensures UI updates on EDT

---

**Speaker Notes:**
"During playback, we use a custom handler that both plays the note and highlights the key. SwingUtilities.invokeLater ensures all UI updates happen on the Event Dispatch Thread, which is required for thread safety in Swing."

---

## SLIDE 12: SOLID Principles in Action

**Single Responsibility**
- ChordManager: Only generates chords
- Recorder: Only handles timestamps
- Each controller: One specific aspect

**Open/Closed**
- ChordType enum: Extend without modification
- Player.EventSource: Accept any implementation

**Liskov Substitution**
- Recording implements EventSource via adapter
- Interchangeable with other EventSource implementations

**Interface Segregation**
- KeyPressHandler: Minimal interface (2 methods)
- NotePlaybackHandler: Separates on/off concerns

**Dependency Inversion**
- Controllers depend on PianoModel (abstraction)
- Player depends on EventSource interface

---

**Speaker Notes:**
"Every SOLID principle is demonstrated in KeyChord. Single Responsibility is evident in every class. Open/Closed through the ChordType enum. Liskov Substitution through the EventSource interface. Interface Segregation with minimal, focused interfaces. And Dependency Inversion - high-level classes depend on abstractions, not concrete implementations."

---

## SLIDE 13: Design Patterns

**Singleton Pattern**
```java
MidiSoundManager.getInstance()
// Ensures single synthesizer instance
```

**Strategy Pattern**
```java
ChordType.MAJOR, ChordType.MINOR, ...
// Different algorithms for chord generation
```

**Adapter Pattern**
```java
RecordingEventSource implements Player.EventSource
// Adapts Recording to EventSource interface
```

**Observer Pattern**
```java
KeyListener, ActionListener
// Event-driven architecture
```

---

**Speaker Notes:**
"We use four design patterns. Singleton ensures we only have one MIDI synthesizer. Strategy pattern for chord types. Adapter to make Recording work with Player. And Observer pattern through Swing's event listeners."

---

## SLIDE 14: Demo Steps

**Live Demonstration**

1. **Play Notes**
   - Press keyboard keys or click mouse
   - Show single notes and chords

2. **Record Performance**
   - Press Space to start recording
   - Play a short melody
   - Press Space to stop

3. **Playback with Visual Feedback**
   - Select recording from list
   - Press Enter or click Play
   - Watch keys highlight during playback

4. **Export to MIDI**
   - Select recording
   - Click Export MIDI
   - Show exported file

5. **Show Code Structure**
   - Open IDE
   - Show Model classes (no Swing imports)
   - Show test coverage

---

**Speaker Notes:**
"Now let's see KeyChord in action. [Live demo] Notice how the keys highlight during playback - this demonstrates the coordination between Model and View through the Controller. The visual feedback makes it clear what's happening."

---

## SLIDE 15: Engineering Challenges

**Challenge 1: Thread Safety**
- **Problem**: Recording and playback can happen simultaneously
- **Solution**: Synchronized blocks, immutable NoteEvent
- **Result**: Thread-safe without blocking UI

**Challenge 2: Timing Accuracy**
- **Problem**: Playback must match original timing
- **Solution**: Relative timestamps, separate playback thread
- **Result**: Accurate playback with millisecond precision

**Challenge 3: Testing Audio**
- **Problem**: How to test MIDI without actual audio?
- **Solution**: Mock MidiSoundManager, test logic separately
- **Result**: Comprehensive test coverage

**Challenge 4: MVC Separation**
- **Problem**: Temptation to mix UI and logic
- **Solution**: Strict discipline, zero Swing in Model
- **Result**: Model fully testable in headless environment

---

**Speaker Notes:**
"We faced several challenges. Thread safety was critical - we solved it with synchronized blocks and immutable data. Timing accuracy required careful timestamp management. Testing audio was tricky - we mocked the MIDI manager. And maintaining strict MVC separation required discipline, but it paid off in testability."

---

## SLIDE 16: Lessons Learned

**What We Learned**

1. **MVC Separation Pays Off**
   - Model can be tested independently
   - Easy to swap UI implementations
   - Clear separation of concerns

2. **SOLID Principles Are Practical**
   - Not just theory - they solve real problems
   - Make code easier to extend and test
   - Single Responsibility makes debugging easier

3. **Design Patterns Solve Specific Problems**
   - Strategy for extensibility
   - Singleton for resource management
   - Adapter for interface compatibility

4. **Testing Requires Good Design**
   - Hard to test tightly coupled code
   - Good design enables good testing
   - Mocking requires interfaces

5. **Thread Safety Is Critical**
   - Immutable objects simplify concurrency
   - Synchronization must be carefully designed
   - UI updates must be on EDT

---

**Speaker Notes:**
"The biggest lesson is that good OOD principles aren't just academic - they solve real problems. MVC separation made testing possible. SOLID principles made the code maintainable. And design patterns solved specific problems elegantly."

---

## SLIDE 17: Future Work

**Potential Enhancements**

- **More Chord Types**: Add jazz chords, extended chords
- **Multiple Tracks**: Record different instruments separately
- **Tempo Control**: Adjust playback speed
- **Undo/Redo**: Edit recordings
- **MIDI Import**: Load external MIDI files
- **Visual Effects**: Waveform display, note visualization
- **Network Playback**: Share recordings online

**Architecture Supports Extension**
- New chord types: Add to ChordType enum
- New features: Extend existing classes
- New UI: Swap View implementation

---

**Speaker Notes:**
"KeyChord's architecture makes it easy to extend. Adding new chord types is trivial - just add to the enum. The MVC separation means we could swap the entire UI without touching the Model. The design patterns we used make the code extensible."

---

## SLIDE 18: Contributions

**What We Achieved**

✅ **Strict MVC Architecture**
   - Zero cross-layer dependencies
   - Model fully testable without UI

✅ **All Five SOLID Principles**
   - Demonstrated in practical application
   - Each principle solves real problems

✅ **Three Design Patterns**
   - Singleton, Strategy, Adapter
   - Each serves a specific purpose

✅ **Comprehensive Testing**
   - Unit tests for Model, View, Controller
   - JUnit 5 and Mockito

✅ **Functional Application**
   - Full recording and playback
   - MIDI export capability
   - Professional code quality

---

**Speaker Notes:**
"We've enjoyed sharing KeyChord with you. We value your feedback, and we look forward to discussing how these OOD principles can be applied to other projects."

---

## APPENDIX: ASCII Diagrams

### Complete MVC Flow

```
User Input (Keyboard/Mouse)
        │
        ▼
┌─────────────────┐
│ PianoController │
└─────────────────┘
        │
        ├──► PianoModel.playNote()
        │         │
        │         ├──► MidiSoundManager.playNote()
        │         │         │
        │         │         └──► Audio Output
        │         │
        │         └──► If Recording:
        │               Recorder.recordNoteOn()
        │                 │
        │                 └──► Recording.addNoteEvent()
        │
        └──► SwingUtilities.invokeLater()
                  │
                  └──► PianoKeyboardPanel.highlightKey()
                            │
                            └──► PianoKeyView.setPressed()
                                      │
                                      └──► Visual Update
```

### Model Relationships Detail

```
PianoModel
    │
    ├──► Recorder
    │       │
    │       └──► Creates NoteEvent objects
    │
    ├──► Player
    │       │
    │       ├──► Uses EventSource interface
    │       └──► Uses NotePlaybackHandler interface
    │
    ├──► ChordManager
    │       │
    │       └──► ChordType enum (Strategy pattern)
    │
    ├──► RecordingManager
    │       │
    │       └──► Manages Map<String, Recording>
    │
    └──► MidiSoundManager (Singleton)
            │
            └──► Single synthesizer instance
```

### View Hierarchy Detail

```
MainWindow (JFrame)
    │
    └──► PianoView (JPanel, BorderLayout)
            │
            ├──► PianoKeyboardPanel (CENTER)
            │       │
            │       ├──► Map<Integer, PianoKeyView>
            │       │       │
            │       │       └──► PianoKeyView extends JButton
            │       │               (One per MIDI note)
            │       │
            │       └──► KeyPressHandler interface
            │
            ├──► ControlPanelView (SOUTH)
            │       └──► Chord mode selector (JComboBox)
            │
            └──► RecordingPanel (EAST)
                    ├──► Recording list (JList)
                    └──► Action buttons (JButton)
```

---

## Presentation Timing Guide

- **Slide 1**: 30 seconds (Title)
- **Slide 2**: 1 minute (Motivation)
- **Slide 3**: 30 seconds (Features)
- **Slides 4-7**: 5 minutes (Architecture)
- **Slides 8-11**: 4 minutes (Implementation)
- **Slides 12-13**: 2 minutes (SOLID & Patterns)
- **Slide 14**: 3 minutes (Demo)
- **Slides 15-17**: 3 minutes (Challenges, Lessons, Future)
- **Slide 18**: 1 minute (Contributions)
- **Q&A**: Remaining time

**Total**: ~20 minutes presentation + Q&A

---

## Visual Suggestions

1. **Color Coding**:
   - Model: Blue background
   - View: Green background
   - Controller: Yellow background

2. **Class Diagram**:
   - Use the generated ClassDiagram_Simple.png
   - Highlight relationships with arrows

3. **Code Snippets**:
   - Syntax highlighting
   - Minimal, focused examples
   - Highlight key lines

4. **Live Demo**:
   - Screen recording backup
   - Show IDE alongside application
   - Display test results

---

## Key Talking Points

1. **Emphasize MVC Separation**: "Notice - no Swing imports in Model"
2. **Show Testability**: "We can test the entire Model without UI"
3. **Demonstrate Patterns**: "This is Strategy pattern in action"
4. **Highlight SOLID**: "Single Responsibility - each class does one thing"
5. **Show Extensibility**: "Adding a new chord type is just one line"

---

*End of Presentation*

