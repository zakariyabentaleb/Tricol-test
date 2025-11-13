package com.tricol.service;

import static org.junit.jupiter.api.Assertions.*;


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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
        import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeServiceTest {

    @Mock private CommandeRepository commandeRepository;
    @Mock private CommandeMapper commandeMapper;
    @Mock private FournisseurRepository fournisseurRepository;
    @Mock private CommandeLigneRepository commandeLigneRepository;
    @Mock private ProduitRepository produitRepository;
    @Mock private MouvementStockService mouvementStockService;

    @InjectMocks private CommandeService commandeService;

    private CommandeDTO commandeDTO;
    private Commande commande;
    private Fournisseur fournisseur;

    @BeforeEach
    void setUp() {
        fournisseur = new Fournisseur();
        fournisseur.setId(1);

        commandeDTO = new CommandeDTO();
        commandeDTO.setFournisseurId(1);
        commandeDTO.setStatut(StatutCommande.EN_ATTENTE);

        commande = new Commande();
        commande.setId(10);
        commande.setStatut(StatutCommande.EN_ATTENTE);
    }

    // ---------------------------------------------------------------------
    // 1. TEST : Création de commande (sans livraison)
    // ---------------------------------------------------------------------
    @Test
    void testCreateCommande_Success() {
        // Given
        given(fournisseurRepository.findById(1)).willReturn(Optional.of(fournisseur));
        given(commandeMapper.toEntity(commandeDTO)).willReturn(commande);
        given(commandeRepository.save(commande)).willReturn(commande);
        given(commandeMapper.toDTO(commande)).willReturn(commandeDTO);

        // When
        CommandeDTO result = commandeService.createCommande(commandeDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(commande.getFournisseur()).isEqualTo(fournisseur);

        // Aucune gestion stock ou mouvement ne doit être appelée
        then(commandeLigneRepository).shouldHaveNoInteractions();
        then(mouvementStockService).shouldHaveNoInteractions();
    }

    // ---------------------------------------------------------------------
    // 2. TEST : Validation d’une commande livrée → Doit déclencher niveau stock
    // ---------------------------------------------------------------------
    @Test
    void testCreateCommande_WhenLivree_ShouldProcessStock() {
        // Given : commande livrée directement
        commandeDTO.setStatut(StatutCommande.LIVREE);
        commande.setStatut(StatutCommande.LIVREE);

        Produit produit = new Produit();
        produit.setId(5);
        produit.setNom("Clavier");
        produit.setStockActuel(50);

        CommandeLigne ligne = new CommandeLigne();
        ligne.setProduit(produit);
        ligne.setQuantite(10);
        ligne.setCommande(commande);

        given(fournisseurRepository.findById(1)).willReturn(Optional.of(fournisseur));
        given(commandeMapper.toEntity(commandeDTO)).willReturn(commande);
        given(commandeRepository.save(commande)).willReturn(commande);
        given(commandeLigneRepository.findByCommande(commande)).willReturn(List.of(ligne));
        given(commandeMapper.toDTO(commande)).willReturn(commandeDTO);

        // When
        commandeService.createCommande(commandeDTO);

        // Then → stock mis à jour
        assertThat(produit.getStockActuel()).isEqualTo(40);

        then(produitRepository).should().save(produit);

        // Vérification du mouvement de stock
        ArgumentCaptor<MouvementStockDTO> captor = ArgumentCaptor.forClass(MouvementStockDTO.class);
        then(mouvementStockService).should().create(captor.capture());

        MouvementStockDTO mouv = captor.getValue();
        assertThat(mouv.getCommandeId()).isEqualTo(10);
    }

    // ---------------------------------------------------------------------
    // 3. TEST : Livraison → Stock insuffisant → Exception
    // ---------------------------------------------------------------------
    @Test
    void testLivraisonCommande_StockInsuffisant_ShouldThrowException() {
        // Given
        commandeDTO.setStatut(StatutCommande.LIVREE);
        commande.setStatut(StatutCommande.LIVREE);

        Produit produit = new Produit();
        produit.setNom("Souris");
        produit.setStockActuel(5);     // très faible stock

        CommandeLigne ligne = new CommandeLigne();
        ligne.setProduit(produit);
        ligne.setQuantite(10);         // demande > stock → erreur

        given(fournisseurRepository.findById(1)).willReturn(Optional.of(fournisseur));
        given(commandeMapper.toEntity(commandeDTO)).willReturn(commande);
        given(commandeRepository.save(commande)).willReturn(commande);
        given(commandeLigneRepository.findByCommande(commande)).willReturn(List.of(ligne));

        // When / Then
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> commandeService.createCommande(commandeDTO)
        );

        assertThat(ex.getMessage()).contains("Stock insuffisant");

        // Aucun mouvement ne doit être créé
        then(mouvementStockService).shouldHaveNoInteractions();
    }
}