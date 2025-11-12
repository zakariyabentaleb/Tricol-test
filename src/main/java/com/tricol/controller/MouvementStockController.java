package com.tricol.controller;


import com.tricol.dto.MouvementStockDTO;
import com.tricol.service.MouvementStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mouvements-stock")
@RequiredArgsConstructor
public class MouvementStockController {

    private final MouvementStockService mouvementStockService;

    @GetMapping
    public ResponseEntity<Page<MouvementStockDTO>> getAll(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(mouvementStockService.getAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MouvementStockDTO> getById(@PathVariable int id){
        return ResponseEntity.ok(mouvementStockService.getById(id));
    }

    @PostMapping
    public ResponseEntity<MouvementStockDTO> create(@RequestBody MouvementStockDTO dto){
        return ResponseEntity.ok(mouvementStockService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MouvementStockDTO> update(@PathVariable int id, @RequestBody MouvementStockDTO dto){
        return ResponseEntity.ok(mouvementStockService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id){
        mouvementStockService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
