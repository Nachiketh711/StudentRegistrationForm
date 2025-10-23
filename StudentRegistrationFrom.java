import javax.swing.*;
import java.awt.*;
import java.sql.*;

 class StudentRegistrationForm extends JFrame {
    private JTextField tfName, tfEmail, tfContact, tfAge;
    private JComboBox<String> cbGender;
    private JButton btnSubmit, btnClear;
    private Connection conn;

    public StudentRegistrationForm() {
        connectDatabase();

        setTitle("Student Registration Form");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        JLabel lblName = new JLabel("Name:");
        JLabel lblEmail = new JLabel("Email:");
        JLabel lblContact = new JLabel("Contact:");
        JLabel lblAge = new JLabel("Age:");
        JLabel lblGender = new JLabel("Gender:");

        tfName = new JTextField(20);
        tfEmail = new JTextField(20);
        tfContact = new JTextField(20);
        tfAge = new JTextField(5);

        String[] genders = {"Select", "Male", "Female", "Other"};
        cbGender = new JComboBox<>(genders);

        btnSubmit = new JButton("Submit");
        btnClear = new JButton("Clear");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        add(lblName, gbc);
        gbc.gridx = 1;
        add(tfName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(lblEmail, gbc);
        gbc.gridx = 1;
        add(tfEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(lblContact, gbc);
        gbc.gridx = 1;
        add(tfContact, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(lblAge, gbc);
        gbc.gridx = 1;
        add(tfAge, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(lblGender, gbc);
        gbc.gridx = 1;
        add(cbGender, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        add(btnSubmit, gbc);
        gbc.gridx = 1;
        add(btnClear, gbc);

        btnSubmit.addActionListener(e -> submitForm());
        btnClear.addActionListener(e -> clearForm());
    }

    private void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Change the DB details as per your setup
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_registration", "root", "772006");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitForm() {
        String name = tfName.getText().trim();
        String email = tfEmail.getText().trim();
        String contact = tfContact.getText().trim();
        String ageStr = tfAge.getText().trim();
        String gender = (String) cbGender.getSelectedItem();

        if(name.isEmpty() || email.isEmpty() || contact.isEmpty() || ageStr.isEmpty() || gender.equals("Select")) {
            JOptionPane.showMessageDialog(this, "Please fill all fields correctly.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(!email.matches("^\\S+@\\S+\\.\\S+$")) {
            JOptionPane.showMessageDialog(this, "Enter a valid email.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(!contact.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Contact must be 10 digits.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int age;
        try {
            age = Integer.parseInt(ageStr);
            if(age < 3 || age > 100) {
                JOptionPane.showMessageDialog(this, "Age should be between 3 and 100.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO students (name, email, contact, age, gender) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, contact);
            pst.setInt(4, age);
            pst.setString(5, gender);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Student registered successfully!");
                clearForm();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        tfName.setText("");
        tfEmail.setText("");
        tfContact.setText("");
        tfAge.setText("");
        cbGender.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentRegistrationForm().setVisible(true);
        });
    }
}