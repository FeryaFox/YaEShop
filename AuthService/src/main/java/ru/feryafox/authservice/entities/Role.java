package ru.feryafox.authservice.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    public enum RoleName {
        BUYER, SELLER, ADMIN, DISTRIBUTION_POINT_EMPLOYEE
    }

    public String getRoleName() {
        return name.name();
    }
}
