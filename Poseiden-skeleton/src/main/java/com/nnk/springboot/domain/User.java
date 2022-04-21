package com.nnk.springboot.domain;

import com.nnk.springboot.domain.validation.ValidPassword;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "Username is mandatory")
    @Size(max = 125, message = "Must be at most 125 characters in length")
    private String username;
    @ValidPassword
    private String password;
    @NotBlank(message = "FullName is mandatory")
    @Size(max = 125, message = "Must be at most 125 characters in length")
    private String fullname;
    @NotBlank(message = "Role is mandatory")
    @Size(max = 125, message = "Must be at most 125 characters in length")
    private String role;

    public User(String username, String password, String fullname, String role) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.role = role;
    }

    public User() {
    }
}
