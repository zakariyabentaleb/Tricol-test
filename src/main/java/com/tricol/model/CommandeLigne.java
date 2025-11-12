package com.tricol.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "commandes_lignes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeLigne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Relation ManyToOne avec Produit
    @ManyToOne
    @JoinColumn(name = "id_produit", nullable = false)
    private Produit produit;

    // Relation ManyToOne avec Commande
    @ManyToOne
    @JoinColumn(name = "id_commande", nullable = false)
    private Commande commande;
    private int quantite;

    @Column(name = "prix_achat")
    private double prixAchat;
}
