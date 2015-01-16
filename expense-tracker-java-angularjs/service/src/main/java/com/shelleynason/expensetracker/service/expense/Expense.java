package com.shelleynason.expensetracker.service.expense;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shelleynason.expensetracker.service.user.User;

/**
 * The expense domain object.
 */
@Entity
@Table(name="Expenses")
public class Expense {
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyy");
    private static final DateFormat TIME_FORMATTER = new SimpleDateFormat("hh:mm a");

    @Id
    @Column(name="Expense_Id", nullable=false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    @ManyToOne(optional=false)
    @JoinColumn(name="User_Id")
    private User user;
    
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(nullable=false)
    @NotNull
    private Date date;
    
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date time;
    
    @Size(max=50)
    @Column(length=50)
    private String description;
    
    @Digits(integer=10,fraction=2)
    @NotNull
    @Column(precision=10,scale=2,nullable=false)
    private BigDecimal amount;
    
    @Size(max=500)
    @Column(length=500)
    private String comment;

    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    @JsonIgnore
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
         this.user = user;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public Date getTime() {
        return time;
    }
    
    public void setTime(Date time) {
        this.time = time;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getComment() {
        return comment;
    }
       
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    @Override
    public String toString() {
        return String.format(
                "Expense[id=%d, userId=%d, date=%s, time=%s, description=%s, amount=%s, comment=%s]",
                id,
                user.getId(),
                date != null ? DATE_FORMATTER.format(date) : "",
                time != null ? TIME_FORMATTER.format(time) : "",
                description != null ? description.toString() : "",
                amount != null ? amount.toString() : "",
                comment != null ? comment.toString() : "");
    }
    
}
