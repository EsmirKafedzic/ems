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
        setContentPane(panelAssignTask);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setModal(true);

        populateUsers();

        btnAssign.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String taskDescription = taTaskDescription.getText().trim();
                String selectedUser = (String) userComboBox.getSelectedItem();

                if (taskDescription.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Opis zadatka ne može biti prazan!", "Greška", JOptionPane.ERROR_MESSAGE);
                } else if (selectedUser == null || selectedUser.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Molimo odaberite korisnika!", "Greška", JOptionPane.ERROR_MESSAGE);
                } else {
                    int userId = getUserId(selectedUser);
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
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/managment?serverTimeZone=UTC", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM users");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                userComboBox.addItem(name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja korisnika!", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getUserId(String userName) {
        int userId = -1;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/managment?serverTimeZone=UTC", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM users WHERE name = ?")) {

            stmt.setString(1, userName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("id");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom dohvaćanja ID-a korisnika!", "Greška", JOptionPane.ERROR_MESSAGE);
        }
        return userId;
    }

    private void assignTaskToUser(int userId, String taskDescription) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/managment?serverTimeZone=UTC", "root", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO tasks (user_id, description, name) VALUES (?, ?, ?)")) {

            stmt.setInt(1, userId);
            stmt.setString(2, taskDescription);
            stmt.setString(3, "Zadatak"); // Dodavanje vrijednosti za 'name' kako bi se izbjegla SQL greška

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Zadatak uspješno dodijeljen korisniku!", "Uspješno", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom dodjeljivanja zadatka!", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
}
