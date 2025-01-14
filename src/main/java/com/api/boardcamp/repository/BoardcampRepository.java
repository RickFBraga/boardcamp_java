package com.api.boardcamp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.boardcamp.models.BoardcampGameModel;

@Repository
public interface BoardcampRepository extends JpaRepository<BoardcampGameModel, Long> {
    boolean existsByName(String name);
}
