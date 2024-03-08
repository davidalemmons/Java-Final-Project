import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import org.mindrot.jbcrypt.BCrypt;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class FruitfulFitness extends Application {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/fruitful_fitness";
    private static final String USER = "root";
    private static final String PASS = "Look2the1!";

    private String currentIntakeGoal = "2000 cal";
    private String currentBurnedCaloriesGoal = "500 cal";
    private VBox titleAndProgressLayout;
    private int currentUserID = -1;
    private TableView<Activity> activityTableView = new TableView<>();

    private Connection connectToDB() {
        Connection conn = null;
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish the connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(); // Handle exceptions properly in real applications
        }
        return conn;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox loginLayout = new VBox(10);
        loginLayout.setAlignment(Pos.CENTER);
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Username");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Password");
        Button signinButton = new Button("Signin");
        Button registerButton = new Button("Register");
        loginLayout.getChildren().addAll(usernameInput, passwordInput, signinButton, registerButton);
        Scene loginScene = new Scene(loginLayout, 300, 250);
        primaryStage.setScene(loginScene);
        primaryStage.show();

        signinButton.setOnAction(e -> loginUser(usernameInput.getText(), passwordInput.getText(), primaryStage));
        registerButton.setOnAction(e -> registerUser(usernameInput.getText(), passwordInput.getText(), primaryStage));
    }

    private void registerUser(String username, String password, Stage primaryStage) {
        try (Connection conn = connectToDB()) {
            if (!checkUsernameExists(username, conn)) {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) { // Change
                                                                                                                      // here
                                                                                                                      // to
                                                                                                                      // return
                                                                                                                      // generated
                                                                                                                      // keys
                    pstmt.setString(1, username);
                    pstmt.setString(2, hashedPassword);
                    int affectedRows = pstmt.executeUpdate();

                    if (affectedRows > 0) {
                        // Get the generated user ID
                        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                currentUserID = generatedKeys.getInt(1); // Assuming the first column contains the ID
                            } else {
                                throw new SQLException("Creating user failed, no ID obtained.");
                            }
                        }

                        // Provide user feedback
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Registration Successful");
                        alert.setHeaderText(null);
                        alert.setContentText("Registration successful. Welcome!");
                        alert.showAndWait();

                        // Direct login (assuming you want to log in the user immediately after
                        // registration)
                        initializeMainScene(primaryStage);
                    }
                }
            } else {
                // Username already exists
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Registration Failed");
                alert.setHeaderText(null);
                alert.setContentText("Username already exists. Please try a different username.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception properly
            // Show error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("A database error occurred. Please try again.");
            alert.showAndWait();
        }
    }

    private boolean checkUsernameExists(String username, Connection conn) {
        String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; // Username exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions properly
        }
        return false; // Username does not exist
    }

    private void loginUser(String username, String password, Stage primaryStage) {
        // Step 1: Connect to Database
        Connection conn = connectToDB();
        if (conn == null) {
            // Handle database connection error
            return;
        }

        try {
            String sql = "SELECT id, password FROM users WHERE username = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String hashedPasswordFromDB = rs.getString("password");
                        if (BCrypt.checkpw(password, hashedPasswordFromDB)) {
                            // Store the user's ID in the field
                            currentUserID = rs.getInt("id");

                            // Transition to Main Scene
                            initializeMainScene(primaryStage);
                            return; // Exit the method after successful login
                        }
                    }
                }
            }
            // Step 5: Invalid Login Alert
            new Alert(Alert.AlertType.ERROR, "Invalid username or password").showAndWait();

        } catch (SQLException e) {
            // Handle SQL exception
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // Handle connection closing exception
                e.printStackTrace();
            }
        }
    }

    private void initializeMainScene(Stage primaryStage) {
        Button homeButton = createNavigationButton("Home");
        Button activityButton = createNavigationButton("Activity");
        Button foodItemButton = createNavigationButton("Food Items");
        Button goalButton = createNavigationButton("Goal");
        Button userProgressButton = createNavigationButton("User Progress");

        setupButtonActions(homeButton, activityButton, foodItemButton, goalButton, userProgressButton);

        HBox navigationBar = new HBox(homeButton, activityButton, foodItemButton, goalButton, userProgressButton);
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
            Label currentIntakeGoalLabel = new Label("Current Daily Intake Goal: " + currentIntakeGoal);
            Label currentBurnedCaloriesGoalLabel = new Label(
                    "Current Daily Burned Calories Goal: " + currentBurnedCaloriesGoal);
            TextField newIntakeGoalField = new TextField();
            newIntakeGoalField.setPromptText("New intake goal (cal)");
            TextField newBurnedCaloriesGoalField = new TextField();
            newBurnedCaloriesGoalField.setPromptText("New burned calories goal (cal)");
            Button updateIntakeGoalButton = new Button("Update Intake Goal");
            Button updateBurnedCaloriesGoalButton = new Button("Update Burned Calories Goal");
            updateIntakeGoalButton.setOnAction(e -> {
                String newIntakeGoalText = newIntakeGoalField.getText();

                // Assuming currentIntakeGoal stores just the numerical value without " cal"
                currentIntakeGoal = newIntakeGoalText + " cal";

                // Update the label to reflect the new goal
                currentIntakeGoalLabel.setText("Current Daily Intake Goal: " + currentIntakeGoal);

                // Now update the database with the new goal
                // Assuming you have already fetched the current user's ID into currentUserID
                // And assuming the updateUserGoals method exists and is properly implemented
                // For the burned calories goal, you might pass the current value or modify as
                // needed
                updateUserGoals(currentUserID, newIntakeGoalText, String.valueOf(currentBurnedCaloriesGoal));

                // Optionally, display a confirmation message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Goal Updated");
                alert.setHeaderText(null);
                alert.setContentText("Your intake goal has been updated to: " + currentIntakeGoal);
                alert.showAndWait();
            });
            updateBurnedCaloriesGoalButton.setOnAction(e -> {
                String newBurnedCaloriesGoalText = newBurnedCaloriesGoalField.getText();

                // Assuming currentBurnedCaloriesGoal stores just the numerical value without "
                // cal"
                currentBurnedCaloriesGoal = newBurnedCaloriesGoalText + " cal";

                // Update the label to reflect the new goal
                currentBurnedCaloriesGoalLabel
                        .setText("Current Daily Burned Calories Goal: " + currentBurnedCaloriesGoal);

                // Attempt to parse the new goal as an integer
                try {
                    int newBurnedCaloriesGoal = Integer.parseInt(newBurnedCaloriesGoalText);

                    // Now update the database with the new goal
                    updateUserGoals(currentUserID, String.valueOf(currentIntakeGoal), newBurnedCaloriesGoalText);

                    // Optionally, display a confirmation message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Goal Updated");
                    alert.setHeaderText(null);
                    alert.setContentText("Your burned calories goal has been updated to: " + currentBurnedCaloriesGoal);
                    alert.showAndWait();
                } catch (NumberFormatException ex) {
                    // Handle the case where the input is not a valid integer
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter a valid number for the burned calories goal.");
                    alert.showAndWait();
                }
            });

            goalLayout.getChildren().addAll(currentIntakeGoalLabel, newIntakeGoalField, updateIntakeGoalButton,
                    currentBurnedCaloriesGoalLabel, newBurnedCaloriesGoalField, updateBurnedCaloriesGoalButton);
            root.setCenter(goalLayout);
        });

        activityButton.setOnAction(event -> {
            VBox activityLayout = new VBox(10);
            activityLayout.setAlignment(Pos.CENTER);
            activityLayout.setPadding(new Insets(15));
            TextField nameInput = new TextField();
            nameInput.setPromptText("Activity Name");
            TextField durationInput = new TextField();
            durationInput.setPromptText("Duration (minutes)");
            TextField intensityInput = new TextField();
            intensityInput.setPromptText("Intensity (1-10)");
            DatePicker datePicker = new DatePicker();
            Button submitButton = new Button("Add Activity");
            submitButton.setOnAction(e -> {
                // Handle the form submission here
                String name = nameInput.getText();
                int duration = Integer.parseInt(durationInput.getText());
                int intensity = Integer.parseInt(intensityInput.getText());
                LocalDate date = datePicker.getValue();
                int user_id = currentUserID;

                int caloriesBurned = duration * intensity;

                // Connect to the database
                Connection conn = connectToDB();

                if (conn != null) {
                    try {
                        // Prepare the SQL statement
                        String sql = "INSERT INTO activities (name, duration, intensity, date, calories_burned, user_id) VALUES (?, ?, ?, ?, ?, ?)";
                        PreparedStatement statement = conn.prepareStatement(sql);
                        statement.setString(1, name);
                        statement.setInt(2, duration);
                        statement.setInt(3, intensity);
                        statement.setDate(4, java.sql.Date.valueOf(date));
                        statement.setInt(5, caloriesBurned);
                        statement.setInt(6, user_id);
                        // Execute the SQL statement
                        int rowsInserted = statement.executeUpdate();

                        if (rowsInserted > 0) {
                            // Data insertion successful
                            System.out.println("Activity data inserted successfully.");
                            // Optionally, show a message to the user indicating successful submission
                            updateActivityTableView();
                        } else {
                            // Data insertion failed
                            System.out.println("Failed to insert activity data.");
                            // Optionally, show an error message to the user
                        }
                    } catch (SQLException ex) {
                        // Handle any SQL exceptions
                        ex.printStackTrace();
                    } finally {
                        // Close the connection
                        try {
                            if (conn != null) {
                                conn.close();
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    // Connection to database failed
                    System.out.println("Failed to connect to the database.");
                    // Optionally, show an error message to the user
                }

            });

            TableView<Activity> activityTableView = new TableView<>();
            TableColumn<Activity, String> nameColumn = new TableColumn<>("Name");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<Activity, Integer> durationColumn = new TableColumn<>("Duration");
            durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

            TableColumn<Activity, Integer> intensityColumn = new TableColumn<>("Intensity");
            intensityColumn.setCellValueFactory(new PropertyValueFactory<>("intensity"));

            TableColumn<Activity, Integer> caloriesBurnedColumn = new TableColumn<>("Calories Burned");
            caloriesBurnedColumn.setCellValueFactory(new PropertyValueFactory<>("caloriesBurned"));

            TableColumn<Activity, LocalDate> dateColumn = new TableColumn<>("Date");
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

            // Populate table data from the database
            ObservableList<Activity> activities = FXCollections.observableArrayList();
            activities.addAll(fetchActivitiesForCurrentUserAndDate());
            // Fetch activities from the database and add them to the list
            // Example: activities.addAll(fetchActivitiesFromDatabase());

            // Assign data to table
            activityTableView.setItems(activities);
            activityTableView.getColumns().addAll(nameColumn, durationColumn, intensityColumn, caloriesBurnedColumn,
                    dateColumn);

            activityLayout.getChildren().addAll(nameInput, durationInput, intensityInput, datePicker, submitButton,
                    activityTableView);
            root.setCenter(activityLayout);
        });

        foodItemButton.setOnAction(event -> {
            VBox foodItemLayout = new VBox(10);
            foodItemLayout.setAlignment(Pos.CENTER);
            foodItemLayout.setPadding(new Insets(15));

            TextField foodNameInput = new TextField();
            foodNameInput.setPromptText("Food Item Name");
            TextField caloriesInput = new TextField();
            caloriesInput.setPromptText("Calories");
            TextField carbInput = new TextField();
            carbInput.setPromptText("Carbs");
            TextField fatsInput = new TextField();
            fatsInput.setPromptText("Fats");
            TextField protienInput = new TextField();
            protienInput.setPromptText("Protien");
            TextField servingSizeInput = new TextField();
            servingSizeInput.setPromptText("Serving Size");
            DatePicker datePicker = new DatePicker();
            Button submitButton = new Button("Add Food Item");
            submitButton.setOnAction(e -> {
                // Handle the form submission here
                String name = foodNameInput.getText();
                int calories = Integer.parseInt(caloriesInput.getText());
                int carbs = Integer.parseInt(carbInput.getText());
                int fats = Integer.parseInt(fatsInput.getText());
                int protiens = Integer.parseInt(protienInput.getText());
                int servingSize = Integer.parseInt(servingSizeInput.getText());
                LocalDate date = datePicker.getValue();
                int user_id = currentUserID;

                // Connect to the database
                Connection conn = connectToDB();

                if (conn != null) {
                    try {
                        // Prepare the SQL statement
                        String sql = "INSERT INTO food_items (name, calories, carbs, fats, protiens, serving_size, date, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement statement = conn.prepareStatement(sql);
                        statement.setString(1, name);
                        statement.setInt(2, calories);
                        statement.setInt(3, carbs);
                        statement.setInt(4, fats);
                        statement.setInt(5, protiens);
                        statement.setInt(6, servingSize);
                        statement.setDate(7, java.sql.Date.valueOf(date));
                        statement.setInt(8, user_id);

                        // Execute the SQL statement
                        int rowsInserted = statement.executeUpdate();

                        if (rowsInserted > 0) {
                            // Data insertion successful
                            new Alert(Alert.AlertType.CONFIRMATION, "Inserted Food Item Successfully.").showAndWait();
                            // Optionally, show a message to the user indicating successful submission
                        } else {
                            // Data insertion failed
                            new Alert(Alert.AlertType.ERROR, "Failed to insert food item.").showAndWait();
                            // Optionally, show an error message to the user
                        }
                    } catch (SQLException ex) {
                        // Handle any SQL exceptions
                        ex.printStackTrace();
                    } finally {
                        // Close the connection
                        try {
                            conn.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    // Connection to database failed
                    System.out.println("Failed to connect to the database.");
                    // Optionally, show an error message to the user
                }
            });

            TableView<FoodItem> food_itemTableView = new TableView<>();
            TableColumn<FoodItem, String> nameColumn = new TableColumn<>("Name");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<FoodItem, Integer> caloriesColumn = new TableColumn<>("Calories");
            caloriesColumn.setCellValueFactory(new PropertyValueFactory<>("calories"));

            TableColumn<FoodItem, Integer> carbsColumn = new TableColumn<>("Carbs");
            carbsColumn.setCellValueFactory(new PropertyValueFactory<>("carbs"));

            TableColumn<FoodItem, Integer> fatsColumn = new TableColumn<>("Fats");
            fatsColumn.setCellValueFactory(new PropertyValueFactory<>("fats"));

            TableColumn<FoodItem, Integer> protiensColumn = new TableColumn<>("Protien");
            protiensColumn.setCellValueFactory(new PropertyValueFactory<>("protiens"));

            TableColumn<FoodItem, Integer> serving_sizeColumn = new TableColumn<>("Serving Size");
            serving_sizeColumn.setCellValueFactory(new PropertyValueFactory<>("servingSize"));

            TableColumn<FoodItem, LocalDate> dateColumn = new TableColumn<>("Date");
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

            // Populate table data from the database
            ObservableList<FoodItem> food_items = fetchFoodItemsForCurrentUserAndDate();
            food_itemTableView.setItems(food_items); // Use consistent variable name

            // Assign data to table
            food_itemTableView.getColumns().addAll(nameColumn, caloriesColumn, carbsColumn, fatsColumn,
                    protiensColumn, serving_sizeColumn, dateColumn);

            // Assuming foodItemLayout is a previously defined layout container (e.g., VBox)
            foodItemLayout.getChildren().addAll(foodNameInput, caloriesInput, carbInput, fatsInput, protienInput,
                    servingSizeInput, datePicker, submitButton, food_itemTableView);
            root.setCenter(foodItemLayout);

        });

        userProgressButton.setOnAction(event -> {
            VBox userProgressLayout = new VBox(10);
            userProgressLayout.setAlignment(Pos.TOP_CENTER);
            userProgressLayout.setPadding(new Insets(10, 0, 0, 0));

            Label progressLabel = new Label("User Progress");

            // Assuming currentIntakeGoal and currentBurnedCaloriesGoal are int or can be
            // parsed to one
            int totalCaloriesConsumed = fetchTotalCaloriesForCurrentUserAndDate();
            int totalCaloriesBurned = fetchTotalCaloriesBurnedForCurrentUserAndDate();
            int intakeGoalValue = Integer.parseInt(currentIntakeGoal.replaceAll("[^0-9]", "")); // Remove non-numeric
                                                                                                // characters
            int burnedGoalValue = Integer.parseInt(currentBurnedCaloriesGoal.replaceAll("[^0-9]", ""));

            // Setup for intake goal bar chart
            CategoryAxis xAxisIntake = new CategoryAxis();
            xAxisIntake.setLabel("Goal Type");
            NumberAxis yAxisIntake = new NumberAxis();
            yAxisIntake.setLabel("Calories");

            BarChart<String, Number> intakeBarChart = new BarChart<>(xAxisIntake, yAxisIntake);
            intakeBarChart.setTitle("Calorie Intake");

            XYChart.Series<String, Number> intakeGoalSeries = new XYChart.Series<>();
            intakeGoalSeries.setName("Goal");
            intakeGoalSeries.getData().add(new XYChart.Data<>("Intake", intakeGoalValue));

            XYChart.Series<String, Number> totalIntakeSeries = new XYChart.Series<>();
            totalIntakeSeries.setName("Consumed");
            totalIntakeSeries.getData().add(new XYChart.Data<>("Intake", totalCaloriesConsumed));

            intakeBarChart.getData().addAll(intakeGoalSeries, totalIntakeSeries);
            intakeBarChart.setLegendVisible(true);

            // Setup for burned goal bar chart
            CategoryAxis xAxisBurned = new CategoryAxis();
            xAxisBurned.setLabel("Goal Type");
            NumberAxis yAxisBurned = new NumberAxis();
            yAxisBurned.setLabel("Calories");

            BarChart<String, Number> burnedBarChart = new BarChart<>(xAxisBurned, yAxisBurned);
            burnedBarChart.setTitle("Calorie Burned");

            XYChart.Series<String, Number> burnedGoalSeries = new XYChart.Series<>();
            burnedGoalSeries.setName("Goal");
            burnedGoalSeries.getData().add(new XYChart.Data<>("Burned", burnedGoalValue));

            XYChart.Series<String, Number> totalBurnedSeries = new XYChart.Series<>();
            totalBurnedSeries.setName("Burned");
            totalBurnedSeries.getData().add(new XYChart.Data<>("Burned", totalCaloriesBurned));

            burnedBarChart.getData().addAll(burnedGoalSeries, totalBurnedSeries);
            burnedBarChart.setLegendVisible(true);

            HBox chartsLayout = new HBox(10);
            chartsLayout.getChildren().addAll(intakeBarChart, burnedBarChart);

            // Adding elements to the layout
            userProgressLayout.getChildren().addAll(progressLabel, chartsLayout);

            // Set this layout to be displayed in your scene or a specific pane
            root.setCenter(userProgressLayout);
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

    public void updateUserGoals(int userId, String newIntakeGoal, String newBurnedCaloriesGoal) {
        String sql = "INSERT INTO user_goals (user_id, intake_goal, burned_calories_goal, date_updated) VALUES (?, ?, ?, CURDATE()) ON DUPLICATE KEY UPDATE intake_goal = VALUES(intake_goal), burned_calories_goal = VALUES(burned_calories_goal), date_updated = CURDATE();";

        try (Connection conn = connectToDB();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, newIntakeGoal);
            pstmt.setString(3, newBurnedCaloriesGoal);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }
    }

    public ObservableList<Activity> fetchActivitiesForCurrentUserAndDate() {
        ObservableList<Activity> activities = FXCollections.observableArrayList();
        LocalDate currentDate = LocalDate.now(); // Get the current date

        String sql = "SELECT * FROM activities WHERE user_id = ? AND date = ?";

        try (Connection conn = connectToDB();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentUserID);
            pstmt.setDate(2, java.sql.Date.valueOf(currentDate));

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Assuming you have an Activity class that matches your table schema
                String name = rs.getString("name");
                int duration = rs.getInt("duration");
                int intensity = rs.getInt("intensity");
                int caloriesBurned = rs.getInt("calories_burned");
                LocalDate date = rs.getDate("date").toLocalDate();

                activities.add(new Activity(name, duration, intensity, caloriesBurned, date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions properly
        }

        return activities;
    }

    private void updateActivityTableView() {
        // Fetch activities from the database
        ObservableList<Activity> activities = fetchActivitiesForCurrentUserAndDate();

        // Set the new data on the table view
        activityTableView.setItems(activities);
    }

    public ObservableList<FoodItem> fetchFoodItemsForCurrentUserAndDate() {
        ObservableList<FoodItem> foodItems = FXCollections.observableArrayList();
        LocalDate currentDate = LocalDate.now(); // Get the current date

        String sql = "SELECT * FROM food_items WHERE user_id = ? AND date = ?";

        try (Connection conn = connectToDB();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentUserID);
            pstmt.setDate(2, java.sql.Date.valueOf(currentDate));

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                int calories = rs.getInt("calories");
                int carbs = rs.getInt("carbs");
                int fats = rs.getInt("fats");
                int protiens = rs.getInt("protiens");
                int servingSize = rs.getInt("serving_size");
                LocalDate date = rs.getDate("date").toLocalDate();

                foodItems.add(new FoodItem(name, calories, carbs, fats, protiens, servingSize, date));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions properly
        }

        return foodItems;
    }

    private int fetchTotalCaloriesForCurrentUserAndDate() {
        int totalCalories = 0;
        // Get the current date in the format that matches your database
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.toString(); // Assuming the date is stored in 'YYYY-MM-DD' format

        // SQL query to sum calories for the current user on the current date
        String sql = "SELECT SUM(calories) AS total FROM food_items WHERE user_id = ? AND DATE(date) = ?";

        try (Connection conn = connectToDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set parameters
            pstmt.setInt(1, currentUserID);
            pstmt.setString(2, formattedDate);

            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalCalories = rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching total calories for current user and date: " + e.getMessage());
            // Handle exception appropriately - this could be logging or showing an error
            // message to the user
        }

        return totalCalories;
    }

    private int fetchTotalCaloriesBurnedForCurrentUserAndDate() {
        // Initialize the total calories burned to 0
        int totalCaloriesBurned = 0;

        // Get the current date in the format that matches your database
        LocalDate currentDate = LocalDate.now();

        // SQL query to sum up all calories burned by the current user on the current
        // date
        String sql = "SELECT SUM(calories_burned) AS total FROM activities WHERE user_id = ? AND date = ?";

        // Try-with-resources to automatically manage resource (connection and prepared
        // statement) closing
        try (Connection conn = connectToDB(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the parameters for the prepared statement
            pstmt.setInt(1, currentUserID);
            pstmt.setDate(2, java.sql.Date.valueOf(currentDate));

            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                // If there's a result, set the total calories burned to the value of "total"
                if (rs.next()) {
                    totalCaloriesBurned = rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            // Print any SQL errors to the console
            System.err.println("Error fetching total calories burned for the current user and date: " + e.getMessage());
        }

        // Return the total calories burned
        return totalCaloriesBurned;
    }

}
