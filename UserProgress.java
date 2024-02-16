import java.util.ArrayList;
import java.util.List;

public class UserProgress {
    private String userID;
    private List<Activity> activities = new ArrayList<>();
    private List<FoodItem> foodItems = new ArrayList<>();
    
    public void addActivity(Activity activity) {
        activities.add(activity);
    }
    
    public void addFoodItem(FoodItem foodItem) {
        foodItems.add(foodItem);
    }
    
    public String generateProgressReport() {
        StringBuilder report = new StringBuilder();
        report.append("User Progress Report for: ").append(userID).append("\nActivities:\n");
        for (Activity activity : activities) {
            report.append(activity.getActivityDetails()).append("\n");
        }
        report.append("Food Items:\n");
        for (FoodItem foodItem : foodItems) {
            report.append(foodItem.getNutritionalInfo()).append("\n");
        }
        return report.toString();
    }
}
