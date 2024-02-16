public class Workout extends Activity {
    private String workoutName;
    private String intensity;
    
    @Override
    public double calculateCaloriesBurned() {
        // Logic to calculate calories burned based on workout details.
        return 0.0;
    }
    
    public String getWorkoutDetails() {
        return super.getActivityDetails() + " Name: " + workoutName + ", Intensity: " + intensity;
    }
}
