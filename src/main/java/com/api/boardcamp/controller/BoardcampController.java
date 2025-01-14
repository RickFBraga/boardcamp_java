package com.api.boardcamp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.boardcamp.dtos.BoardcampDto;
import com.api.boardcamp.models.BoardcampGameModel;
import com.api.boardcamp.repository.BoardcampRepository;

@RestController
@RequestMapping("/games")
public class BoardcampController {

    private final BoardcampRepository boardcampRepository;

    @Autowired
    public BoardcampController(BoardcampRepository boardcampRepository) {
        this.boardcampRepository = boardcampRepository;
    }

    @PostMapping
    public ResponseEntity<?> insertGames(@RequestBody BoardcampDto boardcampDto) {
        try {
            if (boardcampDto.name() == null || boardcampDto.name().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("O campo 'name' deve estar presente e não pode ser nulo.");
            }

            if (boardcampDto.stockTotal() == null || boardcampDto.stockTotal() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("O campo 'stockTotal' deve ser maior que 0 e não pode ser nulo.");
            }

            if (boardcampDto.pricePerDay() == null || boardcampDto.pricePerDay() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("O campo 'pricePerDay' deve ser maior que 0 e não pode ser nulo.");
            }

            if (boardcampRepository.existsByName(boardcampDto.name())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Já existe um jogo com o nome '" + boardcampDto.name() + "'.");
            }

            BoardcampGameModel boardcampGameModel = new BoardcampGameModel(
                    null,
                    boardcampDto.name(),
                    boardcampDto.image(),
                    boardcampDto.stockTotal(),
                    boardcampDto.pricePerDay());

            boardcampRepository.save(boardcampGameModel);

            return ResponseEntity.status(HttpStatus.CREATED).body(boardcampDto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getGames() {
        try {
            List<BoardcampGameModel> games = boardcampRepository.findAll();
            return ResponseEntity.status(HttpStatus.OK).body(games);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
