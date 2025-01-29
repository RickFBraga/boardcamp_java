package com.boardcamp.boardcamp.IntegrTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.api.boardcamp.BoardcampApplication;
import com.api.boardcamp.dtos.BoardcampDto;
import com.api.boardcamp.models.BoardcampGameModel;
import com.api.boardcamp.repository.BoardcampRepository;
import com.api.boardcamp.services.BoardcampService;

@SpringBootTest(classes = BoardcampApplication.class)
public class BoardcampServiceIntegrationTest {

    @Mock
    private BoardcampRepository boardcampRepository;

    @InjectMocks
    private BoardcampService boardcampService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInsertGames_EmptyName() {
        Long pricePerDay = 1L;
        int stockTotal = 100;
        BoardcampDto gameWithEmptyName = new BoardcampDto(null, "Image Empty Name", stockTotal, pricePerDay);

        ResponseEntity<?> response = boardcampService.insertGames(gameWithEmptyName);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("O campo 'name' deve estar presente e não pode ser nulo.", response.getBody());
        verify(boardcampRepository, times(0)).existsByName(anyString());
        verify(boardcampRepository, times(0)).save(any(BoardcampGameModel.class));
    }

    @Test
    void testInsertGames_InvalidStockTotal() {
        Long pricePerDay = 1L;
        int stockTotal = 0; 
        BoardcampDto gameWithInvalidStock = new BoardcampDto("WAR", "Image WAR", stockTotal, pricePerDay);

        ResponseEntity<?> response = boardcampService.insertGames(gameWithInvalidStock);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("O campo 'stockTotal' deve ser maior que 0 e não pode ser nulo.", response.getBody());
        verify(boardcampRepository, times(0)).existsByName(anyString());
        verify(boardcampRepository, times(0)).save(any(BoardcampGameModel.class));
    }

    @Test
    void testInsertGames_InvalidPricePerDay() {
        Long pricePerDay = 0L;
        int stockTotal = 100;
        BoardcampDto gameWithInvalidPrice = new BoardcampDto("WAR", "Image WAR", stockTotal, pricePerDay);

        ResponseEntity<?> response = boardcampService.insertGames(gameWithInvalidPrice);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("O campo 'pricePerDay' deve ser maior que 0 e não pode ser nulo.", response.getBody());
        verify(boardcampRepository, times(0)).existsByName(anyString());
        verify(boardcampRepository, times(0)).save(any(BoardcampGameModel.class));
    }
}