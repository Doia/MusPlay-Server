package com.andres.curso.springboot.app.springbootcrud.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.andres.curso.springboot.app.springbootcrud.entities.AuthToken;

@Repository
public interface MyAuthTokenRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findByUsername(String username);
    Optional<AuthToken> findByRefreshToken(String refreshToken);
    void deleteByUsername(String username);
}
