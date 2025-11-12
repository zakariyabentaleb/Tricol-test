package com.tricol.controller;

import com.tricol.dto.CommandeDTO;
import com.tricol.dto.CommandeLigneDTO;
import com.tricol.model.CommandeLigne;
import com.tricol.service.CommandeLigneService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commandes-lignes")
@RequiredArgsConstructor
public class CommandeLigneController {
    private final CommandeLigneService commandeLigneService;

    //Get All
    @GetMapping
    public ResponseEntity<Page<CommandeLigneDTO>> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "0") int nbrEelement){
        return ResponseEntity.ok(commandeLigneService.getAll(page,nbrEelement));
    }

    //Get by id
    @GetMapping("/{id}")
    public ResponseEntity<CommandeLigneDTO> getCommandeLigneById(@PathVariable int id){
        return ResponseEntity.ok(commandeLigneService.getById(id));
    }

    //Post
    @PostMapping
    public ResponseEntity<CommandeLigneDTO> createCommandeLigne(@RequestBody CommandeLigneDTO commandeLigneDTO){
        CommandeLigneDTO commandeLigneDTOCreated=commandeLigneService.createCommandeLigne(commandeLigneDTO);
        return ResponseEntity.ok(commandeLigneDTOCreated);
    }

    //Put
    @PutMapping("/{id}")
    public ResponseEntity<CommandeLigneDTO> updateCommandeLigne(@PathVariable int id,@RequestBody CommandeLigneDTO commandeLigneDTO){
        CommandeLigneDTO commandeLigneDTOUpdated=commandeLigneService.updateCommandeLigne(id,commandeLigneDTO);
        return ResponseEntity.ok(commandeLigneDTOUpdated);
    }

    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommandeLigne(@PathVariable int id){
        commandeLigneService.deleteCommandeLigne(id);
        return ResponseEntity.noContent().build();
    }

}
