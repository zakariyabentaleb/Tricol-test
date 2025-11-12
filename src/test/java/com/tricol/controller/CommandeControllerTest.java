package com.tricol.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tricol.dto.CommandeDTO;
import com.tricol.dto.FournisseurDTO;
import com.tricol.enums.StatutCommande;
import com.tricol.repository.FournisseurRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")

class CommandeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    private int fournisseurId;

    @BeforeEach
    void setup() {
        // Créer un fournisseur pour associer aux commandes
        FournisseurDTO fournisseur = new FournisseurDTO();
        fournisseur.setSociete("Test Fournisseur");
        fournisseur.setAdresse("Casablanca");
        fournisseur.setContact("John Doe");
        fournisseur.setEmail("test@example.com");
        fournisseur.setTelephone("0600000000");
        fournisseur.setVille("Casablanca");
        fournisseur.setICE("ICE123456");

        fournisseurId = fournisseurRepository.save(
                objectMapper.convertValue(fournisseur, com.tricol.model.Fournisseur.class)
        ).getId();
    }

    // ----------------------------------------------------------
    // Test : création d’une commande
    // ----------------------------------------------------------
    @Test
    void testCreateCommande() throws Exception {
        CommandeDTO dto = new CommandeDTO();
        dto.setDateCommande(LocalDateTime.now());
        dto.setStatut(StatutCommande.EN_ATTENTE);
        dto.setFournisseurId(fournisseurId);

        mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"))
                .andExpect(jsonPath("$.fournisseurId").value(fournisseurId));
    }

    // ----------------------------------------------------------
    // Test : récupération d’une commande par ID
    // ----------------------------------------------------------
    @Test
    void testGetCommandeById() throws Exception {
        CommandeDTO dto = new CommandeDTO();
        dto.setDateCommande(LocalDateTime.now());
        dto.setStatut(StatutCommande.EN_ATTENTE);
        dto.setFournisseurId(fournisseurId);

        // Création d’abord
        String response = mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CommandeDTO created = objectMapper.readValue(response, CommandeDTO.class);

        // Récupération par ID
        mockMvc.perform(get("/api/commandes/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"))
                .andExpect(jsonPath("$.fournisseurId").value(fournisseurId));
    }

    // ----------------------------------------------------------
    // Test : récupération de toutes les commandes
    // ----------------------------------------------------------
    @Test
    void testGetAllCommandes() throws Exception {
        // Créer 3 commandes
        for (int i = 0; i < 3; i++) {
            CommandeDTO dto = new CommandeDTO();
            dto.setDateCommande(LocalDateTime.now());
            dto.setStatut(StatutCommande.EN_ATTENTE);
            dto.setFournisseurId(fournisseurId);

            mockMvc.perform(post("/api/commandes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }

        // Vérifier getAll
        mockMvc.perform(get("/api/commandes")
                        .param("page", "0")
                        .param("nbrEelement", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3));
    }

    // ----------------------------------------------------------
    // Test : validation (update) d’une commande
    // ----------------------------------------------------------
    @Test
    void testUpdateCommande() throws Exception {
        CommandeDTO dto = new CommandeDTO();
        dto.setDateCommande(LocalDateTime.now());
        dto.setStatut(StatutCommande.EN_ATTENTE);
        dto.setFournisseurId(fournisseurId);

        // Création
        String response = mockMvc.perform(post("/api/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CommandeDTO created = objectMapper.readValue(response, CommandeDTO.class);

        // Modification du statut
        created.setStatut(StatutCommande.VALIDEE);

        mockMvc.perform(put("/api/commandes/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("VALIDEE"));
    }
}