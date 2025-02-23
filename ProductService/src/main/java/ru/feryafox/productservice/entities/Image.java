package ru.feryafox.productservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "position", nullable = false)
    private int position;

    @Column(name = "uploaded_user")
    private UUID uploadedUser;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
