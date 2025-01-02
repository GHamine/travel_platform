package services.booking_management_service;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Hotel {
    private int hotelId;
    private String hotelName;
    private Timestamp checkInDate;
    private Timestamp checkOutDate;
    private String roomType;
    private int numberOfGuests;
    private BigDecimal price;

    // Constructor
    public Hotel(int hotelId, String hotelName, Timestamp checkInDate, Timestamp checkOutDate, 
                 String roomType, int numberOfGuests, BigDecimal price) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomType = roomType;
        this.numberOfGuests = numberOfGuests;
        this.price = price;
    }

    // Getters and Setters
    public int getHotelId() { return hotelId; }
    public void setHotelId(int hotelId) { this.hotelId = hotelId; }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public Timestamp getCheckInDate() { return checkInDate; }
    public void setCheckInDate(Timestamp checkInDate) { this.checkInDate = checkInDate; }

    public Timestamp getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(Timestamp checkOutDate) { this.checkOutDate = checkOutDate; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public int getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(int numberOfGuests) { this.numberOfGuests = numberOfGuests; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    
    @Override
    public String toString() {
        return hotelName ;
    }
}
