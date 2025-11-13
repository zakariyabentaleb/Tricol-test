package com.tricol.service;

import static org.junit.jupiter.api.Assertions.*;


import com.tricol.dto.ProduitDTO;
import com.tricol.mapper.ProduitMapper;
import com.tricol.model.Produit;
import com.tricol.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private ProduitMapper produitMapper;

    @InjectMocks
    private ProduitService produitService;

    private Produit produit;
    private ProduitDTO produitDTO;

    @BeforeEach
    void setUp() {
        produit = Produit.builder()
                .id(1)
                .nom("Produit A")
                .prixUnitaire(100)
                .stockActuel(10)
                .coutMoyenUnitaire(100)
                .build();

        produitDTO = ProduitDTO.builder()
                .id(1)
                .nom("Produit A")
                .prixUnitaire(100)
                .stockActuel(10)
                .build();
    }

    // ------------------------------------------------------
    // TEST getAllProduits
    // ------------------------------------------------------
    @Test
    void testGetAllProduits() {

        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").ascending());
        Page<Produit> pageProduit = new PageImpl<>(List.of(produit));

        when(produitRepository.findAll(pageable)).thenReturn(pageProduit);
        when(produitMapper.toDTO(produit)).thenReturn(produitDTO);

        Page<ProduitDTO> result = produitService.getAllProduits(0, 5);

        assertEquals(1, result.getTotalElements());
        assertEquals("Produit A", result.getContent().get(0).getNom());
        verify(produitRepository).findAll(pageable);
    }

    // ------------------------------------------------------
    // TEST getProduitById
    // ------------------------------------------------------
    @Test
    void testGetProduitById_Success() {

        when(produitRepository.findById(1)).thenReturn(Optional.of(produit));
        when(produitMapper.toDTO(produit)).thenReturn(produitDTO);

        ProduitDTO result = produitService.getProduitById(1);

        assertNotNull(result);
        assertEquals("Produit A", result.getNom());
    }

    @Test
    void testGetProduitById_NotFound() {

        when(produitRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> produitService.getProduitById(99));

        assertEquals("Produit non trouvé avec id : 99", ex.getMessage());
    }

    // ------------------------------------------------------
    // TEST createProduit → produit n'existe pas
    // ------------------------------------------------------
    @Test
    void testCreateProduit_NewProduct() {

        when(produitRepository.findByNom("Produit A")).thenReturn(null);
        when(produitMapper.toEntity(produitDTO)).thenReturn(produit);
        when(produitRepository.save(produit)).thenReturn(produit);
        when(produitMapper.toDTO(produit)).thenReturn(produitDTO);

        ProduitDTO result = produitService.createProduit(produitDTO);

        assertNotNull(result);
        assertEquals("Produit A", result.getNom());
        assertEquals(100, produit.getCoutMoyenUnitaire()); // CUMP = prix
    }

    // ------------------------------------------------------
    // TEST createProduit → produit existe → recalcul CUMP
    // ------------------------------------------------------
    @Test
    void testCreateProduit_UpdateExistingProduct() {

        Produit existing = Produit.builder()
                .id(1)
                .nom("Produit A")
                .prixUnitaire(100)
                .stockActuel(10)
                .coutMoyenUnitaire(100)
                .build();

        ProduitDTO newDto = ProduitDTO.builder()
                .nom("Produit A")
                .prixUnitaire(200)
                .stockActuel(5)
                .build();

        when(produitRepository.findByNom("Produit A")).thenReturn(existing);
        when(produitRepository.save(existing)).thenReturn(existing);
        when(produitMapper.toDTO(existing)).thenReturn(newDto);

        ProduitDTO result = produitService.createProduit(newDto);

        assertNotNull(result);
        assertEquals(200, existing.getPrixUnitaire());
        assertEquals(15, existing.getStockActuel());
        // CUMP attendu = (10*100 + 5*200) / 15 = 133.33
        assertEquals(133.33, existing.getCoutMoyenUnitaire(), 0.01);
    }

    // ------------------------------------------------------
    // TEST updateProduit
    // ------------------------------------------------------
    @Test
    void testUpdateProduit() {

        ProduitDTO modif = ProduitDTO.builder()
                .nom("Produit B")
                .description("Desc")
                .prixUnitaire(150)
                .categorie("Cat")
                .stockActuel(20)
                .build();

        when(produitRepository.findById(1)).thenReturn(Optional.of(produit));
        when(produitRepository.save(produit)).thenReturn(produit);
        when(produitMapper.toDTO(produit)).thenReturn(modif);

        ProduitDTO result = produitService.updateProduit(1, modif);

        assertEquals("Produit B", produit.getNom());
        assertEquals(150, produit.getPrixUnitaire());
        assertEquals(20, produit.getStockActuel());
    }

    // ------------------------------------------------------
    // TEST deleteProduit
    // ------------------------------------------------------
    @Test
    void testDeleteProduit() {

        doNothing().when(produitRepository).deleteById(1);

        produitService.deleteProduit(1);

        verify(produitRepository, times(1)).deleteById(1);
    }
}