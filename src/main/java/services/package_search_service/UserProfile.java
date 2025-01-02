package services.package_search_service;


public class UserProfile {

    private int userId;
    private String preferredDestinations;
    private Double budget;
    private String username;
    private String password;
    private String role;
    @Override
    public String toString() {
        return "UserProfile [username=" + username + ", role=" + role + "]";
    }

    private int travelDuration;
    
    public UserProfile(int userId, String username, String password,
            String role, Double budget, String preferredDestinations, int travelDuration) {
        this.userId = userId;
        this.preferredDestinations = preferredDestinations;
        this.budget = budget;
        this.username = username;
        this.password = password;
        this.role = role;
        this.travelDuration = travelDuration;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPreferredDestinations() {
        return preferredDestinations;
    }

    public void setPreferredDestinations(String preferredDestinations) {
        this.preferredDestinations = preferredDestinations;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getTravelDuration() {
        return travelDuration;
    }

    public void setTravelDuration(int travelDuration) {
        this.travelDuration = travelDuration;
    }





}
