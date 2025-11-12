package com.tricol.controller;

import com.tricol.dto.MouvementStockDTO;
import com.tricol.dto.ProduitDTO;
import com.tricol.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    // GET /api/produits
    @GetMapping
    public ResponseEntity<Page<ProduitDTO>> getAllProduits(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(produitService.getAllProduits(page, size));
    }

    // GET /api/produits/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProduitDTO> getProduitById(@PathVariable int id) {
        return ResponseEntity.ok(produitService.getProduitById(id));
    }

    // POST /api/produits
    @PostMapping
    public ResponseEntity<ProduitDTO> createProduit(@RequestBody ProduitDTO produitDTO) {
        ProduitDTO created = produitService.createProduit(produitDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // PUT /api/produits/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProduitDTO> updateProduit(@PathVariable int id, @RequestBody ProduitDTO produitDTO) {
        ProduitDTO updated = produitService.updateProduit(id, produitDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/produits/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable int id) {
        produitService.deleteProduit(id);
        return ResponseEntity.noContent().build();
    }
}
