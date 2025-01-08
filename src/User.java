public class User {
    public int id;
    public String name;
    public String email;
    public String phone;
    public String address;
    public boolean isAdmin; // Dodaj ovu promenljivu

    public User(int id, String name, String email, String phone, String address, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.isAdmin = isAdmin;


    }

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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}

