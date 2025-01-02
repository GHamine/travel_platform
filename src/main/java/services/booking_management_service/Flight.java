package services.booking_management_service;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Flight {
    
    private int flightId;
    private String airlineName;
    private String flightNumber;
    private String departureCity;
    private String destinationCity;
    private Timestamp departureDate;
    private Timestamp arrivalDate;
    private BigDecimal price;

    // Constructor
    public Flight(int flightId, String airlineName, String flightNumber, String departureCity, String destinationCity,
                  Timestamp departureDate, Timestamp arrivalDate, BigDecimal price) {
        this.flightId = flightId;
        this.airlineName = airlineName;
        this.flightNumber = flightNumber;
        this.departureCity = departureCity;
        this.destinationCity = destinationCity;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.price = price;
    }

    // Getters and Setters
    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }

    public String getAirlineName() { return airlineName; }
    public void setAirlineName(String airlineName) { this.airlineName = airlineName; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getDepartureCity() { return departureCity; }
    public void setDepartureCity(String departureCity) { this.departureCity = departureCity; }

    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }

    public Timestamp getDepartureDate() { return departureDate; }
    public void setDepartureDate(Timestamp departureDate) { this.departureDate = departureDate; }

    public Timestamp getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(Timestamp arrivalDate) { this.arrivalDate = arrivalDate; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    @Override
    public String toString() {
        return airlineName ;
    }
}
