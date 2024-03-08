import java.time.LocalDate;

public class FoodItem {
    private int id;
    private String name;
    private int calories;
    private int carbs;
    private int fats;
    private int protiens;
    private int servingSize;
    private LocalDate date;
    private int userId;

    // Constructor
    public FoodItem(String name, int calories, int carbs, int fats, int protiens, int servingSize, LocalDate date) {
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.fats = fats;
        this.protiens = protiens;
        this.servingSize = servingSize;
        this.date = date;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getCarbs() {
        return carbs;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public int getFats() {
        return fats;
    }

    public void setFats(int fats) {
        this.fats = fats;
    }

    public int getProtiens() {
        return protiens;
    }

    public void setProtiens(int protiens) {
        this.protiens = protiens;
    }

    public int getServingSize() {
        return servingSize;
    }

    public void setServingSize(int servingSize) {
        this.servingSize = servingSize;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
