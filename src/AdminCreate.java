import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class AdminCreate extends JDialog{
    private JComboBox employeeIDComboBox;
    private JTextField taskNameTextField;
    private JTextArea taskDescriptionTextArea;
    private JTextField taskDueDateMonthTextField;
    private JButton createTaskButton;
    private JTextField taskDueDateDayTextField;
    private JTextField taskDueDateYearTextField;
    private JPanel panel1;
    private JButton backButton;

    public AdminCreate(JFrame parent){
        super(parent);
        setTitle("Admin Create");
        setContentPane(panel1);
        setMinimumSize(new Dimension(600,400));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        //set employee combo box values
        employeeIDComboBox.setModel(getEmployeeIDs());
        employeeIDComboBox.setSelectedIndex(0);
        System.out.println(employeeIDComboBox.getItemAt(0));





        createTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check if any fields are empty
                if(taskNameTextField.getText().equals("") || taskDescriptionTextArea.getText().equals("") || taskDueDateMonthTextField.getText().equals("") || taskDueDateDayTextField.getText().equals("") || taskDueDateYearTextField.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "Please fill out all fields.");
                    return;
                }

                //check if month, day, and year are integers
                try{
                    Integer.parseInt(taskDueDateMonthTextField.getText().trim());
                    Integer.parseInt(taskDueDateDayTextField.getText().trim());
                    Integer.parseInt(taskDueDateYearTextField.getText().trim());
                }
                catch (Exception ex){
                    JOptionPane.showMessageDialog(null, "Please enter a integers for date.");
                    return;
                }
                if(Integer.parseInt(taskDueDateMonthTextField.getText().trim()) < 1 || Integer.parseInt(taskDueDateMonthTextField.getText().trim()) > 12){
                    JOptionPane.showMessageDialog(null, "Please enter a valid month. (1-12)");
                    return;
                }
                if(Integer.parseInt(taskDueDateDayTextField.getText().trim()) < 1 || Integer.parseInt(taskDueDateDayTextField.getText().trim()) > 31){
                    JOptionPane.showMessageDialog(null, "Please enter a valid day. (1-31)");
                    return;
                }
                if(Integer.parseInt(taskDueDateYearTextField.getText().trim()) < 2023 || Integer.parseInt(taskDueDateYearTextField.getText().trim()) > 2100){
                    JOptionPane.showMessageDialog(null, "Please enter a valid year. (2023-2100)");
                    return;
                }


                Task task = new Task();
                int maxID = 0;
                try{
                    Connection conn = DriverManager.getConnection(DatabaseConnectionManager.url, DatabaseConnectionManager.usernameToDatabase, DatabaseConnectionManager.passwordToDatabase);
                    String sql = "SELECT MAX(id) FROM tasks";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    while(rs.next()){
                        maxID = rs.getInt("MAX(id)")+1;
                    }
                    conn.close();
                    rs.close();
                    stmt.close();
                }
                catch (Exception ex){
                    System.out.println(ex);
                }
                task.id = maxID;
                task.name = taskNameTextField.getText();
                task.dueDate = taskDueDateYearTextField.getText() + "-" + taskDueDateMonthTextField.getText() + "-" + taskDueDateDayTextField.getText();
                task.description = taskDescriptionTextArea.getText();
                task.progress = 0;
                task.paymentStatus = false;

                //retrieve current Employee assignedTasks
                Employee employee = Employee.RetrieveData((int)employeeIDComboBox.getSelectedItem());
                ArrayList<Integer> assignedTasks = employee.tasks;
                assignedTasks.add(task.id);
                String newAssignedTasks = "";
                //update employee assignedTasks
                for(int i = 0; i < assignedTasks.size(); i++){
                    if(i == assignedTasks.size()-1){
                        newAssignedTasks += assignedTasks.get(i);
                    }else {
                        newAssignedTasks += assignedTasks.get(i) + ",";
                    }
                }

                //update employee assignedTasks
                try{
                    Connection conn = DriverManager.getConnection(DatabaseConnectionManager.url, DatabaseConnectionManager.usernameToDatabase, DatabaseConnectionManager.passwordToDatabase);
                    String sql = "UPDATE employee SET assigned_tasks = ? WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, newAssignedTasks);
                    stmt.setInt(2, (int)employeeIDComboBox.getSelectedItem());
                    stmt.executeUpdate();
                    conn.close();
                    stmt.close();
                }
                catch (Exception ex){
                    System.out.println(ex);
                }

                //insert new task into database
                try{
                    Connection conn = DriverManager.getConnection(DatabaseConnectionManager.url, DatabaseConnectionManager.usernameToDatabase, DatabaseConnectionManager.passwordToDatabase);
                    String sql = "INSERT INTO tasks (id, name, due_date, description, progress, payment_status) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, task.id);
                    stmt.setString(2, task.name);
                    stmt.setString(3, task.dueDate);
                    stmt.setString(4, task.description);
                    stmt.setInt(5, task.progress);
                    stmt.setBoolean(6, task.paymentStatus);
                    stmt.executeUpdate();
                    conn.close();
                    stmt.close();
                }
                catch (Exception ex){
                    System.out.println(ex);
                }


                System.out.println("Created task: " + task.name + " with ID: " + task.id);

                JOptionPane.showMessageDialog(null, "Task created successfully.");
            }
        });


        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                AdminHome adminHome = new AdminHome(null);
            }
        });

        setVisible(true);

    }

    public DefaultComboBoxModel getEmployeeIDs(){
        DefaultComboBoxModel<Integer> model = new DefaultComboBoxModel<Integer>();
        try{
            Connection conn = DriverManager.getConnection(DatabaseConnectionManager.url, DatabaseConnectionManager.usernameToDatabase, DatabaseConnectionManager.passwordToDatabase);
            String sql = "SELECT id FROM employee";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<Integer> employeeIDs = new ArrayList<Integer>();
            while(rs.next()){
                employeeIDs.add(rs.getInt("id"));
            }
            for(int i = 0; i < employeeIDs.size(); i++){
                model.addElement(employeeIDs.get(i));
            }

            conn.close();
            stmt.close();
            rs.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
        return model;
    }
}
