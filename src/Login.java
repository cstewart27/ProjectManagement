import com.mysql.cj.x.protobuf.MysqlxPrepare;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class Login extends JDialog{
    private JRadioButton adminRadioButton;
    private JRadioButton employeeRadioButton;
    private ButtonGroup buttonGroup = new ButtonGroup();
    private JTextField employeeIDTextField;
    private JButton loginButton;
    private JLabel employeeLabel;
    private JPanel panel1;

    public Login(JFrame parent){



        super(parent);
        setTitle("Login");
        setContentPane(panel1);
        setMinimumSize(new Dimension(600,400));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buttonGroup.add(adminRadioButton);
        buttonGroup.add(employeeRadioButton);
        ActionListener radioButtonListener= new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(adminRadioButton.isSelected()){
                    employeeLabel.setVisible(false);
                    employeeIDTextField.setVisible(false);
                }
                else if(employeeRadioButton.isSelected()){
                    employeeLabel.setVisible(true);
                    employeeIDTextField.setVisible(true);
                }
            }
        };

        adminRadioButton.addActionListener(radioButtonListener);
        employeeRadioButton.addActionListener(radioButtonListener);


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(adminRadioButton.isSelected()){
                    dispose();
                    AdminHome adminHome = new AdminHome(null);
                }
                else if(employeeRadioButton.isSelected()){
                    //check if id exists in database
                    try{
                        Connection conn = DriverManager.getConnection(DatabaseConnectionManager.url, DatabaseConnectionManager.usernameToDatabase, DatabaseConnectionManager.passwordToDatabase);
                        String sql = "SELECT id FROM employee WHERE id = ?";
                        PreparedStatement preparedStmt = conn.prepareStatement(sql);
                        preparedStmt.setInt(1, Integer.parseInt(employeeIDTextField.getText().trim()));
                        preparedStmt.execute();
                        conn.close();
                        preparedStmt.close();
                        dispose();
                        EmployeeHome employeeHome = new EmployeeHome(null, Integer.parseInt(employeeIDTextField.getText().trim()));
                    }
                    catch (Exception ex){
                        JOptionPane.showMessageDialog(null, "Please enter a valid employee ID.");
                        return;
                    }
                }
                else{
                    JOptionPane.showMessageDialog(null, "Please select a user type.");
                }
            }
        });
        setVisible(true);
    }

    public static void main(String[] args) {
        Login login = new Login(null);
    }
}
