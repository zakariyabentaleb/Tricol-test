package com.tricol.repository;

import com.tricol.model.Commande;
import com.tricol.model.CommandeLigne;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommandeLigneRepository extends JpaRepository<CommandeLigne,Integer> {
    List<CommandeLigne> findByCommande(Commande commande);
}