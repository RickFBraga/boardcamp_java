package com.api.boardcamp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.boardcamp.dtos.BoardcampCustomersDto;
import com.api.boardcamp.services.BoardcampCustomerService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/customers")
public class BoardcampCustomersController {

    private final BoardcampCustomerService boardcampCustomerService;

    public BoardcampCustomersController(BoardcampCustomerService boardcampCustomerService) {
        this.boardcampCustomerService = boardcampCustomerService;
    }

    @PostMapping
    public ResponseEntity<?> insertCustomer(@RequestBody BoardcampCustomersDto boardcampCustomersDto) {
        return boardcampCustomerService.insertCustomer(boardcampCustomersDto);
    }

    @GetMapping
    public ResponseEntity<?> getCustomers() {
        return boardcampCustomerService.getCustomers();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        return boardcampCustomerService.getCustomerById(id);
    }
}
