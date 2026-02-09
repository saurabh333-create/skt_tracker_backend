package com.skt.bean;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Getter
@Entity
@Data
@Table(name = "auth")
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id")
    private Long id;

    @Column(name = "auth_username")
    private String username;

    @Column(name = "auth_password")
    private String password;

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
