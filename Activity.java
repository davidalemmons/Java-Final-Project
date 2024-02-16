import java.time.LocalDate;

public abstract class Activity {
    protected String activityID;
    protected int duration; // in minutes
    protected double caloriesBurned;
    protected LocalDate activityDate;
    protected String activityType;
    
    public abstract double calculateCaloriesBurned();
    
    public String getActivityDetails() {
        return "Activity: " + activityType + " on " + activityDate + " for " + duration + " minutes.";
    }
}
