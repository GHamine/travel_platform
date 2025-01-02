package services.itinerary_generation_service;

/**
 * - Generates detailed travel itineraries, including:
 *      - Real-time currency conversion for payments.
 *      - Integration with external SOAP services for weather updates at destinations.
 * - Provides itinerary export options in PDF or XML format.
 */

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


import services.booking_management_service.*;
import services.booking_management_service.Booking.BookingStatus;
import services.booking_management_service.Booking.BookingType;

public class ItineraryGenerationService {

    
    private static final String URL = "jdbc:mysql://localhost:3306/travel_agency";
    private static final String USER = "root";  
    private static final String PASSWORD = "0000";

    
    private static List<Flight> myFlights = new ArrayList<>();
    private static List<Hotel> myHotels = new ArrayList<>();
    private static List<Activity> myActivities = new ArrayList<>();


    // Method for currency conversion (stub implementation)
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        // Simulate currency conversion. Replace with actual API integration.
        BigDecimal conversionRate = new BigDecimal("1.1"); // Example rate
        return amount.multiply(conversionRate);
    }

    // Method to fetch weather information (stub implementation)
    public String getWeatherUpdate(String destination) {
        // Simulate fetching weather data. Replace with actual SOAP service integration.
        return "Weather in " + destination + ": Sunny, 25°C";
    }



    public static void main(String[] args) {
        

        int userId = -1;
        String username = "gherbi";
        String password = "1234";

        String query = "SELECT user_id FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userId = resultSet.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (userId == -1) {
            System.out.println("Invalid username or password");
            return;
        }else{
            System.out.println("User ID: " + userId);
        }
        

        // Fetch bookings for the user
        
        List<Booking> allBookings = new ArrayList<>();
        query = "SELECT * FROM bookings WHERE user_id = ? AND booking_status = 'Confirmed'";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int bookingId = resultSet.getInt("booking_id");
                int packageId = resultSet.getInt("package_id");
                
                
                Integer flightId = resultSet.getInt("flight_id");
                Flight flight = null;
                if (flightId != null) {
                    // Fetch flight details
                    query = "SELECT * FROM flights WHERE flight_id = ?";
                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(query)) {
                        preparedStatement2.setInt(1, flightId);
                        ResultSet resultSet2 = preparedStatement2.executeQuery();
                        if (resultSet2.next()) {
                            
                            flight = new Flight(
                                resultSet2.getInt("flight_id"),
                                resultSet2.getString("airline_name"),
                                resultSet2.getString("flight_number"),
                                resultSet2.getString("departure_city"),
                                resultSet2.getString("destination_city"),
                                resultSet2.getTimestamp("departure_date"),
                                resultSet2.getTimestamp("arrival_date"),
                                resultSet2.getBigDecimal("price")
                            );
                        }
                    }
                }
                Integer hotelId = resultSet.getInt("hotel_id");
                Hotel hotel = null;
                if (hotelId != null) {
                    // Fetch hotel details
                    query = "SELECT * FROM hotels WHERE hotel_id = ?";
                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(query)) {
                        preparedStatement2.setInt(1, hotelId);
                        ResultSet resultSet2 = preparedStatement2.executeQuery();
                        if (resultSet2.next()) {
                            hotel = new Hotel(
                                resultSet2.getInt("hotel_id"),
                                resultSet2.getString("hotel_name"),
                                resultSet2.getTimestamp("check_in_date"),
                                resultSet2.getTimestamp("check_out_date"),
                                resultSet2.getString("room_type"),
                                resultSet2.getInt("number_of_guests"),
                                resultSet2.getBigDecimal("price")
                            );
                        }
                    }
                }
                Integer activityId = resultSet.getInt("activity_id");
                Activity activity = null;
                if (activityId != null) {
                    // Fetch activity details
                    query = "SELECT * FROM activities WHERE activity_id = ?";
                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(query)) {
                        preparedStatement2.setInt(1, activityId);
                        ResultSet resultSet2 = preparedStatement2.executeQuery();
                        if (resultSet2.next()) {
                            activity = new Activity(
                                resultSet2.getInt("activity_id"),
                                resultSet2.getString("activity_name"),
                                resultSet2.getTimestamp("activity_date"),
                                resultSet2.getString("location"),
                                resultSet2.getInt("duration"),
                                resultSet2.getBigDecimal("price")
                            );
                        }
                    }
                }
                
                if (flightId != 0) {
                    allBookings.add(new Booking(bookingId, userId, packageId, BookingStatus.CONFIRMED, BookingType.FLIGHT, flight));
                    myFlights.add(flight);
                } 
                if (hotelId != 0) {
                    allBookings.add(new Booking(bookingId, userId, packageId, BookingStatus.CONFIRMED, BookingType.HOTEL, hotel));
                    myHotels.add(hotel);
                }

                if(activityId != 0) {
                    allBookings.add(new Booking(bookingId, userId, packageId, BookingStatus.CONFIRMED, BookingType.ACTIVITY, activity));
                    myActivities.add(activity);
                }
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        // Display the all bookings
        System.out.println("All Bookings:");
        for (Booking booking : allBookings) {
            System.out.println(booking);
        }

        // get package id without duplicates
        List<Integer> packageIds = new ArrayList<>();
        for (Booking booking : allBookings) {
            if (!packageIds.contains(booking.getPackageId())) {
                packageIds.add(booking.getPackageId());
            }
        }

        //get packages details
        query = "SELECT * FROM travel_packages WHERE package_id = ?";
        List<TravelPackage> myPackages = new ArrayList<>();  
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int packageId : packageIds) {
                preparedStatement.setInt(1, packageId);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    myPackages.add(new TravelPackage(
                        resultSet.getInt("package_id"),
                        resultSet.getString("package_name"),
                        resultSet.getString("destination"),
                        resultSet.getDouble("total_price"),
                        resultSet.getInt("travel_dates"),
                        resultSet.getString("description")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Display the all packages
        System.out.print("All Packages:");
        for (TravelPackage travelPackage : myPackages) {
            System.out.print(travelPackage);
        }

        int total_price = 0;


        // display the all flights hotels and activities
        System.out.println("All Flights:");
        for (Flight flight : myFlights) {
            System.out.println("Flight: " + flight + " from " + flight.getDepartureCity() + " to " + flight.getDestinationCity() + " on " + flight.getDepartureDate() + " and arrival date " + flight.getArrivalDate() + " with price " + flight.getPrice() + " USD");
            total_price += flight.getPrice().intValue();
        }
        
        System.out.println("All Hotels:");
        for (Hotel hotel : myHotels) {
            System.out.println("Hotel: " + hotel + " from " + hotel.getCheckInDate() + " to " + hotel.getCheckOutDate() + " with room type " + hotel.getRoomType() + " for " + hotel.getNumberOfGuests() + " guests" + " with price " + hotel.getPrice() + " USD");
            total_price += hotel.getPrice().intValue();
        }

        System.out.println("All Activities:");
        for (Activity activity : myActivities) {
            System.out.println("Activity: " + activity + " on " + activity.getActivityDate() + " at " + activity.getLocation() + " for " + activity.getDuration() + " hours" + " with price " + activity.getPrice() + " USD");
            total_price += activity.getPrice().intValue();
        }


        // Display the total price
        System.out.println("Total Price: " + total_price + " USD");



        String itinerary = "Itinerary for User: " + username + "\n";
        
        itinerary += "Total Price: " + total_price + " USD\n\n";
        itinerary += "Flights:\n";
        for (Flight flight : myFlights) {
            itinerary += "Flight: " + flight + " from " + flight.getDepartureCity() + " to " + flight.getDestinationCity() + " on " + flight.getDepartureDate() + " and arrival date " + flight.getArrivalDate() + " with price " + flight.getPrice() + " USD\n";
        }
        itinerary += "\nHotels:\n";
        for (Hotel hotel : myHotels) {
            itinerary += "Hotel: " + hotel + " from " + hotel.getCheckInDate() + " to " + hotel.getCheckOutDate() + " with room type " + hotel.getRoomType() + " for " + hotel.getNumberOfGuests() + " guests" + " with price " + hotel.getPrice() + " USD\n";
        }
        itinerary += "\nActivities:\n";
        for (Activity activity : myActivities) {
            itinerary += "Activity: " + activity + " on " + activity.getActivityDate() + " at " + activity.getLocation() + " for " + activity.getDuration() + " hours" + " with price " + activity.getPrice() + " USD\n";
        }


        // Generate txt file
        try {
            File file = new File("itinerary.txt");
            java.io.FileWriter fileWriter = new java.io.FileWriter(file);
            fileWriter.write(itinerary);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



        generateItineraryXML(username, total_price);


    }

    private static void generateItineraryXML(String username, int total_price) throws TransformerFactoryConfigurationError {
        // convert to XML
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            Element root = document.createElement("itinerary");
            document.appendChild(root);

            Element userElement = document.createElement("user");
            userElement.appendChild(document.createTextNode(username));
            root.appendChild(userElement);

            Element priceElement = document.createElement("total_price");
            priceElement.appendChild(document.createTextNode(String.valueOf(total_price)));
            root.appendChild(priceElement);

            Element flightsElement = document.createElement("flights");
            for (Flight flight : myFlights) {
                Element flightElement = document.createElement("flight");
                Element flightIdElement = document.createElement("flight_id");
                flightIdElement.appendChild(document.createTextNode(String.valueOf(flight.getFlightId())));
                flightElement.appendChild(flightIdElement);
                Element airlineNameElement = document.createElement("airline_name");
                airlineNameElement.appendChild(document.createTextNode(flight.getAirlineName()));
                flightElement.appendChild(airlineNameElement);
                Element flightNumberElement = document.createElement("flight_number");
                flightNumberElement.appendChild(document.createTextNode(flight.getFlightNumber()));
                flightElement.appendChild(flightNumberElement);
                Element departureCityElement = document.createElement("departure_city");
                departureCityElement.appendChild(document.createTextNode(flight.getDepartureCity()));
                flightElement.appendChild(departureCityElement);
                Element destinationCityElement = document.createElement("destination_city");
                destinationCityElement.appendChild(document.createTextNode(flight.getDestinationCity()));
                flightElement.appendChild(destinationCityElement);
                Element departureDateElement = document.createElement("departure_date");
                departureDateElement.appendChild(document.createTextNode(flight.getDepartureDate().toString()));
                flightElement.appendChild(departureDateElement);
                Element arrivalDateElement = document.createElement("arrival_date");
                arrivalDateElement.appendChild(document.createTextNode(flight.getArrivalDate().toString()));
                flightElement.appendChild(arrivalDateElement);
                Element priceElement2 = document.createElement("price");
                priceElement2.appendChild(document.createTextNode(flight.getPrice().toString()));
                flightElement.appendChild(priceElement2);
                flightsElement.appendChild(flightElement);
            }
            root.appendChild(flightsElement);

            Element hotelsElement = document.createElement("hotels");
            for (Hotel hotel : myHotels) {
                Element hotelElement = document.createElement("hotel");
                Element hotelIdElement = document.createElement("hotel_id");
                hotelIdElement.appendChild(document.createTextNode(String.valueOf(hotel.getHotelId())));
                hotelElement.appendChild(hotelIdElement);
                Element hotelNameElement = document.createElement("hotel_name");
                hotelNameElement.appendChild(document.createTextNode(hotel.getHotelName()));
                hotelElement.appendChild(hotelNameElement);
                Element checkInDateElement = document.createElement("check_in_date");
                checkInDateElement.appendChild(document.createTextNode(hotel.getCheckInDate().toString()));
                hotelElement.appendChild(checkInDateElement);
                Element checkOutDateElement = document.createElement("check_out_date");
                checkOutDateElement.appendChild(document.createTextNode(hotel.getCheckOutDate().toString()));
                hotelElement.appendChild(checkOutDateElement);
                Element roomTypeElement = document.createElement("room_type");
                roomTypeElement.appendChild(document.createTextNode(hotel.getRoomType()));
                hotelElement.appendChild(roomTypeElement);
                Element numberOfGuestsElement = document.createElement("number_of_guests");
                numberOfGuestsElement.appendChild(document.createTextNode(String.valueOf(hotel.getNumberOfGuests())));
                hotelElement.appendChild(numberOfGuestsElement);
                Element priceElement3 = document.createElement("price");
                priceElement3.appendChild(document.createTextNode(hotel.getPrice().toString()));
                hotelElement.appendChild(priceElement3);
                hotelsElement.appendChild(hotelElement);
            }
            root.appendChild(hotelsElement);

            Element activitiesElement = document.createElement("activities");
            for (Activity activity : myActivities) {
                Element activityElement = document.createElement("activity");
                Element activityIdElement = document.createElement("activity_id");
                activityIdElement.appendChild(document.createTextNode(String.valueOf(activity.getActivityId())));
                activityElement.appendChild(activityIdElement);
                Element activityNameElement = document.createElement("activity_name");
                activityNameElement.appendChild(document.createTextNode(activity.getActivityName()));
                activityElement.appendChild(activityNameElement);
                Element activityDateElement = document.createElement("activity_date");
                activityDateElement.appendChild(document.createTextNode(activity.getActivityDate().toString()));
                activityElement.appendChild(activityDateElement);
                Element locationElement = document.createElement("location");
                locationElement.appendChild(document.createTextNode(activity.getLocation()));
                activityElement.appendChild(locationElement);
                Element durationElement = document.createElement("duration");
                durationElement.appendChild(document.createTextNode(String.valueOf(activity.getDuration())));
                activityElement.appendChild(durationElement);
                Element priceElement4 = document.createElement("price");
                priceElement4.appendChild(document.createTextNode(activity.getPrice().toString()));
                activityElement.appendChild(priceElement4);
                activitiesElement.appendChild(activityElement);
            }
            root.appendChild(activitiesElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File("itinerary.xml"));

            transformer.transform(domSource, streamResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    



    
}