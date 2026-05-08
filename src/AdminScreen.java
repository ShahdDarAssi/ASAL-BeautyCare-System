import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class AdminScreen {

	private Stage primaryStage;
	private BorderPane root;
	private VBox leftSide;
	private StackPane centerContent;

	private Button activeButton;

	public AdminScreen(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.root = new BorderPane();
	}

	public void show() {

		HBox topSide = createTopSide();

		leftSide = createSidebar();

		centerContent = new StackPane();
		centerContent.setStyle("-fx-background-color: #FFFAFA;");

		showWelcome();

		root.setTop(topSide);
		root.setLeft(leftSide);
		root.setCenter(centerContent);
		root.setBottom(createFooter());
		root.setStyle("-fx-background-color: #FFF0F5;");

		Scene scene = new Scene(root, 1200, 800);
		primaryStage.setScene(scene);
		primaryStage.setTitle("ASAL BeautyCare - Admin Screen");
		primaryStage.show();
	}

	private HBox createTopSide() {

		HBox topBar = new HBox(20);
		topBar.setPadding(new Insets(15, 30, 15, 30));
		topBar.setStyle("-fx-background-color: linear-gradient(to right, #FF69B4, #FF1493); "
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");
		topBar.setAlignment(Pos.CENTER_LEFT);

		ImageView imageUser = new ImageView(new Image("file:Photo/user.png"));
		imageUser.setFitWidth(40);
		imageUser.setFitHeight(40);
		imageUser.setPreserveRatio(true);

		VBox userInfo = new VBox(2);
		Label userName = new Label("Admin User");
		userName.setFont(Font.font("Arial", FontWeight.BOLD, 16));
		userName.setStyle("-fx-text-fill: white;");

		Label userRole = new Label("Administrator");
		userRole.setFont(Font.font("Arial", 12));
		userRole.setStyle("-fx-text-fill: #FFFAFA;");

		userInfo.getChildren().addAll(userName, userRole);

		Label titleLabel = new Label("ASAL BeautyCare Admin Panel");
		titleLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 22));
		titleLabel.setStyle("-fx-text-fill: white;");

		HBox.setHgrow(titleLabel, Priority.ALWAYS);
		titleLabel.setAlignment(Pos.CENTER);

		ImageView iconNotification = new ImageView(new Image("file:Photo/Notification.png"));
		iconNotification.setFitWidth(30);
		iconNotification.setFitHeight(30);
		iconNotification.setPreserveRatio(true);

		Button notificationsBtn = new Button();
		notificationsBtn.setGraphic(iconNotification);
		notificationsBtn.setStyle("-fx-background-color: #FF69B4; " + "-fx-background-radius: 20; "
				+ "-fx-border-radius: 20; " + "-fx-border-color: white; " + "-fx-border-width: 2; " + "-fx-padding: 5; "
				+ "-fx-cursor: hand;");

		ImageView iconSetting = new ImageView(new Image("file:Photo/settings.png"));
		iconSetting.setFitWidth(30);
		iconSetting.setFitHeight(30);
		iconSetting.setPreserveRatio(true);

		Button settingsBtn = new Button();
		settingsBtn.setGraphic(iconSetting);
		settingsBtn.setStyle("-fx-background-color: #FF69B4; " + "-fx-background-radius: 20; "
				+ "-fx-border-radius: 20; " + "-fx-border-color: white; " + "-fx-border-width: 2; " + "-fx-padding: 5; "
				+ "-fx-cursor: hand;");

		notificationsBtn.setOnMouseEntered(e -> {
			notificationsBtn.setStyle("-fx-background-color: #FF1493; " + "-fx-background-radius: 20; "
					+ "-fx-border-radius: 20; " + "-fx-border-color: white; " + "-fx-border-width: 2; "
					+ "-fx-padding: 5; " + "-fx-cursor: hand; "
					+ "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.3), 10, 0, 0, 0);");
		});

		notificationsBtn.setOnMouseExited(e -> {
			notificationsBtn.setStyle("-fx-background-color: #FF69B4; " + "-fx-background-radius: 20; "
					+ "-fx-border-radius: 20; " + "-fx-border-color: white; " + "-fx-border-width: 2; "
					+ "-fx-padding: 5; " + "-fx-cursor: hand;");
		});

		settingsBtn.setOnMouseEntered(e -> {
			settingsBtn.setStyle("-fx-background-color: #FF1493; " + "-fx-background-radius: 20; "
					+ "-fx-border-radius: 20; " + "-fx-border-color: white; " + "-fx-border-width: 2; "
					+ "-fx-padding: 5; " + "-fx-cursor: hand; "
					+ "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.3), 10, 0, 0, 0);");
		});

		settingsBtn.setOnMouseExited(e -> {
			settingsBtn.setStyle("-fx-background-color: #FF69B4; " + "-fx-background-radius: 20; "
					+ "-fx-border-radius: 20; " + "-fx-border-color: white; " + "-fx-border-width: 2; "
					+ "-fx-padding: 5; " + "-fx-cursor: hand;");
		});

		topBar.getChildren().addAll(imageUser, userInfo, titleLabel, notificationsBtn, settingsBtn);

		return topBar;
	}

	private void showWelcome() {

		VBox welcomeBox = new VBox(30);
		welcomeBox.setAlignment(Pos.CENTER);
		welcomeBox.setPadding(new Insets(50));

		Label welcomeLabel = new Label("Welcome to ASAL BeautyCare Admin Panel");
		welcomeLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 32));
		welcomeLabel.setStyle("-fx-text-fill: #DB7093; -fx-text-alignment: center;");

		Label subLabel = new Label("Select an option from the menu to manage different aspects of the system");
		subLabel.setFont(Font.font("Arial", 18));
		subLabel.setStyle("-fx-text-fill: #8B4513; -fx-text-alignment: center;");

		GridPane statsGrid = createStatsGrid();

		welcomeBox.getChildren().addAll(welcomeLabel, subLabel, statsGrid);
		centerContent.getChildren().setAll(welcomeBox);
	}

	private GridPane createStatsGrid() {

		GridPane grid = new GridPane();
		grid.setHgap(20);
		grid.setVgap(20);
		grid.setAlignment(Pos.CENTER);
		grid.setPadding(new Insets(30, 0, 0, 0));

		grid.add(createStatCard("Total Products", "" + DatabaseOperationsProduct.getProductCount(), "#FF69B4"), 0, 0);
		grid.add(createStatCard("Today's Orders", "" + DatabaseOperations.getTodaysOrders(), "#20B2AA"), 1, 0);
		grid.add(createStatCard("Total Inventory", "" + DatabaseOperations.getTotalInventory(), "#FF6347"), 2, 0);
		grid.add(createStatCard("Active Users", "" + DatabaseOperations.getActiveUsers(), "#9370DB"), 3, 0);

		grid.add(createStatCard("Revenue Today", "$" + DatabaseOperations.getRevenueToday(), "#FFA500"), 0, 1);
		grid.add(createStatCard("Orders Completed", "" + DatabaseOperations.getCompletedOrders(), "#32CD32"), 1, 1);
		grid.add(createStatCard("Most Sold Product", DatabaseOperations.getMostSoldProduct(), "#FFD700"), 2, 1);
		grid.add(createStatCard("Most Used Supplier", DatabaseOperations.getMostUsedSupplier(), "#00CED1"), 3, 1);

		return grid;
	}

	private VBox createStatCard(String title, String value, String color) {
		VBox card = new VBox(10);
		card.setPadding(new Insets(20));
		card.setAlignment(Pos.CENTER);
		card.setPrefSize(180, 140);
		card.setStyle("-fx-background-color: " + color + "; " + "-fx-background-radius: 15; "
				+ "-fx-border-radius: 15; " + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

		Label titleLabel = new Label(title);
		titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		titleLabel.setStyle("-fx-text-fill: white; -fx-text-alignment: center;");
		titleLabel.setWrapText(true);

		Label valueLabel = new Label(value);
		valueLabel.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 28));
		valueLabel.setStyle("-fx-text-fill: white;");

		card.getChildren().addAll(titleLabel, valueLabel);
		return card;
	}

	private VBox createSidebar() {

		VBox sidebar = new VBox();
		sidebar.setPrefWidth(250);
		sidebar.setStyle(
				"-fx-background-color: white; " + "-fx-border-color: #FFB6C1; " + "-fx-border-width: 0 2 0 0;");

		Label menuTitle = new Label("MAIN MENU");
		menuTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
		menuTitle.setStyle("-fx-text-fill: #DB7093; -fx-padding: 20 15 10 15;");

		Button productsBtn = createMenuButton("Products Management", true);
		Button categoriesBtn = createMenuButton("Categories Management", false);
		Button suppliersBtn = createMenuButton("Suppliers Management", false);
		Button staffBtn = createMenuButton("Staff Management", false);
		Button warehousesBtn = createMenuButton("Warehouses Management", false);
		Button inventoryBtn = createMenuButton("Inventory Management", false);
		Button ordersBtn = createMenuButton("Orders", false);
		Button archiveBtn = createMenuButton("Archive", false);
		Button returnsBtn = createMenuButton("Returns", false);

		Region spacer = new Region();
		VBox.setVgrow(spacer, Priority.ALWAYS);

		Button logoutBtn = createMenuButton("Logout", false);
		logoutBtn.setStyle(logoutBtn.getStyle() + " -fx-text-fill: #FF4500;");

		sidebar.getChildren().addAll(menuTitle, productsBtn, categoriesBtn, suppliersBtn, staffBtn, warehousesBtn,
				inventoryBtn, ordersBtn, archiveBtn, returnsBtn, spacer, logoutBtn);

		productsBtn.setOnAction(e -> showProductsManagement());
		categoriesBtn.setOnAction(e -> showCategoriesManagement());
		suppliersBtn.setOnAction(e -> showSuppliersManagement());
		staffBtn.setOnAction(e -> showStaffManagement());
		warehousesBtn.setOnAction(e -> showWarehousesManagement());
		inventoryBtn.setOnAction(e -> showInventoryManagement());
		ordersBtn.setOnAction(e -> showOrdersManagement());
		archiveBtn.setOnAction(e -> showArchive());
		returnsBtn.setOnAction(e -> showReturns());
		logoutBtn.setOnAction(e -> logout());

		return sidebar;
	}

	private Button createMenuButton(String text, boolean isActive) {
		Button button = new Button(text);
		button.setMaxWidth(Double.MAX_VALUE);
		button.setAlignment(Pos.CENTER_LEFT);
		button.setStyle("-fx-background-color: " + (isActive ? "#FFE4E1" : "transparent") + "; " + "-fx-text-fill: "
				+ (isActive ? "#C71585" : "#8B4513") + "; " + "-fx-font-size: 14px; " + "-fx-font-weight: bold; "
				+ "-fx-padding: 15 20; " + "-fx-border-color: transparent; " + "-fx-border-width: 0 0 0 4; "
				+ "-fx-cursor: hand;");

		if (isActive) {
			activeButton = button;
			button.setStyle(button.getStyle() + " -fx-border-color: #FF69B4; -fx-background-color: #FFE4E1;");
		}

		button.setOnMouseEntered(e -> {
			if (button != activeButton) {
				button.setStyle("-fx-background-color: #FFF0F5; " + "-fx-text-fill: #C71585; " + "-fx-font-size: 14px; "
						+ "-fx-font-weight: bold; " + "-fx-padding: 15 20; " + "-fx-border-color: #FFB6C1; "
						+ "-fx-border-width: 0 0 0 4; " + "-fx-cursor: hand;");
			}
		});

		button.setOnMouseExited(e -> {
			if (button != activeButton) {
				button.setStyle("-fx-background-color: transparent; " + "-fx-text-fill: #8B4513; "
						+ "-fx-font-size: 14px; " + "-fx-font-weight: bold; " + "-fx-padding: 15 20; "
						+ "-fx-border-color: transparent; " + "-fx-border-width: 0 0 0 4; " + "-fx-cursor: hand;");
			}
		});

		button.setOnAction(e -> {

			if (activeButton != null) {
				activeButton.setStyle(activeButton.getStyle().replace("-fx-border-color: #FF69B4;",
						"-fx-border-color: transparent;"));
				activeButton.setStyle(activeButton.getStyle().replace("-fx-background-color: #FFE4E1;",
						"-fx-background-color: transparent;"));
				activeButton.setStyle(
						activeButton.getStyle().replace("-fx-text-fill: #C71585;", "-fx-text-fill: #8B4513;"));
			}

			button.setStyle(button.getStyle()
					+ " -fx-border-color: #FF69B4; -fx-background-color: #FFE4E1; -fx-text-fill: #C71585;");
			activeButton = button;
		});

		return button;
	}

	private HBox createFooter() {
		HBox footer = new HBox();
		footer.setPadding(new Insets(15));
		footer.setStyle(
				"-fx-background-color: #FFE4E1; " + "-fx-border-color: #FFB6C1; " + "-fx-border-width: 2 0 0 0;");
		footer.setAlignment(Pos.CENTER);

		Label footerLabel = new Label("2026 ASAL BeautyCare System");
		footerLabel.setFont(Font.font("Arial", 12));
		footerLabel.setStyle("-fx-text-fill: #696969;");

		footer.getChildren().add(footerLabel);
		return footer;
	}

	private void showProductsManagement() {
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setFitToWidth(true);
		scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

		ProductsManagement products = new ProductsManagement(primaryStage);

		BorderPane productsContainer = new BorderPane();
		productsContainer.setStyle("-fx-background-color: #FFFAFA;");

		products.show();

		centerContent.getChildren().setAll(productsContainer);
	}

	private void showCategoriesManagement() {
		ManageCategories categoriesManager = new ManageCategories(primaryStage);
		categoriesManager.show();
	}

	private void showSuppliersManagement() {
		new SupplierManagement(root, primaryStage).show();
	}

	private void showStaffManagement() {
		new StaffManagement(root, primaryStage).show();
	}

	private void showWarehousesManagement() {

		new WarehouseScreen(primaryStage).show();
	}

	private void showInventoryManagement() {
		new InventoryScreen(primaryStage).show();
	}

	private void showOrdersManagement() {

		new OrderManagement(root, primaryStage).show();
	}

	private void showArchive() {
		new ArchiveManagement(root, primaryStage).show();
	}

	private void showReturns() {
		new ReturnsManagement(root, primaryStage).show();
	}

	private VBox createComingSoonPanel(String titleText) {
		VBox content = new VBox(20);
		content.setAlignment(Pos.CENTER);
		content.setPadding(new Insets(40));

		Label title = new Label(titleText);
		title.setFont(Font.font("Arial Rounded MT Bold", FontWeight.BOLD, 28));
		title.setStyle("-fx-text-fill: #DB7093;");

		Label message = new Label("This feature is coming soon!");
		message.setFont(Font.font("Arial", 18));
		message.setStyle("-fx-text-fill: #8B4513;");

		content.getChildren().addAll(title, message);
		return content;
	}

	private void logout() {
		try {
			new LoginScene(primaryStage).showLoginScene();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}