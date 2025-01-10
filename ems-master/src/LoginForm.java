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

    public void logout() {
        user = null; // Resetuj korisnika
        tfEmail.setText("");  // Očisti polje za email
        pfPassword.setText(""); // Očisti polje za lozinku
    }

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

                    // Ako je korisnik menadžer (isAdmin == 1), otvori ManagerForm
                    if (user.getIsAdmin() == 3) { // Ako je menadžer
                        Manager managerForm = new Manager();  // Pokrećemo formu za menadžera
                        managerForm.setVisible(true);
                    }
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
        final String DB_URL = "jdbc:mysql://localhost:3306/managment?serverTimeZone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(
                     "SELECT id, name, email, phone, address, isAdmin FROM users WHERE email = ? AND password = ?")) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                int isAdminValue = rs.getInt("isAdmin");

                user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        isAdminValue
                );
            }
        } catch (SQLException e) {
            // Ispisivanje detaljnijih informacija o grešci u bazi
            JOptionPane.showMessageDialog(this, "Greška prilikom autentifikacije: " + e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();  // Ovo će prikazati detalje greške u konzoli
        }

        return user;
    }


    // Testiranje LoginForm-a
    public static void main(String[] args) {
        LoginForm loginForm = new LoginForm(null);
        User user = loginForm.user;
        if (user != null) {
            System.out.println("Uspješan prijem korisnika: " + user.getName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Phone: " + user.getPhone());
            System.out.println("Address: " + user.getAddress());
        } else {
            System.out.println("Autentifikacija otkazana.");
        }
    }
}
