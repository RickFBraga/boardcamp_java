package com.api.boardcamp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.boardcamp.dtos.BoardcampDto;
import com.api.boardcamp.services.BoardcampService;

@RestController
@RequestMapping("/games")
public class BoardcampController {

    private final BoardcampService boardcampService;

    public BoardcampController(BoardcampService boardcampService) {
        this.boardcampService = boardcampService;
    }

    @PostMapping
    public ResponseEntity<?> insertGames(@RequestBody BoardcampDto boardcampDto) {
        return boardcampService.insertGames(boardcampDto);
    }

    @GetMapping
    public ResponseEntity<?> getGames() {
        return boardcampService.getGames();
    }

}
