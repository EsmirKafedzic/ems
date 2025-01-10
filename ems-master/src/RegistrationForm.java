import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegistrationForm extends JDialog{
    private JTextField tfName;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JTextField tfAddress;
    private JPasswordField pfPassword;
    private JPasswordField pfConfirmPassword;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel registerPanel;

    public  RegistrationForm(JFrame parent) {
        super(parent);
        setTitle("Create new acc");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450,475));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        setVisible(true);
    }
    private void registerUser() {
        String name = tfName.getText();
        String email = tfEmail.getText();
        String phone = tfPhone.getText();
        String address = tfAddress.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String confirmPassword = String.valueOf(pfConfirmPassword.getPassword());

        if(name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Unesite sve podatke", "Pokusajte ponovo",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Vas lozinka se nepodudara",
                    "try again", JOptionPane.ERROR_MESSAGE);
            return;
        }
        user = addUserToDatabase(name, email, phone, address, password);
        if(user != null) {
            dispose();
        }
    }

    public User user;
    private User addUserToDatabase(String name, String email, String phone, String address, String password) {
        User user = null;
        final String DB_URL = "jdbc:mysql://localhost:3306/managment?serverTimeZone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Dodavanje korisnika u bazu
            String sql = "INSERT INTO users (name, email, phone, address, password, isAdmin) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, password);
            preparedStatement.setBoolean(6, false); // Set default value for isAdmin (false, regular user)

            int addedRows = preparedStatement.executeUpdate();

// Ako je korisnik uspešno dodat, povuci podatke o njemu
            if (addedRows > 0) {
                String getUserSql = "SELECT id, name, email, phone, address, isAdmin FROM users WHERE email = ?";
                preparedStatement = conn.prepareStatement(getUserSql);
                preparedStatement.setString(1, email);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    // Kreiraj User objekat
                    boolean isAdminBoolean = rs.getBoolean("isAdmin"); // Ovdje uzimamo boolean vrednost
                    int isAdminInt = isAdminBoolean ? 1 : 0;  // Mapiraj boolean na int (1 ili 0)

                    user = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            isAdminInt // Koristi int vrednost za isAdmin
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }



    public static void main(String[] args) {
        RegistrationForm myForm = new RegistrationForm(null);
        User user = myForm.user;
        if(user != null) {
            System.out.println("Uspjesno registrovan korisnik: " + user.name);
        }
        else {
            System.out.println("Registracija neuspjesna");
        }
    }
}
