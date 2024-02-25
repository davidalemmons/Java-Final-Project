import java.util.Scanner;

public class Workout extends Activity {
    private String workoutName;
    private int intensity;

    public Workout(int activityID, double duration, String activityDate, String activityType, String string, int i) {
        super(activityID, duration, activityDate, activityType);
        this.workoutName = "";
        this.intensity = 0;
        inputWorkoutDetails();
    }

    private void inputWorkoutDetails() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter workout name: ");
        this.workoutName = scanner.nextLine();
        System.out.print("Enter intensity (scale from 1 to 5): ");
        this.intensity = scanner.nextInt();
    }

    @Override
    protected double calculateCaloriesBurned() {
        double caloriesBurnedPerMinute = (intensity * 3.5) + 6.8;
        return duration * caloriesBurnedPerMinute;
    }

    @Override
    public String getActivityDetails() {
        return super.getActivityDetails() + ", Workout Name: " + workoutName + ", Intensity: " + intensity;
    }

    @Override
    public double getDuration() {
        return duration;
    }
}
