package com.api.boardcamp.controller;

import com.api.boardcamp.dtos.BoardcampRentalDTO;
import com.api.boardcamp.services.BoardcampRentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rentals")
public class BoardcampRentalController {

    private final BoardcampRentalService rentalService;

    public BoardcampRentalController(BoardcampRentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    public ResponseEntity<?> createRental(@RequestBody BoardcampRentalDTO rentalDto) {
        return rentalService.insertRental(rentalDto);
    }

    @GetMapping
    public ResponseEntity<?> getAllRentals() {
        return rentalService.getRentals();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRentalById(@PathVariable Long id) {
        return rentalService.getRentalById(id);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<?> returnRental(@PathVariable Long id) {
        return rentalService.returnRental(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRental(@PathVariable Long id) {
        return rentalService.deleteRental(id);
    }
}
