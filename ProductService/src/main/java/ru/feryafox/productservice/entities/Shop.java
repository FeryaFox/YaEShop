package ru.feryafox.productservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "shops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_owner", nullable = false)
    private UUID userOwner;

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    @OneToMany(mappedBy = "shop", cascade = {CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true)
    private Set<Product> products = new LinkedHashSet<>();

}
