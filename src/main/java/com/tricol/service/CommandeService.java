package com.tricol.service;

import com.tricol.dto.CommandeDTO;
import com.tricol.dto.MouvementStockDTO;
import com.tricol.enums.StatutCommande;
import com.tricol.mapper.CommandeMapper;
import com.tricol.model.Commande;
import com.tricol.model.CommandeLigne;
import com.tricol.model.Fournisseur;
import com.tricol.model.Produit;
import com.tricol.repository.CommandeLigneRepository;
import com.tricol.repository.CommandeRepository;
import com.tricol.repository.FournisseurRepository;
import com.tricol.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CommandeService {
    private final CommandeRepository commandeRepository;
    private final CommandeMapper commandeMapper;
    private final FournisseurRepository fournisseurRepository;
    private final CommandeLigneRepository commandeLigneRepository;
    private final ProduitRepository produitRepository;
    private final MouvementStockService mouvementStockService;

    //getAll commandes with pagination sorting by id ascending
    public Page<CommandeDTO> getAllCommandes(int page,int nbrElement){
        if (page < 0) page = 0;
        if (nbrElement <= 0) nbrElement = 10; // default page size
        Pageable pageable= PageRequest.of(page,nbrElement, Sort.by("id").ascending());
        Page<Commande> commandes=commandeRepository.findAll(pageable);
        Page<CommandeDTO> result = commandes.map(commandeMapper::toDTO);
        for (CommandeDTO dto : result) {
           for (Commande com : commandes) {
               dto.setFournisseurId(com.getFournisseur().getId());
            }
        }

        return result;
    }

    //getByid
    public CommandeDTO getById(int id){
        Commande commande =commandeRepository.findById(id).orElseThrow(()->new RuntimeException("Commande not found"));
        return commandeMapper.toDTO(commande);
    }

    //save
    public CommandeDTO createCommande(CommandeDTO commandeDTO){
        // Récupérer le fournisseur
        Fournisseur fournisseur = fournisseurRepository
                .findById(commandeDTO.getFournisseurId())
                .orElseThrow(() -> new RuntimeException("Fournisseur introuvable"));

        Commande commande=commandeMapper.toEntity(commandeDTO);
        // Associer le fournisseur
        commande.setFournisseur(fournisseur);
        Commande saved=commandeRepository.save(commande);

        // Traiter livraison si le statut est LIVREE dès la création
        traiterLivraisonCommande(saved);

        return commandeMapper.toDTO(saved);
    }

    //update
    public CommandeDTO updateCommande(int id,CommandeDTO commandeDTO){
        Commande commande=commandeRepository.findById(id).orElseThrow(()->new RuntimeException("Commande not found"));
        Fournisseur fournisseur=fournisseurRepository.findById(commandeDTO.getFournisseurId()).orElseThrow(()->new RuntimeException("fournisseur not found"));
        commande.setDateCommande(commandeDTO.getDateCommande());
        commande.setStatut(commandeDTO.getStatut());
        commande.setMontantTotal(commandeDTO.getMontantTotal());
        commande.setFournisseur(fournisseur);
        Commande saved=commandeRepository.save(commande);

        // Traiter livraison si le statut devient LIVREE
        traiterLivraisonCommande(saved);

        return commandeMapper.toDTO(saved);
    }

    //delete
    public void deleteCommande(int id){
        commandeRepository.deleteById(id);
    }

    private void traiterLivraisonCommande(Commande commande) {
        if (commande.getStatut() != StatutCommande.LIVREE) {
            return; // Rien à faire si la commande n'est pas LIVREE
        }

        for (CommandeLigne ligne : commandeLigneRepository.findByCommande(commande)) {
            Produit produit = ligne.getProduit();
            int stockRestant = produit.getStockActuel() - ligne.getQuantite();

            if (stockRestant < 0) {
                throw new RuntimeException(
                        "Stock insuffisant pour le produit '" + produit.getNom() +
                                "'. Quantité demandée : " + ligne.getQuantite() +
                                ", stock actuel : " + produit.getStockActuel()
                );
            }

            produit.setStockActuel(stockRestant);
            produitRepository.save(produit);
        }
        MouvementStockDTO mouvementDTO = MouvementStockDTO.builder()
                .commandeId(commande.getId())
                .typeMouvement(com.tricol.enums.TypeMouvement.ENTREE)
                .build();

        mouvementStockService.create(mouvementDTO);
    }
}
