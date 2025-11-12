package com.tricol.model;


import com.tricol.enums.TypeMouvement;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "mouvements_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate dateMouvement;

    @Enumerated(EnumType.STRING)
    private TypeMouvement typeMouvement;

    private int quantite;

    // relation avec commande
    @ManyToOne
    @JoinColumn(name = "id_commande", nullable = false)
    private Commande commande;
}