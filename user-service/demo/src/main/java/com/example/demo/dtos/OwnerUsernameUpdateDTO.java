package com.example.demo.dtos;

public class OwnerUsernameUpdateDTO {

    private String oldUsername;
    private String newUsername;

    public OwnerUsernameUpdateDTO() {}

    public String getOldUsername() {
        return oldUsername;
    }

    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }
}
