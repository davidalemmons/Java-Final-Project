import java.util.ArrayList;
import java.util.List;

// Class to track and report a user's progress including activities, food intake, and goals
public class UserProgress {
    private List<Activity> activities; // List to store the user's physical activities
    private List<FoodItem> foodItems; // List to store the user's food intake
    private Goal goal; // User's health and fitness goals

    // Constructor initializes lists for activities and food items and creates a new Goal
    public UserProgress() {
        activities = new ArrayList<>(); // Initialize the list of activities
        foodItems = new ArrayList<>(); // Initialize the list of food items
        goal = new Goal(); // Create a new goal instance
    }

    // Adds a new activity to the list of user's activities
    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    // Adds a new food item to the list of user's food intake
    public void addFoodItem(FoodItem foodItem) {
        foodItems.add(foodItem);
    }

    // Generates a detailed progress report for the user
    public String generateProgressReport() {
        double totalCaloriesBurned = 0; // Total calories burned through activities
        double totalCaloriesEaten = 0; // Total calories consumed through food
        double totalTimeExercising = 0; // Total time spent on physical activities

        // StringBuilder to construct the progress report string
        StringBuilder report = new StringBuilder();
        report.append("User Progress Report:\n\n");

        // Append goals to the report
        report.append("Goals:\n");
        report.append("Calorie Intake Goal: ").append(goal.getCalorieIntakeGoal()).append(" kcal\n");
        report.append("Calories Burned Goal per Week: ").append(goal.getCaloriesBurnedGoalPerWeek()).append(" kcal\n\n");

        // Append activities and calculate totals
        report.append("Activities:\n");
        for (Activity activity : activities) {
            report.append(activity.getName()).append(" - Duration: ").append(activity.getDuration())
                    .append(" mins, Calories Burned: ").append(activity.getCaloriesBurned()).append(" kcal\n");
            totalCaloriesBurned += activity.getCaloriesBurned();
            totalTimeExercising += activity.getDuration();
        }

        // Append food items and calculate total calories eaten
        report.append("\nFood Items:\n");
        for (FoodItem foodItem : foodItems) {
            report.append(foodItem.getName()).append(" - Calories: ").append(foodItem.getCalories()).append(" kcal\n");
            totalCaloriesEaten += foodItem.getCalories();
        }

        // Append total calculations to the report
        report.append("\nTotal Calories Burned: ").append(totalCaloriesBurned).append(" kcal\n");
        report.append("Total Calories Eaten: ").append(totalCaloriesEaten).append(" kcal\n");
        report.append("Total Time Spent Exercising: ").append(totalTimeExercising).append(" minutes\n");

        // Compare progress to goals and append results to the report
        report.append("\nProgress Compared to Goals:\n");
        double remainingCaloriesBurnGoal = goal.getCaloriesBurnedGoalPerWeek() - totalCaloriesBurned;
        double remainingCaloriesIntakeGoal = goal.getCalorieIntakeGoal() - totalCaloriesEaten;
        report.append("Remaining Calories Burn Goal for the Week: ").append(remainingCaloriesBurnGoal).append(" kcal\n");
        report.append("Remaining Calorie Intake Goal: ").append(remainingCaloriesIntakeGoal).append(" kcal\n");

        // Return the constructed report
        return report.toString();
    }
}
