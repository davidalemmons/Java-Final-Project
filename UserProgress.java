import java.util.ArrayList;
import java.util.List;

public class UserProgress {
    private List<Activity> activities;
    private List<FoodItem> foodItems;
    private Goal goal;

    public UserProgress() {
        activities = new ArrayList<>();
        foodItems = new ArrayList<>();
        goal = new Goal();
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public void addFoodItem(FoodItem foodItem) {
        foodItems.add(foodItem);
    }

    public String generateProgressReport() {
        double totalCaloriesBurned = 0;
        double totalCaloriesEaten = 0;
        double totalTimeExercising = 0;

        StringBuilder report = new StringBuilder();
        report.append("User Progress Report:\n\n");

        report.append("Goals:\n");
        report.append("Calorie Intake Goal: ").append(goal.getCalorieIntakeGoal()).append(" kcal\n");
        report.append("Calories Burned Goal per Week: ").append(goal.getCaloriesBurnedGoalPerWeek())
                .append(" kcal\n\n");

        report.append("Activities:\n");
        for (Activity activity : activities) {
            report.append(activity.getName()).append(" - Duration: ").append(activity.getDuration())
                    .append(" mins, Calories Burned: ").append(activity.getCaloriesBurned()).append(" kcal\n");
            totalCaloriesBurned += activity.getCaloriesBurned();
            totalTimeExercising += activity.getDuration();
        }

        report.append("\nFood Items:\n");
        for (FoodItem foodItem : foodItems) {
            report.append(foodItem.getName()).append(" - Calories: ").append(foodItem.getCalories()).append(" kcal\n");
            totalCaloriesEaten += foodItem.getCalories();
        }

        report.append("\nTotal Calories Burned: ").append(totalCaloriesBurned).append(" kcal\n");
        report.append("Total Calories Eaten: ").append(totalCaloriesEaten).append(" kcal\n");
        report.append("Total Time Spent Exercising: ").append(totalTimeExercising).append(" minutes\n");

        report.append("\nProgress Compared to Goals:\n");
        double remainingCaloriesBurnGoal = goal.getCaloriesBurnedGoalPerWeek() - totalCaloriesBurned;
        double remainingCaloriesIntakeGoal = goal.getCalorieIntakeGoal() - totalCaloriesEaten;
        report.append("Remaining Calories Burn Goal for the Week: ").append(remainingCaloriesBurnGoal)
                .append(" kcal\n");
        report.append("Remaining Calorie Intake Goal: ").append(remainingCaloriesIntakeGoal).append(" kcal\n");

        return report.toString();
    }
}
