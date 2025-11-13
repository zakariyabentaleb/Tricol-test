package com.tricol.service;

import static org.junit.jupiter.api.Assertions.*;


import com.tricol.dto.MouvementStockDTO;
import com.tricol.enums.TypeMouvement;
import com.tricol.mapper.MouvementStockMapper;
import com.tricol.model.Commande;
import com.tricol.model.CommandeLigne;
import com.tricol.model.MouvementStock;
import com.tricol.repository.CommandeLigneRepository;
import com.tricol.repository.CommandeRepository;
import com.tricol.repository.MouvementStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
        import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MouvementStockServiceTest {

    @Mock private MouvementStockRepository mouvementStockRepository;
    @Mock private MouvementStockMapper mouvementStockMapper;
    @Mock private CommandeRepository commandeRepository;
    @Mock private CommandeLigneRepository commandeLigneRepository;

    @InjectMocks private MouvementStockService mouvementStockService;

    private MouvementStockDTO dto;
    private MouvementStock mouvement;
    private Commande commande;

    @BeforeEach
    void setUp() {
        dto = new MouvementStockDTO();
        dto.setCommandeId(5);
        dto.setTypeMouvement(TypeMouvement.ENTREE);

        commande = new Commande();
        commande.setId(5);

        mouvement = new MouvementStock();
        mouvement.setTypeMouvement(TypeMouvement.ENTREE);
    }

    // ----------------------------------------------------------------------
    //  TEST : Création mouvement entrée (quantité auto + date auto)
    // ----------------------------------------------------------------------
    @Test
    void testCreateMouvement_Entree_ShouldCalculateQuantiteAndSetDate() {
        // --- Given ---
        CommandeLigne l1 = new CommandeLigne();
        l1.setQuantite(10);

        CommandeLigne l2 = new CommandeLigne();
        l2.setQuantite(5);

        given(commandeRepository.findById(5)).willReturn(Optional.of(commande));
        given(mouvementStockMapper.toEntity(dto)).willReturn(mouvement);
        given(commandeLigneRepository.findByCommande(commande)).willReturn(List.of(l1, l2));
        given(mouvementStockRepository.save(mouvement)).willReturn(mouvement);
        given(mouvementStockMapper.toDTO(mouvement)).willReturn(dto);

        // --- When ---
        MouvementStockDTO result = mouvementStockService.create(dto);

        // --- Then ---
        assertThat(mouvement.getQuantite()).isEqualTo(15);
        assertThat(mouvement.getDateMouvement()).isEqualTo(LocalDate.now());
        assertThat(result).isNotNull();

        // Vérifie que la commande est bien associée
        assertThat(mouvement.getCommande()).isEqualTo(commande);
    }

    // ----------------------------------------------------------------------
    //  TEST : Sortie - recalcul quantité totale et conserver date manuelle
    // ----------------------------------------------------------------------
    @Test
    void testCreateMouvement_Sortie_ShouldKeepProvidedDate() {
        // --- Given ---
        dto.setTypeMouvement(TypeMouvement.SORTIE);
        dto.setDateMouvement(LocalDate.now().minusDays(2).toString()); // date fournie

        mouvement.setDateMouvement(LocalDate.parse(dto.getDateMouvement()));
        mouvement.setTypeMouvement(TypeMouvement.SORTIE);

        CommandeLigne line = new CommandeLigne();
        line.setQuantite(7);

        given(commandeRepository.findById(5)).willReturn(Optional.of(commande));
        given(mouvementStockMapper.toEntity(dto)).willReturn(mouvement);
        given(commandeLigneRepository.findByCommande(commande)).willReturn(List.of(line));
        given(mouvementStockRepository.save(mouvement)).willReturn(mouvement);
        given(mouvementStockMapper.toDTO(mouvement)).willReturn(dto);

        // --- When ---
        MouvementStockDTO result = mouvementStockService.create(dto);

        // --- Then ---
        assertThat(mouvement.getQuantite()).isEqualTo(7);
        assertThat(mouvement.getTypeMouvement()).isEqualTo(TypeMouvement.SORTIE);
        assertThat(mouvement.getDateMouvement()).isEqualTo(LocalDate.parse(dto.getDateMouvement()));

        assertThat(result).isNotNull();
    }

    // ----------------------------------------------------------------------
    // TEST : Valorisation automatique → calcul somme mouvements
    // ----------------------------------------------------------------------
    @Test
    void testSommeMouvement_ShouldReturnSumOfAllMouvements() {
        // --- Given ---
        MouvementStock m1 = new MouvementStock();
        m1.setQuantite(10);

        MouvementStock m2 = new MouvementStock();
        m2.setQuantite(5);

        given(mouvementStockRepository.findAll()).willReturn(List.of(m1, m2));

        // --- When ---
        int somme = mouvementStockService.sommeMouvement();

        // --- Then ---
        assertThat(somme).isEqualTo(15);
    }
}