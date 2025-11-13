package com.tricol.service;

import static org.junit.jupiter.api.Assertions.*;


import com.tricol.dto.FournisseurDTO;
import com.tricol.mapper.FournisseurMapper;
import com.tricol.model.Fournisseur;
import com.tricol.repository.FournisseurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FournisseurServiceTest {

    @Mock
    private FournisseurRepository fournisseurRepository;

    @Mock
    private FournisseurMapper fournisseurMapper;

    @InjectMocks
    private FournisseurService fournisseurService;

    private Fournisseur fournisseur;
    private FournisseurDTO fournisseurDTO;

    @BeforeEach
    void setUp() {
        fournisseur = new Fournisseur();
        fournisseur.setId(1);
        fournisseur.setSociete("ABC SARL");

        fournisseurDTO = new FournisseurDTO();
        fournisseurDTO.setId(1);
        fournisseurDTO.setSociete("ABC SARL");
    }

    // ----------------------------------------------------
    // 1. TEST : getAllFournisseurs()
    // ----------------------------------------------------
    @Test
    void testGetAllFournisseurs_ReturnsPagedDTO() {
        // Given
        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").ascending());
        Page<Fournisseur> fournisseurPage = new PageImpl<>(List.of(fournisseur));

        given(fournisseurRepository.findAll(pageable)).willReturn(fournisseurPage);
        given(fournisseurMapper.toDTO(fournisseur)).willReturn(fournisseurDTO);

        // When
        Page<FournisseurDTO> result = fournisseurService.getAllFournisseurs(0, 5);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getSociete()).isEqualTo("ABC SARL");
    }

    // ----------------------------------------------------
    // 2. TEST : getFournisseurById() - Success
    // ----------------------------------------------------
    @Test
    void testGetFournisseurById_ReturnsDTO() {
        // Given
        given(fournisseurRepository.findById(1)).willReturn(Optional.of(fournisseur));
        given(fournisseurMapper.toDTO(fournisseur)).willReturn(fournisseurDTO);

        // When
        FournisseurDTO result = fournisseurService.getFournisseurById(1);

        // Then
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getSociete()).isEqualTo("ABC SARL");
    }

    // ----------------------------------------------------
    // 3. TEST : getFournisseurById() - Not Found
    // ----------------------------------------------------
    @Test
    void testGetFournisseurById_NotFound_ThrowsException() {
        // Given
        given(fournisseurRepository.findById(99)).willReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fournisseurService.getFournisseurById(99));

        assertThat(exception.getMessage()).isEqualTo("Fournisseur non trouvé avec id : 99");
    }

    // ----------------------------------------------------
    // 4. TEST : createFournisseur()
    // ----------------------------------------------------
    @Test
    void testCreateFournisseur_Success() {
        // Given
        given(fournisseurMapper.toEntity(fournisseurDTO)).willReturn(fournisseur);
        given(fournisseurRepository.save(fournisseur)).willReturn(fournisseur);
        given(fournisseurMapper.toDTO(fournisseur)).willReturn(fournisseurDTO);

        // When
        FournisseurDTO result = fournisseurService.createFournisseur(fournisseurDTO);

        // Then
        assertThat(result.getSociete()).isEqualTo("ABC SARL");
        then(fournisseurRepository).should().save(fournisseur);
    }

    // ----------------------------------------------------
    // 5. TEST : updateFournisseur() - Success
    // ----------------------------------------------------
    @Test
    void testUpdateFournisseur_Success() {
        // Given
        fournisseurDTO.setSociete("New Societe");

        given(fournisseurRepository.findById(1)).willReturn(Optional.of(fournisseur));
        given(fournisseurRepository.save(fournisseur)).willReturn(fournisseur);
        given(fournisseurMapper.toDTO(fournisseur)).willReturn(fournisseurDTO);

        // When
        FournisseurDTO result = fournisseurService.updateFournisseur(1, fournisseurDTO);

        // Then
        assertThat(result.getSociete()).isEqualTo("New Societe");
        then(fournisseurRepository).should().save(fournisseur);
    }

    // ----------------------------------------------------
    // 6. TEST : updateFournisseur() - Not Found
    // ----------------------------------------------------
    @Test
    void testUpdateFournisseur_NotFound_ThrowsException() {
        // Given
        given(fournisseurRepository.findById(10)).willReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fournisseurService.updateFournisseur(10, fournisseurDTO));

        assertThat(exception.getMessage()).isEqualTo("Fournisseur non trouvé avec id : 10");
    }

    // ----------------------------------------------------
    // 7. TEST : deleteFournisseur()
    // ----------------------------------------------------
    @Test
    void testDeleteFournisseur_Success() {
        // When
        fournisseurService.deleteFournisseur(1);

        // Then
        then(fournisseurRepository).should().deleteById(1);
    }
}