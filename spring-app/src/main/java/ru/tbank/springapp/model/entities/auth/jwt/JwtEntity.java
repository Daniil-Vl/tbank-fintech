package ru.tbank.springapp.model.entities.auth.jwt;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.tbank.springapp.model.entities.auth.UserEntity;

@Entity
@Table(name = "jwt")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JwtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "token", nullable = false)
    private String token;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

}
