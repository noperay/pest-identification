package com.zx.insectdetection.mapper;

import com.zx.insectdetection.entity.others.CardCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardCodeRepository extends JpaRepository<CardCode, Integer> {
    Optional<CardCode> findByCode(String code);
}