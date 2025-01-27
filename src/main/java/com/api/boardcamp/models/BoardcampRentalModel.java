package com.api.boardcamp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "rentals")
public class BoardcampRentalModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private BoardcampCustomerModel customer;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private BoardcampGameModel game;

    private LocalDate rentDate;
    private int daysRented;
    private LocalDate returnDate;
    private Long originalPrice;
    private int delayFee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BoardcampCustomerModel getCustomer() {
        return customer;
    }

    public void setCustomer(BoardcampCustomerModel customer) {
        this.customer = customer;
    }

    public BoardcampGameModel getGame() {
        return game;
    }

    public void setGame(BoardcampGameModel game) {
        this.game = game;
    }

    public LocalDate getRentDate() {
        return rentDate;
    }

    public void setRentDate(LocalDate rentDate) {
        this.rentDate = rentDate;
    }

    public int getDaysRented() {
        return daysRented;
    }

    public void setDaysRented(int daysRented) {
        this.daysRented = daysRented;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Long getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Long originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getDelayFee() {
        return delayFee;
    }

    public void setDelayFee(int delayFee) {
        this.delayFee = delayFee;
    }
}
