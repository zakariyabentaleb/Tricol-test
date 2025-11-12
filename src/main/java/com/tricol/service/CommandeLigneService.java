package com.tricol.service;

import com.tricol.dto.CommandeLigneDTO;
import com.tricol.mapper.CommandeLigneMapper;
import com.tricol.model.Commande;
import com.tricol.model.CommandeLigne;
import com.tricol.model.Produit;
import com.tricol.repository.CommandeLigneRepository;
import com.tricol.repository.CommandeRepository;
import com.tricol.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommandeLigneService {

    private final CommandeLigneRepository commandeLigneRepository;
    private final CommandeLigneMapper commandeLigneMapper;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;

    // GET all
    public Page<CommandeLigneDTO> getAll(int page, int nbrElement){
        // Ensure valid paging params
        if (page < 0) page = 0;
        if (nbrElement <= 0) nbrElement = 10; // default page size

        Pageable pageable = PageRequest.of(page, nbrElement, Sort.by("id").ascending());
        Page<CommandeLigne> commandeLigne = commandeLigneRepository.findAll(pageable);
        Page<CommandeLigneDTO> dto = commandeLigne.map(commandeLigneMapper::toDTO);
        for(CommandeLigneDTO ligne : dto){
            for(CommandeLigne cm : commandeLigne){
                ligne.setCommandeId(cm.getCommande().getId());
                ligne.setProduitId(cm.getProduit().getId());
            }
        }
        return dto;
    }

    // GET by id
    public CommandeLigneDTO getById(int id){
        CommandeLigne ligne = commandeLigneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CommandeLigne not found"));
        return commandeLigneMapper.toDTO(ligne);
    }

    // CREATE (POST) avec mapper
    public CommandeLigneDTO createCommandeLigne(CommandeLigneDTO dto){
        // Convertir le DTO en entity via mapper
        CommandeLigne ligne = commandeLigneMapper.toEntiry(dto);

        // Associer les entités existantes
        Produit produit = produitRepository.findById(dto.getProduitId())
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
        Commande commande = commandeRepository.findById(dto.getCommandeId())
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

        ligne.setProduit(produit);
        ligne.setCommande(commande);

        // le prixAchat = CUMP du produit
        ligne.setPrixAchat(produit.getCoutMoyenUnitaire());

        // Sauvegarder
        CommandeLigne saved = commandeLigneRepository.save(ligne);

        // après avoir ajouté la ligne, recalculer le montant total de la commande
        recalculerMontantTotalCommande(commande);

        return commandeLigneMapper.toDTO(saved);
    }

    // UPDATE (PUT) avec mapper
    public CommandeLigneDTO updateCommandeLigne(int id, CommandeLigneDTO dto){
        CommandeLigne ligne = commandeLigneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CommandeLigne non trouvée"));

        Produit produit = produitRepository.findById(dto.getProduitId())
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
        Commande commande = commandeRepository.findById(dto.getCommandeId())
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

        // Mettre à jour via mapper ou directement
        ligne.setQuantite(dto.getQuantite());
        ligne.setProduit(produit);
        ligne.setCommande(commande);

        CommandeLigne saved = commandeLigneRepository.save(ligne);
        return commandeLigneMapper.toDTO(saved);
    }

    // DELETE
    public void deleteCommandeLigne(int id){
        commandeLigneRepository.deleteById(id);
    }


    //la méthode pour recalculer le total de la commande
    private void recalculerMontantTotalCommande(Commande commande) {

        double total = commandeLigneRepository.findByCommande(commande).stream()
                .mapToDouble(l -> l.getPrixAchat() * l.getQuantite())
                .sum();

        commande.setMontantTotal(total);
        commandeRepository.save(commande);
    }

}

