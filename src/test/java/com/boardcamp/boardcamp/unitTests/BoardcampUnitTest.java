package com.boardcamp.boardcamp.unitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
public class BoardcampUnitTest {

	@InjectMocks
	private BoardcampService boardcampService;

	@Mock
	private BoardcampRepository boardcampRepository;

	@Mock
	private BoardcampGameModel boardcampGameModel;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testInsertGames() {
		Long pricePerDay = 1L;
		int stockTotal = 100;

		BoardcampDto validGame = new BoardcampDto("WAR", "Image WAR", stockTotal, pricePerDay);

		when(boardcampRepository.existsByName(validGame.name())).thenReturn(false);

		ResponseEntity<?> response = boardcampService.insertGames(validGame);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());

		assertEquals(validGame, response.getBody());

		verify(boardcampRepository, times(0)).save(any(BoardcampGameModel.class));

		BoardcampDto gameWithEmptyName = new BoardcampDto(null, "Image Empty Name", stockTotal, pricePerDay);
		ResponseEntity<?> emptyNameResponse = boardcampService.insertGames(gameWithEmptyName);

		assertEquals(HttpStatus.BAD_REQUEST, emptyNameResponse.getStatusCode());
		assertTrue(emptyNameResponse.getBody().toString()
				.contains("O campo 'name' deve estar presente e não pode ser nulo."));

		BoardcampDto gameWithInvalidStock = new BoardcampDto("Invalid Stock", "Image Invalid Stock", -1, pricePerDay);
		ResponseEntity<?> invalidStockResponse = boardcampService.insertGames(gameWithInvalidStock);

		assertEquals(HttpStatus.BAD_REQUEST, invalidStockResponse.getStatusCode());
		assertTrue(invalidStockResponse.getBody().toString()
				.contains("O campo 'stockTotal' deve ser maior que 0 e não pode ser nulo."));

		BoardcampDto gameWithInvalidPrice = new BoardcampDto("Invalid Price", "Image Invalid Price", stockTotal, 0L);
		ResponseEntity<?> invalidPriceResponse = boardcampService.insertGames(gameWithInvalidPrice);

		assertEquals(HttpStatus.BAD_REQUEST, invalidPriceResponse.getStatusCode());
		assertTrue(invalidPriceResponse.getBody().toString()
				.contains("O campo 'pricePerDay' deve ser maior que 0 e não pode ser nulo."));

		BoardcampDto duplicateGame = new BoardcampDto("WAR", "Image WAR", stockTotal, pricePerDay);

		doThrow(new RuntimeException("Erro interno no repositório"))
				.when(boardcampRepository).save(any(BoardcampGameModel.class));

		ResponseEntity<?> exceptionResponse = boardcampService.insertGames(validGame);

		assertEquals(HttpStatus.CREATED, exceptionResponse.getStatusCode());
		assertFalse(exceptionResponse.getBody().toString().contains("Erro interno no repositório"));

		verify(boardcampRepository, times(0)).existsByName(validGame.name());
		verify(boardcampRepository, times(0)).existsByName(duplicateGame.name());
	}
}