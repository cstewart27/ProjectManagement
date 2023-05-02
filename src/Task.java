import java.sql.*;

public class Task {
    private static int currentTaskID = 1;

    public int id;
    public String name;
    public String description;
    public int progress;
    public String dueDate;
    public boolean paymentStatus;

    public static Task RetrieveData(int taskID) {
        Task task = null;

        try{
            Connection conn = DriverManager.getConnection(DatabaseConnectionManager.url, DatabaseConnectionManager.usernameToDatabase, DatabaseConnectionManager.passwordToDatabase);
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM tasks WHERE id = ?";
            PreparedStatement preparedStmt = conn.prepareStatement(sql);
            preparedStmt.setInt(1, taskID);
            ResultSet rs = preparedStmt.executeQuery();
            if(rs.next()){
                task = new Task();
                task.id = rs.getInt("id");
                task.name = rs.getString("name");
                task.description = rs.getString("description");
                task.progress = rs.getInt("progress");
                task.dueDate = rs.getString("due_date");
                task.paymentStatus = rs.getBoolean("payment_status");
            }
            else {
                System.out.println("Task ID not found");
            }

            conn.close();
            stmt.close();
            rs.close();

        }
        catch (Exception e) {
            System.out.println(e);
        }

        return task;
    }

    public Task UpdateData(Task task){
        try{
            Connection conn = DriverManager.getConnection(DatabaseConnectionManager.url, DatabaseConnectionManager.usernameToDatabase, DatabaseConnectionManager.passwordToDatabase);
            Statement stmt = conn.createStatement();
            String sql = "UPDATE tasks SET name = ?, description = ?, progress = ?, due_date = ?, payment_status = ? WHERE id = ?";
            PreparedStatement preparedStmt = conn.prepareStatement(sql);
            preparedStmt.setString(1, task.name);
            preparedStmt.setString(2, task.description);
            preparedStmt.setInt(3, task.progress);
            preparedStmt.setString(4, task.dueDate);
            preparedStmt.setBoolean(5, task.paymentStatus);
            preparedStmt.setInt(6, task.id);
            preparedStmt.executeUpdate();
            conn.close();
            stmt.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return task;
    }

    //getter and setter for currentTaskID
    public static int getCurrentTaskID() {
        return currentTaskID;
    }

    public static void setCurrentTaskID(int currentTaskID) {
        Task.currentTaskID = currentTaskID;
    }

    public static void main(String[] args) {
        Task task = Task.RetrieveData(14);
        System.out.println(task.id);
        System.out.println(task.name);
        System.out.println(task.description);
        System.out.println(task.progress);
        System.out.println(task.dueDate);
        System.out.println(task.paymentStatus);
    }
}
