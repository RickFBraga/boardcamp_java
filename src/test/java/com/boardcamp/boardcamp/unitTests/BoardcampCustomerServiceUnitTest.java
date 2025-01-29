package com.boardcamp.boardcamp.unitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.api.boardcamp.BoardcampApplication;
import com.api.boardcamp.dtos.BoardcampCustomersDto;
import com.api.boardcamp.models.BoardcampCustomerModel;
import com.api.boardcamp.repository.BoardcampCustomersRepository;
import com.api.boardcamp.services.BoardcampCustomerService;

@SpringBootTest(classes = BoardcampApplication.class)
public class BoardcampCustomerServiceUnitTest {

    @Mock
    private BoardcampCustomersRepository boardcampCustomersRepository;

    @InjectMocks
    private BoardcampCustomerService boardcampCustomerService;

    @Test
    public void testInsertValidCustomer() {
        BoardcampCustomersDto validCustomerDto = new BoardcampCustomersDto(null, "João Silva", "12345678901",
                "11987654321");
        BoardcampCustomerModel savedCustomer = new BoardcampCustomerModel(1L, "João Silva", "11987654321",
                "12345678901");

        when(boardcampCustomersRepository.existsByCpf(validCustomerDto.cpf())).thenReturn(false);
        when(boardcampCustomersRepository.save(any(BoardcampCustomerModel.class))).thenReturn(savedCustomer);

        ResponseEntity<?> response = boardcampCustomerService.insertCustomer(validCustomerDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedCustomer, response.getBody());
        verify(boardcampCustomersRepository, times(1)).existsByCpf(validCustomerDto.cpf());
        verify(boardcampCustomersRepository, times(1)).save(any(BoardcampCustomerModel.class));

        BoardcampCustomerModel customer1 = new BoardcampCustomerModel(1L, "João Silva", "11987654321", "12345678901");
        BoardcampCustomerModel customer2 = new BoardcampCustomerModel(2L, "Maria Oliveira", "11987654322",
                "12345678902");
        List<BoardcampCustomerModel> customers = List.of(customer1, customer2);

        when(boardcampCustomersRepository.findAll()).thenReturn(customers);

        ResponseEntity<?> responseData = boardcampCustomerService.getCustomers();

        assertEquals(HttpStatus.OK, responseData.getStatusCode());
        assertEquals(customers, responseData.getBody());
        verify(boardcampCustomersRepository, times(1)).findAll();

        Long customerId = 1L;
        BoardcampCustomerModel customer = new BoardcampCustomerModel(customerId, "João Silva", "11987654321",
                "12345678901");

        when(boardcampCustomersRepository.findById(customerId)).thenReturn(Optional.of(customer));

        ResponseEntity<?> responseTest = boardcampCustomerService.getCustomerById(customerId);

        assertEquals(HttpStatus.OK, responseTest.getStatusCode());
        assertEquals(customer, responseTest.getBody());
        verify(boardcampCustomersRepository, times(1)).findById(customerId);
    }
}