import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/employee_db?allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static String user;
    private static String password;

    public static void setCredentials(String userName, String userPassword) {
        user = userName;
        password = userPassword;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, user, password);
    }
}
