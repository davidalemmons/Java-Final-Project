import java.time.LocalDate;

// Class representing a food item with various nutritional details
public class FoodItem {
    // Attributes of the FoodItem
    private int id; // Unique identifier for the food item
    private String name; // Name of the food item
    private int calories; // Number of calories in the food item
    private int carbs; // Amount of carbohydrates (in grams) in the food item
    private int fats; // Amount of fats (in grams) in the food item
    private int protiens; // Amount of proteins (in grams) in the food item
    private int servingSize; // Serving size of the food item (could be in grams or milliliters, etc.)
    private LocalDate date; // Date when the food item was consumed or added
    private int userId; // Identifier for the user who added or consumed the food item

    // Constructor to initialize a FoodItem object with the given parameters
    public FoodItem(String name, int calories, int carbs, int fats, int protiens, int servingSize, LocalDate date) {
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.fats = fats;
        this.protiens = protiens;
        this.servingSize = servingSize;
        this.date = date;
    }

    // Getter method for the food item ID
    public int getId() {
        return id;
    }

    // Setter method for the food item ID
    public void setId(int id) {
        this.id = id;
    }

    // Getter method for the food item name
    public String getName() {
        return name;
    }

    // Setter method for the food item name
    public void setName(String name) {
        this.name = name;
    }

    // Getter method for the calories of the food item
    public int getCalories() {
        return calories;
    }

    // Setter method for the calories of the food item
    public void setCalories(int calories) {
        this.calories = calories;
    }

    // Getter method for the carbohydrates content of the food item
    public int getCarbs() {
        return carbs;
    }

    // Setter method for the carbohydrates content of the food item
    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    // Getter method for the fats content of the food item
    public int getFats() {
        return fats;
    }

    // Setter method for the fats content of the food item
    public void setFats(int fats) {
        this.fats = fats;
    }

    // Getter method for the proteins content of the food item
    public int getProtiens() {
        return protiens;
    }

    // Setter method for the proteins content of the food item
    public void setProtiens(int protiens) {
        this.protiens = protiens;
    }

    // Getter method for the serving size of the food item
    public int getServingSize() {
        return servingSize;
    }

    // Setter method for the serving size of the food item
    public void setServingSize(int servingSize) {
        this.servingSize = servingSize;
    }

    // Getter method for the date associated with the food item
    public LocalDate getDate() {
        return date;
    }

    // Setter method for the date associated with the food item
    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Getter method for the user ID associated with the food item
    public int getUserId() {
        return userId;
    }

    // Setter method for the user ID associated with the food item
    public void setUserId(int userId) {
        this.userId = userId;
    }
}