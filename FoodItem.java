import java.time.LocalDate;
import java.util.Map;

public class FoodItem {
    private String foodID;
    private String name;
    private double calories;
    private Map<String, Double> macronutrients;
    private String servingSize;
    private LocalDate consumptionDate;
    
    public String getNutritionalInfo() {
        return "Food Item: " + name + ", Calories: " + calories + ", Serving Size: " + servingSize;
    }
}
