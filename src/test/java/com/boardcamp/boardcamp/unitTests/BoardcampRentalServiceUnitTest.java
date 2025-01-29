package com.boardcamp.boardcamp.unitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.api.boardcamp.BoardcampApplication;
import com.api.boardcamp.dtos.BoardcampRentalDTO;
import com.api.boardcamp.models.BoardcampGameModel;
import com.api.boardcamp.models.BoardcampRentalModel;
import com.api.boardcamp.repository.BoardcampRentalsRepository;
import com.api.boardcamp.repository.BoardcampCustomersRepository;
import com.api.boardcamp.repository.BoardcampRepository;
import com.api.boardcamp.services.BoardcampRentalService;

@SpringBootTest(classes = BoardcampApplication.class)
public class BoardcampRentalServiceUnitTest {

    @Mock
    private BoardcampRentalsRepository rentalsRepository;

    @Mock
    private BoardcampCustomersRepository customersRepository;

    @Mock
    private BoardcampRepository gamesRepository;

    @InjectMocks
    private BoardcampRentalService rentalService;

    @Test
    public void testInsertRental_InvalidDaysRented() {
        // Arrange
        BoardcampRentalDTO rentalDto = new BoardcampRentalDTO(1L, 1L, 0);

        // Act
        ResponseEntity<?> response = rentalService.insertRental(rentalDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("daysRented deve ser maior que 0.", response.getBody());
        verify(customersRepository, never()).findById(anyLong());
        verify(gamesRepository, never()).findById(anyLong());
        verify(rentalsRepository, never()).save(any(BoardcampRentalModel.class));
    }

    @Test
    public void testGetRentals_Success() {
        // Arrange
        BoardcampRentalModel rental = new BoardcampRentalModel();
        when(rentalsRepository.findAll()).thenReturn(Collections.singletonList(rental));

        // Act
        ResponseEntity<?> response = rentalService.getRentals();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.singletonList(rental), response.getBody());
        verify(rentalsRepository, times(1)).findAll();
    }

    @Test
    public void testGetRentalById_Success() {
        // Arrange
        BoardcampRentalModel rental = new BoardcampRentalModel();
        when(rentalsRepository.findById(1L)).thenReturn(Optional.of(rental));

        // Act
        ResponseEntity<?> response = rentalService.getRentalById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rental, response.getBody());
        verify(rentalsRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetRentalById_NotFound() {
        // Arrange
        when(rentalsRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = rentalService.getRentalById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Aluguel não encontrado.", response.getBody());
        verify(rentalsRepository, times(1)).findById(1L);
    }

    @Test
    public void testReturnRental_Success() {
        // Arrange
        BoardcampRentalModel rental = new BoardcampRentalModel();
        rental.setRentDate(LocalDate.now().minusDays(5));
        rental.setDaysRented(3);
        rental.setReturnDate(null);
        rental.setGame(new BoardcampGameModel(1L, "WAR", "Image WAR", 100, 10L));

        when(rentalsRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(rentalsRepository.save(any(BoardcampRentalModel.class))).thenReturn(rental);

        // Act
        ResponseEntity<?> response = rentalService.returnRental(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rental, response.getBody());
        assertNotNull(rental.getReturnDate());
        assertEquals(20L, rental.getDelayFee()); // 2 dias de atraso (5 - 3) * 10
        verify(rentalsRepository, times(1)).findById(1L);
        verify(rentalsRepository, times(1)).save(rental);
    }

    @Test
    public void testDeleteRental_Success() {
        // Arrange
        BoardcampRentalModel rental = new BoardcampRentalModel();
        rental.setReturnDate(LocalDate.now());

        when(rentalsRepository.findById(1L)).thenReturn(Optional.of(rental));

        // Act
        ResponseEntity<?> response = rentalService.deleteRental(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Aluguel excluído com sucesso.", response.getBody());
        verify(rentalsRepository, times(1)).findById(1L);
        verify(rentalsRepository, times(1)).deleteById(1L);
    }
}