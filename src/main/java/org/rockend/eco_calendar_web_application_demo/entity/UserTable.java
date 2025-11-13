package org.rockend.eco_calendar_web_application_demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserTable {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "username",  unique = true, nullable = false, length = 100)
    private String username;

    public UserTable() { }

    public UserTable(int id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
