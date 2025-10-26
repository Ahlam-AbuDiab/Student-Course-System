/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JPAphase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author ahlamabudiab
 */
@Entity
@Table(name="Users",uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@NamedQueries({
    @NamedQuery(
        name = "Users.countByEmail",
        query = "SELECT COUNT(u) FROM Users u WHERE LOWER(u.email) = LOWER(:email)"),
    @NamedQuery(
        name = "Users.findByEmail",
        query = "SELECT u FROM Users u WHERE LOWER(u.email) = LOWER(:email)"),
    @NamedQuery(
        name = "Users.findByEmailAndPassword",
        query = "SELECT u FROM Users u WHERE LOWER(u.email) = LOWER(:email) AND u.passwordHash = :hash")
})

public class Users {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private String id;
    @Column(name="first_name")
    private String firstName; 
    @Column(name="last_name")
    private String lastName;
    @Column(name="email")
    private String email;
    @Column(name="password_hash")
    private String passwordHash;

    public Users(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    
    

}
