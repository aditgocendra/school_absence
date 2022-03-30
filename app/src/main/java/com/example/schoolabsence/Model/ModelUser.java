package com.example.schoolabsence.Model;

public class ModelUser {
    private String username;
    private String nik;
    private String role;
    private String email;
    private String url_photo;
    private String key;

    public ModelUser() {
    }

    public ModelUser(String username, String nik, String role, String email, String url_photo) {
        this.username = username;
        this.nik = nik;
        this.role = role;
        this.email = email;
        this.url_photo = url_photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl_photo() {
        return url_photo;
    }

    public void setUrl_photo(String url_photo) {
        this.url_photo = url_photo;
    }
}
