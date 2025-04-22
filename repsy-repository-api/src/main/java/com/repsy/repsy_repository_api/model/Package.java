package com.repsy.repsy_repository_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String version;

    private String author;

    // Diğer metadata alanları (dependencies gibi) eklenebilir.
    // Ödevde dependencies kontrolü istenmiyor, bu yüzden basit tutuyoruz.
} 