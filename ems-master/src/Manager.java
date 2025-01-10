import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Manager extends JFrame {
    private JPanel mainPanel;
    private JTable userTable;
    private JButton updateSalaryButton;

    public Manager() {
        setContentPane(mainPanel);
        setTitle("Manager Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicijalizacija tabele
        initializeTable();

        // Dugme za ažuriranje plata
        updateSalaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSalaries();
            }
        });

        // Učitavanje korisnika iz baze
        loadUsersIntoTable();
    }

    // Funkcija za inicijalizaciju tabele
    private void initializeTable() {
        String[] columnNames = {"ID", "Ime", "Email", "Telefon", "Adresa", "Uloga", "Plata"};
        DefaultTableModel tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Samo kolona "Plata" je izmenjiva
            }
        };
        userTable.setModel(tableModel);
    }

    // Funkcija za učitavanje korisnika iz baze u tabelu
    private void loadUsersIntoTable() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/managment", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, email, phone, address, role, salary FROM users")) {

            DefaultTableModel model = (DefaultTableModel) userTable.getModel();
            model.setRowCount(0); // Očisti tabelu

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("role"),
                        rs.getDouble("salary")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri učitavanju korisnika: " + ex.getMessage());
        }
    }

    // Funkcija za ažuriranje plata u bazi
    private void updateSalaries() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/managment", "root", "")) {
            DefaultTableModel model = (DefaultTableModel) userTable.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                int userId = (int) model.getValueAt(i, 0);
                double salary = Double.parseDouble(model.getValueAt(i, 6).toString());

                // Ažuriranje plate u bazi
                try (PreparedStatement stmt = conn.prepareStatement("UPDATE users SET salary = ? WHERE id = ?")) {
                    stmt.setDouble(1, salary);
                    stmt.setInt(2, userId);
                    stmt.executeUpdate();
                }
            }
            JOptionPane.showMessageDialog(this, "Plate su uspešno ažurirane!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška pri ažuriranju plata: " + ex.getMessage());
        }
    }
}
