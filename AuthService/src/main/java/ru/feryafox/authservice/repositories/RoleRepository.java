package ru.feryafox.authservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.authservice.entities.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Role.RoleName name);
}