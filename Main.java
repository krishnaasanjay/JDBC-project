import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class Main extends JFrame {

    private final JTextField idField = new JTextField(8);
    private final JTextField nameField = new JTextField(14);
    private final JTextField departmentField = new JTextField(14);
    private final JTextField minSalaryField = new JTextField(8);
    private final JTextField maxSalaryField = new JTextField(14);
    private final JComboBox<String> sortBox = new JComboBox<>(new String[]{"Ascending", "Descending"});
    private final JTable table = new JTable();
    private final JLabel statusLabel = new JLabel("Ready");
    private final EmployeeHandler handler = new EmployeeHandler();

    public Main() {
        super("Employee Database Search Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        table.setModel(handler.emptyModel());
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);

        content.add(buildFormPanel(), BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(statusLabel, BorderLayout.SOUTH);

        setContentPane(content);
        setSize(900, 560);
        setMinimumSize(new Dimension(760, 460));
        setLocationRelativeTo(null);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Search Criteria"));

        addField(panel, "Employee ID (optional):", idField, 0, 0);
        addField(panel, "Employee Name:", nameField, 1, 0);
        addField(panel, "Department:", departmentField, 2, 0);
        addField(panel, "Minimum Salary:", minSalaryField, 0, 1);
        addField(panel, "Maximum Salary:", maxSalaryField, 1, 1);
        addField(panel, "Sort by Salary:", sortBox, 2, 1);

        JButton searchButton = new JButton("Search Employees");
        JButton filterButton = new JButton("Filter by Department");
        JButton rangeButton = new JButton("Search by Salary Range");
        JButton sortButton = new JButton("Sort by Salary");
        JButton clearButton = new JButton("Clear");

        searchButton.addActionListener(e -> searchEmployees());
        filterButton.addActionListener(e -> filterByDepartment());
        rangeButton.addActionListener(e -> searchBySalaryRange());
        sortButton.addActionListener(e -> sortBySalary());
        clearButton.addActionListener(e -> clearAll());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        buttons.add(searchButton);
        buttons.add(filterButton);
        buttons.add(rangeButton);
        buttons.add(sortButton);
        buttons.add(clearButton);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 6;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttons, c);

        return panel;
    }

    private void addField(JPanel panel, String labelText, JComponent field, int column, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridy = row;
        c.gridx = column * 2;
        c.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(labelText), c);

        c.gridx = column * 2 + 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, c);
    }

    private void searchEmployees() {
        String idText = idField.getText().trim();
        String name = nameField.getText().trim();

        if (idText.isEmpty() && name.isEmpty()) {
            showWarning("Enter an Employee Name or an Employee ID to search.");
            return;
        }

        Integer id = null;
        if (!idText.isEmpty()) {
            try {
                id = Integer.parseInt(idText);
            } catch (NumberFormatException e) {
                showWarning("Employee ID must be a whole number.");
                return;
            }
        }

        try {
            showResults(handler.searchEmployees(id, name));
        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    private void filterByDepartment() {
        String department = departmentField.getText().trim();
        if (department.isEmpty()) {
            showWarning("Enter a Department to filter.");
            return;
        }

        try {
            showResults(handler.filterByDepartment(department));
        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    private void searchBySalaryRange() {
        BigDecimal min = readSalary(minSalaryField, "Minimum Salary");
        if (min == null) {
            return;
        }
        BigDecimal max = readSalary(maxSalaryField, "Maximum Salary");
        if (max == null) {
            return;
        }
        if (min.compareTo(max) > 0) {
            showWarning("Minimum Salary cannot be greater than Maximum Salary.");
            return;
        }

        try {
            showResults(handler.searchBySalaryRange(min, max));
        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    private void sortBySalary() {
        boolean ascending = sortBox.getSelectedIndex() == 0;
        try {
            showResults(handler.sortBySalary(ascending));
        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    private void clearAll() {
        idField.setText("");
        nameField.setText("");
        departmentField.setText("");
        minSalaryField.setText("");
        maxSalaryField.setText("");
        sortBox.setSelectedIndex(0);
        table.setModel(handler.emptyModel());
        statusLabel.setText("Cleared. Ready for a new search.");
    }

    private BigDecimal readSalary(JTextField field, String label) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            showWarning("Enter the " + label + ".");
            return null;
        }
        try {
            BigDecimal value = new BigDecimal(text);
            if (value.signum() < 0) {
                showWarning(label + " cannot be negative.");
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            showWarning(label + " must be a number.");
            return null;
        }
    }

    private void showResults(DefaultTableModel model) {
        table.setModel(model);
        int count = model.getRowCount();
        if (count == 0) {
            statusLabel.setText("No matching employees found.");
            JOptionPane.showMessageDialog(this, "No matching employees were found.",
                    "No Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            statusLabel.setText(count + " employee record(s) found.");
        }
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.WARNING_MESSAGE);
    }

    private void showDatabaseError(SQLException e) {
        statusLabel.setText("Database error.");
        JOptionPane.showMessageDialog(this,
                "Database error: " + e.getMessage()
                        + "\n\nCheck that MySQL is running and that employee_db exists.",
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    private static boolean login() {
        while (true) {
            JTextField userField = new JTextField("root");
            JPasswordField passwordField = new JPasswordField();
            Object[] fields = {"MySQL username:", userField, "MySQL password:", passwordField};

            int choice = JOptionPane.showConfirmDialog(null, fields, "Database Login",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) {
                return false;
            }

            DBConnection.setCredentials(userField.getText().trim(), new String(passwordField.getPassword()));
            try (Connection connection = DBConnection.getConnection()) {
                return true;
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                        "Could not connect to MySQL:\n" + e.getMessage(),
                        "Connection Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            if (!login()) {
                System.exit(0);
            }
            new Main().setVisible(true);
        });
    }
}
