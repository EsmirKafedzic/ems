public class User {
    public int id;
    public String name;
    public String email;
    public String phone;
    public String address;
    public int isAdmin; // Promenjen tip u int, umesto boolean

    // Konstruktor koji prihvata int za isAdmin (1 za admin, 2 za super admin)
    public User(int id, String name, String email, String phone, String address, int isAdmin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.isAdmin = isAdmin; // Koristi int umesto boolean
    }

    // Getter za ID
    public int getId() {
        return id;
    }

    // Setter za ID
    public void setId(int id) {
        this.id = id;
    }

    // Getter i setter za druge atribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Getter i setter za isAdmin
    public int getIsAdmin() {
        return isAdmin; // Vraća int vrednost
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin; // Postavlja vrednost int za isAdmin
    }

    // Proveri da li je korisnik menadžer
    public boolean isManager() {
        return isAdmin == 1;
    }
}
