package com.andres.curso.springboot.app.springbootcrud.dto;

import java.util.List;
import java.util.Set;

import com.andres.curso.springboot.app.springbootcrud.entities.Role;
import com.andres.curso.springboot.app.springbootcrud.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String phone;

    private int friendsCount;
    private int matchCount = 0;

    @JsonIgnoreProperties({ "users", "handler", "hibernateLazyInitializer" })
    private List<Role> roles;

    private Set<String> friends;
    private Boolean enabled = null; // Changed to Boolean
    private Boolean admin = null; // Changed to Boolean
    private Boolean isAuthenticationUserFriend = null; // Changed to Boolean
    private PrivacyLevel privacyLevel;
    private PrivacyLevel privacyData;

    private String firstName;
    private String lastName;
    private String description;
    private String imagePath;

    public UserDTO(User user, PrivacyLevel privacy) {
        if (privacy.equals(PrivacyLevel.FULL)) {
            setFullDTO(user);
        } else if (privacy.equals(PrivacyLevel.PRIVATE)) {
            setPrivateDTO(user);
        } else if (privacy.equals(PrivacyLevel.PUBLIC)) {
            setPublicDTO(user);
        } else {
            setSampleDTO(user);
        }
    }

    private void setPrivateDTO(User user) {
        this.privacyData = PrivacyLevel.PRIVATE;
        this.id = user.getId();
        this.username = user.getUsername();
        this.enabled = null;
        this.admin = null;
        this.friendsCount = user.getFriends().size();
        this.matchCount = 0;
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.description = user.getDescription();
        this.imagePath = user.getImagePath();
    }

    private void setPublicDTO(User user) {
        this.privacyData = PrivacyLevel.PUBLIC;
        this.id = user.getId();
        this.username = user.getUsername();
        this.friends = user.getFriends();
        this.enabled = null;
        this.admin = null;
        this.friendsCount = user.getFriends().size();
        this.matchCount = 0;
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.description = user.getDescription();
        this.imagePath = user.getImagePath();
    }

    private void setFullDTO(User user) {
        this.privacyData = PrivacyLevel.FULL;
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.roles = user.getRoles();
        this.friends = user.getFriends();
        this.enabled = user.isEnabled();
        this.admin = user.isAdmin();
        this.privacyLevel = user.getPrivacyLevel();
        this.friendsCount = user.getFriends().size();
        this.matchCount = 0;
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.description = user.getDescription();
        this.imagePath = user.getImagePath();
    }

    private void setSampleDTO(User user) {
        this.privacyData = PrivacyLevel.SAMPLE;
        this.id = user.getId();
        this.username = user.getUsername();
        this.friendsCount = user.getFriends().size();
        this.matchCount = 0;
        this.enabled = null;
        this.admin = null;
        this.imagePath = user.getImagePath();
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Set<String> getFriends() {
        return friends;
    }

    public void setFriends(Set<String> friends) {
        this.friends = friends;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getIsAuthenticationUserFriend() {
        return isAuthenticationUserFriend;
    }

    public void setIsAuthenticationUserFriend(Boolean isAuthenticationUserFriend) {
        this.isAuthenticationUserFriend = isAuthenticationUserFriend;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public PrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }

    public void setPrivacyLevel(PrivacyLevel privacyLevel) {
        this.privacyLevel = privacyLevel;
    }

    public PrivacyLevel getPrivacyData() {
        return privacyData;
    }

    public void setPrivacyData(PrivacyLevel privacyData) {
        this.privacyData = privacyData;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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
}
