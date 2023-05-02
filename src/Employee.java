import java.sql.*;
import java.util.ArrayList;

public class Employee {
    private static int currentEmployeeID = 1;

    int employeeID;
    ArrayList<Integer> tasks = new ArrayList<Integer>(); // Create an ArrayList object


    public static Employee RetrieveData(int employeeID) {
        Employee employee = null;

        try{
            Connection conn = DriverManager.getConnection(DatabaseConnectionManager.url, DatabaseConnectionManager.usernameToDatabase, DatabaseConnectionManager.passwordToDatabase);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM employee WHERE id = ?";
            PreparedStatement preparedStmt = conn.prepareStatement(sql);
            preparedStmt.setInt(1, employeeID);
            ResultSet rs = preparedStmt.executeQuery();
            if(rs.next()){
                employee = new Employee();
                employee.employeeID = rs.getInt("id");
                String [] taskIDs = rs.getString("assigned_tasks").split(",");

                for(int i = 0; i < taskIDs.length; i++) {
                    employee.tasks.add(Integer.parseInt(taskIDs[i]));
                }
           }
            else {
                System.out.println("Employee ID not found");
            }

            conn.close();
            stmt.close();
            rs.close();

        }
        catch (Exception e) {
            System.out.println(e);
        }

        return employee;
    }

    //getter and setter for currentEmployeeID
    public static int getCurrentEmployeeID() {
        return currentEmployeeID;
    }

    public static void setCurrentEmployeeID(int currentEmployeeID) {
        Employee.currentEmployeeID = currentEmployeeID;
    }

    public static void main(String[] args) {
        Employee employee = Employee.RetrieveData(getCurrentEmployeeID());
        System.out.println(employee.employeeID);
        System.out.println(employee.tasks);
    }





}

