package com.tricol.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "fournisseur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String societe;

    private String adresse;

    private String contact;

    private String email;

    private String telephone;

    private String ville;

    @Column(unique = true)
    private String ICE;

    @OneToMany(mappedBy = "fournisseur",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commande> commandes;
}