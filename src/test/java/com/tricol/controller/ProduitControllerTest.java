package com.tricol.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tricol.dto.ProduitDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class ProduitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // -----------------------------------------------------------------
    //  Test : Création d’un produit
    // -----------------------------------------------------------------
    @Test
    void testCreateProduit() throws Exception {
        ProduitDTO dto = ProduitDTO.builder()
                .nom("Laptop")
                .description("Laptop Dell Inspiron")
                .prixUnitaire(8000.0)
                .categorie("Informatique")
                .stockActuel(10)
                .build();

        mockMvc.perform(post("/api/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Laptop"))
                .andExpect(jsonPath("$.categorie").value("Informatique"))
                .andExpect(jsonPath("$.stockActuel").value(10));
    }

    // -----------------------------------------------------------------
    //  Test : Récupération d’un produit par ID
    // -----------------------------------------------------------------
    @Test
    void testGetProduitById() throws Exception {
        // Création du produit
        ProduitDTO dto = ProduitDTO.builder()
                .nom("Smartphone")
                .description("Samsung Galaxy")
                .prixUnitaire(4000.0)
                .categorie("Téléphonie")
                .stockActuel(20)
                .build();

        String response = mockMvc.perform(post("/api/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ProduitDTO created = objectMapper.readValue(response, ProduitDTO.class);

        // GET by ID
        mockMvc.perform(get("/api/produits/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Smartphone"))
                .andExpect(jsonPath("$.categorie").value("Téléphonie"))
                .andExpect(jsonPath("$.stockActuel").value(20));
    }

    // -----------------------------------------------------------------
    //  Test : Récupération de tous les produits (pagination)
    // -----------------------------------------------------------------
    @Test
    void testGetAllProduits() throws Exception {
        // Création de plusieurs produits
        for (int i = 1; i <= 3; i++) {
            ProduitDTO dto = ProduitDTO.builder()
                    .nom("Produit " + i)
                    .description("Description " + i)
                    .prixUnitaire(100.0 * i)
                    .categorie("Catégorie " + i)
                    .stockActuel(5 * i)
                    .build();

            mockMvc.perform(post("/api/produits")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }

        // GET all produits avec page=0, nbrElement=5
        mockMvc.perform(get("/api/produits")
                        .param("page", "0")
                        .param("nbrElement", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3)) // 3 produits créés
                .andExpect(jsonPath("$.content[0].nom").value("Produit 1"))
                .andExpect(jsonPath("$.content[1].nom").value("Produit 2"))
                .andExpect(jsonPath("$.content[2].nom").value("Produit 3"));
    }
}