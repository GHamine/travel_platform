package services.booking_management_service;

import java.sql.*;

public class Booking{


    // Enum for Bookings Status
    public enum BookingStatus {
        PENDING_PAYMENT("Pending Payment"),
        CONFIRMED("Confirmed"),
        CANCELLED("Cancelled");

        private final String status;

        BookingStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return this.status;
        }
    }

    // Enum for Bookings Type
    public enum BookingType {
        FLIGHT("flight"),
        HOTEL("hotel"),
        ACTIVITY("activity");

        private final String type;

        BookingType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return this.type;
        }
    }

    // Enum for Bookings Type

    private int bookingId;
    private int userId;
    private int packageId;
    private BookingStatus bookingStatus;
    private BookingType bookingType;
    private Flight flight;
    private Hotel hotel;
    private Activity activity;


    public Booking(int userId, int packageId, Flight flight) {

        this.userId = userId;
        this.packageId = packageId;
        this.bookingType = BookingType.FLIGHT;
        this.flight = flight;
        this.hotel = null;
        this.activity = null;
    }

    public Booking(int userId, int packageId, Hotel hotel) {

        this.userId = userId;
        this.packageId = packageId;
        this.bookingType = BookingType.HOTEL;
        this.flight = null;
        this.hotel = hotel;
        this.activity = null;
    }

    public Booking(int userId, int packageId, Activity activity) {

        this.userId = userId;
        this.packageId = packageId;
        this.bookingType = BookingType.ACTIVITY;
        this.flight = null;
        this.hotel = null;
        this.activity = activity;
    }
    

    public Booking(int bookingId, int userId, int packageId, BookingStatus bookingStatus, BookingType bookingType, Flight flight) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.packageId = packageId;
        this.bookingStatus = bookingStatus;
        this.bookingType = bookingType;
        this.flight = flight;
    }

    public Booking(int bookingId, int userId, int packageId, BookingStatus bookingStatus, BookingType bookingType, Hotel hotel) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.packageId = packageId;
        this.bookingStatus = bookingStatus;
        this.bookingType = bookingType;
        this.hotel = hotel;
    }

    public Booking(int bookingId, int userId, int packageId, BookingStatus bookingStatus, BookingType bookingType, Activity activity) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.packageId = packageId;
        this.bookingStatus = bookingStatus;
        this.bookingType = bookingType;
        this.activity = activity;
    }

    public boolean cancelBooking(Connection connection) {
        
        String query = "UPDATE bookings SET booking_status = 'Cancelled' WHERE booking_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, bookingId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    
    public boolean confirmBooking(Connection connection) {

        // Confirm the booking
        String query = "UPDATE bookings SET booking_status = 'Confirmed' WHERE booking_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, bookingId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public int getBookingId() {
        return bookingId;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public double getBookingPrice() {
        
        switch (bookingType) {
            case FLIGHT:
                return flight.getPrice().doubleValue();
            case HOTEL:
                return hotel.getPrice().doubleValue();
            case ACTIVITY:
                return activity.getPrice().doubleValue();
            default:
                break;
        }
        
       return -1.0;
    }


    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    @Override
    public String toString() {
        return "bookingId=" + bookingId + ", userId=" + userId + ", packageId=" + packageId
                + ", bookingStatus=" + bookingStatus
                + ", bookingType=" + bookingType ;
    }



    
}