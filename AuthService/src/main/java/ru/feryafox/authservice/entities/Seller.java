package ru.feryafox.authservice.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "sellers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seller {

    @Id
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    @Column(name = "business_license", nullable = false)
    private String businessLicense;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;
}
