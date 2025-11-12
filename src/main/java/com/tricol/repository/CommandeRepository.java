package com.tricol.repository;

import com.tricol.model.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeRepository extends JpaRepository<Commande,Integer> {
}
