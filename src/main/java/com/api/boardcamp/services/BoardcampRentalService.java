package com.api.boardcamp.services;

import com.api.boardcamp.dtos.BoardcampCustomersDto;
import com.api.boardcamp.dtos.BoardcampRentalDTO;
import com.api.boardcamp.models.BoardcampCustomerModel;
import com.api.boardcamp.models.BoardcampGameModel;
import com.api.boardcamp.models.BoardcampRentalModel;
import com.api.boardcamp.repository.BoardcampRentalsRepository;
import com.api.boardcamp.repository.BoardcampRepository;
import com.api.boardcamp.repository.BoardcampCustomersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class BoardcampRentalService {

    private final BoardcampRentalsRepository rentalsRepository;
    private final BoardcampCustomersRepository customersRepository;
    private final BoardcampRepository gamesRepository;

    public BoardcampRentalService(BoardcampRentalsRepository rentalsRepository,
            BoardcampCustomersRepository customersRepository,
            BoardcampRepository gamesRepository) {
        this.rentalsRepository = rentalsRepository;
        this.customersRepository = customersRepository;
        this.gamesRepository = gamesRepository;
    }

    public ResponseEntity<?> insertRental(BoardcampRentalDTO rentalDto) {
        Long customerId = rentalDto.getCustomerId();
        Long gameId = rentalDto.getGameId();
        int daysRented = rentalDto.getDaysRented();

        if (daysRented <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("daysRented deve ser maior que 0.");
        }

        if (customerId == null || gameId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("customerId e gameId não podem ser nulos.");
        }

        Optional<BoardcampCustomerModel> customer = customersRepository.findById(customerId);
        Optional<BoardcampGameModel> game = gamesRepository.findById(gameId);

        if (customer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado.");
        }

        if (game.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Jogo não encontrado.");
        }

        if (rentalsRepository.existsByGameIdAndReturnDateNull(gameId)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Não há jogos disponíveis.");
        }

        long originalPrice = game.get().getPricePerDay() * daysRented;

        BoardcampRentalModel rental = new BoardcampRentalModel();
        rental.setCustomer(customer.get());
        rental.setGame(game.get());
        rental.setRentDate(LocalDate.now());
        rental.setDaysRented(daysRented);
        rental.setOriginalPrice(originalPrice);
        rental.setDelayFee(0);
        rental.setReturnDate(null);

        rentalsRepository.save(rental);

        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    public ResponseEntity<?> getRentals() {
        return ResponseEntity.ok(rentalsRepository.findAll());
    }

    public ResponseEntity<?> getRentalById(Long id) {
        Optional<BoardcampRentalModel> rental = rentalsRepository.findById(id);
        if (rental.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aluguel não encontrado.");
        }
        return ResponseEntity.ok(rental.get());
    }

    public ResponseEntity<?> returnRental(Long id) {
        Optional<BoardcampRentalModel> rentalOpt = rentalsRepository.findById(id);
        if (rentalOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aluguel não encontrado.");
        }

        BoardcampRentalModel rental = rentalOpt.get();
        if (rental.getReturnDate() != null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Aluguel já foi finalizado.");
        }

        rental.setReturnDate(LocalDate.now());
        int delayDays = rental.getRentDate().plusDays(rental.getDaysRented()).compareTo(LocalDate.now());
        rental.setDelayFee((int) (delayDays > 0 ? 0 : Math.abs(delayDays) * rental.getGame().getPricePerDay()));

        rentalsRepository.save(rental);

        return ResponseEntity.ok(rental);
    }

    public ResponseEntity<?> deleteRental(Long id) {
        Optional<BoardcampRentalModel> rental = rentalsRepository.findById(id);
        if (rental.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aluguel não encontrado.");
        }

        if (rental.get().getReturnDate() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Aluguel não foi finalizado.");
        }

        rentalsRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Aluguel excluído com sucesso.");
    }
}
