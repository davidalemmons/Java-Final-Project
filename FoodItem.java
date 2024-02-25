import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FoodItem {
    private int foodID;
    private String name;
    private double calories;
    private double carbs;
    private double fats;
    private double proteins;
    private double servingSize;
    private String consumptionDate;

    public double getCalories() {
        return calories;
    }

    public FoodItem(int foodID, String name, double calories, double carbs, double fats, double proteins,
            double servingSize, String consumptionDate) {
        this.foodID = foodID;
        this.name = name;
        this.calories = Math.max(calories, 0);
        this.carbs = Math.max(carbs, 0);
        this.fats = Math.max(fats, 0);
        this.proteins = Math.max(proteins, 0);
        this.servingSize = servingSize;
        this.consumptionDate = consumptionDate;
    }

    public String getNutritionalInfo() {
        return "Name: " + name + ", Calories: " + calories + " kcal, Carbs: " + carbs + "g, Fats: " + fats +
                "g, Proteins: " + proteins + "g, Serving Size: " + servingSize + "g, Date: " + consumptionDate;
    }

    public String getName() {
        return name;
    }

    public static List<FoodItem> createFoodItemListWithUserInput() {
        List<FoodItem> foodItemList = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter food items (press Enter without entering any data to stop):");
        while (true) {
            System.out.print("Enter food name: ");
            String name = scanner.nextLine();
            if (name.isEmpty()) {
                break;
            }
            System.out.print("Enter calories: ");
            double calories = scanner.nextDouble();
            System.out.print("Enter carbs: ");
            double carbs = scanner.nextDouble();
            System.out.print("Enter fats: ");
            double fats = scanner.nextDouble();
            System.out.print("Enter proteins: ");
            double proteins = scanner.nextDouble();
            System.out.print("Enter serving size: ");
            double servingSize = scanner.nextDouble();
            System.out.print("Enter consumption date (YYYY-MM-DD): ");
            String consumptionDate = scanner.next();
            scanner.nextLine();
            int foodID = foodItemList.size() + 1;
            foodItemList.add(new FoodItem(foodID, name, calories, carbs, fats, proteins, servingSize, consumptionDate));
        }
        return foodItemList;
    }
}
