import java.util.HashMap;

public class FruitfulFitness {
    public static void main(String[] args) {
        UserProgress userProgress = new UserProgress();
        
        Workout run = new Workout();
        // Initialize Workout properties
        // ...
        userProgress.addActivity(run);
        
        FoodItem apple = new FoodItem();
        // Initialize FoodItem properties
        // ...
        userProgress.addFoodItem(apple);
        
        // Display the progress report
        System.out.println(userProgress.generateProgressReport());
    }
}
