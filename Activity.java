public class Activity {
    private int activityID;
    private String name;
    private double duration;
    private String intensity;
    private int caloriesBurned;
    private String date;

    public Activity(String name, double duration, String intensity, String date) {
        this.name = name;
        this.duration = duration;
        this.intensity = intensity;
        this.date = date;
        this.caloriesBurned = calculateCaloriesBurned();
    }

    private int calculateCaloriesBurned() {
        return (int) (duration * getIntensityFactor(intensity));
    }

    private double getIntensityFactor(String intensity) {
        switch (intensity.toLowerCase()) {
            case "high":
                return 12;
            case "medium":
                return 8;
            case "low":
                return 5;
            default:
                return 7;
        }
    }

    public String getName() {
        return name;
    }

    public double getDuration() {
        return duration;
    }

    public String getIntensity() {
        return intensity;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public String getDate() {
        return date;
    }

}
