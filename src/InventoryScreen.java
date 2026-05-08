
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Optional;

public class InventoryScreen {

	private TableView<Inventory> table;
	private ObservableList<Inventory> data;
	private Stage primaryStage;
	private BorderPane root;
	private VBox rightside;

	private TextField searchField;
	private ComboBox<String> searchTypeCombo;
	private ProgressBar progressBar;
	private Label loadingLabel;
	private Label statsLabel;

	public InventoryScreen(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void show() {

		root = new BorderPane();

		try {
			root.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		} catch (Exception e) {
			AlertManager.showError("CSS file not found using default styling .");
		}

		root.getStyleClass().add("root-pane");

		VBox leftSide = new VBox(15);
		leftSide.setPadding(new Insets(25));
		leftSide.setPrefWidth(250);
		leftSide.setStyle("-fx-background-color: linear-gradient(to bottom, #FFE4E1, #FFF0F5);");
		leftSide.getStyleClass().add("sidebar");

		Label title = new Label("Inventory Operations");
		title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #C71585; -fx-padding: 0 0 15 0;");

		Button addButton = createSideButton("Add Stock", "#FF69B4");
		addButton.setOnAction(e -> showAddScreen());

		Button editButton = createSideButton("Update Stock", "#FF69B4");
		editButton.setOnAction(e -> {
			Inventory inventory = table.getSelectionModel().getSelectedItem();
			if (inventory == null) {
				AlertManager.showError("Please select an inventory item to update");
				return;
			}
			showUpdateScreen(inventory);
		});

		Button deleteButton = createSideButton("Remove Stock", "#FF69B4");
		deleteButton.setOnAction(e -> {
			Inventory inventory = table.getSelectionModel().getSelectedItem();
			if (inventory == null) {
				AlertManager.showError("Please select an inventory item to remove");
				return;
			}

			if (AlertManager.showConfirmation("Are you sure you want to remove this stock record?")) {
				boolean success = DataBaseInventory.deleteStock(inventory.getWarehouse().getWarehouseId(),
						inventory.getProduct().getProduct_id());
				if (success) {
					refreshTable();
					AlertManager.showInformationMessage("Stock record removed successfully");
				} else {
					AlertManager.showError("Failed to remove stock record");
				}
			}
		});

		Button refreshButton = createSideButton("Refresh", "#FFB6C1");
		refreshButton.setOnAction(e -> refreshTable());

		Separator separator = new Separator();
		separator.setPadding(new Insets(15, 0, 15, 0));

		Label quickLabel = new Label("Quick Filters");
		quickLabel
				.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #DB7093; -fx-padding: 0 0 10 0;");

		Button lowStockBtn = createSideButton("Low Stock", "#FFB6C1");
		lowStockBtn.setOnAction(e -> filterLowStock());

		Button warehouseBtn = createSideButton("By Warehouse", "#FFB6C1");
		warehouseBtn.setOnAction(e -> filterByWarehouse());

		Button backButton = createSideButton("Back", "#DB7093");
		backButton.setOnAction(e -> {
			try {
				AdminScreen adminScreen = new AdminScreen(primaryStage);
				adminScreen.show();
			} catch (Exception ex) {
				ex.printStackTrace();
				AlertManager.showError("Error returning to Admin Screen: " + ex.getMessage());
			}
		});

		leftSide.getChildren().addAll(title, addButton, editButton, deleteButton, refreshButton, separator, quickLabel,
				lowStockBtn, warehouseBtn, backButton);

		rightside = new VBox(20);
		rightside.setPadding(new Insets(25));
		rightside.getStyleClass().add("center-content");

		Label titleLabel = new Label("Stock Inventory");
		titleLabel.getStyleClass().add("welcome-title");

		HBox searchBox = new HBox(15);
		searchBox.setAlignment(Pos.CENTER_LEFT);
		searchBox.setPadding(new Insets(10, 0, 20, 0));
		searchBox.getStyleClass().add("search-box");

		searchTypeCombo = new ComboBox<>();
		searchTypeCombo.getItems().addAll("Product ID", "Warehouse ID", "Product Name");
		searchTypeCombo.setValue("Product ID");
		searchTypeCombo.setPrefWidth(140);
		searchTypeCombo.getStyleClass().add("search-combo");

		searchField = new TextField();
		searchField.setPromptText("Search stock...");
		searchField.setPrefWidth(300);
		searchField.getStyleClass().add("search-field");

		Button searchButton = createStyledButton("Search", "button-normal");
		Button showAllButton = createStyledButton("Show All", "button-normal");

		searchBox.getChildren().addAll(new Label("Search by:"), searchTypeCombo, searchField, searchButton,
				showAllButton);

		statsLabel = new Label();
		statsLabel.getStyleClass().add("product-count-label");

		loadingLabel = new Label();
		loadingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FF69B4; -fx-font-style: italic;");
		loadingLabel.setVisible(false);

		progressBar = new ProgressBar();
		progressBar.setPrefWidth(400);
		progressBar.setVisible(false);

		table = new TableView<>();
		table.getStyleClass().add("table-view");
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setPrefHeight(500);

		TableColumn<Inventory, String> id = new TableColumn<>("ID");
		id.setCellValueFactory(e -> {
			Inventory inv = e.getValue();
			return new SimpleStringProperty(
					"W" + inv.getWarehouse().getWarehouseId() + "-P" + inv.getProduct().getProduct_id());
		});
		id.setPrefWidth(120);
		id.getStyleClass().add("column-center");

		TableColumn<Inventory, Integer> productId = new TableColumn<>("Product ID");
		productId.setCellValueFactory(
				e -> new SimpleIntegerProperty(e.getValue().getProduct().getProduct_id()).asObject());
		productId.setPrefWidth(90);
		productId.getStyleClass().add("column-center");

		TableColumn<Inventory, String> productName = new TableColumn<>("Product Name");
		productName.setCellValueFactory(e -> {
			Product product = e.getValue().getProduct();
			return new SimpleStringProperty(product.getProduct_name() != null ? product.getProduct_name() : "N/A");
		});
		productName.setPrefWidth(200);

		TableColumn<Inventory, Integer> warehouseId = new TableColumn<>("Warehouse ID");
		warehouseId.setCellValueFactory(
				e -> new SimpleIntegerProperty(e.getValue().getWarehouse().getWarehouseId()).asObject());
		warehouseId.setPrefWidth(110);
		warehouseId.getStyleClass().add("column-center");

		TableColumn<Inventory, String> warehouseLocation = new TableColumn<>("Warehouse Location");
		warehouseLocation.setCellValueFactory(e -> {
			Warehouse warehouse = e.getValue().getWarehouse();
			return new SimpleStringProperty(warehouse.getLocation() != null ? warehouse.getLocation() : "N/A");
		});
		warehouseLocation.setPrefWidth(150);

		TableColumn<Inventory, Integer> quantity = new TableColumn<>("Quantity");
		quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		quantity.setPrefWidth(90);
		quantity.getStyleClass().add("column-center");
		quantity.setCellFactory(e -> new TableCell<Inventory, Integer>() {
			@Override
			protected void updateItem(Integer qty, boolean empty) {
				super.updateItem(qty, empty);
				if (empty || qty == null) {
					setText(null);
					setStyle("");
				} else {
					setText(qty.toString());
					if (qty == 0) {
						setStyle("-fx-text-fill: #FF0000; -fx-font-weight: bold;");
					} else if (qty < 10) {
						setStyle("-fx-text-fill: #FFA500; -fx-font-weight: bold;");
					} else if (qty < 20) {
						setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
					} else {
						setStyle("-fx-text-fill: #008000; -fx-font-weight: bold;");
					}
				}
			}
		});

		TableColumn<Inventory, String> status = new TableColumn<>("Status");
		status.setCellValueFactory(e -> {
			int qty = e.getValue().getQuantity();
			if (qty == 0)
				return new SimpleStringProperty("Out of Stock");
			if (qty < 10)
				return new SimpleStringProperty("Low Stock");
			if (qty < 20)
				return new SimpleStringProperty("Moderate");
			return new SimpleStringProperty("In Stock");
		});

		status.setPrefWidth(100);
		status.setCellFactory(e -> new TableCell<Inventory, String>() {
			@Override
			protected void updateItem(String status, boolean empty) {
				super.updateItem(status, empty);
				if (empty || status == null) {
					setText(null);
					setStyle("");
				} else {
					setText(status);
					switch (status) {
					case "Out of Stock":
						setStyle("-fx-text-fill: #FF0000; -fx-font-weight: bold;");
						break;
					case "Low Stock":
						setStyle("-fx-text-fill: #FFA500; -fx-font-weight: bold;");
						break;
					case "Moderate":
						setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
						break;
					default:
						setStyle("-fx-text-fill: #008000; -fx-font-weight: bold;");
					}
				}
			}
		});

		table.getColumns().addAll(id, productId, productName, warehouseId, warehouseLocation, quantity, status);

		data = FXCollections.observableArrayList();
		table.setItems(data);

		HBox bottomButtons = new HBox(15);
		bottomButtons.setAlignment(Pos.CENTER);
		bottomButtons.setPadding(new Insets(15, 0, 0, 0));

		rightside.getChildren().addAll(titleLabel, searchBox, statsLabel, loadingLabel, progressBar, table,
				bottomButtons);

		root.setLeft(leftSide);
		root.setCenter(rightside);

		loadDataAsync();

		searchButton.setOnAction(e -> performSearch());
		showAllButton.setOnAction(e -> {
			searchField.clear();
			refreshTable();
		});
		searchField.setOnAction(e -> performSearch());

		Scene scene = new Scene(root, 1400, 800);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Stock Inventory Management");
		primaryStage.show();
	}

	private void loadDataAsync() {
		loadingLabel.setText("Loading inventory......");
		loadingLabel.setVisible(true);
		progressBar.setVisible(true);
		progressBar.setProgress(-1);

		new Thread(() -> {
			ObservableList<Inventory> allInventory = DataBaseInventory.loadInventory();
			String stats = DataBaseInventory.getInventoryStatistics();

			Platform.runLater(() -> {
				data.clear();
				data.addAll(allInventory);
				statsLabel.setText(stats);
				loadingLabel.setVisible(false);
				progressBar.setVisible(false);
			});
		}).start();
	}

	private void refreshTable() {
		root.setCenter(rightside);

		loadingLabel.setText("Refreshing data....");
		loadingLabel.setVisible(true);
		progressBar.setVisible(true);
		progressBar.setProgress(-1);

		new Thread(() -> {

			ObservableList<Inventory> allInventory = DataBaseInventory.loadInventory();
			String stats = DataBaseInventory.getInventoryStatistics();

			Platform.runLater(() -> {
				data.clear();
				data.addAll(allInventory);
				statsLabel.setText(stats);
				loadingLabel.setVisible(false);
				progressBar.setVisible(false);
			});
		}).start();
	}

	private void performSearch() {

		String keyword = searchField.getText().trim();
		String searchType = searchTypeCombo.getValue();

		if (keyword.isEmpty()) {
			refreshTable();
			return;
		}

		loadingLabel.setText("Searching inventory...");
		loadingLabel.setVisible(true);
		progressBar.setVisible(true);
		progressBar.setProgress(-1);

		new Thread(() -> {
			ObservableList<Inventory> searchResults;

			if ("Product ID".equals(searchType)) {
				try {
					int productId = Integer.parseInt(keyword);
					searchResults = DataBaseInventory.searchByProductId(productId);
				} catch (NumberFormatException e) {
					Platform.runLater(() -> {
						AlertManager.showError("Invalid Product ID\nPlease enter a valid numeric ID");
						loadingLabel.setVisible(false);
						progressBar.setVisible(false);
					});
					return;
				}
			} else if ("Warehouse ID".equals(searchType)) {
				try {
					int warehouseId = Integer.parseInt(keyword);
					searchResults = DataBaseInventory.searchByWarehouseId(warehouseId);
				} catch (NumberFormatException e) {
					Platform.runLater(() -> {
						AlertManager.showError("Invalid Warehouse ID\nPlease enter a valid numeric ID");
						loadingLabel.setVisible(false);
						progressBar.setVisible(false);
					});
					return;
				}
			} else {
				searchResults = DataBaseInventory.searchByProductName(keyword);
			}

			Platform.runLater(() -> {
				data.clear();
				data.addAll(searchResults);
				statsLabel.setText("Search Results : " + searchResults.size() + " items found");
				loadingLabel.setVisible(false);
				progressBar.setVisible(false);

				if (searchResults.isEmpty()) {
					AlertManager.showInformationMessage("No inventory found for : " + keyword);
				}
			});
		}).start();
	}

	private void showAddScreen() {

		Stage stage = new Stage();
		stage.setTitle("Add New Stock");
		stage.initOwner(primaryStage);

		VBox vBoxCenter = new VBox(20);
		vBoxCenter.setPadding(new Insets(30));
		vBoxCenter.setStyle("-fx-background-color: #FFF0F5; -fx-background-radius: 10;");
		vBoxCenter.setAlignment(Pos.CENTER);

		Label title = new Label("Add New Stock");
		title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #C71585;");

		GridPane grid = new GridPane();
		grid.setHgap(15);
		grid.setVgap(15);
		grid.setAlignment(Pos.CENTER);

		ObservableList<Product> products = DataBaseInventory.loadProducts();
		ObservableList<Warehouse> warehouses = DataBaseInventory.loadWarehouses();

		ComboBox<Product> productCombo = new ComboBox<>(products);
		productCombo.setPromptText("Select Product");
		productCombo.setPrefWidth(300);
		productCombo.setCellFactory(lv -> new ListCell<Product>() {
			@Override
			protected void updateItem(Product item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getProduct_id() + " - " + item.getProduct_name());
			}
		});
		productCombo.setButtonCell(new ListCell<Product>() {
			@Override
			protected void updateItem(Product item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getProduct_id() + " - " + item.getProduct_name());
			}
		});

