package com.inventory.project.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddUser {
    @NotBlank(message = "Name cannot be blank!")
    private String name;

    @NotBlank(message = "Username cannot be blank!")
    @Size(min = 3,message = "Username must be 3 characters!")
    private String username;

    @NotBlank(message = "Email cannot be blank!")
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank(message = "Role cannot be blank!")
    @Size(max = 50)
    private String role;

    @NotBlank(message = "Password cannot be blank!")
    @Size(min = 8, message = "Password must be 8 characters!")
    private String password;

    private Long contactNumber;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(Long contactNumber) {
        this.contactNumber = contactNumber;
    }
}
