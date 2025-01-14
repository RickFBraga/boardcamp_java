package com.api.boardcamp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.boardcamp.dtos.BoardcampCustomersDto;
import com.api.boardcamp.models.BoardcampCustomerModel;
import com.api.boardcamp.repository.BoardcampCustomersRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/customers")
public class BoardcampCustomersController {

    private final BoardcampCustomersRepository boardcampCustomersRepository;

    public BoardcampCustomersController(BoardcampCustomersRepository boardcampCustomersRepository) {
        this.boardcampCustomersRepository = boardcampCustomersRepository;
    }

    @PostMapping
    public ResponseEntity<?> insertCustomer(@RequestBody BoardcampCustomersDto boardcampCustomersDto) {
        try {
            String cpf = boardcampCustomersDto.cpf();
            if (cpf == null || cpf.isEmpty() || cpf.length() != 11) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("CPF deve ser uma string com 11 caracteres e não pode ser vazio.");
            }

            String phone = boardcampCustomersDto.phone();
            if (phone == null || !phone.matches("\\d{10,11}")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Phone deve ser uma string numérica com 10 ou 11 caracteres.");
            }

            String name = boardcampCustomersDto.name();
            if (name == null || name.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nome não pode ser nulo nem vazio.");
            }

            if (boardcampCustomersRepository.existsByCpf(cpf)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Já existe um cliente com o CPF fornecido.");
            }

            BoardcampCustomerModel boardcampCustomerModel = new BoardcampCustomerModel(
                    null,
                    name,
                    phone,
                    cpf);

            boardcampCustomersRepository.save(boardcampCustomerModel);

            return ResponseEntity.status(HttpStatus.OK).body(boardcampCustomerModel);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar cliente: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getCustomer() {
        try {
            List<BoardcampCustomerModel> customers = boardcampCustomersRepository.findAll();
            return ResponseEntity.status(HttpStatus.OK).body(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable("id") String id) {
        try {
            Long customerId = Long.parseLong(id);

            Optional<BoardcampCustomerModel> customersById = boardcampCustomersRepository.findById(customerId);
            if (!customersById.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer não existe");
            }

            return ResponseEntity.status(HttpStatus.OK).body(customersById.get());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID inválido, deve ser um número");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
