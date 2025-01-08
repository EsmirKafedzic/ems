import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DashboardForm extends JFrame {
    private JPanel dashboardPanel;
    private JLabel lbAdmin;
    private JButton btnRegister;
    private JButton btnViewUsers;
    private JButton btnLogout;
    private JButton btnAssignTask;
    private JLabel lblTaskDescription; // Prikazivanje zadatka
    private JButton btnCompleteTask; // Dugme za završavanje zadatka
    private JButton btnShowAllTasks; // Dugme za prikaz svih zadataka
    private JLabel lbReg;
    private JLabel lbShow;
    private JLabel lbAdd;
    private JLabel lbShowT;
    private JTextField tfSearch; // Polje za unos (ime korisnika + zadatak)
    private JButton btnSearch;   // Dugme za pretragu
    private JLabel srLabelTask;

    private User user; // Globalna varijabla user

    public DashboardForm() {
        setTitle("Dashboard");
        setContentPane(dashboardPanel);
        setMinimumSize(new Dimension(500, 429));
        setSize(900, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        boolean hasRegisteredUsers = connectToDatabase();

        btnAssignTask.addActionListener(e -> {
            AssignTaskForm form = new AssignTaskForm(this);
            form.setVisible(true);
        });


        if (hasRegisteredUsers) {
            // Ako postoji registrovanih korisnika, pozivamo LoginForm
            LoginForm loginForm = new LoginForm(this);
            user = loginForm.user;

            if (user != null) {
                lbAdmin.setText("User: " + user.name);

                // Dodajemo provjeru da li je korisnik admin (ID = 1)
                if (user.getId() == 1) {
                    // Ako je korisnik admin, vidljiva su dugmadi za Show Users i Register
                    btnViewUsers.setVisible(true); // Dugme za prikaz korisnika
                    btnRegister.setVisible(true);  // Dugme za registraciju novog korisnika
                    btnAssignTask.setVisible(true); // Dugme za dodelu zadatka
                    btnCompleteTask.setVisible(false); // Adminu nije potrebno dugme za završavanje zadatka
                    lblTaskDescription.setVisible(false); // Adminu nije potrebno dugme za zadatke
                    btnShowAllTasks.setVisible(true); // Dugme za prikaz svih zadataka za admina
                    lbAdd.setVisible(true);  // Prikazivanje labela samo za admina
                    lbReg.setVisible(true);
                    lbShow.setVisible(true);
                    lbShowT.setVisible(true);

                    // Prikaz svih zadataka odmah pri prijavi admina

                } else {
                    // Ako nije admin, sakrivamo dugmadi za admin funkcije
                    btnViewUsers.setVisible(false);  // Obični korisnik ne može da vidi korisnike
                    btnRegister.setVisible(false);  // Obični korisnik ne treba opciju za registraciju
                    btnAssignTask.setVisible(false); // Obični korisnik ne može da dodeljuje zadatke
                    btnCompleteTask.setVisible(true); // Obični korisnik može da završi zadatak
                    lblTaskDescription.setVisible(true); // Obični korisnik vidi svoj zadatak
                    btnShowAllTasks.setVisible(false); // Obični korisnik ne vidi dugme za prikaz svih zadataka
                    lbAdd.setVisible(false);  // Sakrijte labele za običnog korisnika
                    lbReg.setVisible(false);
                    lbShow.setVisible(false);
                    lbShowT.setVisible(false);
                    tfSearch.setVisible(false);
                    btnSearch.setVisible(false);
                    srLabelTask.setVisible(false);
                    showUserTask();  // Prikazivanje zadatka za običnog korisnika
                }

                setLocationRelativeTo(null); // Centriranje prozora
                setVisible(true);

            } else {
                dispose(); // Zatvori DashboardForm ako nije prijavljen korisnik
            }
        } else {
            // Ako nema registrovanih korisnika, pozivamo RegistrationForm
            RegistrationForm registrationForm = new RegistrationForm(this);
            user = registrationForm.user;

            if (user != null) {
                lbAdmin.setText("User: " + user.name);
                if (user.isAdmin) {
                    btnViewUsers.setVisible(true); // Admin može da vidi korisnike
                    lbAdd.setVisible(true);  // Prikazivanje labela samo za admina
                    lbReg.setVisible(true);
                    lbShow.setVisible(true);
                    lbShowT.setVisible(true);
                } else {
                    btnViewUsers.setVisible(false); // Obični korisnik ne može da vidi korisnike
                    lbAdd.setVisible(false);  // Sakrijte labele za običnog korisnika
                    lbReg.setVisible(false);
                    lbShow.setVisible(false);
                    lbShowT.setVisible(false);
                }
                setLocationRelativeTo(null);
                setVisible(true);
            } else {
                dispose(); // Ako korisnik nije registrovan, zatvori DashboardForm
            }
        }
        btnSearch.addActionListener(e -> performSearch());
        btnCompleteTask.addActionListener(e -> completeUserTask());
        btnShowAllTasks.addActionListener(e -> showAllTasks());
        btnRegister.addActionListener(e -> {
            RegistrationForm registrationForm = new RegistrationForm(DashboardForm.this);
//            registrationForm.setVisible(true);
        });
        btnViewUsers.addActionListener(e -> showAllUsers());
        // Dugme za odjavu
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(DashboardForm.this,
                    "Da li ste sigurni da želite da se odjavite?",
                    "Logout", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Resetovanje dugmadi i elemenata
                btnViewUsers.setVisible(false);
                btnRegister.setVisible(false);
                btnAssignTask.setVisible(false);
                btnCompleteTask.setVisible(false);
                lblTaskDescription.setText("");

                setVisible(false); // Sakrij DashboardForm

                // Ponovna prijava
                LoginForm loginForm1 = new LoginForm(DashboardForm.this);
                User loggedInUser = loginForm1.user;
                if (loggedInUser != null) {
                    user = loggedInUser; // Setuj ponovo korisnika
                    lbAdmin.setText("User: " + user.name);

                    // Prikazivanje odgovarajućih dugmadi i opcija za prijavljenog korisnika
                    if (user.isAdmin()) {
                        btnViewUsers.setVisible(true); // Admin može da vidi korisnike
                        btnRegister.setVisible(true);  // Admin može da registruje novog korisnika
                        btnAssignTask.setVisible(true); // Admin može da dodeljuje zadatke
                        btnCompleteTask.setVisible(false); // Admin ne treba dugme za završavanje zadatka
                        lblTaskDescription.setVisible(false); // Admin ne treba da vidi zadatke
                        btnShowAllTasks.setVisible(true);
                        lbAdd.setVisible(true);  // Prikazivanje labela samo za admina
                        lbReg.setVisible(true);
                        lbShow.setVisible(true);
                        lbShowT.setVisible(true);
                        tfSearch.setVisible(true);
                        btnSearch.setVisible(true);
                        srLabelTask.setVisible(true);
                    } else {
                        btnViewUsers.setVisible(false); // Obični korisnik ne može da vidi korisnike
                        btnRegister.setVisible(false);  // Obični korisnik ne treba opciju za registraciju
                        btnAssignTask.setVisible(false); // Obični korisnik ne može da dodeljuje zadatke
                        btnCompleteTask.setVisible(true); // Obični korisnik može da završi zadatak
                        lblTaskDescription.setVisible(true); // Obični korisnik vidi svoj zadatak
                        lbAdd.setVisible(false);  // Sakrijte labele za običnog korisnika
                        lbReg.setVisible(false);
                        lbShow.setVisible(false);
                        lbShowT.setVisible(false);
                        btnShowAllTasks.setVisible(false);
                        tfSearch.setVisible(false);
                        btnSearch.setVisible(false);
                        srLabelTask.setVisible(false);
                        showUserTask();  // Prikazivanje zadatka za običnog korisnika
                    }

                    setVisible(true); // Prikazivanje DashboardForm
                } else {
                    JOptionPane.showMessageDialog(DashboardForm.this,
                            "Login failed.", "Error", JOptionPane.ERROR_MESSAGE);
                    dispose(); // Ako prijava ne uspe, zatvori aplikaciju
                }
            }
        });
    }


    private void performSearch() {
        String searchTerm = tfSearch.getText().toLowerCase().trim();  // Uzimamo tekst iz input polja

        // Proveravamo da li je unos prazan
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Molimo vas da unesete bar jedan kriterijum za pretragu.", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final String DB_URL = "jdbc:mysql://localhost/managment?serverTimeZone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            // SQL upit za pretragu tačnog imena korisnika ili opisa zadatka
            String sql = "SELECT t.id, t.description, t.completed, u.name AS user_name " +
                    "FROM tasks t " +
                    "JOIN users u ON t.user_id = u.id " +
                    "WHERE u.name = ? OR t.description LIKE ?";  // Koristi "=" za tačno ime korisnika

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Postavljamo parametre za pretragu
                stmt.setString(1, searchTerm);  // Tačno ime korisnika
                stmt.setString(2, "%" + searchTerm + "%");  // Opis zadatka, delimično pretraživanje

                try (ResultSet rs = stmt.executeQuery()) {
                    StringBuilder results = new StringBuilder("Rezultati pretrage:\n\n");

                    while (rs.next()) {
                        int taskId = rs.getInt("id");
                        String taskDescription = rs.getString("description");
                        boolean taskCompleted = rs.getBoolean("completed");
                        String taskStatus = taskCompleted ? "Završen" : "Nije završen";
                        String userName = rs.getString("user_name");

                        results.append("ID: ").append(taskId)
                                .append(", Zadataka: ").append(taskDescription)
                                .append(", Korisnik: ").append(userName)
                                .append(", Status: ").append(taskStatus)
                                .append("\n");
                    }

                    // Prikazivanje korisničkog interfejsa sa rezultatima
                    String resultString = results.toString();
                    if (resultString.trim().isEmpty()) {
                        resultString = "Nema rezultata za vašu pretragu.";
                    }

                    JOptionPane.showMessageDialog(this, resultString, "Rezultati pretrage", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja podataka.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void showAllTasks() {
        final String DB_URL = "jdbc:mysql://localhost/managment?serverTimeZone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT t.id, t.description, t.completed, u.name AS user_name " +
                     "FROM tasks t JOIN users u ON t.user_id = u.id")) {

            StringBuilder taskList = new StringBuilder("Svi zadaci:\n\n");
            while (rs.next()) {
                int taskId = rs.getInt("id");
                String taskDescription = rs.getString("description");
                boolean taskCompleted = rs.getBoolean("completed");
                String taskStatus = taskCompleted ? "Završen" : "Nije završen";
                String userName = rs.getString("user_name");

                taskList.append("ID: ").append(taskId)
                        .append(", Zadataka: ").append(taskDescription)
                        .append(", Korisnik: ").append(userName)
                        .append(", Status: ").append(taskStatus)
                        .append("\n");
            }

            // Prikazivanje korisničkog interfejsa
            String taskString = taskList.toString();
            JOptionPane.showMessageDialog(this, taskString, "Lista svih zadataka", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja zadataka.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showUserTask() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/managment?serverTimeZone=UTC", "root", "");
            String sql = "SELECT description FROM tasks WHERE user_id = ? AND completed = false"; // Pretpostavljamo da postoji kolona 'completed'
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user.getId()); // Postavljamo ID korisnika
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String taskDescription = rs.getString("description");
                lblTaskDescription.setText("Vaš zadatak: " + taskDescription); // Prikazivanje opisa zadatka

                // Ako korisnik ima aktivan zadatak, dugme za završavanje zadatka treba da bude vidljivo
                btnCompleteTask.setVisible(true);
            } else {
                lblTaskDescription.setText("Nemate dodeljen zadatak."); // Ako nema dodeljen zadatak

                // Ako korisnik nema zadatak, dugme za završavanje zadatka treba da bude sakriveno
                btnCompleteTask.setVisible(false);
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja zadatka!", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void showAllUsers() {
        final String DB_URL = "jdbc:mysql://localhost/managment?serverTimeZone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, email, phone, address FROM users")) {

            StringBuilder userList = new StringBuilder("Users:\n\n");
            while (rs.next()) {
                int userId = rs.getInt("id");
                String userName = rs.getString("name");
                userList.append("ID: ").append(userId)
                        .append(", Name: ").append(userName)
                        .append(", Email: ").append(rs.getString("email"))
                        .append(", Phone: ").append(rs.getString("phone"))
                        .append(", Address: ").append(rs.getString("address"))
                        .append("\n");
            }

            // Prikazivanje korisničkog interfejsa
            String userString = userList.toString();
            JOptionPane.showMessageDialog(this, userString, "Lista korisnika", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja korisnika.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void completeUserTask() {
        try {
            // Uspostavi vezu sa bazom
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/managment?serverTimeZone=UTC", "root", "");

            // SQL upit za označavanje zadatka kao završen
            String sql = "UPDATE tasks SET completed = true WHERE user_id = ? AND completed = false";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user.getId()); // Postavljamo ID korisnika da se završi njegov zadatak

            // Izvršavamo upit
            int rowsUpdated = stmt.executeUpdate();

            // Proveravamo da li je neki red ažuriran (tj. zadatak je pronađen i označen kao završen)
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Zadatak je uspešno završen.", "Uspešno", JOptionPane.INFORMATION_MESSAGE);
                lblTaskDescription.setText("Nemate dodeljen zadatak."); // Brisanje prikaza zadatka nakon što je završen

                // Sakrij dugme za završavanje zadatka jer je sada završen
                btnCompleteTask.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Nemate dodeljen zadatak ili je zadatak već završen.", "Greška", JOptionPane.ERROR_MESSAGE);
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom završavanja zadatka.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }




    private boolean connectToDatabase() {
        // Ostatak koda za povezivanje na bazu
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardForm());
    }
}
