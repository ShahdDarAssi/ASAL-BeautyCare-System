import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainScene extends Application {

	@Override
	public void start(Stage primaryStage) {

		BorderPane root = new BorderPane();
		root.setStyle("-fx-background-color: #FBEFEF;");

		VBox centerBox = new VBox(30);
		centerBox.setAlignment(Pos.CENTER);
		centerBox.setStyle("-fx-padding: 50;");

		Label welcomeLabel = new Label("Welcome to Asal BeautyCare!");
		welcomeLabel.setFont(Font.font("Arial", 36));
		welcomeLabel.setStyle("-fx-text-fill: #8B4513; -fx-font-weight: bold;");

		ImageView makeUpimage = new ImageView(new Image("file:Photo/make up.JPEG"));
		makeUpimage.setFitWidth(250);
		makeUpimage.setFitHeight(250);
		makeUpimage.setPreserveRatio(true);

		Label startLabel = new Label("Click Start to begin");
		startLabel.setFont(Font.font("Arial", 24));
		startLabel.setStyle("-fx-text-fill: #B76E79;");

		Button startButton = new Button("Start");
		startButton.setStyle("-fx-background-color: linear-gradient(to bottom, #F9DFDF, #F0C8C8);"
				+ "-fx-text-fill: #333333;" + "-fx-border-color: #B76E79;" + "-fx-border-width: 2;"
				+ "-fx-border-radius: 10;" + "-fx-background-radius: 10;" + "-fx-font-size: 24px;"
				+ "-fx-font-weight: bold;" + "-fx-padding: 15 40;" + "-fx-cursor: hand;");

		startButton.setOnMouseEntered(e -> {
			startButton.setStyle("-fx-background-color: linear-gradient(to bottom, #FFE4E4, #F9DFDF);"
					+ "-fx-text-fill: #B76E79;" + "-fx-border-color: #B76E79;" + "-fx-border-width: 3;"
					+ "-fx-border-radius: 10;" + "-fx-background-radius: 10;" + "-fx-font-size: 24px;"
					+ "-fx-font-weight: bold;" + "-fx-padding: 15 40;" + "-fx-cursor: hand;");
		});

		startButton.setOnMouseExited(e -> {
			startButton.setStyle("-fx-background-color: linear-gradient(to bottom, #F9DFDF, #F0C8C8);"
					+ "-fx-text-fill: #333333;" + "-fx-border-color: #B76E79;" + "-fx-border-width: 2;"
					+ "-fx-border-radius: 10;" + "-fx-background-radius: 10;" + "-fx-font-size: 24px;"
					+ "-fx-font-weight: bold;" + "-fx-padding: 15 40;" + "-fx-cursor: hand;");
		});

		centerBox.getChildren().addAll(welcomeLabel, makeUpimage, startLabel, startButton);

		root.setCenter(centerBox);

		Scene scene = new Scene(root, 900, 700);

		primaryStage.setTitle("Asal Beauty Care System");
		primaryStage.setScene(scene);
		primaryStage.setMinWidth(600);
		primaryStage.setMinHeight(500);
		primaryStage.show();

		startButton.setOnAction(e -> {
			new LoginScene(primaryStage).showLoginScene();
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}