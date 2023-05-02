import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class AdminHome extends JDialog{
    private JComboBox <Integer> selectEmployeeComboBox;

    private JComboBox <Integer> selectTaskComboBox;
    private JTextArea taskDescriptionTextArea;
    private JTextField defaultDueDateTextField;
    private JButton updateCurrentTaskButton;
    private JButton createNewTaskButton;
    private JTextField taskNameTextField;
    private JTextField taskProgressTextField;
    private JComboBox <Boolean> paymentStatusComboBox;
    private JPanel panel1;
    private JButton logoutButton;

    public AdminHome(JFrame parent){


        super(parent);
        setTitle("Admin Home");
        setContentPane(panel1);
        setMinimumSize(new Dimension(600,400));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        //set employee combo box values
        selectEmployeeComboBox.setModel(getEmployeeIDs());
        selectEmployeeComboBox.setSelectedIndex(0);
        System.out.println(selectEmployeeComboBox.getItemAt(0));


        //add model for tasks combo box instead of addItems
        selectTaskComboBox.setModel(setTaskIDs(selectEmployeeComboBox.getItemAt(0)));
        selectTaskComboBox.setSelectedIndex(0);
        System.out.println(selectTaskComboBox.getItemAt(0));

        taskNameTextField.setText(Task.RetrieveData(selectTaskComboBox.getItemAt(0)).name);
        taskDescriptionTextArea.setText(Task.RetrieveData(selectTaskComboBox.getItemAt(0)).description);
        taskProgressTextField.setText(Integer.toString(Task.RetrieveData(selectTaskComboBox.getItemAt(0)).progress));
        defaultDueDateTextField.setText(Task.RetrieveData(selectTaskComboBox.getItemAt(0)).dueDate);


        //set payment status combo box values
        paymentStatusComboBox.setModel(setPaymentStatus());
        paymentStatusComboBox.setSelectedIndex(Task.RetrieveData(selectTaskComboBox.getItemAt(0)).paymentStatus ? 0 : 1);


        selectEmployeeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("changed to:" + selectEmployeeComboBox.getSelectedIndex());


                int employeeID = selectEmployeeComboBox.getItemAt(selectEmployeeComboBox.getSelectedIndex());
                //set task combo box values
                selectTaskComboBox.setModel(setTaskIDs(selectEmployeeComboBox.getItemAt(0)));
                selectTaskComboBox.setSelectedIndex(0);
                System.out.println(selectTaskComboBox.getItemAt(0));

            }
        });

        selectTaskComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int taskID = selectTaskComboBox.getItemAt(selectTaskComboBox.getSelectedIndex());
                Task task = Task.RetrieveData(taskID);
                taskNameTextField.setText(task.name);
                taskDescriptionTextArea.setText(task.description);
                taskProgressTextField.setText(Integer.toString(task.progress));
                defaultDueDateTextField.setText(task.dueDate);

                //set payment status combo box values
                paymentStatusComboBox.setModel(setPaymentStatus());
                if(task.paymentStatus){
                    paymentStatusComboBox.setSelectedIndex(0);
                }
                else{
                    paymentStatusComboBox.setSelectedIndex(1);
                }



            }
        });

        paymentStatusComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //set payment status combo box values
                //paymentStatusComboBox.setModel(setPaymentStatus());
               // paymentStatusComboBox.setSelectedIndex(Task.RetrieveData(selectTaskComboBox.getItemAt(0)).paymentStatus ? 0 : 1);
                System.out.println(paymentStatusComboBox.getItemAt(paymentStatusComboBox.getSelectedIndex()));
            }
        });

        updateCurrentTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int taskID = selectTaskComboBox.getItemAt(selectTaskComboBox.getSelectedIndex());
                Task task = Task.RetrieveData(taskID);
                task.name = taskNameTextField.getText().trim();
                task.description = taskDescriptionTextArea.getText().trim();
                task.progress = Integer.parseInt(taskProgressTextField.getText().trim());
                task.dueDate = defaultDueDateTextField.getText().trim();
                //validate due date to follow format year-month-day
                if(!task.dueDate.matches("^\\d{4}-(0[1-9]|1[0-2])-\\d{2}$")){
                    JOptionPane.showMessageDialog(null, "Invalid Due Date Format (yyyy-mm-dd)");
                    return;
                }
                task.paymentStatus = paymentStatusComboBox.getItemAt(paymentStatusComboBox.getSelectedIndex());
                task.UpdateData(task);

                JOptionPane.showMessageDialog(null, "Task Updated");

            }
        });

        createNewTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                AdminCreate adminCreate = new AdminCreate(null);
            }
        });



        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Login login = new Login(null);
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

    public DefaultComboBoxModel setTaskIDs(int employeeID){
        DefaultComboBoxModel<Integer> model = new DefaultComboBoxModel<Integer>();

        Employee employee = Employee.RetrieveData(selectEmployeeComboBox.getItemAt(selectEmployeeComboBox.getSelectedIndex()));
        //set task combo box values
        for(int i = 0; i < employee.tasks.size(); i++){
            model.addElement(employee.tasks.get(i));
        }
        return model;
    }

    public DefaultComboBoxModel setPaymentStatus(){
        DefaultComboBoxModel<Boolean> model = new DefaultComboBoxModel<Boolean>();
        model.addElement(true);
        model.addElement(false);
        return model;
    }


    public static void main(String[] args) {
        AdminHome adminHome = new AdminHome(null);
    }

}
