import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class LoginForm extends JDialog {
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JButton btnOK;
    private JButton btnCancel;
    private JPanel loginPanel;

    public User user; // Trenutno prijavljeni korisnik

    public LoginForm(JFrame parent) {
        super(parent);
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450, 475));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Akcija za dugme "OK"
        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = String.valueOf(pfPassword.getPassword());

                user = authenticateUser(email, password);

                if (user != null) {
                    dispose(); // Zatvori LoginForm nakon uspešne autentifikacije
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this, "Email ili Password nisu ispravni", "Pokušajte ponovo", JOptionPane.ERROR_MESSAGE);
                }
            }
        });



        // Akcija za dugme "Cancel"
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Zatvara login formu
            }
        });

        setVisible(true);
    }

    // Autentifikacija korisnika na osnovu email-a i password-a
    private User authenticateUser(String email, String password) {
        User user = null;
        final String DB_URL = "jdbc:mysql://localhost/managment?serverTimeZone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(
                     "SELECT id, name, email, phone, address, isAdmin FROM users WHERE email = ? AND password = ?")) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getBoolean("isAdmin")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    // Testiranje LoginForm-a
    public static void main(String[] args) {
        LoginForm loginForm = new LoginForm(null);
        User user = loginForm.user;
        if (user != null) {
            System.out.println("Uspješan prijem korisnika: " + user.name);
            System.out.println("Email: " + user.email);
            System.out.println("Phone: " + user.phone);
            System.out.println("Address: " + user.address);
        } else {
            System.out.println("Autentifikacija otkazana.");
        }
    }
}
