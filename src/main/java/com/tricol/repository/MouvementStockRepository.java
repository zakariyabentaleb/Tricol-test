package com.tricol.repository;

import com.tricol.model.MouvementStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MouvementStockRepository extends JpaRepository<MouvementStock,Integer> {
}