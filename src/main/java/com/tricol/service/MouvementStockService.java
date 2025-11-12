package com.tricol.service;

import com.tricol.dto.MouvementStockDTO;
import com.tricol.mapper.MouvementStockMapper;
import com.tricol.model.Commande;
import com.tricol.model.MouvementStock;
import com.tricol.repository.CommandeRepository;
import com.tricol.repository.CommandeLigneRepository;
import com.tricol.repository.MouvementStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MouvementStockService {

    private final MouvementStockRepository mouvementStockRepository;
    private final MouvementStockMapper mouvementStockMapper;
    private final CommandeRepository commandeRepository;
    private final CommandeLigneRepository commandeLigneRepository;

    // GET all
    public Page<MouvementStockDTO> getAll(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<MouvementStock> mouvements = mouvementStockRepository.findAll(pageable);
        Page<MouvementStockDTO> dto = mouvements.map(mouvementStockMapper::toDTO);
        for (MouvementStockDTO msdto : dto ){
            for (MouvementStock ms : mouvements ){
                msdto.setCommandeId(ms.getCommande().getId());
            }
        }
        return dto;
    }

    // GET by ID
    public MouvementStockDTO getById(int id){
        MouvementStock mouvementStock = mouvementStockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("mouvementStock not found"));
        return mouvementStockMapper.toDTO(mouvementStock);
    }

    // CREATE
    public MouvementStockDTO create(MouvementStockDTO dto){

        // Récupérer la commande
        Commande commande = commandeRepository.findById(dto.getCommandeId())
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

        // Créer l'entité depuis le DTO via mapper
        MouvementStock mouvement = mouvementStockMapper.toEntity(dto);

        // Associer la commande
        mouvement.setCommande(commande);

        // Calculer la quantité totale de tous les produits de cette commande
        int totalQuantite = commandeLigneRepository.findByCommande(commande)
                .stream()
                .mapToInt(ligne -> ligne.getQuantite())
                .sum();
        mouvement.setQuantite(totalQuantite);

        // Si date non renseignée, utiliser date actuelle
        if (mouvement.getDateMouvement() == null) {
            mouvement.setDateMouvement(LocalDate.now());
        }

        // Sauvegarder
        MouvementStock saved = mouvementStockRepository.save(mouvement);
        return mouvementStockMapper.toDTO(saved);
    }

    // UPDATE
    public MouvementStockDTO update(int id, MouvementStockDTO dto){
        MouvementStock mouvement = mouvementStockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("mouvementStock not found"));

        Commande commande = commandeRepository.findById(dto.getCommandeId())
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

        // Mettre à jour depuis le DTO (mapper peut être utilisé si nécessaire)
        mouvement.setTypeMouvement(dto.getTypeMouvement());
        mouvement.setDateMouvement(dto.getDateMouvement() != null ?
                LocalDate.parse(dto.getDateMouvement()) : mouvement.getDateMouvement());
        mouvement.setCommande(commande);

        // Recalculer quantité totale
        int totalQuantite = commandeLigneRepository.findByCommande(commande)
                .stream()
                .mapToInt(ligne -> ligne.getQuantite())
                .sum();
        mouvement.setQuantite(totalQuantite);

        MouvementStock saved = mouvementStockRepository.save(mouvement);
        return mouvementStockMapper.toDTO(saved);
    }

    // DELETE
    public void delete(int id){
        mouvementStockRepository.deleteById(id);
    }
}