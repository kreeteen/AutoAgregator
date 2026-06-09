package ru.vsu.cs.projectcars.dto;

public class UserSession {

    private Integer userId;
    private String email;
    private String firstName;
    private String lastName;
    private String token;

    public UserSession() {}

    public UserSession(Integer userId, String email, String firstName, String lastName) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getFullName() {
        return lastName != null ? firstName + " " + lastName : firstName;
    }
}
