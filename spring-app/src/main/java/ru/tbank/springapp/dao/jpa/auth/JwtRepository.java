package ru.tbank.springapp.dao.jpa.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tbank.springapp.model.entities.auth.jwt.JwtEntity;

@Repository
public interface JwtRepository extends JpaRepository<JwtEntity, Long> {
    JwtEntity findByToken(String token);

    @Query(value = "UPDATE jwt SET revoked = true FROM users WHERE users.username = :username AND jwt.user_id = users.id", nativeQuery = true)
    @Modifying
    void revokeByUsername(
            @Param("username") String username);
}
