package com.andres.curso.springboot.app.springbootcrud.dto;

public class UserBasicDTOImpl implements UserBasicDTO {

    private Long id;
    private String username;
    private String imagePath;
    private String name;

    // Constructor
    public UserBasicDTOImpl(Long id, String username, String imagePath, String name) {
        this.id = id;
        this.username = username;
        this.imagePath = imagePath;
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String getName() {
        return name;
    }

    // Setters if needed (or you can make fields final and omit setters)
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setName(String name) {
        this.name = name;
    }
}
