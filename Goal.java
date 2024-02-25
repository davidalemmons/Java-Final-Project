import java.util.Scanner;

public class Goal {
    private int calorieIntakeGoal;
    private int caloriesBurnedGoalPerWeek;

    public Goal() {
        this.calorieIntakeGoal = 0;
        this.caloriesBurnedGoalPerWeek = 0;
        setGoals();
    }

    private void setGoals() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your calorie intake goal: ");
        this.calorieIntakeGoal = scanner.nextInt();
        System.out.print("Enter your calories burned goal per week: ");
        this.caloriesBurnedGoalPerWeek = scanner.nextInt();
    }

    public int getCalorieIntakeGoal() {
        return calorieIntakeGoal;
    }

    public int getCaloriesBurnedGoalPerWeek() {
        return caloriesBurnedGoalPerWeek;
    }

    public void setCalorieIntakeGoal(int calorieIntakeGoal) {
        this.calorieIntakeGoal = calorieIntakeGoal;
    }

    public void setCaloriesBurnedGoalPerWeek(int caloriesBurnedGoalPerWeek) {
        this.caloriesBurnedGoalPerWeek = caloriesBurnedGoalPerWeek;
    }
}
