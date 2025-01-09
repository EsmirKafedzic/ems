import javax.swing.*;
import java.awt.*;
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

        boolean hasRegisteredUsers = connectToDatabase();  // Provjerite ako postoje korisnici u bazi

        // Ako postoje registrovani korisnici, pozivamo LoginForm
        if (hasRegisteredUsers) {
            LoginForm loginForm = new LoginForm(this);
            user = loginForm.user;
            System.out.println(user.getIsAdmin());
            if (user != null) {
                lbAdmin.setText("User: " + user.name);

                // Provjera da li je korisnik admin ili super admin
                if (user.getIsAdmin() == 2) {
                    setSuperAdminPrivileges();  // Superadmin privilegije
                } else if (user.getIsAdmin() == 1) {
                    setAdminPrivileges();  // Admin privilegije
                } else {
                    setUserPrivileges();  // Obični korisnik privilegije
                }


                setLocationRelativeTo(null);
                setVisible(true);
            } else {
                dispose();  // Ako nije prijavljen korisnik, zatvori DashboardForm
            }
        } else {
            // Ako nema registrovanih korisnika, pozivamo RegistrationForm
            RegistrationForm registrationForm = new RegistrationForm(this);
            user = registrationForm.user;

            if (user != null) {
                lbAdmin.setText("User: " + user.name);

                // Prikazivanje dugmadi i privilegija na osnovu korisničkog tipa
                if (user.getIsAdmin() == 1) {
                    setAdminPrivileges();
                } else if (user.getIsAdmin() == 2) {
                    setSuperAdminPrivileges();
                } else {
                    setUserPrivileges();
                }

                setLocationRelativeTo(null);
                setVisible(true);
            } else {
                dispose();  // Ako korisnik nije registrovan, zatvori DashboardForm
            }
        }




        btnSearch.addActionListener(e -> performSearch());
        btnCompleteTask.addActionListener(e -> completeUserTask());
        btnShowAllTasks.addActionListener(e -> showAllTasks());
        btnAssignTask.addActionListener(e -> {
            AssignTaskForm form = new AssignTaskForm(this);
            form.setVisible(true);
        });

        btnRegister.addActionListener(e -> {
            RegistrationForm registrationForm = new RegistrationForm(DashboardForm.this);
        });

        btnViewUsers.addActionListener(e -> showAllUsers());

        // Prilagoditi kod u "btnLogout" i kod ponovne prijave:
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(DashboardForm.this,
                    "Da li ste sigurni da želite da se odjavite?",
                    "Logout", JOptionPane.YES_NO_OPTION);
            this.user = null;

            if (confirm == JOptionPane.YES_OPTION) {
                resetDashboard(); // Resetovanje dugmadi i elemenata
                setVisible(false); // Sakrij DashboardForm

                // Ponovna prijava
                LoginForm loginForm1 = new LoginForm(DashboardForm.this);
                User loggedInUser = loginForm1.user;
                if (loggedInUser != null) {
                    user = loggedInUser; // Setuj ponovo korisnika
                    lbAdmin.setText("User: " + user.name);

                    // Prikazivanje odgovarajućih dugmadi i opcija za prijavljenog korisnika
                    if (user.getIsAdmin() == 1) {
                        setAdminPrivileges();
                    } else if (user.getIsAdmin() == 2) {
                        setSuperAdminPrivileges(); // Pozovite za super admina
                    } else {
                        setUserPrivileges();
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

    private void setAdminPrivileges() {
        // Resetuj sve komponente
        btnSearch.setVisible(false);
        tfSearch.setVisible(false);

        // Privilegije koje imaju admini
        btnViewUsers.setVisible(true);
        btnRegister.setVisible(true);
        btnAssignTask.setVisible(true);
        btnCompleteTask.setVisible(false);  // Admini ne završavaju zadatke
        lblTaskDescription.setVisible(false);
        btnShowAllTasks.setVisible(true);
        lbAdd.setVisible(true);
        lbReg.setVisible(true);
        lbShow.setVisible(true);
        lbShowT.setVisible(true);
        srLabelTask.setVisible(true);

        // Admini mogu da vide korisnike, ali ne mogu da ih brišu
        // Uklonite dugme za brisanje korisnika, jer to može biti samo za superadmina
        // Brisanje korisnika je omogućeno samo za superadmina, ne za admina
    }


    private void setSuperAdminPrivileges() {
        // Resetuj sve komponente
        btnSearch.setVisible(true);
        tfSearch.setVisible(true);

        // Superadmini imaju sve privilegije admina, plus dodatne privilegije
        btnViewUsers.setVisible(true);
        btnRegister.setVisible(true);
        btnAssignTask.setVisible(true);
        btnCompleteTask.setVisible(false);  // Superadmini ne završavaju zadatke
        lblTaskDescription.setVisible(false);
        btnShowAllTasks.setVisible(true);
        lbAdd.setVisible(true);
        lbReg.setVisible(true);
        lbShow.setVisible(true);
        lbShowT.setVisible(true);
        srLabelTask.setVisible(true);

        // Superadmini mogu brisati korisnike
        // Brisanje korisnika se omogućava samo za superadmina
    }

    private void setUserPrivileges() {
        // Resetuj sve komponente
        btnSearch.setVisible(false);
        tfSearch.setVisible(false);

        // Obični korisnici imaju minimalne privilegije
        btnViewUsers.setVisible(false);
        btnRegister.setVisible(false);
        btnAssignTask.setVisible(false);
        btnCompleteTask.setVisible(true);  // Samo obični korisnici mogu završavati zadatke
        lblTaskDescription.setVisible(true);
        btnShowAllTasks.setVisible(false);
        lbAdd.setVisible(false);
        lbReg.setVisible(false);
        lbShow.setVisible(false);
        lbShowT.setVisible(false);
        tfSearch.setVisible(false);
        btnSearch.setVisible(false);
        srLabelTask.setVisible(false);

        showUserTask();
    }


    // Metoda za resetovanje Dashboard-a pri odjavi
    private void resetDashboard() {
        btnViewUsers.setVisible(false);
        btnRegister.setVisible(false);
        btnAssignTask.setVisible(false);
        btnCompleteTask.setVisible(false);
        lblTaskDescription.setText("");
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
    public void clearSession() {
        this.user = null;  // Resetuj sve podatke koji su vezani za trenutnu sesiju
        // Dodaj sve druge potrebne akcije za resetovanje sesije
    }



    private void showAllUsers() {
        final String DB_URL = "jdbc:mysql://localhost/managment?serverTimeZone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, email, phone, address FROM users")) {

            // Kreiramo StringBuilder za sve korisnike koje ćemo prikazati u JOptionPane
            StringBuilder userList = new StringBuilder("Users:\n\n");
            while (rs.next()) {
                int userId = rs.getInt("id");
                String userName = rs.getString("name");
                String userEmail = rs.getString("email");
                String userPhone = rs.getString("phone");
                String userAddress = rs.getString("address");

                userList.append("ID: ").append(userId)
                        .append(", Name: ").append(userName)
                        .append(", Email: ").append(userEmail)
                        .append(", Phone: ").append(userPhone)
                        .append(", Address: ").append(userAddress)
                        .append("\n");
            }

            // Prikazivanje korisničkog interfejsa sa svim korisnicima
            String userString = userList.toString();
            if (userString.trim().isEmpty()) {
                userString = "Nema korisnika u sistemu.";
            }

            // Ako je superadmin, omogućavamo mu da obriše korisnike
            if (user.getIsAdmin() == 2) {
                String userInput = JOptionPane.showInputDialog(this, userString + "\nUnesite ID korisnika kojeg želite da obrišete:");

                // Ako korisnik nije uneo ID (pritisnuo Cancel ili ostavio prazno), izlazimo
                if (userInput == null || userInput.trim().isEmpty()) {
                    return;
                }

                try {
                    int userIdToDelete = Integer.parseInt(userInput.trim()); // Pretvaramo uneseni tekst u broj
                    // Pozivamo metodu za brisanje korisnika sa tim ID-om
                    deleteUser(userIdToDelete);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Uneti ID nije validan broj!", "Greška", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Ako korisnik nije superadmin, samo prikazujemo listu korisnika bez brisanja
                JOptionPane.showMessageDialog(this, userString, "Lista korisnika", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom učitavanja korisnika.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void deleteUser(int userId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/managment?serverTimeZone=UTC", "root", "")) {
            String sql = "DELETE FROM users WHERE id = ?"; // Brisanje korisnika na osnovu ID-a
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Korisnik sa ID " + userId + " je uspešno obrisan.", "Uspešno", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Korisnik sa tim ID-om ne postoji.", "Greška", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Greška prilikom brisanja korisnika.", "Greška", JOptionPane.ERROR_MESSAGE);
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
