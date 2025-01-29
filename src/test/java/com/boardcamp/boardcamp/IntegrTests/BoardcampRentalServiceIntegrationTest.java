package com.boardcamp.boardcamp.IntegrTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.api.boardcamp.BoardcampApplication;
import com.api.boardcamp.dtos.BoardcampRentalDTO;
import com.api.boardcamp.models.BoardcampCustomerModel;
import com.api.boardcamp.models.BoardcampGameModel;
import com.api.boardcamp.models.BoardcampRentalModel;
import com.api.boardcamp.repository.BoardcampCustomersRepository;
import com.api.boardcamp.repository.BoardcampRepository;
import com.api.boardcamp.repository.BoardcampRentalsRepository;
import com.api.boardcamp.services.BoardcampRentalService;

@SpringBootTest(classes = BoardcampApplication.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BoardcampRentalServiceIntegrationTest {

    @Autowired
    private BoardcampRentalService boardcampRentalService;

    @Autowired
    private BoardcampRentalsRepository rentalsRepository;

    @Autowired
    private BoardcampCustomersRepository customersRepository;

    @Autowired
    private BoardcampRepository gamesRepository;

    private BoardcampCustomerModel customer;
    private BoardcampGameModel game;

    @BeforeEach
    public void setup() {
        customer = new BoardcampCustomerModel();
        customer.setName("João Silva");
        customer.setCpf("12345678901");
        customer.setPhone("11987654321");
        customersRepository.save(customer);

        game = new BoardcampGameModel();
        game.setName("WAR");
        game.setImage("Image WAR");
        game.setStockTotal(10);
        game.setPricePerDay(100L);
        gamesRepository.save(game);
    }

    @Test
    void testInsertRental_InvalidDaysRented() {
        BoardcampRentalDTO rentalDto = new BoardcampRentalDTO(customer.getId(), game.getId(), 0);

        ResponseEntity<?> response = boardcampRentalService.insertRental(rentalDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("daysRented deve ser maior que 0.", response.getBody());
    }

    @Test
    void testGetRentalById_Success() {
        BoardcampRentalModel rental = new BoardcampRentalModel();
        rental.setCustomer(customer);
        rental.setGame(game);
        rental.setRentDate(LocalDate.now());
        rental.setDaysRented(3);
        rental.setOriginalPrice(300L);
        rental.setDelayFee(0);
        rental.setReturnDate(null);
        rentalsRepository.save(rental);

        ResponseEntity<?> response = boardcampRentalService.getRentalById(rental.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof BoardcampRentalModel);

        BoardcampRentalModel foundRental = (BoardcampRentalModel) response.getBody();
        assertEquals(rental.getId(), foundRental.getId());
    }

    @Test
    void testGetRentalById_NotFound() {
        Long nonExistentId = 999L;

        ResponseEntity<?> response = boardcampRentalService.getRentalById(nonExistentId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Aluguel não encontrado.", response.getBody());
    }

    @Test
    void testReturnRental_Success() {
        BoardcampRentalModel rental = new BoardcampRentalModel();
        rental.setCustomer(customer);
        rental.setGame(game);
        rental.setRentDate(LocalDate.now().minusDays(5));
        rental.setDaysRented(3);
        rental.setOriginalPrice(300L);
        rental.setDelayFee(0);
        rental.setReturnDate(null);
        rentalsRepository.save(rental);

        ResponseEntity<?> response = boardcampRentalService.returnRental(rental.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof BoardcampRentalModel);

        BoardcampRentalModel returnedRental = (BoardcampRentalModel) response.getBody();
        assertEquals(LocalDate.now(), returnedRental.getReturnDate());
        assertEquals(200L, returnedRental.getDelayFee());
    }

    @Test
    void testReturnRental_AlreadyReturned() {
        BoardcampRentalModel rental = new BoardcampRentalModel();
        rental.setCustomer(customer);
        rental.setGame(game);
        rental.setRentDate(LocalDate.now().minusDays(5));
        rental.setDaysRented(3);
        rental.setOriginalPrice(300L);
        rental.setDelayFee(0);
        rental.setReturnDate(LocalDate.now());
        rentalsRepository.save(rental);

        ResponseEntity<?> response = boardcampRentalService.returnRental(rental.getId());

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("Aluguel já foi finalizado.", response.getBody());
    }

    @Test
    void testDeleteRental_Success() {
        BoardcampRentalModel rental = new BoardcampRentalModel();
        rental.setCustomer(customer);
        rental.setGame(game);
        rental.setRentDate(LocalDate.now());
        rental.setDaysRented(3);
        rental.setOriginalPrice(300L);
        rental.setDelayFee(0);
        rental.setReturnDate(LocalDate.now());
        rentalsRepository.save(rental);

        ResponseEntity<?> response = boardcampRentalService.deleteRental(rental.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Aluguel excluído com sucesso.", response.getBody());
    }

    @Test
    void testDeleteRental_NotReturned() {
        BoardcampRentalModel rental = new BoardcampRentalModel();
        rental.setCustomer(customer);
        rental.setGame(game);
        rental.setRentDate(LocalDate.now());
        rental.setDaysRented(3);
        rental.setOriginalPrice(300L);
        rental.setDelayFee(0);
        rental.setReturnDate(null);
        rentalsRepository.save(rental);

        ResponseEntity<?> response = boardcampRentalService.deleteRental(rental.getId());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Aluguel não foi finalizado.", response.getBody());
    }
}