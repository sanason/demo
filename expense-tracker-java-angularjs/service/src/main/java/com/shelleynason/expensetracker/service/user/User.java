package com.shelleynason.expensetracker.service.user;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shelleynason.expensetracker.service.expense.Expense;

/**
 * The user domain object.
 */
@Entity
@Table(name="Users")
public class User {

    @Id
    @Column(name="User_Id", nullable=false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    @Size(max=20,min=1)
    @NotNull
    @Column(length=20, nullable=false, unique=true)
    private String username;
    
    // TODO figure out this length
    // Length of $mcfFormatId$algorithmName$iterationCount$base64EncodedSalt$base64EncodedDigest
    // 1 + length(shiro1) + 1 + length(SHA-256) + 1 + length(500000) + 1 + length(salt) + 1 + length(digest)
    @Column(nullable=false, name="Hashed_Password")
    private String hashedPassword;
    
    // Not in the database
    // Only for data transfer
    @Size(min=8,max=100)
    @NotNull
    @Transient
    private char[] password;
    
    @OneToMany(mappedBy="user")
    private List<Expense> expenses;
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    @JsonIgnore
    public char[] getPassword() {
        return password;
    }
    
    @JsonProperty
    public void setPassword(char[] password) {
        this.password = password;
    }
    
    @JsonIgnore
    public String getHashedPassword() {
        return this.hashedPassword;
    }
    
    @JsonIgnore
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    
    @JsonIgnore
    public List<Expense> getExpenses() {
        return expenses;
    }
   
    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }
    
    @Override
    public String toString() {
        return String.format(
                "User[id=%d, username=%s]", id, username);
    }
}
