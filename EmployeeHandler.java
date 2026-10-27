import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeHandler {

    private static final String[] COLUMNS = {"Employee ID", "Name", "Department", "Salary"};
    private static final String SELECT = "SELECT emp_id, name, department, salary FROM employee ";

    public DefaultTableModel emptyModel() {
        return new EmployeeTableModel();
    }

    public DefaultTableModel searchEmployees(Integer id, String name) throws SQLException {
        if (id == null) {
            return query(SELECT + "WHERE name LIKE ? ORDER BY emp_id", "%" + name + "%");
        }
        return query(SELECT + "WHERE emp_id = ? AND name LIKE ? ORDER BY emp_id", id, "%" + name + "%");
    }

    public DefaultTableModel filterByDepartment(String department) throws SQLException {
        return query(SELECT + "WHERE department = ? ORDER BY emp_id", department);
    }

    public DefaultTableModel searchBySalaryRange(BigDecimal min, BigDecimal max) throws SQLException {
        return query(SELECT + "WHERE salary BETWEEN ? AND ? ORDER BY salary", min, max);
    }

    public DefaultTableModel sortBySalary(boolean ascending) throws SQLException {
        if (ascending) {
            return query(SELECT + "ORDER BY salary ASC");
        }
        return query(SELECT + "ORDER BY salary DESC");
    }

    private DefaultTableModel query(String sql, Object... parameters) throws SQLException {
        DefaultTableModel model = new EmployeeTableModel();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    model.addRow(new Object[]{
                            resultSet.getInt("emp_id"),
                            resultSet.getString("name"),
                            resultSet.getString("department"),
                            resultSet.getBigDecimal("salary")
                    });
                }
            }
        }
        return model;
    }

    private static class EmployeeTableModel extends DefaultTableModel {

        EmployeeTableModel() {
            super(COLUMNS, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            if (column == 0) {
                return Integer.class;
            }
            if (column == 3) {
                return BigDecimal.class;
            }
            return String.class;
        }
    }
}
