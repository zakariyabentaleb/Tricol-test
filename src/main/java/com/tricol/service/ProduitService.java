package com.tricol.service;

import com.tricol.dto.ProduitDTO;
import com.tricol.mapper.ProduitMapper;
import com.tricol.model.Produit;
import com.tricol.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper;

    public Page<ProduitDTO> getAllProduits(int page,int nbrElement) {
        if (page < 0) page = 0;
        if (nbrElement <= 0) nbrElement = 10; // default page size
        Pageable pageable= PageRequest.of(page,nbrElement, Sort.by("id").ascending());
        Page<Produit> produits=produitRepository.findAll(pageable);
        return produits.map(produitMapper::toDTO);
    }

    public ProduitDTO getProduitById(int id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec id : " + id));
        return produitMapper.toDTO(produit);
    }

    public ProduitDTO createProduit(ProduitDTO produitDTO) {

        // vérifier si produit existe déjà
        Produit existing = produitRepository.findByNom(produitDTO.getNom());

        Produit produit;

        if (existing != null) {
            // => deuxième entrée
            double newCump = calculerCUMP(
                    existing.getCoutMoyenUnitaire(),
                    existing.getStockActuel(),
                    produitDTO.getPrixUnitaire(),
                    produitDTO.getStockActuel()
            );

            existing.setCoutMoyenUnitaire(newCump);
            existing.setStockActuel(existing.getStockActuel() + produitDTO.getStockActuel());
            existing.setPrixUnitaire(produitDTO.getPrixUnitaire()); // tu peux stocker le dernier

            produit = existing;

        } else {
            // => création produit
            produit = produitMapper.toEntity(produitDTO);
            produit.setCoutMoyenUnitaire(produit.getPrixUnitaire()); // CUMP = prix
        }

        Produit saved = produitRepository.save(produit);
        return produitMapper.toDTO(saved);
    }


    public ProduitDTO updateProduit(int id, ProduitDTO produitDTO) {
        Produit existing = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec id : " + id));

        // Mise à jour des champs
        existing.setNom(produitDTO.getNom());
        existing.setDescription(produitDTO.getDescription());
        existing.setPrixUnitaire(produitDTO.getPrixUnitaire());
        existing.setCategorie(produitDTO.getCategorie());
        existing.setStockActuel(produitDTO.getStockActuel());

        Produit updated = produitRepository.save(existing);
        return produitMapper.toDTO(updated);
    }

    public void deleteProduit(int id) {
        produitRepository.deleteById(id);
    }

    //Méthode pour le calucl de cump
    private double calculerCUMP(double ancienCUMP, int ancienStock,
                                double nouveauPrix, int nouvelleQuantite) {

        if (ancienStock == 0) return nouveauPrix;
        double result=((ancienStock * ancienCUMP) + (nouvelleQuantite * nouveauPrix)) / (ancienStock + nouvelleQuantite );

        return result;


    }


}