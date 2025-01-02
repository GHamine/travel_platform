package services.booking_management_service;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Activity {

    private int activityId;
    private String activityName;
    private Timestamp activityDate;
    private String location;
    private int duration;
    private BigDecimal price;

    // Constructor
    public Activity(int activityId, String activityName, Timestamp activityDate, String location, 
                    int duration, BigDecimal price) {
        this.activityId = activityId;
        this.activityName = activityName;
        this.activityDate = activityDate;
        this.location = location;
        this.duration = duration;
        this.price = price;
    }

    // Getters and Setters
    public int getActivityId() { return activityId; }
    public void setActivityId(int activityId) { this.activityId = activityId; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public Timestamp getActivityDate() { return activityDate; }
    public void setActivityDate(Timestamp activityDate) { this.activityDate = activityDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }



    
    @Override
    public String toString() {
        return activityName ;
    }
}
