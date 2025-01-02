package services.package_search_service;
public class TravelPackage {
    private int packageId;
    private String packageName;
    private String destination;
    private String description;
    private double totalPrice;


    private int travelDates;


    public TravelPackage(int packageId, String packageName, String destination, double totalPrice,
            int travelDates, String description) {
        this.packageId = packageId;
        this.packageName = packageName;
        this.destination = destination;
        this.description = description;
        this.totalPrice = totalPrice;
        this.travelDates = travelDates;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setTravelDates(int travelDates) {
        this.travelDates = travelDates;
    }

    public String getDestination() {
        return destination;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getTravelDates() {
        return travelDates;
    }

    @Override
    public String toString() {
        return "\n\n  packageName=" + packageName + ", destination=" + destination + ", description="
                + description + ", totalPrice=" + totalPrice + ", travelDates=" + travelDates + "]";
    }


}
