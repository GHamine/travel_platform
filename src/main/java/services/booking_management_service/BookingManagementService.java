package services.booking_management_service;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import services.booking_management_service.Booking.BookingStatus;
import services.booking_management_service.Booking.BookingType;

/**
 * - Handles bookings for multi-leg trips, including flights, hotels, and activities.
 */
public class BookingManagementService {


    private static final String URL = "jdbc:mysql://localhost:3306/travel_agency";
    private static final String USER = "root";  
    private static final String PASSWORD = "0000";

    
    private static List<Flight> myFlights = new ArrayList<>();
    private static List<Hotel> myHotels = new ArrayList<>();
    private static List<Activity> myActivities = new ArrayList<>();

    public static List<Flight> getMyFlights() {
        return myFlights;
    }


    public static List<Hotel> getMyHotels() {
        return myHotels;
    }


    public static List<Activity> getMyActivities() {
        return myActivities;
    }


    
    public boolean makePayment(Booking booking, Connection connection) throws IllegalArgumentException {
        
        // test if user has enough budget
        Double userBudget = 0.0;
        String query = "SELECT budget FROM users WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, booking.getUserId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userBudget = resultSet.getBigDecimal("budget").doubleValue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        if (userBudget < booking.getBookingPrice()) {
            // return false;
            throw new IllegalArgumentException("User does not have enough budget to make this booking");
        }
        
        query = "UPDATE users SET budget = budget - ? WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBigDecimal(1, new BigDecimal(booking.getBookingPrice()));
            preparedStatement.setInt(2, booking.getUserId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        booking.confirmBooking(connection);
        return true;
    }


