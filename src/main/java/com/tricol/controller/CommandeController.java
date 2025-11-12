package com.tricol.controller;

import com.tricol.dto.CommandeDTO;
import com.tricol.service.CommandeService;
import com.tricol.service.FournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
public class CommandeController {
    private final CommandeService commandeService;

    //Get All
    @GetMapping
    public ResponseEntity<Page<CommandeDTO>> getAll(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "0") int nbrEelement){
        return ResponseEntity.ok(commandeService.getAllCommandes(page,nbrEelement));
    }

    //Get by id
    @GetMapping("/{id}")
    public ResponseEntity<CommandeDTO> getCommandeById(@PathVariable int id){
        return ResponseEntity.ok(commandeService.getById(id));
    }

    //Post
    @PostMapping
    public ResponseEntity<CommandeDTO> createCommande(@RequestBody CommandeDTO commandeDTO){
        CommandeDTO commandeDTOCreated=commandeService.createCommande(commandeDTO);
        return ResponseEntity.ok(commandeDTOCreated);
    }

    //Put
    @PutMapping("/{id}")
    public ResponseEntity<CommandeDTO> updateCommande(@PathVariable int id,@RequestBody CommandeDTO commandeDTO){
        CommandeDTO commandeDTOUpdated=commandeService.updateCommande(id,commandeDTO);
        return ResponseEntity.ok(commandeDTOUpdated);
    }

    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable int id){
        commandeService.deleteCommande(id);
        return ResponseEntity.noContent().build();
    }

}
