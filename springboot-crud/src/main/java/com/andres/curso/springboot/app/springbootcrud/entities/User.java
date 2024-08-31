package com.andres.curso.springboot.app.springbootcrud.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.andres.curso.springboot.app.springbootcrud.dto.PrivacyLevel;
import com.andres.curso.springboot.app.springbootcrud.dto.UserBasicDTO;
import com.andres.curso.springboot.app.springbootcrud.dto.UserBasicDTOImpl;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 4, max = 12)
    @Column(unique = true)
    private String username;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @Column(unique = false)
    private String phone;

    @Size(max = 50)
    private String name;

    @Size(max = 50)
    private String lastName;

    @Size(max = 255)
    private String description;

    @Column(name = "image_path")
    private String imagePath;

    @JsonIgnoreProperties({ "users", "handler", "hibernateLazyInitializer" })
    @ManyToMany
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"), uniqueConstraints = {
            @UniqueConstraint(columnNames = { "user_id", "role_id" }) })
    private List<Role> roles;

    @ManyToMany
    @JoinTable(name = "user_followers", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "follower_id"))
    private Set<User> followers = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_follows", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "follows_id"))
    private Set<User> follows = new HashSet<>();

    @OneToMany(mappedBy = "receiver")
    private Set<FollowRequest> receivedFollowRequests = new HashSet<>();

    @OneToMany(mappedBy = "sender")
    private Set<FollowRequest> sentFollowRequests = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "privacy_level")
    private PrivacyLevel privacyLevel;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean admin;

    @Column(nullable = false)
    private boolean enabled = true;

    public User() {
        roles = new ArrayList<>();
    }

    @PrePersist
    public void prePersist() {
        enabled = true;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public int getFollowersCount() {
        return followers.size();
    }

    public Set<User> getFollows() {
        return follows;
    }

    public void setFollows(Set<User> follows) {
        this.follows = follows;
    }

    public int getFollowsCount() {
        return follows.size();
    }

    public Set<FollowRequest> getReceivedFollowRequests() {
        return receivedFollowRequests;
    }

    public void setFollowRequest(Set<FollowRequest> receivedFollowRequests) {
        this.receivedFollowRequests = receivedFollowRequests;
    }

    public Set<FollowRequest> getSentFollowRequests() {
        return sentFollowRequests;
    }

    public void setSentFollowRequests(Set<FollowRequest> sentFollowRequests) {
        this.sentFollowRequests = sentFollowRequests;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public PrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }

    public void setPrivacyLevel(PrivacyLevel privacyLevel) {
        this.privacyLevel = privacyLevel;
    }

    // DTOs

    public UserBasicDTO toUserBasicDTO() {
        return new UserBasicDTOImpl(
                this.id,
                this.username,
                this.imagePath,
                this.name);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    public boolean isFollowing(String username2) {
        return true;
    }
}
