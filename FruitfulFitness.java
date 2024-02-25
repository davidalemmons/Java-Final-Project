import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class FruitfulFitness extends Application {
    private String currentIntakeGoal = "2000 kcal";
    private String currentBurnedCaloriesGoal = "500 kcal";
    private VBox titleAndProgressLayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Button homeButton = createNavigationButton("Home");
        Button activityButton = createNavigationButton("Activity");
        Button foodItemButton = createNavigationButton("Food Items");
        Button goalButton = createNavigationButton("Goal");

        setupButtonActions(homeButton, activityButton, foodItemButton, goalButton);

        HBox navigationBar = new HBox(homeButton, activityButton, foodItemButton, goalButton);
        navigationBar.setSpacing(10);
        navigationBar.setAlignment(Pos.CENTER);

        titleAndProgressLayout = new VBox();
        titleAndProgressLayout.setAlignment(Pos.CENTER);
        titleAndProgressLayout.setSpacing(20);
        Label titleLabel = new Label("Fruitful Fitness");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setTextFill(Color.GREEN);
        Label userProgressLabel = new Label("Welcome to Fruitful Fitness!");
        userProgressLabel.setFont(Font.font("Arial", 16));
        userProgressLabel.setTextFill(Color.GRAY);
        titleAndProgressLayout.getChildren().addAll(titleLabel, userProgressLabel);

        BorderPane root = new BorderPane();
        root.setTop(navigationBar);
        root.setCenter(titleAndProgressLayout);
        root.setPadding(new Insets(20));
        root.setStyle(
                "-fx-background-image: url('" + getClass().getResource("assets/BackgroundFFapp.png").toExternalForm()
                        + "'); -fx-background-size: cover;");

        goalButton.setOnAction(event -> {
            VBox goalLayout = new VBox(10);
            goalLayout.setAlignment(Pos.CENTER);
            goalLayout.setPadding(new Insets(15));
            Label currentIntakeGoalLabel = new Label("Current Intake Goal: " + currentIntakeGoal);
            Label currentBurnedCaloriesGoalLabel = new Label(
                    "Current Burned Calories Goal: " + currentBurnedCaloriesGoal);
            TextField newIntakeGoalField = new TextField();
            newIntakeGoalField.setPromptText("New intake goal (kcal)");
            TextField newBurnedCaloriesGoalField = new TextField();
            newBurnedCaloriesGoalField.setPromptText("New burned calories goal (kcal)");
            Button updateIntakeGoalButton = new Button("Update Intake Goal");
            Button updateBurnedCaloriesGoalButton = new Button("Update Burned Calories Goal");
            updateIntakeGoalButton.setOnAction(e -> {
                currentIntakeGoal = newIntakeGoalField.getText() + " kcal";
                currentIntakeGoalLabel.setText("Current Intake Goal: " + currentIntakeGoal);
            });
            updateBurnedCaloriesGoalButton.setOnAction(e -> {
                currentBurnedCaloriesGoal = newBurnedCaloriesGoalField.getText() + " kcal";
                currentBurnedCaloriesGoalLabel.setText("Current Burned Calories Goal: " + currentBurnedCaloriesGoal);
            });
            goalLayout.getChildren().addAll(currentIntakeGoalLabel, newIntakeGoalField, updateIntakeGoalButton,
                    currentBurnedCaloriesGoalLabel, newBurnedCaloriesGoalField, updateBurnedCaloriesGoalButton);
            root.setCenter(goalLayout);
        });

        homeButton.setOnAction(e -> root.setCenter(titleAndProgressLayout));

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Fruitful Fitness");
        primaryStage.show();
    }

    private void setupButtonActions(Button... buttons) {
        for (Button btn : buttons) {
            btn.setOnAction(event -> {
                for (Button otherBtn : buttons) {
                    otherBtn.getStyleClass().remove("button-active");
                }
                btn.getStyleClass().add("button-active");
                btn.applyCss();
                btn.layout();
            });
        }
    }

    private Button createNavigationButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(100);
        button.getStyleClass().add("button");
        return button;
    }
}
