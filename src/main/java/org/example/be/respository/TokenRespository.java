package org.example.be.respository;

import org.example.be.modal.JwtToken;
import org.example.be.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRespository extends JpaRepository<JwtToken, Long> {
    JwtToken findByTokenEquals(String token);
    JwtToken findByUser(User user);
}
