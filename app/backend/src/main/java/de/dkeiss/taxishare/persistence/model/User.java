package de.dkeiss.taxishare.persistence.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "\"user\"") // Quoting the table name to avoid conflicts with reserved keywords
@Data
public class User {
    @Id @GeneratedValue
    private Long userId;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    @NotNull
    private String password;

    @ManyToMany
    @JoinTable(
            name = "userRoles",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private List<Role> roles;

    @NotNull
    private Date creationDate;

    private Date modificationDate;

    public User() {}

    public User(@NotBlank @Size(min = 3, max = 20) String username, @NotBlank @Size(max = 50) @Email String email, String password, Set<Role> roles) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = List.copyOf(roles);
        this.creationDate = new Date();
    }
}