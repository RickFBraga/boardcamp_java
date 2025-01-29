package com.api.boardcamp.dtos;

public class BoardcampRentalDTO {

    private Long customerId;
    private Long gameId;
    private int daysRented;

    public BoardcampRentalDTO(long l, long m, int i) {
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public int getDaysRented() {
        return daysRented;
    }

    public void setDaysRented(int daysRented) {
        this.daysRented = daysRented;
    }
}
