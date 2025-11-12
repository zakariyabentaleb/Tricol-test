package com.tricol.controller;

import com.tricol.dto.FournisseurDTO;
import com.tricol.service.FournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fournisseurs")
@RequiredArgsConstructor
public class FournisseurController {

    private final FournisseurService fournisseurService;

    // GET /api/fournisseurs
    @GetMapping
    public ResponseEntity<Page<FournisseurDTO>> getAllFournisseurs(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "0") int nbrElement) {

        return ResponseEntity.ok(fournisseurService.getAllFournisseurs(page,nbrElement));
    }

    // GET /api/fournisseurs/{id}

    @GetMapping("/{id}")
    public ResponseEntity<?> getFournisseurById(@PathVariable int id) {
        try {
            FournisseurDTO dto = fournisseurService.getFournisseurById(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Fournisseur non trouvé avec id : " + id);
        }
    }


    // POST /api/fournisseurs
    @PostMapping
    public ResponseEntity<FournisseurDTO> createFournisseur(@RequestBody FournisseurDTO fournisseurDTO) {
        FournisseurDTO created = fournisseurService.createFournisseur(fournisseurDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // PUT /api/fournisseurs/{id}
    @PutMapping("/{id}")
    public ResponseEntity<FournisseurDTO> updateFournisseur(@PathVariable int id, @RequestBody FournisseurDTO fournisseurDTO) {
        FournisseurDTO updated = fournisseurService.updateFournisseur(id, fournisseurDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/fournisseurs/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFournisseur(@PathVariable int id) {
        fournisseurService.deleteFournisseur(id);
        return ResponseEntity.noContent().build();
    }
}
