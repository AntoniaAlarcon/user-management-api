package com.antonia.dev.userapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String nickname;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private Audit audit = new Audit();

    public User(String name, String nickname, String email, String password, Role role) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
