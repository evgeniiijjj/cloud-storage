package com.example.cloudstorage.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "authorization_data",
        uniqueConstraints = @UniqueConstraint(columnNames = "login"))
@NoArgsConstructor
@Getter
@Setter
public class AuthorizationData {
    @Id
    @GeneratedValue
    private long id;
    private String login;
    private String password;
}
