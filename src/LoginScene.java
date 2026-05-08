
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginScene {

	private Stage primaryStage;

	public LoginScene(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void showLoginScene() {

		VBox mainLayout = new VBox(30);
		mainLayout.setAlignment(Pos.CENTER);
		mainLayout.setPadding(new Insets(40));
		mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #FFF0F5, #FFE4E1);");

		ImageView imageflower = new ImageView(new Image("file:Photo/flower.png"));
		imageflower.setFitWidth(45);
		imageflower.setFitHeight(45);
		imageflower.setPreserveRatio(true);

		Label titleLabel = new Label("ASAL BeautyCare");
		titleLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 36));
		titleLabel.setStyle(
				"-fx-text-fill: #DB7093; -fx-effect: dropshadow(gaussian, rgba(219,112,147,0.5), 10, 0, 0, 3);");
		titleLabel.setGraphic(imageflower);

		Label welcomeLabel = new Label("Welcome to ASAL BeautyCare System");
		welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		welcomeLabel.setStyle("-fx-text-fill: #8B4513;");

		VBox roleSelectionBox = new VBox(25);
		roleSelectionBox.setAlignment(Pos.CENTER);
		roleSelectionBox.setPadding(new Insets(30, 50, 30, 50));
		roleSelectionBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); " + "-fx-background-radius: 20; "
				+ "-fx-border-radius: 20; " + "-fx-border-color: #FFB6C1; " + "-fx-border-width: 3; "
				+ "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 0);");

		Label chooseOptionLabel = new Label("Choose an option:");
		chooseOptionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
		chooseOptionLabel.setStyle("-fx-text-fill: #C71585;");

		Button adminButton = createOptionButton("Login as Admin", "#FF69B4", "#FF1493");

		Button customerButton = createOptionButton("Login as Customer", "#20B2AA", "#008B8B");

		Label infoLabel = new Label("Select your role to continue to the system");
		infoLabel.setFont(Font.font("Arial", 14));
		infoLabel.setStyle("-fx-text-fill: #696969;");

		roleSelectionBox.getChildren().addAll(chooseOptionLabel, adminButton, customerButton, infoLabel);

		Label footerLabel = new Label("2026 ASAL BeautyCare System");
		footerLabel.setFont(Font.font("Arial", 12));
		footerLabel.setStyle("-fx-text-fill: #808080;");

		mainLayout.getChildren().addAll(titleLabel, welcomeLabel, roleSelectionBox, footerLabel);

		adminButton.setOnAction(e -> showAdminLogin());
		customerButton.setOnAction(e -> showCustomerScreen());

		Scene scene = new Scene(mainLayout, 900, 700);

		try {
			scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		} catch (Exception e) {
			AlertManager.showError("CSS file not found \n using default styling");
		}

		primaryStage.setTitle("ASAL BeautyCare Login");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private Button createOptionButton(String title, String baseColor, String hoverColor) {

		VBox buttonContent = new VBox(5);
		buttonContent.setAlignment(Pos.CENTER_LEFT);
		buttonContent.setPadding(new Insets(15, 25, 15, 25));

		Label titleLabel = new Label(title);
		titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		titleLabel.setStyle("-fx-text-fill: white;");

		buttonContent.getChildren().add(titleLabel);

		Button button = new Button();
		button.setGraphic(buttonContent);
		button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

		button.setStyle(
				"-fx-background-color: " + baseColor + "; " + "-fx-background-radius: 15; " + "-fx-border-radius: 15; "
						+ "-fx-border-color: " + (baseColor.equals("#FF69B4") ? "#DB7093" : "#008080") + "; "
						+ "-fx-border-width: 2; " + "-fx-cursor: hand; " + "-fx-pref-width: 400; "
						+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");

		button.setOnMouseEntered(e -> {
			button.setStyle("-fx-background-color: " + hoverColor + "; " + "-fx-background-radius: 15; "
					+ "-fx-border-radius: 15; " + "-fx-border-color: "
					+ (hoverColor.equals("#FF1493") ? "#C71585" : "#006666") + "; " + "-fx-border-width: 2; "
					+ "-fx-cursor: hand; " + "-fx-pref-width: 400; "
					+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);");
		});

		button.setOnMouseExited(e -> {
			button.setStyle("-fx-background-color: " + baseColor + "; " + "-fx-background-radius: 15; "
					+ "-fx-border-radius: 15; " + "-fx-border-color: "
					+ (baseColor.equals("#FF69B4") ? "#DB7093" : "#008080") + "; " + "-fx-border-width: 2; "
					+ "-fx-cursor: hand; " + "-fx-pref-width: 400; "
					+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");
		});

		button.setOnMousePressed(e -> {
			button.setStyle("-fx-background-color: " + (baseColor.equals("#FF69B4") ? "#C71585" : "#006666") + "; "
					+ "-fx-background-radius: 15; " + "-fx-border-radius: 15; " + "-fx-border-color: "
					+ (baseColor.equals("#FF69B4") ? "#8B0A50" : "#004D4D") + "; " + "-fx-border-width: 2; "
					+ "-fx-cursor: hand; " + "-fx-pref-width: 400; " + "-fx-translate-y: 2px;");
		});

		button.setOnMouseReleased(e -> {
			button.setStyle("-fx-background-color: " + baseColor + "; " + "-fx-background-radius: 15; "
					+ "-fx-border-radius: 15; " + "-fx-border-color: "
					+ (baseColor.equals("#FF69B4") ? "#DB7093" : "#008080") + "; " + "-fx-border-width: 2; "
					+ "-fx-cursor: hand; " + "-fx-pref-width: 400; " + "-fx-translate-y: 0px;");
		});

		return button;
	}

	private void showAdminLogin() {

		Stage loginStage = new Stage();
		loginStage.setTitle("Admin Login ASAL BeautyCare");

		VBox loginLayout = new VBox(25);
		loginLayout.setAlignment(Pos.CENTER);
		loginLayout.setPadding(new Insets(40));
		loginLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #FFF0F5, #FFE4E1);");

		Label titleLabel = new Label("Admin Login");
		titleLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 28));
		titleLabel.setStyle("-fx-text-fill: #DB7093;");

		TextField usernameField = createStyledTextField("Username");

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Password");
		passwordField.setPrefHeight(45);
		passwordField.setMaxWidth(300);
		passwordField.setStyle("-fx-font-size: 16px; -fx-background-radius: 10; -fx-border-radius: 10; "
				+ "-fx-border-color: #FFB6C1; -fx-border-width: 2; -fx-padding: 10;");

		HBox buttonBox = new HBox(20);
		buttonBox.setAlignment(Pos.CENTER);

		Button loginButton = createPinkButton("Login", "#FF69B4", "#FF1493");
		Button cancelButton = createPinkButton("Cancel", "#F5AFAF", "#FFB6C1");

		buttonBox.getChildren().addAll(loginButton, cancelButton);

		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: #FF0000; -fx-font-size: 14px;");
		errorLabel.setVisible(false);

		loginLayout.getChildren().addAll(titleLabel, usernameField, passwordField, buttonBox, errorLabel);

		loginButton.setOnAction(e -> {
			if (validateAdminLogin(usernameField.getText(), passwordField.getText())) {
				loginStage.close();
				showAdminDashboard();
			} else {
				errorLabel.setText("Invalid username or password! ");
				errorLabel.setVisible(true);
			}
		});

		cancelButton.setOnAction(e -> loginStage.close());

		Scene loginScene = new Scene(loginLayout, 500, 400);
		loginStage.setScene(loginScene);
		loginStage.setResizable(false);
		loginStage.show();
	}

	private TextField createStyledTextField(String prompt) {
		TextField field = new TextField();
		field.setPromptText(prompt);
		field.setPrefHeight(45);
		field.setMaxWidth(300);
		field.setStyle("-fx-font-size: 16px; -fx-background-radius: 10; -fx-border-radius: 10; "
				+ "-fx-border-color: #FFB6C1; -fx-border-width: 2; -fx-padding: 10;");
		return field;
	}

	private Button createPinkButton(String text, String baseColor, String hoverColor) {
		Button button = new Button(text);
		button.setStyle("-fx-background-color: " + baseColor + "; " + "-fx-text-fill: white; -fx-font-size: 16px; "
				+ "-fx-padding: 12px 30px; -fx-font-weight: bold; "
				+ "-fx-background-radius: 10; -fx-border-radius: 10; " + "-fx-cursor: hand;");

		button.setOnMouseEntered(e -> {
			button.setStyle("-fx-background-color: " + hoverColor + "; " + "-fx-text-fill: white; -fx-font-size: 16px; "
					+ "-fx-padding: 12px 30px; -fx-font-weight: bold; "
					+ "-fx-background-radius: 10; -fx-border-radius: 10; " + "-fx-cursor: hand; "
					+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");
		});

		button.setOnMouseExited(e -> {
			button.setStyle("-fx-background-color: " + baseColor + "; " + "-fx-text-fill: white; -fx-font-size: 16px; "
					+ "-fx-padding: 12px 30px; -fx-font-weight: bold; "
					+ "-fx-background-radius: 10; -fx-border-radius: 10; " + "-fx-cursor: hand;");
		});

		return button;
	}

	private boolean validateAdminLogin(String username, String password) {
		return username.equals("admin") && password.equals("admin123");
	}

	private void showAdminDashboard() {
		new AdminScreen(primaryStage).show();

	}

	private void showCustomerScreen() {
		new CustomerProduct(primaryStage).show();
	}

}