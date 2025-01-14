package com.api.boardcamp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.boardcamp.models.BoardcampCustomerModel;

public interface BoardcampCustomersRepository extends JpaRepository<BoardcampCustomerModel, Long> {
    Optional<BoardcampCustomerModel> findById(Integer id);
    boolean existsByCpf(String cpf);
}
