package ru.feryafox.authservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.authservice.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}