package pl.pw.bookbox.library.user;

import java.time.LocalDateTime;

public class UserProfileResponse {

    private Long id;
    private String email;
    private String fullName;
    private String role;
    private LocalDateTime createdAt;

    public UserProfileResponse(Long id, String email, String fullName, String role, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
