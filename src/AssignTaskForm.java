import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AssignTaskForm extends JDialog {
    private JPanel panelAssignTask;
    private JTextArea taTaskDescription;
    private JButton btnAssign;
    private JComboBox<String> userComboBox;

    public AssignTaskForm(JFrame parent) {
        super(parent);
        setTitle("Dodjela zadatka");
        setContentPane(panelAssignTask); // panelAssignTask mora biti inicijalizovan
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setModal(true);

        populateUsers(); // Popunjavanje ComboBox-a korisnicima

        // Dodaj funkcionalnost dugmeta
        btnAssign.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String taskDescription = taTaskDescription.getText();
                String selectedUser = (String) userComboBox.getSelectedItem();

                if (taskDescription.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Opis zadatka ne može biti prazan!", "Greška", JOptionPane.ERROR_MESSAGE);
                } else if (selectedUser == null) {
                    JOptionPane.showMessageDialog(null, "Molimo odaberite korisnika!", "Greška", JOptionPane.ERROR_MESSAGE);
                } else {
                    int userId = getUserId(selectedUser); // Dohvati ID korisnika
                    if (userId != -1) {
                        assignTaskToUser(userId, taskDescription);
                    } else {
                        JOptionPane.showMessageDialog(null, "Greška: Korisnik nije pronađen!", "Greška", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void populateUsers() {
        try {
            // Priključak na bazu
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/managment?serverTimeZone=UTC", "root", "");
            String sql = "SELECT name FROM users"; // Pretpostavlja se da tabela korisnika ima kolonu 'name'
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                userComboBox.addItem(name); // Dodavanje korisnika u ComboBox
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja korisnika!", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Metoda koja dohvaća ID korisnika na osnovu njegovog imena
    private int getUserId(String userName) {
        int userId = -1;
        try {
            // Priključak na bazu
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/managment?serverTimeZone=UTC", "root", "");
            String sql = "SELECT id FROM users WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                userId = rs.getInt("id"); // Dohvatanje ID-a korisnika
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom dohvaćanja ID-a korisnika!", "Greška", JOptionPane.ERROR_MESSAGE);
        }
        return userId;
    }

    private void assignTaskToUser(int userId, String taskDescription) {
        try {
            // Priključak na bazu
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/managment?serverTimeZone=UTC", "root", "");
            String sql = "INSERT INTO tasks (user_id, description) VALUES (?, ?)"; // Promenili smo kolonu sa 'username' na 'user_id'
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId); // Koristimo ID korisnika
            stmt.setString(2, taskDescription);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Zadatak uspešno dodeljen korisniku!", "Uspešno", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Zatvaranje prozora nakon uspešne dodele
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom dodeljivanja zadatka!", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
}
