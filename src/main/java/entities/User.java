package entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private int id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
    private boolean isVerified;
    private boolean isActive;
    private String city;
    private String country;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructeurs
    public User() {
        this.isActive = true;
        this.isVerified = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public User(String email, String password, String firstName, String lastName, String role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.isActive = true;
        this.isVerified = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public User(int id, String email, String password, String firstName, String lastName, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.isActive = true;
        this.isVerified = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ================ ID ================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // ================ EMAIL ================
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // ================ PASSWORD ================
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // ================ FIRST NAME ================
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    // ================ LAST NAME ================
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    // ================ PHONE ================
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    // ================ ROLE ================
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // ================ VERIFIED ================
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    // ================ ACTIVE ================
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // ================ CITY ================
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    // ================ COUNTRY ================
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    // ================ CREATED AT ================
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // ================ UPDATED AT ================
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ================ UTILITAIRES ================
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isAgent() {
        return "AGENT".equals(role);
    }

    public boolean isUser() {
        return "USER".equals(role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                ", isVerified=" + isVerified +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}