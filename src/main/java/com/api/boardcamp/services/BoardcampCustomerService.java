package com.api.boardcamp.services;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.boardcamp.dtos.BoardcampCustomersDto;
import com.api.boardcamp.models.BoardcampCustomerModel;
import com.api.boardcamp.repository.BoardcampCustomersRepository;

@Service
public class BoardcampCustomerService {

    private final BoardcampCustomersRepository boardcampCustomersRepository;

    public BoardcampCustomerService(BoardcampCustomersRepository boardcampCustomersRepository) {
        this.boardcampCustomersRepository = boardcampCustomersRepository;
    }

    public ResponseEntity<?> insertCustomer(BoardcampCustomersDto boardcampCustomersDto) {
        try {
            if (boardcampCustomersDto.cpf() == null || boardcampCustomersDto.cpf().trim().length() != 11) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("CPF deve conter exatamente 11 caracteres.");
            }

            if (boardcampCustomersDto.phone() == null || !boardcampCustomersDto.phone().matches("\\d{10,11}")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Phone deve conter 10 ou 11 caracteres numéricos.");
            }

            if (boardcampCustomersDto.name() == null || boardcampCustomersDto.name().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Nome não pode ser nulo ou vazio.");
            }

            if (boardcampCustomersRepository.existsByCpf(boardcampCustomersDto.cpf())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Já existe um cliente com o CPF fornecido.");
            }

            BoardcampCustomerModel customer = new BoardcampCustomerModel(
                    null,
                    boardcampCustomersDto.name().trim(),
                    boardcampCustomersDto.phone(),
                    boardcampCustomersDto.cpf());

            BoardcampCustomerModel savedCustomer = boardcampCustomersRepository.save(customer);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar cliente: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getCustomers() {
        try {
            List<BoardcampCustomerModel> customers = boardcampCustomersRepository.findAll();
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar clientes: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getCustomerById(Long id) {
        try {
            Optional<BoardcampCustomerModel> customer = boardcampCustomersRepository.findById(id);
            if (customer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Cliente não encontrado.");
            }

            return ResponseEntity.ok(customer.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar cliente: " + e.getMessage());
        }
    }
}
