package services.package_search_service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PackageSearchService {

    private static final String URL = "jdbc:mysql://localhost:3306/travel_agency";
    private static final String USER = "root";  
    private static final String PASSWORD = "0000";

    private List<TravelPackage> allPackages;

    public PackageSearchService() {
        this.allPackages = getAllTravelPackages();
    }


    public List<TravelPackage> getAllTravelPackages() {
        List<TravelPackage> travelPackages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT * FROM travel_packages";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                TravelPackage travelPackage = new TravelPackage(
                resultSet.getInt("package_id"),
                resultSet.getString("package_name"),
                resultSet.getString("destination"),
                resultSet.getBigDecimal("total_price").doubleValue(),
                resultSet.getInt("travel_dates"),
                resultSet.getString("description"));
                travelPackages.add(travelPackage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return travelPackages;
    }

    public UserProfile loginUser(String username, String password) throws SQLException {
        UserProfile userProfile = null;

        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    userProfile = new UserProfile(
                        resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        password,
                        resultSet.getString("role"),
                        resultSet.getBigDecimal("budget").doubleValue(),
                        resultSet.getString("preferred_destination"),
                        resultSet.getInt("travel_duration")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userProfile;
    }

    

    public boolean hasVisitedBefore(UserProfile userProfile, TravelPackage travelPackage) {

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT * FROM bookings b JOIN travel_packages tp ON b.package_id = tp.package_id WHERE b.user_id = ? AND tp.destination = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userProfile.getUserId());
            statement.setString(2, travelPackage.getDestination());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public List<TravelPackage> searchByBudget(Double budget) {
        List<TravelPackage> filteredPackages = new ArrayList<>();

        for (TravelPackage pkg : allPackages) {
            if (budget == null || pkg.getTotalPrice() <= budget) {
                filteredPackages.add(pkg);
            }
        }

        return filteredPackages;
    }

    public List<TravelPackage> searchByTravelDates(int travelDates) {
        List<TravelPackage> filteredPackages = new ArrayList<>();

        for (TravelPackage pkg : allPackages) {
           if (travelDates > 0 || pkg.getTravelDates() >= travelDates){
                filteredPackages.add(pkg);
            }
        }

        return filteredPackages;
    }

    public List<TravelPackage> searchByDestinationPrefs(String destinationPrefs) {
        List<TravelPackage> filteredPackages = new ArrayList<>();

        for (TravelPackage pkg : allPackages) {
            if (destinationPrefs == null || destinationPrefs.isEmpty()
                    || destinationPrefs.equals(pkg.getDestination())) {
                filteredPackages.add(pkg);
            }
        }

        return filteredPackages;
    }

    public List<TravelPackage> searchPackages(Double budget, int travelDates, String destinationPrefs) {
        List<TravelPackage> filteredPackages = new ArrayList<>();

        for (TravelPackage pkg : allPackages) {
            if ((budget == null || pkg.getTotalPrice() <= budget)
                    && (travelDates >= 0 || pkg.getTravelDates() >= travelDates)
                    && (destinationPrefs == null || destinationPrefs.isEmpty()
                            || destinationPrefs.equals(pkg.getDestination()))) {
                filteredPackages.add(pkg);
            }
        }

        return filteredPackages;
    }

    public List<TravelPackage> getRecommendPackages(UserProfile userProfile) {
        List<TravelPackage> recommendedPackages = new ArrayList<>();

        for (TravelPackage pkg : allPackages) {
            double score = calculateRecommendationScore(userProfile, allPackages, pkg);
            if (score > 0) {
                recommendedPackages.add(pkg);
            }
        }

        return recommendedPackages;
    }

    private double calculateRecommendationScore(UserProfile userProfile, List<TravelPackage> allPackages, TravelPackage pkg) {
        double score = 0;

        for (TravelPackage travelPackage : allPackages) {
            if (travelPackage == pkg && hasVisitedBefore(userProfile, travelPackage))  {
                score += 2;
            }
        }

        if (pkg.getTotalPrice() <= userProfile.getBudget()) {
            score += 5;
        }else if (pkg.getTotalPrice() <= userProfile.getBudget() + 500) {
            score += 3;
        }else if (pkg.getTotalPrice() <= userProfile.getBudget() + 1000) {
            score += 1;
        }else {
            score -= 10;
        }

        if (pkg.getTravelDates() >= userProfile.getTravelDuration()) {
            score += 5;
        }else if (pkg.getTravelDates() >= userProfile.getTravelDuration() - 2) {
            score += 3;
        }else if (pkg.getTravelDates() >= userProfile.getTravelDuration() - 4) {
            score += 1;
        }else { 
            score -= 10;
        }

        if (pkg.getDestination().equals(userProfile.getPreferredDestinations())) {
            score += 10;
        }else {
            score -= 7;
        }


        return score;
    }


    public static void main(String[] args) {
        PackageSearchService service = new PackageSearchService();

        // Demonstrate getAllTravelPackages
        List<TravelPackage> allPackages = service.getAllTravelPackages();
        System.out.println("All Travel Packages:");
        for (TravelPackage travelPackage : allPackages) {
            System.out.println(travelPackage.getPackageName());
        }
        System.out.println("*************************************************************************************************************");
        // Demonstrate loginUser
        try {
            UserProfile user = service.loginUser("gherbi", "1234");
            System.out.println("Logged in User: " + user.getUsername() + " with role: " + user.getRole());
            
            System.out.println("*************************************************************************************************************");
            // Demonstrate hasVisitedBefore
            if (!allPackages.isEmpty()) {
                long visitedDestinationsCount = allPackages.stream()
                .filter(pkg -> service.hasVisitedBefore(user, pkg))
                .count();
                System.out.println("Number of visited destinations: " + visitedDestinationsCount);
                System.out.println("Visited destinations:");
                allPackages.stream()
                .filter(pkg -> service.hasVisitedBefore(user, pkg))
                .forEach(pkg -> System.out.println(pkg.getPackageName()));
                // System.out.println("Has Visited Before: " + hasVisited);
                System.out.println("*************************************************************************************************************");
            }
            
            System.out.println("*************************************************************************************************************");
            System.out.println("*************************************************************************************************************");
            // Demonstrate searchByBudget
            List<TravelPackage> budgetPackages = service.searchByBudget(2000.0);
            System.out.println("Packages within Budget: " + budgetPackages);
            
            System.out.println("*************************************************************************************************************");
            System.out.println("*************************************************************************************************************");
            // Demonstrate searchByTravelDates
            List<TravelPackage> datePackages = service.searchByTravelDates(6);
            System.out.println("Packages with Travel Dates: " + datePackages);
            
            System.out.println("*************************************************************************************************************");
            System.out.println("*************************************************************************************************************");
            // Demonstrate searchByDestinationPrefs
            List<TravelPackage> destinationPackages = service.searchByDestinationPrefs("Paris");
            System.out.println("Packages with Destination Preferences: " + destinationPackages);
            
            System.out.println("*************************************************************************************************************");
            System.out.println("*************************************************************************************************************");
            // Demonstrate searchPackages
            List<TravelPackage> searchPackages = service.searchPackages(3000.0, 7, "Dubai");
            System.out.println("Search Packages: " + searchPackages);
            
            System.out.println("*************************************************************************************************************");
            System.out.println("*************************************************************************************************************");
            // Demonstrate getRecommendPackages
            List<TravelPackage> recommendedPackages = service.getRecommendPackages(user);
            System.out.println("Recommended Packages: " + recommendedPackages);
            
            System.out.println("*************************************************************************************************************");
            System.out.println("*************************************************************************************************************");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
