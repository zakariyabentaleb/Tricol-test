package com.tricol.repository;

import com.tricol.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduitRepository extends JpaRepository<Produit,Integer> {
    Produit findByNom(String nom);
}