    public static void main(String[] args) {
        BookingManagementService bookingManagementService = new BookingManagementService();


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
        
        // get all the flights, hotels, and activities
        List<Flight> flights = new ArrayList<>();
        List<Hotel> hotels = new ArrayList<>();
        List<Activity> activities = new ArrayList<>();
        

        fetchBookingsData(flights, hotels, activities);

        // print the data
        System.out.println("Flights:");
        for (Flight flight : flights) {
            System.out.println(flight.getAirlineName() + " " + flight.getFlightNumber() + " " + flight.getDepartureCity() + " " + flight.getDestinationCity() + " " + flight.getDepartureDate() + " " + flight.getArrivalDate() + " " + flight.getPrice());
        }
        System.out.println("Hotels:");
        for (Hotel hotel : hotels) {
            System.out.println(hotel.getHotelName() + " " + hotel.getCheckInDate() + " " + hotel.getCheckOutDate() + " " + hotel.getRoomType() + " " + hotel.getNumberOfGuests() + " " + hotel.getPrice());
        }
        System.out.println("Activities:");
        for (Activity activity : activities) {
            System.out.println(activity.getActivityName() + " " + activity.getActivityDate() + " " + activity.getLocation() + " " + activity.getDuration() + " " + activity.getPrice());
        }



        
        TravelPackage myPackage = new TravelPackage( username + "_Package_" + ((int) (Math.random() * 9000) + 1000));
        query = "insert into travel_packages (package_name) values (?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, myPackage.getPackageName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        query = "SELECT * FROM travel_packages WHERE package_name = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, myPackage.getPackageName());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                myPackage.setPackageId(resultSet.getInt("package_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        
        Connection conn = null;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false);  
            
            
            Booking flightBooking = initializeBookingFlight(userId, myPackage.getPackageId(), flights.get(0), conn);
            
            
            myFlights.add(flights.get(0));
            Booking hotelBooking = initializeBookingHotel(userId, myPackage.getPackageId(), hotels.get(0), conn);
            
            myHotels.add(hotels.get(0));
            Booking activityBooking = initializeBookingActivity(userId, myPackage.getPackageId(), activities.get(0), conn);
            myActivities.add(activities.get(0));
            
            activityBooking.cancelBooking(conn);
            myActivities.remove(activities.get(0));
            activityBooking = initializeBookingActivity(userId, myPackage.getPackageId(), activities.get(1), conn);
            myActivities.add(activities.get(1));

            
            bookingManagementService.makePayment(flightBooking, conn);
            bookingManagementService.makePayment(hotelBooking, conn);
            bookingManagementService.makePayment(activityBooking, conn);
            
            myPackage.setDescription( "flights: " + myFlights + " hotels: " + myHotels + " activities: " + myActivities);
            
            // calculate the total price of the package
            myPackage.setTotalPrice(( myFlights.stream().map(Flight::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add)
            .add(myHotels.stream().map(Hotel::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add))
            .add(myActivities.stream().map(Activity::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add))).doubleValue());

            
            // get the destination of the package from the last flight
            myPackage.setDestination(myFlights.get(myFlights.size() - 1).getDestinationCity());


            
            // myPackage.setTravelDates(  (int)(
            //     Duration.between(firstDepartureDate.toInstant(), lastArrivalDate.toInstant()).toDays()
            //   + Duration.between(firstCheckInDate.toInstant(), lastCheckOutDate.toInstant()).toDays()
            //   + myActivities.stream().map(Activity::getDuration).reduce(0, Integer::sum)  )
            //   );

            myPackage.setTravelDates(myFlights.size()+myHotels.size()+myActivities.size());
            
            // insert the package into the database
            query = "update travel_packages set destination = ?, total_price = ?, travel_dates = ?, description = ? where package_id = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, myPackage.getDestination());
                preparedStatement.setBigDecimal(2, new BigDecimal(myPackage.getTotalPrice()));
                preparedStatement.setInt(3, myPackage.getTravelDates());
                preparedStatement.setString(4, myPackage.getDescription());
                preparedStatement.setInt(5, myPackage.getPackageId());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            conn.commit();
        
        } catch (SQLException | IllegalArgumentException e) {
            
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        

        

        //print the package
        System.out.println("Package: " + myPackage.getPackageName() + " " + myPackage.getPackageId() + " " + myPackage.getDestination() + " " + myPackage.getTotalPrice() + " " + myPackage.getDescription());
    

    }


    
    public void userSelectedPackage(int userId, TravelPackage myPackage) {
        
        String query = "SELECT * FROM travel_packages WHERE package_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, myPackage.getPackageId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                myPackage.setPackageName(resultSet.getString("package_name"));
                myPackage.setDestination(resultSet.getString("destination"));
                myPackage.setTotalPrice(resultSet.getDouble("total_price"));
                myPackage.setTravelDates(resultSet.getInt("travel_dates"));
                myPackage.setDescription(resultSet.getString("description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // get all the bookings for this package
        List<Flight> flights = new ArrayList<>();
        List<Hotel> hotels = new ArrayList<>();
        List<Activity> activities = new ArrayList<>();
        query = "SELECT * FROM bookings WHERE package_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, myPackage.getPackageId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString("booking_type").equals("flight")) {
                    for (Flight flight : myFlights) {
                        if (flight.getFlightId() == resultSet.getInt("flight_id")) {
                            flights.add(flight);
                        }
                    }
                } else if (resultSet.getString("booking_type").equals("hotel")) {
                    for (Hotel hotel : myHotels) {
                        if (hotel.getHotelId() == resultSet.getInt("hotel_id")) {
                            hotels.add(hotel);
                        }
                    }
                } else if (resultSet.getString("booking_type").equals("activity")) {
                    for (Activity activity : myActivities) {
                        if (activity.getActivityId() == resultSet.getInt("activity_id")) {
                            activities.add(activity);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
       
        Connection conn = null;
        try {
            BookingManagementService bookingManagementService = new BookingManagementService();
            // create a connection to the database
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false);  
            
            for (Flight flight : myFlights) {
                Booking flightBooking = initializeBookingFlight(userId, myPackage.getPackageId(), flight, conn);
                bookingManagementService.makePayment(flightBooking, conn);
            }
            for (Hotel hotel : myHotels) {
                Booking hotelBooking = initializeBookingHotel(userId, myPackage.getPackageId(), hotel, conn);
                bookingManagementService.makePayment(hotelBooking, conn);
            }
            for (Activity activity : myActivities) {
                Booking activityBooking = initializeBookingActivity(userId, myPackage.getPackageId(), activity, conn);
                bookingManagementService.makePayment(activityBooking, conn);
            }
            
            
            conn.commit();
        
        } catch (SQLException | IllegalArgumentException e) {
            
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }


    private static void fetchBookingsData(List<Flight> flights, List<Hotel> hotels, List<Activity> activities) {
        String query;
        query = "SELECT * FROM flights";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Flight flight = new Flight(
                    resultSet.getInt("flight_id"), 
                    resultSet.getString("airline_name"), 
                    resultSet.getString("flight_number"), 
                    resultSet.getString("departure_city"), 
                    resultSet.getString("destination_city"), 
                    resultSet.getTimestamp("departure_date"), 
                    resultSet.getTimestamp("arrival_date"), 
                    resultSet.getBigDecimal("price"));
                flights.add(flight);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        query = "SELECT * FROM hotels";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Hotel hotel = new Hotel(
                    resultSet.getInt("hotel_id"), 
                    resultSet.getString("hotel_name"), 
                    resultSet.getTimestamp("check_in_date"), 
                    resultSet.getTimestamp("check_out_date"), 
                    resultSet.getString("room_type"), 
                    resultSet.getInt("number_of_guests"), 
                    resultSet.getBigDecimal("price"));
                hotels.add(hotel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        query = "SELECT * FROM activities";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Activity activity = new Activity(
                    resultSet.getInt("activity_id"), 
                    resultSet.getString("activity_name"), 
                    resultSet.getTimestamp("activity_date"), 
                    resultSet.getString("location"), 
                    resultSet.getInt("duration"), 
                    resultSet.getBigDecimal("price"));
                activities.add(activity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private static Booking initializeBookingFlight(int userId, int package_id, Flight flight, Connection connection) throws SQLException {
        String query;
        query = "INSERT INTO bookings (user_id, package_id, booking_status, booking_type, flight_id, hotel_id, activity_id) VALUES (?, ?, ?, ?, ?, ?, ?)"; 
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, package_id);
            preparedStatement.setString(3, "Pending Payment");
            preparedStatement.setString(4, "flight");
            preparedStatement.setInt(5, flight.getFlightId());
            preparedStatement.setNull(6, Types.INTEGER);
            preparedStatement.setNull(7, Types.INTEGER);
            preparedStatement.executeUpdate();
        }catch (SQLException e) {

            e.printStackTrace();
        }

        Booking booking = null;
        query = "SELECT * FROM bookings WHERE user_id = ? AND package_id = ? AND booking_status = ? AND booking_type = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, package_id);
            preparedStatement.setString(3, "Pending Payment");
            preparedStatement.setString(4, "flight");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                booking = new Booking(
                    resultSet.getInt("booking_id"), 
                    resultSet.getInt("user_id"), 
                    resultSet.getInt("package_id"), 
                    BookingStatus.PENDING_PAYMENT, 
                    BookingType.FLIGHT, 
                    flight);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return booking;
    }

    private static Booking initializeBookingHotel(int userId, int package_id, Hotel hotel, Connection connection) throws SQLException {
        String query;
        query = "INSERT INTO bookings (user_id, package_id, booking_status, booking_type, flight_id, hotel_id, activity_id) VALUES (?, ?, ?, ?, ?, ?, ?)"; 
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, package_id);
            preparedStatement.setString(3, "Pending Payment");
            preparedStatement.setString(4, "hotel");
            preparedStatement.setNull(5, Types.INTEGER);
            preparedStatement.setInt(6, hotel.getHotelId());
            preparedStatement.setNull(7, Types.INTEGER);
            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        Booking booking = null;
        query = "SELECT * FROM bookings WHERE user_id = ? AND package_id = ? AND booking_status = ? AND booking_type = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, package_id);
            preparedStatement.setString(3, "Pending Payment");
            preparedStatement.setString(4, "hotel");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                booking = new Booking(
                    resultSet.getInt("booking_id"), 
                    resultSet.getInt("user_id"), 
                    resultSet.getInt("package_id"), 
                    BookingStatus.PENDING_PAYMENT, 
                    BookingType.HOTEL, 
                    hotel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return booking;
    }

    private static Booking initializeBookingActivity(int userId, int package_id, Activity activity, Connection connection) throws SQLException {
        String query;
        query = "INSERT INTO bookings (user_id, package_id, booking_status, booking_type, flight_id, hotel_id, activity_id) VALUES (?, ?, ?, ?, ?, ?, ?)"; 
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, package_id);
            preparedStatement.setString(3, "Pending Payment");
            preparedStatement.setString(4, "activity");
            preparedStatement.setNull(5, Types.INTEGER);
            preparedStatement.setNull(6, Types.INTEGER);
            preparedStatement.setInt(7, activity.getActivityId());
            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }

        Booking booking = null;
        query = "SELECT * FROM bookings WHERE user_id = ? AND package_id = ? AND booking_status = ? AND booking_type = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, package_id);
            preparedStatement.setString(3, "Pending Payment");
            preparedStatement.setString(4, "activity");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                booking = new Booking(
                    resultSet.getInt("booking_id"), 
                    resultSet.getInt("user_id"), 
                    resultSet.getInt("package_id"), 
                    BookingStatus.PENDING_PAYMENT, 
                    BookingType.ACTIVITY, 
                    activity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return booking;
    }
}