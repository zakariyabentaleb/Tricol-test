package com.tricol.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tricol.dto.FournisseurDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FournisseurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // -----------------------------------------------------------------
    //  Test : Création d’un fournisseur
    // -----------------------------------------------------------------
    @Test
    void testCreateFournisseur() throws Exception {
        FournisseurDTO dto = new FournisseurDTO();
        dto.setSociete("ABC SARL");
        dto.setAdresse("Casablanca");
        dto.setContact("John Doe");
        dto.setEmail("abc@example.com");
        dto.setTelephone("0600000000");
        dto.setVille("Casablanca");
        dto.setICE("ICE123456");

        mockMvc.perform(post("/api/fournisseurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.societe").value("ABC SARL"))
                .andExpect(jsonPath("$.ville").value("Casablanca"));
    }

    // -----------------------------------------------------------------
    //  Test : Récupération d’un fournisseur par ID
    // -----------------------------------------------------------------
    @Test
    void testGetFournisseurById() throws Exception {
        // Créer d’abord le fournisseur
        FournisseurDTO dto = new FournisseurDTO();
        dto.setSociete("XYZ SARL");

        String content = objectMapper.writeValueAsString(dto);

        String response = mockMvc.perform(post("/api/fournisseurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        FournisseurDTO created = objectMapper.readValue(response, FournisseurDTO.class);

        mockMvc.perform(get("/api/fournisseurs/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.societe").value("XYZ SARL"));
    }

    // -----------------------------------------------------------------
    //  Test : Mise à jour d’un fournisseur
    // -----------------------------------------------------------------
    @Test
    void testUpdateFournisseur() throws Exception {
        // Création initiale
        FournisseurDTO dto = new FournisseurDTO();
        dto.setSociete("Initial SARL");

        String response = mockMvc.perform(post("/api/fournisseurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        FournisseurDTO created = objectMapper.readValue(response, FournisseurDTO.class);

        // Modification
        created.setSociete("Updated SARL");

        mockMvc.perform(put("/api/fournisseurs/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.societe").value("Updated SARL"));
    }

    // -----------------------------------------------------------------
    //  Test : Suppression d’un fournisseur
    // -----------------------------------------------------------------
    @Test
    void testDeleteFournisseur() throws Exception {
        FournisseurDTO dto = new FournisseurDTO();
        dto.setSociete("Delete SARL");

        // Création
        String response = mockMvc.perform(post("/api/fournisseurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        FournisseurDTO created = objectMapper.readValue(response, FournisseurDTO.class);

        // Suppression
        mockMvc.perform(delete("/api/fournisseurs/{id}", created.getId()))
                .andExpect(status().isNoContent());

        // Vérifie que la récupération échoue avec 404
        mockMvc.perform(get("/api/fournisseurs/{id}", created.getId()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Fournisseur non trouvé avec id : " + created.getId()));
    }

}