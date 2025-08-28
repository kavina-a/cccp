//package com.syos.domain.entity;
//
//import jakarta.persistence.*;
//import java.util.Date;
//
//@MappedSuperclass // Not a table by itself, but fields inherited by Employee & Customer
//public abstract class User {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "UserID")
//    private int userID;
//
//    @Column(name = "Username", nullable = false, unique = true)
//    private String username;
//
//    @Column(name = "PasswordHash", nullable = false)
//    private String passwordHash;
//
//    @Column(name = "Email", unique = true)
//    private String email;
//
//    @Column(name = "IsActive", nullable = false)
//    private boolean isActive = true;
//
//    @Column(name = "CreatedAt", nullable = false)
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date createdAt = new Date();
//
//    @Column(name = "UpdatedDateTime", nullable = false)
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date updatedDateTime = new Date();
//
//
//    public int getUserID() { return userID; }
//    public void setUserID(int userID) { this.userID = userID; }
//
//    public String getUsername() { return username; }
//    public void setUsername(String username) { this.username = username; }
//
//    public String getPasswordHash() { return passwordHash; }
//    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
//
//    public String getEmail() { return email; }
//    public void setEmail(String email) { this.email = email; }
//
//    public boolean isActive() { return isActive; }
//    public void setActive(boolean active) { isActive = active; }
//
//    public Date getCreatedAt() { return createdAt; }
//    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
//
//    public Date getUpdatedDateTime() { return updatedDateTime; }
//    public void setUpdatedDateTime(Date updatedDateTime) { this.updatedDateTime = updatedDateTime; }
//
//
//}
//
