import java.util.Scanner;

// Class representing a user's health goals including calorie intake and calories burned
public class Goal {
    // Attributes of the Goal
    private int calorieIntakeGoal; // Daily calorie intake goal
    private int caloriesBurnedGoalPerWeek; // Weekly calories burned goal

    // Default constructor initializing goals to zero and prompting user to set them
    public Goal() {
        this.calorieIntakeGoal = 0; // Initialize calorie intake goal to zero
        this.caloriesBurnedGoalPerWeek = 0; // Initialize calories burned goal to zero
        setGoals(); // Calls the setGoals method to prompt user for actual values
    }

    // Private method to set goals based on user input
    private void setGoals() {
        Scanner scanner = new Scanner(System.in); // Create a new Scanner object for input
        System.out.print("Enter your calorie intake goal: "); // Prompt for calorie intake goal
        this.calorieIntakeGoal = scanner.nextInt(); // Read and set calorie intake goal from user input
        System.out.print("Enter your calories burned goal per week: "); // Prompt for calories burned goal
        this.caloriesBurnedGoalPerWeek = scanner.nextInt(); // Read and set calories burned goal from user input
    }

    // Getter method for the calorie intake goal
    public int getCalorieIntakeGoal() {
        return calorieIntakeGoal;
    }

    // Getter method for the calories burned goal per week
    public int getCaloriesBurnedGoalPerWeek() {
        return caloriesBurnedGoalPerWeek;
    }

    // Setter method for the calorie intake goal
    public void setCalorieIntakeGoal(int calorieIntakeGoal) {
        this.calorieIntakeGoal = calorieIntakeGoal; // Set the calorie intake goal to the specified value
    }

    // Setter method for the calories burned goal per week
    public void setCaloriesBurnedGoalPerWeek(int caloriesBurnedGoalPerWeek) {
        this.caloriesBurnedGoalPerWeek = caloriesBurnedGoalPerWeek; // Set the calories burned goal to the specified value
    }
}
