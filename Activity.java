import java.time.LocalDate;

// Class to represent an activity with attributes like name, duration, intensity, etc.
public class Activity {
    // Attributes of the Activity
    private int id; // Unique identifier for the activity
    private String name; // Name of the activity
    private int duration; // Duration of the activity in minutes
    private int intensity; // Intensity level of the activity
    private int caloriesBurned; // Amount of calories burned during the activity
    private LocalDate date; // Date when the activity was performed
    private int userId; // Identifier for the user who performed the activity

    // Constructor to initialize an Activity object with given parameters
    public Activity(String name, int duration, int intensity, int caloriesBurned, LocalDate date) {
        this.name = name;
        this.duration = duration;
        this.intensity = intensity;
        this.caloriesBurned = caloriesBurned;
        this.date = date;
    }

    // Getter method for activity ID
    public int getId() {
        return id;
    }

    // Setter method for activity ID
    public void setId(int id) {
        this.id = id;
    }

    // Getter method for activity name
    public String getName() {
        return name;
    }

    // Setter method for activity name
    public void setName(String name) {
        this.name = name;
    }

    // Getter method for activity duration
    public int getDuration() {
        return duration;
    }

    // Setter method for activity duration
    public void setDuration(int duration) {
        this.duration = duration;
    }

    // Getter method for activity intensity
    public int getIntensity() {
        return intensity;
    }

    // Setter method for activity intensity
    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    // Getter method for calories burned
    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    // Setter method for calories burned
    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    // Getter method for the date of activity
    public LocalDate getDate() {
        return date;
    }

    // Setter method for the date of activity
    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Getter method for user ID
    public int getUserId() {
        return userId;
    }

    // Setter method for user ID
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
