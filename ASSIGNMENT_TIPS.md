# Assignment Tips and Suggestions

## General Tips

### Before Writing:
1. **Watch the Winston video first** - It's over an hour, but it's excellent advice that will help with both the assignment and your presentation
2. **Review your project analysis** - The PROJECT_ANALYSIS.md file has all the information you need
3. **Check actual code counts** - I've provided estimates, but verify with:
   ```bash
   find src/main/java -name "*.java" -exec wc -l {} + | tail -1
   find src/test/java -name "*.java" -exec wc -l {} + | tail -1
   ```

### Part 1 Specific Tips:

#### (c) Instructional Team Contact
- Be specific: "Spoke with TA [Name] on [Date] during recitation"
- If you haven't spoken yet, schedule a meeting ASAP
- Mention what feedback you received

#### (d) Feasibility Confirmation
- Be honest about what was said
- If they suggested changes, mention them briefly
- Example: "Yes, confirmed feasible. TA suggested focusing on MVC separation, which we've implemented."

#### (e) Class Diagram
- You can create a simple text diagram (I've provided one)
- Or use a tool like:
  - draw.io (free, online)
  - PlantUML (text-based)
  - Screenshot from IDE (IntelliJ has built-in diagram tool)
- Keep it simple but show relationships (implements, uses, contains)

#### (f) Team Members
- If solo: "Solo project - no team members"
- If team: List all names clearly
- Each team member should submit their own Part 1

#### (g) Lines of Code
- Current estimate: ~3,350 source + ~1,500 test = ~4,850 total
- Round to nearest 50: **4,850 lines**
- This is reasonable for a final project

#### (h) File Count
- Current: 21 source + 17 test + 1 config = **39 files**
- Don't count README, analysis docs, or presentation materials

#### (i) Non-Java Code
- Include: pom.xml, README.md, any config files
- Current estimate: ~1,160 lines (mostly documentation)
- This is fine - documentation is important!

#### (j) AI Usage
- **Be honest!** The assignment allows up to 20%
- If you used AI for:
  - Boilerplate code
  - Test templates
  - JavaDoc generation
  - Configuration files
- Be specific about what and how much
- Example: "Used AI for ~500 lines (15%): JUnit test templates, JavaDoc comments, pom.xml setup. All code reviewed and understood."

#### (k) OOD Principles
- I've listed 12 principles in the template
- You don't need all 12, but show 5-7 clearly
- For each principle, give a concrete example:
  - "Single Responsibility: ChordManager only generates chords (see line 64-93)"
  - "Dependency Inversion: Controllers depend on PianoModel interface, not concrete class"

### Part 2 Specific Tips:

#### Winston Video Notes:
The video covers many topics. Key points include:
1. **Start with a cycle** (promise → deliver → summarize)
2. **Build a fence** (distinguish your work)
3. **Use props** (demonstrations)
4. **Tell a story** (narrative structure)
5. **Use the board** (visual communication)
6. **Speak with enthusiasm**
7. **Handle questions gracefully**
8. **Practice timing**
9. **Use examples**
10. **End strong**

#### Your 5 Examples Should:
- Be specific to YOUR project
- Show HOW you'll apply the advice
- Be concrete, not abstract
- Connect to your presentation plan

#### Example Structure:
1. **Winston's advice**: Quote or summarize
2. **My application**: How you'll use it
3. **Specific example**: Concrete detail

## Common Mistakes to Avoid:

1. **Too vague**: "I will use good design principles"
   - Better: "I will demonstrate Single Responsibility by showing how ChordManager only generates chords"

2. **Not project-specific**: Generic advice that could apply to any project
   - Better: Tie each example to KeyChord specifically

3. **Missing details**: Not filling in parts (c), (d), (f), (j)
   - These are required! Fill them in before submitting

4. **Overestimating code**: Claiming 10,000+ lines when you have 3,000
   - Be realistic based on actual counts

5. **Underestimating OOD principles**: Only listing 2-3 principles
   - Show 5-7 clearly with examples

## Submission Checklist:

- [ ] Part 1 completed (all 11 items)
- [ ] Part 2 completed (5 examples)
- [ ] Class diagram included (if separate file)
- [ ] All [TO BE FILLED] sections completed
- [ ] Files are plain text (.txt)
- [ ] Files are in a single folder
- [ ] Folder is compressed to .zip
- [ ] .zip file is ready to upload

## Final Reminders:

1. **Be honest** - Especially about AI usage and team members
2. **Be specific** - Give concrete examples, not vague statements
3. **Be realistic** - Don't overpromise or underestimate
4. **Be professional** - This is a progress report, write it clearly
5. **Be prepared** - Use this to plan your actual presentation

Good luck with your assignment!


