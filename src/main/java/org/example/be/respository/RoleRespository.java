package org.example.be.respository;
import org.example.be.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRespository extends JpaRepository<Role, Long> {
    Role findByName(String roleName);
}