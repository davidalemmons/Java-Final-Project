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
    // Database configuration constants
    private static final String DB_URL = "jdbc:mysql://localhost:3306/fruitful_fitness";
    private static final String USER = "root";
    private static final String PASS = "Look2the1!";

    // User-specific goals and layout setup
    private String currentIntakeGoal = "2000 cal";
    private String currentBurnedCaloriesGoal = "500 cal";
    private VBox titleAndProgressLayout;
    private int currentUserID = -1; // Default -1, updated upon successful login
    private TableView<Activity> activityTableView = new TableView<>();

    // Method to establish connection with the MySQL database
    private Connection connectToDB() {
        Connection conn = null;
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish connection to the database
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            // Handle errors for JDBC
            e.printStackTrace();
        }
        return conn;
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Login interface layout setup
        VBox loginLayout = new VBox(10);
        loginLayout.setAlignment(Pos.CENTER);// Center the login form
        TextField usernameInput = new TextField();// User input for username
        usernameInput.setPromptText("Username");// Placeholder text
        PasswordField passwordInput = new PasswordField();// User input for password (hidden)
        passwordInput.setPromptText("Password");// Placeholder text
        Button signinButton = new Button("Signin");// Button for signing in
        Button registerButton = new Button("Register");// Button for new user registration
        // Add all elements to the layout
        loginLayout.getChildren().addAll(usernameInput, passwordInput, signinButton, registerButton);
        // Create a new scene with the login layout
        Scene loginScene = new Scene(loginLayout, 300, 250);
        // Set the primary stage with the login scene
        primaryStage.setScene(loginScene);
        primaryStage.show(); // Display the stage

        // Set action for the signin button
        signinButton.setOnAction(e -> loginUser(usernameInput.getText(), passwordInput.getText(), primaryStage));
        // Set action for the register button
        registerButton.setOnAction(e -> registerUser(usernameInput.getText(), passwordInput.getText(), primaryStage));
    }

    private void registerUser(String username, String password, Stage primaryStage) {
        try (Connection conn = connectToDB()) { // Try with resources to ensure connection is closed
            if (!checkUsernameExists(username, conn)) { // Check if the username already exists
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt()); // Hash the password for security
                String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)"; // SQL query to insert new
                                                                                             // user
                try (PreparedStatement pstmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, username); // Set username in the prepared statement
                    pstmt.setString(2, hashedPassword); // Set hashed password in the prepared statement
                    int affectedRows = pstmt.executeUpdate(); // Execute the insert operation

                    if (affectedRows > 0) { // Check if the user was successfully added
                        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                currentUserID = generatedKeys.getInt(1); // Retrieve and store the new user's ID
                            } else {
                                throw new SQLException("Creating user failed, no ID obtained."); // Handle failed user
                                                                                                 // creation
                            }
                        }

                        // Show success message
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Registration Successful");
                        alert.setHeaderText(null);
                        alert.setContentText("Registration successful. Welcome!");
                        alert.showAndWait();
                        initializeMainScene(primaryStage); // Initialize the main scene after successful registration
                    }
                }
            } else {
                // Show error if username exists
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Registration Failed");
                alert.setHeaderText(null);
                alert.setContentText("Username already exists. Please try a different username.");
                alert.showAndWait();
            }
        } catch (SQLException e) { // Handle SQL exceptions
            e.printStackTrace(); // Print exception stack trace for debugging
            // Show database error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("A database error occurred. Please try again.");
            alert.showAndWait();
        }
    }

    // Method to check if a username already exists in the database
    private boolean checkUsernameExists(String username, Connection conn) {
        String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?"; // SQL query to check existence of username
        try (PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
            stmt.setString(1, username); // Set the username in the SQL query
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; // Return true if the username exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print SQL exception errors
        }
        return false; // Return false if the username does not exist
    }

    // Method to log in a user with username and password
    private void loginUser(String username, String password, Stage primaryStage) {
        Connection conn = connectToDB(); // Connect to the database
        if (conn == null) {
            return; // Exit if the database connection is not established
        }

        try {
            String sql = "SELECT id, password FROM users WHERE username = ?"; // SQL query to get user by username
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username); // Set the username in the SQL query
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) { // Check if the user exists
                        String hashedPasswordFromDB = rs.getString("password"); // Get the hashed password from database
                        if (BCrypt.checkpw(password, hashedPasswordFromDB)) { // Compare provided password with the
                                                                              // hashed one
                            currentUserID = rs.getInt("id"); // Set the current user ID if password matches
                            initializeMainScene(primaryStage); // Initialize the main scene upon successful login
                            return; // Exit the method after successful login
                        }
                    }
                }
            }
            // Show error if username or password is invalid
            new Alert(Alert.AlertType.ERROR, "Invalid username or password").showAndWait();

        } catch (SQLException e) {
            e.printStackTrace(); // Print SQL exception errors
        } finally {
            try {
                if (conn != null) {
                    conn.close(); // Ensure the database connection is closed
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Print SQL exception errors if closing the connection fails
            }
        }
    }

    private void initializeMainScene(Stage primaryStage) {
        // Create navigation buttons for different sections of the application
        Button homeButton = createNavigationButton("Home");
        Button activityButton = createNavigationButton("Activity");
        Button foodItemButton = createNavigationButton("Food Items");
        Button goalButton = createNavigationButton("Goal");
        Button userProgressButton = createNavigationButton("User Progress");

        // Set up actions for each navigation button to change the view
        setupButtonActions(homeButton, activityButton, foodItemButton, goalButton, userProgressButton);

        // Create and configure the navigation bar
        HBox navigationBar = new HBox(homeButton, activityButton, foodItemButton, goalButton, userProgressButton);
        navigationBar.setSpacing(10); // Set spacing between buttons
        navigationBar.setAlignment(Pos.CENTER); // Align buttons to the center

        // Set up the main layout for the title and user progress messages
        titleAndProgressLayout = new VBox();
        titleAndProgressLayout.setAlignment(Pos.CENTER);
        titleAndProgressLayout.setSpacing(20);
        Label titleLabel = new Label("Fruitful Fitness");
        titleLabel.setFont(Font.font("Arial", 24)); // Set the font size and type
        titleLabel.setTextFill(Color.GREEN); // Set the font color
        Label userProgressLabel = new Label("Welcome to Fruitful Fitness!");
        userProgressLabel.setFont(Font.font("Arial", 16)); // Set the font size and type
        userProgressLabel.setTextFill(Color.GRAY); // Set the font color
        titleAndProgressLayout.getChildren().addAll(titleLabel, userProgressLabel); // Add the labels to the layout

        // Set up the main application layout
        BorderPane root = new BorderPane();
        root.setTop(navigationBar); // Set the navigation bar at the top
        root.setCenter(titleAndProgressLayout); // Set the main content area
        root.setPadding(new Insets(20)); // Set padding for the layout
        root.setStyle(
                "-fx-background-image: url('" + getClass().getResource("assets/BackgroundFFapp.png").toExternalForm()
                        + "'); -fx-background-size: cover;"); // Set the background image

        // Set action for the 'Goal' button: display goal setting view when clicked
        goalButton.setOnAction(event -> {
            VBox goalLayout = new VBox(10); // Layout for goal settings
            goalLayout.setAlignment(Pos.CENTER);
            goalLayout.setPadding(new Insets(15));

            // Display current goals
            Label currentIntakeGoalLabel = new Label("Current Daily Intake Goal: " + currentIntakeGoal);
            Label currentBurnedCaloriesGoalLabel = new Label(
                    "Current Daily Burned Calories Goal: " + currentBurnedCaloriesGoal);

            // Fields for entering new goals
            TextField newIntakeGoalField = new TextField();
            newIntakeGoalField.setPromptText("New intake goal (cal)");
            TextField newBurnedCaloriesGoalField = new TextField();
            newBurnedCaloriesGoalField.setPromptText("New burned calories goal (cal)");

            // Buttons for updating goals
            Button updateIntakeGoalButton = new Button("Update Intake Goal");
            Button updateBurnedCaloriesGoalButton = new Button("Update Burned Calories Goal");

            // Set actions for updating goals
            updateIntakeGoalButton.setOnAction(e -> {
                // Update intake goal logic here...
            });
            updateBurnedCaloriesGoalButton.setOnAction(e -> {
                // Update burned calories goal logic here...
            });

            // Add all goal components to the goal layout
            goalLayout.getChildren().addAll(currentIntakeGoalLabel, newIntakeGoalField, updateIntakeGoalButton,
                    currentBurnedCaloriesGoalLabel, newBurnedCaloriesGoalField, updateBurnedCaloriesGoalButton);
            root.setCenter(goalLayout); // Set the goal layout as the main content area
        });

        // Set the action for the 'Activity' button: display activity form when clicked
        activityButton.setOnAction(event -> {
            // Layout for activity input form
            VBox activityLayout = new VBox(10);
            activityLayout.setAlignment(Pos.CENTER);
            activityLayout.setPadding(new Insets(15));

            // Input fields for activity details
            TextField nameInput = new TextField();
            nameInput.setPromptText("Activity Name");
            TextField durationInput = new TextField();
            durationInput.setPromptText("Duration (minutes)");
            TextField intensityInput = new TextField();
            intensityInput.setPromptText("Intensity (1-10)");
            DatePicker datePicker = new DatePicker(); // Date picker for selecting the date of the activity

            // Button for submitting the activity form
            Button submitButton = new Button("Add Activity");
            submitButton.setOnAction(e -> {
                // Collect input data from the form
                String name = nameInput.getText();
                int duration = Integer.parseInt(durationInput.getText()); // Convert duration to integer
                int intensity = Integer.parseInt(intensityInput.getText()); // Convert intensity to integer
                LocalDate date = datePicker.getValue(); // Get the date from the date picker
                int user_id = currentUserID; // Current user's ID

                // Calculate calories burned based on duration and intensity (simplified
                // calculation)
                int caloriesBurned = duration * intensity;

                // Connect to the database to insert the new activity
                Connection conn = connectToDB();

                if (conn != null) { // Check if the database connection was successful
                    try {
                        // SQL query to insert the new activity
                        String sql = "INSERT INTO activities (name, duration, intensity, date, calories_burned, user_id) VALUES (?, ?, ?, ?, ?, ?)";
                        PreparedStatement statement = conn.prepareStatement(sql);
                        // Set the values for the prepared statement based on activity input
                        statement.setString(1, name);
                        statement.setInt(2, duration);
                        statement.setInt(3, intensity);
                        statement.setDate(4, java.sql.Date.valueOf(date));
                        statement.setInt(5, caloriesBurned);
                        statement.setInt(6, user_id);
                        // Execute the insert operation
                        int rowsInserted = statement.executeUpdate();

                        if (rowsInserted > 0) {
                            System.out.println("Activity data inserted successfully.");
                            // Update the activity table view to reflect the new activity
                            updateActivityTableView();
                        } else {
                            System.out.println("Failed to insert activity data.");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace(); // Print SQL exception errors
                    } finally {
                        try {
                            if (conn != null) {
                                conn.close(); // Ensure the database connection is closed
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace(); // Print SQL exception errors if closing the connection fails
                        }
                    }
                } else {
                    System.out.println("Failed to connect to the database."); // Print failure message if connection
                                                                              // failed
                }

            });

            // Set up the activity table view
            TableView<Activity> activityTableView = new TableView<>();
            // Define and set up columns for the table view
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

            // Fetch and display activities for the current user
            ObservableList<Activity> activities = FXCollections.observableArrayList();
            activities.addAll(fetchActivitiesForCurrentUserAndDate());
            activityTableView.setItems(activities);
            activityTableView.getColumns().addAll(nameColumn, durationColumn, intensityColumn, caloriesBurnedColumn,
                    dateColumn);

            // Add all form components and the table view to the activity layout
            activityLayout.getChildren().addAll(nameInput, durationInput, intensityInput, datePicker, submitButton,
                    activityTableView);
            // Set the activity layout as the main content area
            root.setCenter(activityLayout);
        });

        // Set the action for the 'Food Items' button: display food item form when
        // clicked
        foodItemButton.setOnAction(event -> {
            // Layout for food item input form
            VBox foodItemLayout = new VBox(10);
            foodItemLayout.setAlignment(Pos.CENTER);
            foodItemLayout.setPadding(new Insets(15));

            // Input fields for food item details
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
            DatePicker datePicker = new DatePicker(); // Date picker for selecting the date of consumption

            // Button for submitting the food item form
            Button submitButton = new Button("Add Food Item");
            submitButton.setOnAction(e -> {
                // Collect input data from the form
                String name = foodNameInput.getText();
                int calories = Integer.parseInt(caloriesInput.getText()); // Convert calories to integer
                int carbs = Integer.parseInt(carbInput.getText()); // Convert carbs to integer
                int fats = Integer.parseInt(fatsInput.getText()); // Convert fats to integer
                int protiens = Integer.parseInt(protienInput.getText()); // Convert proteins to integer
                int servingSize = Integer.parseInt(servingSizeInput.getText()); // Convert serving size to integer
                LocalDate date = datePicker.getValue(); // Get the date from the date picker
                int user_id = currentUserID; // Current user's ID

                // Connect to the database to insert the new food item
                Connection conn = connectToDB();

                if (conn != null) { // Check if the database connection was successful
                    try {
                        // SQL query to insert the new food item
                        String sql = "INSERT INTO food_items (name, calories, carbs, fats, protiens, serving_size, date, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement statement = conn.prepareStatement(sql);
                        // Set the values for the prepared statement based on food item input
                        statement.setString(1, name);
                        statement.setInt(2, calories);
                        statement.setInt(3, carbs);
                        statement.setInt(4, fats);
                        statement.setInt(5, protiens);
                        statement.setInt(6, servingSize);
                        statement.setDate(7, java.sql.Date.valueOf(date));
                        statement.setInt(8, user_id);
                        // Execute the insert operation
                        int rowsInserted = statement.executeUpdate();

                        if (rowsInserted > 0) {
                            // Display confirmation alert if the food item was successfully inserted
                            new Alert(Alert.AlertType.CONFIRMATION, "Inserted Food Item Successfully.").showAndWait();
                        } else {
                            // Display error alert if the insert operation failed
                            new Alert(Alert.AlertType.ERROR, "Failed to insert food item.").showAndWait();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace(); // Print SQL exception errors
                    } finally {
                        try {
                            conn.close(); // Ensure the database connection is closed
                        } catch (SQLException ex) {
                            ex.printStackTrace(); // Print SQL exception errors if closing the connection fails
                        }
                    }
                } else {
                    // Print failure message if connection to the database failed
                    System.out.println("Failed to connect to the database.");
                }
            });

            // Set up the food item table view
            TableView<FoodItem> food_itemTableView = new TableView<>();
            // Define and set up columns for the table view based on food item attributes
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

            // Fetch and display food items for the current user
            ObservableList<FoodItem> food_items = fetchFoodItemsForCurrentUserAndDate();
            food_itemTableView.setItems(food_items);
            food_itemTableView.getColumns().addAll(nameColumn, caloriesColumn, carbsColumn, fatsColumn, protiensColumn,
                    serving_sizeColumn, dateColumn);

            // Add all form components and the table view to the food item layout
            foodItemLayout.getChildren().addAll(foodNameInput, caloriesInput, carbInput, fatsInput, protienInput,
                    servingSizeInput, datePicker, submitButton, food_itemTableView);
            // Set the food item layout as the main content area
            root.setCenter(foodItemLayout);
        });

        // Set the action for the 'User Progress' button: display user progress view
        // when clicked
        userProgressButton.setOnAction(event -> {
            // Layout for user progress display
            VBox userProgressLayout = new VBox(10);
            userProgressLayout.setAlignment(Pos.TOP_CENTER);
            userProgressLayout.setPadding(new Insets(10, 0, 0, 0));

            // Label for the user progress section
            Label progressLabel = new Label("User Progress");

            // Fetch user's total calories consumed and burned from the database
            int totalCaloriesConsumed = fetchTotalCaloriesForCurrentUserAndDate();
            int totalCaloriesBurned = fetchTotalCaloriesBurnedForCurrentUserAndDate();
            // Extract numeric values from goal strings
            int intakeGoalValue = Integer.parseInt(currentIntakeGoal.replaceAll("[^0-9]", ""));
            int burnedGoalValue = Integer.parseInt(currentBurnedCaloriesGoal.replaceAll("[^0-9]", ""));

            // Setup bar charts for calorie intake and burned goals comparison
            CategoryAxis xAxisIntake = new CategoryAxis();
            xAxisIntake.setLabel("Goal Type");
            NumberAxis yAxisIntake = new NumberAxis();
            yAxisIntake.setLabel("Calories");

            // Bar chart for comparing calorie intake
            BarChart<String, Number> intakeBarChart = new BarChart<>(xAxisIntake, yAxisIntake);
            intakeBarChart.setTitle("Calorie Intake");

            // Data series for intake goal and actual intake
            XYChart.Series<String, Number> intakeGoalSeries = new XYChart.Series<>();
            intakeGoalSeries.setName("Goal");
            intakeGoalSeries.getData().add(new XYChart.Data<>("Intake", intakeGoalValue));

            XYChart.Series<String, Number> totalIntakeSeries = new XYChart.Series<>();
            totalIntakeSeries.setName("Consumed");
            totalIntakeSeries.getData().add(new XYChart.Data<>("Intake", totalCaloriesConsumed));

            // Add series to intake bar chart
            intakeBarChart.getData().addAll(intakeGoalSeries, totalIntakeSeries);
            intakeBarChart.setLegendVisible(true);

            // Setup for calories burned bar chart
            CategoryAxis xAxisBurned = new CategoryAxis();
            xAxisBurned.setLabel("Goal Type");
            NumberAxis yAxisBurned = new NumberAxis();
            yAxisBurned.setLabel("Calories");

            BarChart<String, Number> burnedBarChart = new BarChart<>(xAxisBurned, yAxisBurned);
            burnedBarChart.setTitle("Calorie Burned");

            // Data series for burned goal and actual burned
            XYChart.Series<String, Number> burnedGoalSeries = new XYChart.Series<>();
            burnedGoalSeries.setName("Goal");
            burnedGoalSeries.getData().add(new XYChart.Data<>("Burned", burnedGoalValue));

            XYChart.Series<String, Number> totalBurnedSeries = new XYChart.Series<>();
            totalBurnedSeries.setName("Burned");
            totalBurnedSeries.getData().add(new XYChart.Data<>("Burned", totalCaloriesBurned));

            // Add series to burned bar chart
            burnedBarChart.getData().addAll(burnedGoalSeries, totalBurnedSeries);
            burnedBarChart.setLegendVisible(true);

            // Layout for displaying both bar charts
            HBox chartsLayout = new HBox(10);
            chartsLayout.getChildren().addAll(intakeBarChart, burnedBarChart);

            // Add progress label and charts to the user progress layout
            userProgressLayout.getChildren().addAll(progressLabel, chartsLayout);

            // Set the user progress layout as the main content of the root pane
            root.setCenter(userProgressLayout);
        });

        // Action for 'Home' button to return to the main title and progress overview
        homeButton.setOnAction(e -> root.setCenter(titleAndProgressLayout));

        // Setup the scene and apply CSS styling
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        // Set the primary stage with the scene containing the entire layout
        primaryStage.setScene(scene);
        primaryStage.setTitle("Fruitful Fitness");
        primaryStage.show(); // Display the stage
    }

    // Sets the on-action event for a variable number of buttons. This method is
    // used to unify the appearance and behavior of navigation buttons.
    private void setupButtonActions(Button... buttons) {
        for (Button btn : buttons) { // Loop through each button
            btn.setOnAction(event -> { // Set an action for when the button is clicked
                for (Button otherBtn : buttons) { // Remove 'button-active' class from all buttons to reset their style
                    otherBtn.getStyleClass().remove("button-active");
                }
                btn.getStyleClass().add("button-active"); // Add 'button-active' class to the clicked button
                btn.applyCss(); // Apply the CSS to the button - ensuring changes take effect
                btn.layout(); // Lay out the button again to ensure changes are displayed
            });
        }
    }

    // Creates a standardized navigation button with a specified text label.
    private Button createNavigationButton(String text) {
        Button button = new Button(text); // Create new button with the given text
        button.setPrefWidth(100); // Set a preferred width for uniformity
        button.getStyleClass().add("button"); // Add CSS class for styling
        return button; // Return the newly created button
    }

    // Updates the user's goals for calorie intake and calories burned in the
    // database.
    public void updateUserGoals(int userId, String newIntakeGoal, String newBurnedCaloriesGoal) {
        // SQL query to insert new goals or update existing ones for a specific user
        String sql = "INSERT INTO user_goals (user_id, intake_goal, burned_calories_goal, date_updated) VALUES (?, ?, ?, CURDATE()) ON DUPLICATE KEY UPDATE intake_goal = VALUES(intake_goal), burned_calories_goal = VALUES(burned_calories_goal), date_updated = CURDATE();";

        try (Connection conn = connectToDB(); // Attempt to connect to the database
                PreparedStatement pstmt = conn.prepareStatement(sql)) { // Prepare the SQL statement
            // Set the parameters for the prepared statement
            pstmt.setInt(1, userId);
            pstmt.setString(2, newIntakeGoal);
            pstmt.setString(3, newBurnedCaloriesGoal);

            // Execute the update in the database
            pstmt.executeUpdate();
        } catch (SQLException e) { // Catch and print any SQL exceptions
            e.printStackTrace();
        }
    }

    // Fetches the list of activities for the current user for today's date.
    public ObservableList<Activity> fetchActivitiesForCurrentUserAndDate() {
        ObservableList<Activity> activities = FXCollections.observableArrayList(); // Create a new list to hold
                                                                                   // activities
        LocalDate currentDate = LocalDate.now(); // Get today's date

        // SQL query to select activities for the current user on the current date
        String sql = "SELECT * FROM activities WHERE user_id = ? AND date = ?";

        try (Connection conn = connectToDB(); // Attempt to connect to the database
                PreparedStatement pstmt = conn.prepareStatement(sql)) { // Prepare the SQL statement
            // Set the parameters for the prepared statement with the current user ID and
            // date
            pstmt.setInt(1, currentUserID);
            pstmt.setDate(2, java.sql.Date.valueOf(currentDate));

            ResultSet rs = pstmt.executeQuery(); // Execute the query and get the result set

            while (rs.next()) { // Iterate through the result set and add each activity to the list
                // Extract activity details from the result set
                String name = rs.getString("name");
                int duration = rs.getInt("duration");
                int intensity = rs.getInt("intensity");
                int caloriesBurned = rs.getInt("calories_burned");
                LocalDate date = rs.getDate("date").toLocalDate(); // Convert SQL date to LocalDate

                // Add a new Activity object to the list
                activities.add(new Activity(name, duration, intensity, caloriesBurned, date));
            }
        } catch (SQLException e) { // Catch and print any SQL exceptions
            e.printStackTrace();
        }

        return activities; // Return the list of activities
    }

    // Refreshes the data in the activity table view based on the current user and
    // date.
    private void updateActivityTableView() {
        ObservableList<Activity> activities = fetchActivitiesForCurrentUserAndDate(); // Fetch the latest activities

        activityTableView.setItems(activities); // Update the table view with the new list of activities
    }

    // Fetches a list of FoodItem objects for the current user for today's date.
    public ObservableList<FoodItem> fetchFoodItemsForCurrentUserAndDate() {
        ObservableList<FoodItem> foodItems = FXCollections.observableArrayList(); // Create a new list to hold FoodItem
                                                                                  // objects
        LocalDate currentDate = LocalDate.now(); // Get today's date

        // SQL query to select food items for the current user on the current date
        String sql = "SELECT * FROM food_items WHERE user_id = ? AND date = ?";

        try (Connection conn = connectToDB(); // Attempt to connect to the database
                PreparedStatement pstmt = conn.prepareStatement(sql)) { // Prepare the SQL statement
            pstmt.setInt(1, currentUserID); // Set the current user's ID
            pstmt.setDate(2, java.sql.Date.valueOf(currentDate)); // Set the current date

            ResultSet rs = pstmt.executeQuery(); // Execute the query

            while (rs.next()) { // Iterate through the result set
                // Extract food item details from the result set
                String name = rs.getString("name");
                int calories = rs.getInt("calories");
                int carbs = rs.getInt("carbs");
                int fats = rs.getInt("fats");
                int protiens = rs.getInt("protiens");
                int servingSize = rs.getInt("serving_size");
                LocalDate date = rs.getDate("date").toLocalDate(); // Convert SQL date to LocalDate

                // Add a new FoodItem object to the list
                foodItems.add(new FoodItem(name, calories, carbs, fats, protiens, servingSize, date));
            }
        } catch (SQLException e) { // Catch and print any SQL exceptions
            e.printStackTrace();
        }

        return foodItems; // Return the list of food items
    }

    // Fetches the total number of calories consumed by the current user on today's
    // date.
    private int fetchTotalCaloriesForCurrentUserAndDate() {
        int totalCalories = 0; // Initialize the total calories consumed
        LocalDate currentDate = LocalDate.now(); // Get today's date
        String formattedDate = currentDate.toString(); // Convert today's date to a String

        // SQL query to calculate the sum of calories for food items consumed by the
        // user on the current date
        String sql = "SELECT SUM(calories) AS total FROM food_items WHERE user_id = ? AND DATE(date) = ?";

        try (Connection conn = connectToDB(); // Attempt to connect to the database
                PreparedStatement pstmt = conn.prepareStatement(sql)) { // Prepare the SQL statement
            pstmt.setInt(1, currentUserID); // Set the current user's ID
            pstmt.setString(2, formattedDate); // Set the current date

            ResultSet rs = pstmt.executeQuery(); // Execute the query

            if (rs.next()) { // If the result set is not empty
                totalCalories = rs.getInt("total"); // Get the total calories from the result set
            }
        } catch (SQLException e) { // Catch and print any SQL exceptions
            System.out.println("Error fetching total calories for current user and date: " + e.getMessage());
        }

        return totalCalories; // Return the total calories consumed
    }

    // Fetches the total number of calories burned by the current user on today's
    // date.
    private int fetchTotalCaloriesBurnedForCurrentUserAndDate() {
        int totalCaloriesBurned = 0; // Initialize the total calories burned

        LocalDate currentDate = LocalDate.now(); // Get today's date

        // SQL query to calculate the sum of calories burned by the user in activities
        // on the current date
        String sql = "SELECT SUM(calories_burned) AS total FROM activities WHERE user_id = ? AND date = ?";

        try (Connection conn = connectToDB(); // Attempt to connect to the database
                PreparedStatement pstmt = conn.prepareStatement(sql)) { // Prepare the SQL statement
            pstmt.setInt(1, currentUserID); // Set the current user's ID
            pstmt.setDate(2, java.sql.Date.valueOf(currentDate)); // Set the current date

            ResultSet rs = pstmt.executeQuery(); // Execute the query

            if (rs.next()) { // If the result set is not empty
                totalCaloriesBurned = rs.getInt("total"); // Get the total calories burned from the result set
            }
        } catch (SQLException e) { // Catch and print any SQL exceptions
            System.err.println("Error fetching total calories burned for the current user and date: " + e.getMessage());
        }

        return totalCaloriesBurned; // Return the total calories burned
    }

}
