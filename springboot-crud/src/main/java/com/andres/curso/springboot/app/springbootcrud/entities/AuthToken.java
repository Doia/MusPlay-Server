package com.andres.curso.springboot.app.springbootcrud.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "auth_tokens", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "refreshToken")
})
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String refreshToken;

    public AuthToken() {}

    public AuthToken(String username, String refreshToken) {
        this.username = username;
        this.refreshToken = refreshToken;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
