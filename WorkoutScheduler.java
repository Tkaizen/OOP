import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class WorkoutScheduler {
    private JFrame frame;
    private JPanel mainPanel, addTaskPanel, todoPanelList;
    private DefaultListModel<JCheckBox> todoListModel;
    private DefaultListModel<String> monthScheduleListModel;

    public WorkoutScheduler() {
        // Initialize JFrame
        frame = new JFrame("Workout Scheduler");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Main Panel
        mainPanel = new JPanel(new GridLayout(1, 2));
        createMainView();

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void createMainView() {
        // Left: "This Month's Schedule"
        JPanel monthSchedulePanel = new JPanel(new BorderLayout());
        JLabel monthLabel = new JLabel("This Month's Schedule", SwingConstants.CENTER);
        monthScheduleListModel = new DefaultListModel<>();
        JList<String> monthScheduleList = new JList<>(monthScheduleListModel);
        monthSchedulePanel.add(monthLabel, BorderLayout.NORTH);
        monthSchedulePanel.add(new JScrollPane(monthScheduleList), BorderLayout.CENTER);

        // Right: "To-do Workouts"
        JPanel todoPanel = new JPanel(new BorderLayout());
        JLabel todoLabel = new JLabel("To-do Workouts", SwingConstants.CENTER);
        todoListModel = new DefaultListModel<>();
        todoPanelList = new JPanel();
        todoPanelList.setLayout(new BoxLayout(todoPanelList, BoxLayout.Y_AXIS));

        JScrollPane todoScrollPane = new JScrollPane(todoPanelList);
        todoPanel.add(todoLabel, BorderLayout.NORTH);
        todoPanel.add(todoScrollPane, BorderLayout.CENTER);

        // Buttons: Add, Edit, Finish Task
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3));
        JButton addTaskButton = new JButton("Add Task");
        JButton editTaskButton = new JButton("Edit Task");
        JButton finishTaskButton = new JButton("Finish Task");

        addTaskButton.addActionListener(e -> switchToAddTaskView());
        editTaskButton.addActionListener(e -> editSelectedTask());
        finishTaskButton.addActionListener(e -> finishSelectedTask());

        buttonsPanel.add(addTaskButton);
        buttonsPanel.add(editTaskButton);
        buttonsPanel.add(finishTaskButton);

        todoPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Add to Main Panel
        mainPanel.add(monthSchedulePanel);
        mainPanel.add(todoPanel);
    }

    private void switchToAddTaskView() {
        // Create "Add Task" Panel
        addTaskPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        addTaskPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Task Name:");
        JTextField nameField = new JTextField();
        JLabel descLabel = new JLabel("Description:");
        JTextField descField = new JTextField();
        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"Strength Training", "Cardio", "Yoga", "Flexibility", "Balance"};
        JComboBox<String> categoryComboBox = new JComboBox<>(categories);

        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> addTask(nameField, descField, categoryComboBox));
        cancelButton.addActionListener(e -> switchToMainView());

        addTaskPanel.add(nameLabel);
        addTaskPanel.add(nameField);
        addTaskPanel.add(descLabel);
        addTaskPanel.add(descField);
        addTaskPanel.add(categoryLabel);
        addTaskPanel.add(categoryComboBox);
        addTaskPanel.add(new JLabel()); // Empty placeholder
        addTaskPanel.add(new JLabel()); // Empty placeholder
        addTaskPanel.add(confirmButton);
        addTaskPanel.add(cancelButton);

        // Replace Main Panel with Add Task Panel
        frame.getContentPane().remove(mainPanel);
        frame.add(addTaskPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private void switchToMainView() {
        frame.getContentPane().remove(addTaskPanel);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private void addTask(JTextField nameField, JTextField descField, JComboBox<String> categoryComboBox) {
        String taskName = nameField.getText().trim();
        String taskDesc = descField.getText().trim();
        String taskCategory = categoryComboBox.getSelectedItem().toString();

        if (!taskName.isEmpty()) {
            String taskFull = taskName + (taskDesc.isEmpty() ? "" : " - " + taskDesc) + " (" + taskCategory + ")";

            // Add to To-Do List
            JCheckBox taskCheckBox = new JCheckBox(taskFull);
            todoListModel.addElement(taskCheckBox);
            todoPanelList.add(taskCheckBox);

            // Add to Month's Schedule
            monthScheduleListModel.addElement(taskFull);

            todoPanelList.revalidate();
            todoPanelList.repaint();

            switchToMainView(); // Return to main view
        } else {
            JOptionPane.showMessageDialog(frame, "Task name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelectedTask() {
        ArrayList<JCheckBox> selectedTasks = getSelectedTasks();
        if (selectedTasks.size() == 1) {
            JCheckBox selectedTask = selectedTasks.get(0);
            String[] parts = selectedTask.getText().split(" - ", 2);
            String taskName = parts[0];
            String taskDesc = parts.length > 1 ? parts[1] : "";

            JTextField nameField = new JTextField(taskName);
            JTextField descField = new JTextField(taskDesc);

            int result = JOptionPane.showConfirmDialog(
                frame,
                new Object[]{"Task Name:", nameField, "Description:", descField},
                "Edit Task",
                JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {
                String newTaskName = nameField.getText().trim();
                String newTaskDesc = descField.getText().trim();
                if (!newTaskName.isEmpty()) {
                    String updatedTask = newTaskName + (newTaskDesc.isEmpty() ? "" : " - " + newTaskDesc);
                    selectedTask.setText(updatedTask);

                    // Update Month's Schedule
                    int index = monthScheduleListModel.indexOf(selectedTask.getText());
                    if (index >= 0) {
                        monthScheduleListModel.set(index, updatedTask);
                    }

                    todoPanelList.revalidate();
                    todoPanelList.repaint();
                } else {
                    JOptionPane.showMessageDialog(frame, "Task name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select one task to edit.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finishSelectedTask() {
        ArrayList<JCheckBox> selectedTasks = getSelectedTasks();
        if (!selectedTasks.isEmpty()) {
            for (JCheckBox task : selectedTasks) {
                // Remove from To-Do List
                todoListModel.removeElement(task);
                todoPanelList.remove(task);

                // Remove from Month's Schedule
                monthScheduleListModel.removeElement(task.getText());
            }
            todoPanelList.revalidate();
            todoPanelList.repaint();
        } else {
            JOptionPane.showMessageDialog(frame, "Please select at least one task to finish.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ArrayList<JCheckBox> getSelectedTasks() {
        ArrayList<JCheckBox> selectedTasks = new ArrayList<>();
        for (Component component : todoPanelList.getComponents()) {
            if (component instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) component;
                if (checkBox.isSelected()) {
                    selectedTasks.add(checkBox);
                }
            }
        }
        return selectedTasks;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WorkoutScheduler::new);
    }
}
