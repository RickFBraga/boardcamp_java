package com.boardcamp.boardcamp.IntegrTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.api.boardcamp.BoardcampApplication;
import com.api.boardcamp.dtos.BoardcampCustomersDto;
import com.api.boardcamp.models.BoardcampCustomerModel;
import com.api.boardcamp.repository.BoardcampCustomersRepository;
import com.api.boardcamp.services.BoardcampCustomerService;

@SpringBootTest(classes = BoardcampApplication.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BoardcampCustomerServiceIntegrationTest {

    @Autowired
    private BoardcampCustomerService boardcampCustomerService;

    @Autowired
    private BoardcampCustomersRepository boardcampCustomersRepository;

    @Test
    void testInsertCustomer_InvalidCpf() {
        BoardcampCustomersDto customerDto = new BoardcampCustomersDto(
                null, "João Silva",
                "11987654321",
                "123456789");

        ResponseEntity<?> response = boardcampCustomerService.insertCustomer(customerDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("CPF deve conter exatamente 11 caracteres.", response.getBody());
    }

    @Test
    void testInsertCustomer_InvalidPhone() {
        BoardcampCustomersDto customerDto = new BoardcampCustomersDto(
                null, "João Silva",
                "119876543", 
                "12345678901");

        ResponseEntity<?> response = boardcampCustomerService.insertCustomer(customerDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Phone deve conter 10 ou 11 caracteres numéricos.", response.getBody());
    }

    @Test
    void testInsertCustomer_EmptyName() {
        BoardcampCustomersDto customerDto = new BoardcampCustomersDto(
                null, "",
                "11987654321",
                "12345678901");

        ResponseEntity<?> response = boardcampCustomerService.insertCustomer(customerDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Nome não pode ser nulo ou vazio.", response.getBody());
    }

    @Test
    void testInsertCustomer_DuplicateCpf() {
        BoardcampCustomersDto customerDto = new BoardcampCustomersDto(
                null, "João Silva",
                "11987654321",
                "12345678901");

        boardcampCustomerService.insertCustomer(customerDto);

        ResponseEntity<?> response = boardcampCustomerService.insertCustomer(customerDto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Já existe um cliente com o CPF fornecido.", response.getBody());
    }

    @Test
    void testGetCustomers_Success() {
        BoardcampCustomersDto customerDto = new BoardcampCustomersDto(
                null, "João Silva",
                "11987654321",
                "12345678901");

        boardcampCustomerService.insertCustomer(customerDto);

        ResponseEntity<?> response = boardcampCustomerService.getCustomers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);

        List<BoardcampCustomerModel> customers = (List<BoardcampCustomerModel>) response.getBody();
        assertEquals(1, customers.size());
        assertEquals(customerDto.name(), customers.get(0).getName());
    }

    @Test
    void testGetCustomerById_NotFound() {
        Long nonExistentId = 999L;

        ResponseEntity<?> response = boardcampCustomerService.getCustomerById(nonExistentId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Cliente não encontrado.", response.getBody());
    }
}