import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployeeHome extends JDialog{
    private JComboBox <Integer> assignedTaskIDComboBox;
    private JTextField defaultTaskNameTextField;
    private JTextArea defaultTaskDescription;
    private JButton logoutButton;
    private JButton updateTaskProgressButton;
    private JTextField defaultTaskDueDate;
    private JTextField defaultProgressTextField;
    private JPanel panel1;

    public EmployeeHome(JFrame parent, int employeeID){
        super(parent);
        setTitle("Employee Home");
        setContentPane(panel1);
        setMinimumSize(new Dimension(600,400));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        assignedTaskIDComboBox.setModel(setTaskIDs(employeeID));
        assignedTaskIDComboBox.setSelectedIndex(0);
        System.out.println(assignedTaskIDComboBox.getItemAt(0));

        defaultTaskNameTextField.setText((Task.RetrieveData(assignedTaskIDComboBox.getItemAt(0)).name));
        defaultTaskDescription.setText(Task.RetrieveData(assignedTaskIDComboBox.getItemAt(0)).description);
        defaultTaskDueDate.setText(Task.RetrieveData(assignedTaskIDComboBox.getItemAt(0)).dueDate);
        defaultProgressTextField.setText(Integer.toString(Task.RetrieveData(assignedTaskIDComboBox.getItemAt(0)).progress));

        assignedTaskIDComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int taskID = assignedTaskIDComboBox.getItemAt(assignedTaskIDComboBox.getSelectedIndex());
                defaultTaskNameTextField.setText((Task.RetrieveData(taskID).name));
                defaultTaskDescription.setText(Task.RetrieveData(taskID).description);
                defaultTaskDueDate.setText(Task.RetrieveData(taskID).dueDate);
                defaultProgressTextField.setText(Integer.toString(Task.RetrieveData(taskID).progress));
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Login login = new Login(null);
            }
        });


        updateTaskProgressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int taskID = assignedTaskIDComboBox.getItemAt(assignedTaskIDComboBox.getSelectedIndex());
                Task task = Task.RetrieveData(taskID);
                task.progress = Integer.parseInt(defaultProgressTextField.getText());
                task.UpdateData(task);

                JOptionPane.showMessageDialog(null, "Task Progress Updated");

            }
        });

        setVisible(true);

    }
    public DefaultComboBoxModel setTaskIDs(int employeeID){
        DefaultComboBoxModel<Integer> model = new DefaultComboBoxModel<Integer>();

        Employee employee = Employee.RetrieveData(employeeID);
        //set task combo box values
        for(int i = 0; i < employee.tasks.size(); i++){
            model.addElement(employee.tasks.get(i));
        }
        return model;
    }

}
