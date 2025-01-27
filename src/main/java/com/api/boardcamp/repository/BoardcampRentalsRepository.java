package com.api.boardcamp.repository;

import com.api.boardcamp.models.BoardcampRentalModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardcampRentalsRepository extends JpaRepository<BoardcampRentalModel, Long> {
    Optional<BoardcampRentalModel> findById(Long id);

    boolean existsByGameIdAndReturnDateNull(Long gameId);
}
