package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel that displays a scrollable list of recordings with rename/delete/export buttons.
 * Located on the right side of the main window.
 * 
 * @author KeyChord
 */
public class RecordingPanel extends JPanel {
    private final DefaultListModel<String> listModel;
    private final JList<String> recordingList;
    private final JScrollPane scrollPane;
    private final JButton playButton;
    private final JButton deleteButton;
    private final JButton exportButton;
    private final JButton renameButton;
    
    /**
     * Creates a new RecordingPanel.
     */
    public RecordingPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
            null, "Recordings", TitledBorder.LEFT, TitledBorder.TOP));
        
        // Create list model and list
        listModel = new DefaultListModel<>();
        recordingList = new JList<>(listModel);
        recordingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recordingList.setVisibleRowCount(5);
        
        scrollPane = new JScrollPane(recordingList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Create buttons
        playButton = new JButton("Play");
        deleteButton = new JButton("Delete");
        exportButton = new JButton("Export MIDI");
        renameButton = new JButton("Rename");
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        buttonPanel.add(playButton);
        buttonPanel.add(renameButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(deleteButton);
        
        // Add components
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set preferred size
        setPreferredSize(new Dimension(200, 200));
        setMinimumSize(new Dimension(180, 200));
    }
    
    /**
     * Updates the list of recordings.
     * 
     * @param recordingNames the list of recording names to display
     */
    public void updateRecordings(List<String> recordingNames) {
        listModel.clear();
        for (String name : recordingNames) {
            listModel.addElement(name);
        }
    }
    
    /**
     * Gets the selected recording name.
     * 
     * @return the selected name, or null if nothing selected
     */
    public String getSelectedRecording() {
        return recordingList.getSelectedValue();
    }
    
    /**
     * Gets the play button.
     * 
     * @return the play button
     */
    public JButton getPlayButton() {
        return playButton;
    }
    
    /**
     * Gets the delete button.
     * 
     * @return the delete button
     */
    public JButton getDeleteButton() {
        return deleteButton;
    }
    
    /**
     * Gets the export button.
     * 
     * @return the export button
     */
    public JButton getExportButton() {
        return exportButton;
    }
    
    /**
     * Gets the rename button.
     * 
     * @return the rename button
     */
    public JButton getRenameButton() {
        return renameButton;
    }
    
    /**
     * Adds an action listener to the play button.
     * 
     * @param listener the action listener
     */
    public void addPlayListener(ActionListener listener) {
        playButton.addActionListener(listener);
    }
    
    /**
     * Adds an action listener to the delete button.
     * 
     * @param listener the action listener
     */
    public void addDeleteListener(ActionListener listener) {
        deleteButton.addActionListener(listener);
    }
    
    /**
     * Adds an action listener to the export button.
     * 
     * @param listener the action listener
     */
    public void addExportListener(ActionListener listener) {
        exportButton.addActionListener(listener);
    }
    
    /**
     * Adds an action listener to the rename button.
     * 
     * @param listener the action listener
     */
    public void addRenameListener(ActionListener listener) {
        renameButton.addActionListener(listener);
    }
}