		ComboBox<Warehouse> warehouseCombo = new ComboBox<>(warehouses);
		warehouseCombo.setPromptText("Select Warehouse");
		warehouseCombo.setPrefWidth(300);
		warehouseCombo.setCellFactory(lv -> new ListCell<Warehouse>() {
			@Override
			protected void updateItem(Warehouse item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : "ID: " + item.getWarehouseId() + " - " + item.getLocation());
			}
		});
		warehouseCombo.setButtonCell(new ListCell<Warehouse>() {
			@Override
			protected void updateItem(Warehouse item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : "ID: " + item.getWarehouseId() + " - " + item.getLocation());
			}
		});

		TextField quantityField = new TextField();
		quantityField.setPromptText("Enter Quantity");
		quantityField.setPrefWidth(300);

		Label productLabel = new Label("Product:");
		productLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #DB7093;");

		Label warehouseLabel = new Label("Warehouse:");
		warehouseLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #DB7093;");

		Label quantityLabel = new Label("Quantity:");
		quantityLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #DB7093;");

		grid.add(productLabel, 0, 0);
		grid.add(productCombo, 1, 0);
		grid.add(warehouseLabel, 0, 1);
		grid.add(warehouseCombo, 1, 1);
		grid.add(quantityLabel, 0, 2);
		grid.add(quantityField, 1, 2);

		HBox buttonBox = new HBox(20);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20, 0, 0, 0));

		Button saveBtn = new Button("Save");
		saveBtn.setStyle("-fx-background-color: #FF69B4; -fx-text-fill: white; -fx-font-weight: bold; "
				+ "-fx-font-size: 14px; -fx-padding: 10 25; -fx-background-radius: 5;");
		saveBtn.setOnAction(e -> {
			try {
				if (productCombo.getValue() == null || warehouseCombo.getValue() == null
						|| quantityField.getText().isEmpty()) {
					AlertManager.showError("Please fill all fields");
					return;
				}

				int pid = productCombo.getValue().getProduct_id();
				int wid = warehouseCombo.getValue().getWarehouseId();
				int qty = Integer.parseInt(quantityField.getText());

				if (qty < 0) {
					AlertManager.showError("Quantity cannot be negative ! ");
					return;
				}

				if (!DataBaseInventory.productExists(pid)) {
					AlertManager.showError("Product does not exist !");
					return;
				}

				if (!DataBaseInventory.warehouseExists(wid)) {
					AlertManager.showError("Warehouse does not exist ! ");
					return;
				}

				boolean success = DataBaseInventory.addStock(wid, pid, qty);
				if (success) {
					AlertManager.showInformationMessage("Stock added successfully");
					stage.close();
					refreshTable();
				} else {
					AlertManager.showError("Failed to add stock");
				}
			} catch (NumberFormatException ex) {
				AlertManager.showError("Please enter a valid numeric quantity");
			}
		});

		Button cancelBtn = new Button("Cancel");
		cancelBtn.setStyle("-fx-background-color: #B0B0B0; -fx-text-fill: white; -fx-font-weight: bold; "
				+ "-fx-font-size: 14px; -fx-padding: 10 25; -fx-background-radius: 5;");
		cancelBtn.setOnAction(e -> stage.close());

		buttonBox.getChildren().addAll(saveBtn, cancelBtn);

		vBoxCenter.getChildren().addAll(title, grid, buttonBox);

		Scene scene = new Scene(vBoxCenter, 500, 400);
		stage.setScene(scene);
		stage.showAndWait();
	}

	private void showUpdateScreen(Inventory inventory) {

		Stage stage = new Stage();
		stage.setTitle("Update Stock");
		stage.initOwner(primaryStage);

		VBox vBox = new VBox(20);
		vBox.setPadding(new Insets(30));
		vBox.setStyle("-fx-background-color: #FFF0F5; -fx-background-radius: 10;");
		vBox.setAlignment(Pos.CENTER);

		Label title = new Label("Update Stock");
		title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #C71585;");

		GridPane grid = new GridPane();
		grid.setHgap(15);
		grid.setVgap(15);
		grid.setAlignment(Pos.CENTER);

		TextField productField = new TextField(
				inventory.getProduct().getProduct_id() + " - " + inventory.getProduct().getProduct_name());
		productField.setEditable(false);
		productField.setPrefWidth(300);
		productField.setStyle("-fx-background-color: #F5F5F5;");

		TextField warehouseField = new TextField(
				"ID: " + inventory.getWarehouse().getWarehouseId() + " - " + inventory.getWarehouse().getLocation());
		warehouseField.setEditable(false);
		warehouseField.setPrefWidth(300);
		warehouseField.setStyle("-fx-background-color: #F5F5F5;");

		Spinner<Integer> quantitySpinner = new Spinner<>(0, 10000, inventory.getQuantity(), 1);
		quantitySpinner.setPrefWidth(300);
		quantitySpinner.setEditable(true);

		Label productLabel = new Label("Product :");
		productLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #DB7093;");

		Label warehouseLabel = new Label("Warehouse: ");
		warehouseLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #DB7093;");

		Label quantityLabel = new Label("Quantity :");
		quantityLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #DB7093;");

		grid.add(productLabel, 0, 0);
		grid.add(productField, 1, 0);
		grid.add(warehouseLabel, 0, 1);
		grid.add(warehouseField, 1, 1);
		grid.add(quantityLabel, 0, 2);
		grid.add(quantitySpinner, 1, 2);

		Label currentStatus = new Label("Current Status: " + (inventory.getQuantity() == 0 ? "Out of Stock"
				: inventory.getQuantity() < 10 ? "Low Stock" : inventory.getQuantity() < 20 ? "Moderate" : "In Stock"));
		currentStatus.setStyle("-fx-font-style: italic; -fx-text-fill: #666;");

		HBox buttonBox = new HBox(20);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20, 0, 0, 0));

		Button saveBtn = new Button("Update");
		saveBtn.setStyle("-fx-background-color: #FF69B4; -fx-text-fill: white; -fx-font-weight: bold; "
				+ "-fx-font-size: 14px; -fx-padding: 10 25; -fx-background-radius: 5;");
		saveBtn.setOnAction(e -> {
			try {
				int qty = quantitySpinner.getValue();

				if (qty < 0) {
					AlertManager.showError("Quantity cannot be negative ");
					return;
				}

				boolean success = DataBaseInventory.updateStock(inventory.getWarehouse().getWarehouseId(),
						inventory.getProduct().getProduct_id(), qty);

				if (success) {
					AlertManager.showInformationMessage("Stock updated successfully");
					stage.close();
					refreshTable();
				} else {
					AlertManager.showError("Failed to update stock!");
				}
			} catch (Exception ex) {
				AlertManager.showError("Error updating stock: " + ex.getMessage());
			}
		});

		Button cancelBtn = new Button("Cancel");
		cancelBtn.setStyle("-fx-background-color: #B0B0B0; -fx-text-fill: white; -fx-font-weight: bold; "
				+ "-fx-font-size: 14px; -fx-padding: 10 25; -fx-background-radius: 5;");
		cancelBtn.setOnAction(e -> stage.close());

		buttonBox.getChildren().addAll(saveBtn, cancelBtn);

		vBox.getChildren().addAll(title, grid, currentStatus, buttonBox);

		Scene scene = new Scene(vBox, 500, 400);
		stage.setScene(scene);
		stage.showAndWait();
	}

	private Button createSideButton(String text, String color) {
		Button button = new Button(text);
		button.setMaxWidth(Double.MAX_VALUE);
		button.setPrefHeight(45);
		button.setStyle("-fx-background-color: " + color + "; " + "-fx-text-fill: white; " + "-fx-font-size: 14px; "
				+ "-fx-font-weight: bold; " + "-fx-background-radius: 10; " + "-fx-border-radius: 10; "
				+ "-fx-cursor: hand; " + "-fx-alignment: center-left; " + "-fx-padding: 10 15;");

		button.setOnMouseEntered(e -> {
			button.setStyle("-fx-background-color: derive(" + color + ", -20%); " + "-fx-text-fill: white; "
					+ "-fx-font-size: 14px; " + "-fx-font-weight: bold; " + "-fx-background-radius: 10; "
					+ "-fx-border-radius: 10; " + "-fx-cursor: hand; " + "-fx-alignment: center-left; "
					+ "-fx-padding: 10 15; " + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);");
		});

		button.setOnMouseExited(e -> {
			button.setStyle("-fx-background-color: " + color + "; " + "-fx-text-fill: white; " + "-fx-font-size: 14px; "
					+ "-fx-font-weight: bold; " + "-fx-background-radius: 10; " + "-fx-border-radius: 10; "
					+ "-fx-cursor: hand; " + "-fx-alignment: center-left; " + "-fx-padding: 10 15;");
		});

		return button;
	}

	private Button createStyledButton(String text, String styleClass) {
		Button button = new Button(text);
		button.getStyleClass().add(styleClass);
		button.setPrefSize(140, 40);
		return button;
	}

	private void filterLowStock() {

		loadingLabel.setText("Filtering low stock items...");
		loadingLabel.setVisible(true);
		progressBar.setVisible(true);
		progressBar.setProgress(-1);

		new Thread(() -> {
			ObservableList<Inventory> lowStock = DataBaseInventory.getLowStockItems(10);

			Platform.runLater(() -> {
				data.clear();
				data.addAll(lowStock);
				statsLabel.setText("Low Stock Items ( < 10): " + lowStock.size() + " items");
				loadingLabel.setVisible(false);
				progressBar.setVisible(false);
			});
		}).start();
	}

	private void filterByWarehouse() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Filter by Warehouse");
		dialog.setHeaderText("Enter Warehouse ID");
		dialog.setContentText("Warehouse ID : ");

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(warehouseId -> {
			loadingLabel.setText("Filtering by warehouse....");
			loadingLabel.setVisible(true);
			progressBar.setVisible(true);
			progressBar.setProgress(-1);

			new Thread(() -> {
				try {
					int id = Integer.parseInt(warehouseId);
					ObservableList<Inventory> warehouseItems = DataBaseInventory.searchByWarehouseId(id);

					Platform.runLater(() -> {
						data.clear();
						data.addAll(warehouseItems);
						statsLabel.setText("Warehouse " + id + " items : " + warehouseItems.size() + " items");
						loadingLabel.setVisible(false);
						progressBar.setVisible(false);
					});
				} catch (NumberFormatException e) {
					Platform.runLater(() -> {
						AlertManager.showError("Invalid Warehouse ID");
						loadingLabel.setVisible(false);
						progressBar.setVisible(false);
					});
				}
			}).start();
		});
	}

	
}
