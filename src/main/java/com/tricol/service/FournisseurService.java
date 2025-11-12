package com.tricol.service;

import com.tricol.dto.FournisseurDTO;
import com.tricol.mapper.FournisseurMapper;
import com.tricol.model.Fournisseur;
import com.tricol.repository.FournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;
    private final FournisseurMapper fournisseurMapper;

    public Page<FournisseurDTO> getAllFournisseurs(int page,int nbrElement) {
        // Ensure valid paging params
        if (page < 0) page = 0;
        if (nbrElement <= 0) nbrElement = 10; // default page size

        Pageable pageable= PageRequest.of(page,nbrElement, Sort.by("id").ascending());
        Page<Fournisseur> fournisseurs=fournisseurRepository.findAll(pageable);
        return fournisseurs.map(fournisseurMapper::toDTO);
    }

    public FournisseurDTO getFournisseurById(int id) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec id : " + id));
        return fournisseurMapper.toDTO(fournisseur);
    }

    public FournisseurDTO createFournisseur(FournisseurDTO fournisseurDTO) {
        Fournisseur fournisseur = fournisseurMapper.toEntity(fournisseurDTO);
        Fournisseur saved = fournisseurRepository.save(fournisseur);
        return fournisseurMapper.toDTO(saved);
    }

    public FournisseurDTO updateFournisseur(int id, FournisseurDTO fournisseurDTO) {
        Fournisseur existing = fournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec id : " + id));

        // Mise à jour des champs
        existing.setSociete(fournisseurDTO.getSociete());
        existing.setAdresse(fournisseurDTO.getAdresse());
        existing.setContact(fournisseurDTO.getContact());
        existing.setEmail(fournisseurDTO.getEmail());
        existing.setTelephone(fournisseurDTO.getTelephone());
        existing.setVille(fournisseurDTO.getVille());
        existing.setICE(fournisseurDTO.getICE());

        Fournisseur updated = fournisseurRepository.save(existing);
        return fournisseurMapper.toDTO(updated);
    }

    public void deleteFournisseur(int id) {
        fournisseurRepository.deleteById(id);
    }
}
