
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Cursor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WarehouseScreen {

	private Stage primaryStage;
	private BorderPane root;
	private ProgressBar progressBar;
	private Label loadingLabel;
	private int currentWarehouseId = 1;
	private WarehouseDB warehouseDB;
	private VBox warehouseInfo;
	private HBox statsCards;
	private HBox warehousesBox;
	private StackPane contentStack;
	private Button prevWarehouseBtn;
	private Button nextWarehouseBtn;
	private ArrayList<Warehouse> allWarehouses;
	private Label currentWarehouseLabel;

	public WarehouseScreen(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.warehouseDB = new WarehouseDB();
	}

	public void show() {

		root = new BorderPane();

		try {
			root.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		} catch (Exception e) {
			AlertManager.showError("CSS file not found using default styling");
		}

		root.getStyleClass().add("root-pane");

		VBox leftPanel = createLeftSide();
		VBox rightContent = createRightSide();

		root.setLeft(leftPanel);
		root.setCenter(rightContent);

		loadAllWarehouses();

		Scene scene = new Scene(root, 1400, 800);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Warehouse Management");
		primaryStage.show();
	}

	private VBox createLeftSide() {

		VBox leftPanel = new VBox(15);
		leftPanel.setPadding(new Insets(25));
		leftPanel.setPrefWidth(250);
		leftPanel.setStyle("-fx-background-color: linear-gradient(to bottom, #FFE4E1, #FFF0F5);");
		leftPanel.getStyleClass().add("sidebar");

		Label panelTitle = new Label("Warehouse Operations");
		panelTitle
				.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #C71585; -fx-padding: 0 0 15 0;");

		Button addButton = createSideButton("Add Warehouse", "#FF69B4");
		addButton.setOnAction(e -> showAddScreenInCenter());

		Button editButton = createSideButton("Edit Warehouse", "#FF69B4");
		editButton.setOnAction(e -> showEditScreenInCenter());

		Button inventoryBtn = createSideButton("Manage Inventory", "#FF69B4");
		inventoryBtn.setOnAction(e -> {
			InventoryScreen inventoryScreen = new InventoryScreen(primaryStage);
			inventoryScreen.show();
		});

		Button viewAllBtn = createSideButton("View All Warehouses", "#FFB6C1");
		viewAllBtn.setOnAction(e -> showAllWarehouses());

		Button refreshButton = createSideButton("Refresh", "#FFB6C1");
		refreshButton.setOnAction(e -> refreshData());

		Separator separator = new Separator();
		separator.setPadding(new Insets(15, 0, 15, 0));

		Label quickLabel = new Label("Quick Views");
		quickLabel
				.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #DB7093; -fx-padding: 0 0 10 0;");

		Button backButton = createSideButton("Back", "#DB7093");
		backButton.setOnAction(e -> {
			try {
				AdminScreen adminScreen = new AdminScreen(primaryStage);
				adminScreen.show();
			} catch (Exception ex) {
				ex.printStackTrace();
				AlertManager.showError("Error returning to admin screen : " + ex.getMessage());
			}
		});

		leftPanel.getChildren().addAll(panelTitle, addButton, editButton, inventoryBtn, viewAllBtn, refreshButton,
				separator, quickLabel, backButton);

		return leftPanel;
	}

	private VBox createRightSide() {

		VBox rightContent = new VBox(15);
		rightContent.setPadding(new Insets(25));
		rightContent.getStyleClass().add("center-content");

		HBox headerBox = new HBox(20);
		headerBox.setAlignment(Pos.CENTER_LEFT);

		Label titleLabel = new Label("Warehouse Management");
		titleLabel.getStyleClass().add("welcome-title");
		titleLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: #C71585; -fx-font-weight: bold;");

		headerBox.getChildren().add(titleLabel);

		warehousesBox = new HBox(10);
		warehousesBox.setAlignment(Pos.CENTER);
		warehousesBox.setPadding(new Insets(10, 0, 20, 0));
		warehousesBox
				.setStyle("-fx-border-color: #FFE4E1; -fx-border-radius: 10; -fx-border-width: 1; -fx-padding: 10;");

		prevWarehouseBtn = new Button("Previous");
		prevWarehouseBtn.setStyle("-fx-background-color: #FFB6C1; -fx-text-fill: #C71585; "
				+ "-fx-font-weight: bold; -fx-padding: 5 15; -fx-background-radius: 5;");
		prevWarehouseBtn.setCursor(Cursor.HAND);
		prevWarehouseBtn.setOnAction(e -> toPreviousWarehouse());

		nextWarehouseBtn = new Button("Next");
		nextWarehouseBtn.setStyle("-fx-background-color: #FFB6C1; -fx-text-fill: #C71585; "
				+ "-fx-font-weight: bold; -fx-padding: 5 15; -fx-background-radius: 5;");
		nextWarehouseBtn.setCursor(Cursor.HAND);
		nextWarehouseBtn.setOnAction(e -> toNextWarehouse());

		loadWarehousesNavigation();

		loadingLabel = new Label("Loading warehouse data...");
		loadingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FF69B4; -fx-font-style: italic;");
		loadingLabel.setVisible(true);

		progressBar = new ProgressBar();
		progressBar.setPrefWidth(400);
		progressBar.setVisible(true);
		progressBar.setProgress(-1);

		warehouseInfo = new VBox(15);
		warehouseInfo.setPadding(new Insets(20));
		warehouseInfo.getStyleClass().add("add-edit-box");
		warehouseInfo.setStyle("-fx-background-color: white; -fx-background-radius: 15; "
				+ "-fx-border-color: #FFE4E1; -fx-border-radius: 15; -fx-border-width: 2; "
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");
		warehouseInfo.setVisible(false);

		VBox statsContainer = new VBox(10);
		statsContainer.setAlignment(Pos.TOP_CENTER);
		statsContainer.setPadding(new Insets(10, 0, 0, 0));

		Label statsTitle = new Label("Warehouse Statistics");
		statsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #C71585;");

		statsCards = new HBox(20);
		statsCards.setAlignment(Pos.CENTER);
		statsCards.setVisible(false);

		statsContainer.getChildren().addAll(statsTitle, statsCards);

		contentStack = new StackPane();
		contentStack.setPadding(new Insets(20, 0, 0, 0));

		VBox mainViewContainer = new VBox(20);
		mainViewContainer.getChildren().addAll(warehouseInfo, statsContainer);

		contentStack.getChildren().add(mainViewContainer);

		rightContent.getChildren().addAll(headerBox, warehousesBox, loadingLabel, progressBar, contentStack);

		return rightContent;
	}

	private void loadAllWarehouses() {

		new Thread(() -> {
			try {
				allWarehouses = warehouseDB.getAllWarehouses();
				Platform.runLater(() -> {
					loadWarehouseData(currentWarehouseId);
					updateButtons();
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void loadWarehousesNavigation() {

		new Thread(() -> {
			try {

				allWarehouses = warehouseDB.getAllWarehouses();
				Platform.runLater(() -> {

					warehousesBox.getChildren().clear();

					HBox navContainer = new HBox(10);
					navContainer.setAlignment(Pos.CENTER);
					navContainer.setStyle("-fx-background-color: #FFF0F5; -fx-background-radius: 10; -fx-padding: 8;");

					Button prevBtn = new Button("Previous");
					prevBtn.setStyle("-fx-background-color: #FFB6C1; -fx-text-fill: #C71585; "
							+ "-fx-font-weight: bold; -fx-padding: 5 15; -fx-background-radius: 5;");
					prevBtn.setCursor(Cursor.HAND);
					prevBtn.setOnAction(e -> toPreviousWarehouse());

					Label currentInfo = new Label();
					currentInfo.setStyle("-fx-font-weight: bold; -fx-text-fill: #C71585; -fx-font-size: 16px;");
					currentInfo.setPadding(new Insets(0, 20, 0, 20));
					currentInfo.setMinWidth(150);
					currentInfo.setAlignment(Pos.CENTER);

					Button nextBtn = new Button("Next");
					nextBtn.setStyle("-fx-background-color: #FFB6C1; -fx-text-fill: #C71585; "
							+ "-fx-font-weight: bold; -fx-padding: 5 15; -fx-background-radius: 5;");
					nextBtn.setCursor(Cursor.HAND);
					nextBtn.setOnAction(e -> toNextWarehouse());

					navContainer.getChildren().addAll(prevBtn, nextBtn, currentInfo);
					warehousesBox.getChildren().add(navContainer);

					this.prevWarehouseBtn = prevBtn;
					this.nextWarehouseBtn = nextBtn;
					this.currentWarehouseLabel = currentInfo;

					if (!allWarehouses.isEmpty()) {
						updateCurrentWarehouseDisplay();
					} else {
						currentInfo.setText("No warehouses");
						prevBtn.setDisable(true);
						nextBtn.setDisable(true);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void updateCurrentWarehouseDisplay() {

		if (allWarehouses == null || allWarehouses.isEmpty()) {
			currentWarehouseLabel.setText("No warehouses");
			return;
		}

		int currentIndex = getCurrentWarehouseIndex();
		if (currentIndex >= 0 && currentIndex < allWarehouses.size()) {

			Warehouse currentWarehouse = allWarehouses.get(currentIndex);
			currentWarehouseLabel.setText("Warehouse ID : " + currentWarehouse.getWarehouseId());

		} else {
			currentWarehouseLabel.setText("Not found");
		}

		updateButtons();
	}

	private void updateButtons() {

		if (allWarehouses == null || allWarehouses.isEmpty()) {
			prevWarehouseBtn.setDisable(true);
			nextWarehouseBtn.setDisable(true);
			return;
		}

		int currentIndex = getCurrentWarehouseIndex();
		prevWarehouseBtn.setDisable(currentIndex <= 0);
		nextWarehouseBtn.setDisable(currentIndex >= allWarehouses.size() - 1);
	}

	private int getCurrentWarehouseIndex() {

		if (allWarehouses == null) {
			return -1;
		}

		for (int i = 0; i < allWarehouses.size(); i++) {
			if (allWarehouses.get(i).getWarehouseId() == currentWarehouseId) {
				return i;
			}
		}
		return -1;
	}

	private void toPreviousWarehouse() {

		int currentIndex = getCurrentWarehouseIndex();
		if (currentIndex > 0) {

			currentWarehouseId = allWarehouses.get(currentIndex - 1).getWarehouseId();

			Platform.runLater(() -> {
				updateCurrentWarehouseDisplay();
				loadWarehouseData(currentWarehouseId);
			});
		}
	}

	private void toNextWarehouse() {

		int currentIndex = getCurrentWarehouseIndex();
		if (currentIndex < allWarehouses.size() - 1) {
			currentWarehouseId = allWarehouses.get(currentIndex + 1).getWarehouseId();

			Platform.runLater(() -> {
				updateCurrentWarehouseDisplay();
				loadWarehouseData(currentWarehouseId);
			});
		}
	}

	private void loadWarehouseData(int warehouseId) {

		new Thread(() -> {
			try {
				Warehouse warehouse = warehouseDB.getWarehouse(warehouseId);

				int totalProducts = WarehouseDB.getProductCount(warehouseId);
				int totalItems = getTotalItems();
				int capacity = warehouse != null ? warehouse.getCapacity() : 0;
				int capacityUsed = totalItems;
				int capacityAvailable = capacity - capacityUsed;
				double capacityPercentage = capacity > 0 ? (capacityUsed * 100.0 / capacity) : 0;

				Platform.runLater(() -> {
					loadingLabel.setVisible(false);
					progressBar.setVisible(false);

					if (warehouse != null) {
						showWarehouseDetails(warehouse, totalProducts, totalItems, capacityUsed, capacityAvailable,
								capacityPercentage);
					} else {
						showNoWarehouseMessage();
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
				Platform.runLater(() -> {
					loadingLabel.setText("Error loading data!");
					loadingLabel.setStyle("-fx-text-fill: #FF0000;");
				});
			}
		}).start();
	}

	private void showWarehouseDetails(Warehouse warehouse, int totalProducts, int totalItems, int capacityUsed,
			int capacityAvailable, double capacityPercentage) {

		warehouseInfo.getChildren().clear();

		Label infoTitle = new Label("Warehouse Information");
		infoTitle
				.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #C71585; -fx-padding: 0 0 10 0;");

		GridPane infoGrid = new GridPane();
		infoGrid.setHgap(12);
		infoGrid.setVgap(8);
		infoGrid.setPadding(new Insets(10, 0, 10, 0));

		infoGrid.add(createInfoLabel("ID:"), 0, 0);
		infoGrid.add(createInfoValue(String.valueOf(warehouse.getWarehouseId())), 1, 0);

		infoGrid.add(createInfoLabel("Location:"), 0, 1);
		infoGrid.add(createInfoValue(warehouse.getLocation()), 1, 1);

		infoGrid.add(createInfoLabel("Capacity:"), 0, 2);
		infoGrid.add(createInfoValue(warehouse.getCapacity() + " units"), 1, 2);

		infoGrid.add(createInfoLabel("Manager:"), 0, 3);
		infoGrid.add(createInfoValue(warehouse.getStockManager()), 1, 3);

		infoGrid.add(createInfoLabel("Store:"), 0, 4);
		infoGrid.add(createInfoValue(
            warehouse.getOnlineStore().getStoreId() + " - " + warehouse.getOnlineStore().getStoreName()), 1,
				4);

		infoGrid.getChildren().forEach(node -> {
			if (node instanceof Label) {
				Label label = (Label) node;
				String currentStyle = label.getStyle();
				if (currentStyle.contains("-fx-font-size:")) {
					currentStyle = currentStyle.replaceAll("-fx-font-size: \\d+px;", "-fx-font-size: 12px;");
				} else {
					currentStyle += " -fx-font-size: 12px;";
				}
				label.setStyle(currentStyle);
			}
		});

		HBox quickActions = new HBox(10);
		quickActions.setAlignment(Pos.CENTER);
		quickActions.setPadding(new Insets(10, 0, 0, 0));

		Button editBtn = createQuickActionButton("Edit", "#FF69B4");
		Button deleteBtn = createQuickActionButton("Delete", "#FF4500");

		editBtn.setStyle(editBtn.getStyle() + " -fx-font-size: 11px; -fx-padding: 4 8;");
		deleteBtn.setStyle(deleteBtn.getStyle() + " -fx-font-size: 11px; -fx-padding: 4 8;");

		editBtn.setOnAction(e -> showEditScreenInCenter(warehouse));
		deleteBtn.setOnAction(e -> deleteWarehouseWithConfirmation(warehouse));

		quickActions.getChildren().addAll(editBtn, deleteBtn);

		warehouseInfo.getChildren().addAll(infoTitle, infoGrid, quickActions);
		warehouseInfo.setPadding(new Insets(15));

		updateStatsCards(totalProducts, totalItems, capacityUsed, capacityAvailable, capacityPercentage);

		warehouseInfo.setVisible(true);
		statsCards.setVisible(true);

	}

	private void updateStatsCards(int totalProducts, int totalItems, int capacityUsed, int capacityAvailable,
			double capacityPercentage) {
		statsCards.getChildren().clear();

		VBox productsCard = createStatCard("Products", String.valueOf(totalProducts), "#FF69B4",
				"Total products in inventory", 180);

		VBox inventoryCard = createStatCard("Inventory", String.valueOf(totalItems), "#DB7093", "Total items in stock",
				180);

		VBox capacityCard = createStatCard("Capacity", String.format("%.1f%%", capacityPercentage), "#FF1493",
				capacityUsed + "/" + (capacityUsed + capacityAvailable) + " units used", 180);

		VBox spaceCard = createStatCard("Available", String.valueOf(capacityAvailable), "#C71585",
				"Available capacity units", 180);

		statsCards.getChildren().addAll(productsCard, inventoryCard, capacityCard, spaceCard);
	}

	private void showNoWarehouseMessage() {

		warehouseInfo.getChildren().clear();

		Label noDataLabel = new Label("No warehouse data available");
		noDataLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #888; -fx-font-style: italic;");

		Button addFirstBtn = new Button("Add First Warehouse");
		addFirstBtn.setStyle("-fx-background-color: #FF69B4; -fx-text-fill: white; "
				+ "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 10;");
		addFirstBtn.setOnAction(e -> showAddScreenInCenter());

		VBox noDataBox = new VBox(20);
		noDataBox.setAlignment(Pos.CENTER);
		noDataBox.getChildren().addAll(noDataLabel, addFirstBtn);

		warehouseInfo.getChildren().add(noDataBox);
		warehouseInfo.setVisible(true);
		statsCards.setVisible(false);
	}

	private void showWarehouseView() {

		contentStack.getChildren().clear();

		VBox mainViewContainer = new VBox(20);
		mainViewContainer.getChildren().addAll(warehouseInfo, createStatsContainer());
		contentStack.getChildren().add(mainViewContainer);

		warehouseInfo.setVisible(true);
		statsCards.setVisible(true);

		loadWarehouseData(currentWarehouseId);
		updateButtons();
	}

	private VBox createStatsContainer() {

		VBox statsContainer = new VBox(20);
		statsContainer.setAlignment(Pos.TOP_CENTER);
		statsContainer.setPadding(new Insets(20, 0, 0, 0));

		Label statsTitle = new Label("Warehouse Statistics");
		statsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #C71585;");

		statsContainer.getChildren().addAll(statsTitle, statsCards);
		return statsContainer;
	}

	private void showAddScreenInCenter() {

		showFormScreenInCenter(null, "Add New Warehouse");
	}

	private void showEditScreenInCenter() {
		Warehouse warehouse = warehouseDB.getWarehouse(currentWarehouseId);
		if (warehouse != null) {
			showFormScreenInCenter(warehouse, "Edit Warehouse");
		} else {
			AlertManager.showError("No warehouse found to edit");
		}
	}

	private void showEditScreenInCenter(Warehouse warehouse) {
		showFormScreenInCenter(warehouse, "Edit Warehouse");
	}

	private void showFormScreenInCenter(Warehouse warehouse, String title) {

		warehouseInfo.setVisible(false);
		statsCards.setVisible(false);

		VBox formContainer = createWarehouseForm(warehouse, title);
		contentStack.getChildren().clear();
		contentStack.getChildren().add(formContainer);
	}
	
	
	private VBox createWarehouseForm(Warehouse warehouse, String title) {
	
		Label formTitle = new Label(title);
		formTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #C71585;");

		GridPane formGrid = new GridPane();
		formGrid.setHgap(15);
		formGrid.setVgap(15);
		formGrid.setPadding(new Insets(20, 0, 20, 0));

		TextField locationField = createFormTextField();
		locationField.setPromptText("Enter location");

		TextField capacityField = createFormTextField();
		capacityField.setPromptText("Enter capacity (units)");

		TextField managerField = createFormTextField();
		managerField.setPromptText("Enter stock manager name");

		ComboBox<OnlineStore> storeComboBox = new ComboBox<>();
		storeComboBox.setPromptText("Select a store");
		storeComboBox.setPrefWidth(200);

		storeComboBox.setStyle(
				"-fx-background-color: white; -fx-border-color: #FFE4E1; " + "-fx-border-radius: 5; -fx-padding: 5;");

		ArrayList<OnlineStore> stores = warehouseDB.getAllStores();
		storeComboBox.getItems().addAll(stores);

		
		storeComboBox.setCellFactory(param -> new ListCell<OnlineStore>() {
			@Override
			protected void updateItem(OnlineStore store, boolean empty) {
				super.updateItem(store, empty);
				if (empty || store == null) {
					setText(null);
				} else {
					setText(store.getStoreId() + " - " + store.getStoreName());
				}
			}
		});

		storeComboBox.setButtonCell(new ListCell<OnlineStore>() {
			@Override
			protected void updateItem(OnlineStore store, boolean empty) {
				super.updateItem(store, empty);
				if (empty || store == null) {
					setText(null);
				} else {
					setText(store.getStoreId() + " - " + store.getStoreName());
				}
			}
		});

		if (warehouse != null) {
			locationField.setText(warehouse.getLocation());
			capacityField.setText(String.valueOf(warehouse.getCapacity()));
			managerField.setText(warehouse.getStockManager());

			for (OnlineStore store : stores) {
				if (store.getStoreId() == warehouse.getOnlineStore().getStoreId()) {
					storeComboBox.getSelectionModel().select(store);
					break;
				}
			}
		}

		formGrid.add(createFormLabel("Location:"), 0, 0);
		formGrid.add(locationField, 1, 0);
		formGrid.add(createFormLabel("Capacity:"), 0, 1);
		formGrid.add(capacityField, 1, 1);
		formGrid.add(createFormLabel("Manager:"), 0, 2);
		formGrid.add(managerField, 1, 2);
		formGrid.add(createFormLabel("Store:"), 0, 3);
		formGrid.add(storeComboBox, 1, 3);

		HBox buttonBox = new HBox(15);
		buttonBox.setAlignment(Pos.CENTER);

		Button cancelBtn = createFormButton("Cancel", "#DB7093");
		Button saveBtn = createFormButton(warehouse == null ? "Add" : "Save", "#FF69B4");

		if (warehouse != null) {
			Button deleteBtn = createFormButton("Delete", "#FF4500");
			deleteBtn.setOnAction(e -> {
				if (AlertManager.showConfirmation("Delete this warehouse ?")) {
					deleteWarehouseWithConfirmation(warehouse);
				}
			});
			buttonBox.getChildren().add(deleteBtn);
		}

		buttonBox.getChildren().addAll(cancelBtn, saveBtn);

		cancelBtn.setOnAction(e -> showWarehouseView());

		saveBtn.setOnAction(e -> {
		
			if (validateWarehouseForm(locationField, capacityField, managerField, storeComboBox)) {
				String location = locationField.getText().trim();
				int capacity = Integer.parseInt(capacityField.getText().trim());
				String manager = managerField.getText().trim();

				OnlineStore selectedStore = storeComboBox.getValue();

				if (selectedStore == null) {
					AlertManager.showError("Please select a store");
					return;
				}

				String storeId = String.valueOf(selectedStore.getStoreId());

				if (warehouse == null) {
					
					boolean success = warehouseDB.addWarehouse(location, capacity, manager, storeId);
					if (success) {
						AlertManager.showInformationMessage("Warehouse added successfully");
						showWarehouseView();
						refreshData();
					} else {
						AlertManager.showError("Failed to add warehouse");
					}
				} else {
				
					warehouse.setLocation(location);
					warehouse.setCapacity(capacity);
					warehouse.setStockManager(manager);
					warehouse.setOnlineStore(selectedStore);

					boolean success = warehouseDB.updateWarehouse(warehouse);
					if (success) {
						AlertManager.showInformationMessage("Warehouse updated successfully");
						showWarehouseView();
						refreshData();
					} else {
						AlertManager.showError("Failed to update warehouse");
					}
				}
			}
		});

		VBox vBoxCenter = new VBox(20);
		vBoxCenter.setPadding(new Insets(20));
		vBoxCenter.setStyle("-fx-background-color: white; -fx-background-radius: 15; "
				+ "-fx-border-color: #FFE4E1; -fx-border-radius: 15; -fx-border-width: 2; "
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");
		vBoxCenter.setMaxWidth(500);

		vBoxCenter.getChildren().addAll(formTitle, formGrid, buttonBox);
		return vBoxCenter;
	}
	
	private void showAllWarehouses() {

		Stage stage = new Stage();
		stage.setTitle("All Warehouses");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(primaryStage);

		VBox container = new VBox(15);
		container.setPadding(new Insets(20));
		container.setStyle("-fx-background-color: #FFF5F5;");

		Label title = new Label("All Warehouses");
		title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #C71585;");

		TableView<Warehouse> table = new TableView<>();
		table.setPrefHeight(400);

		TableColumn<Warehouse, Integer> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("warehouseId"));
		idCol.setPrefWidth(80);

		TableColumn<Warehouse, String> locationCol = new TableColumn<>("Location");
		locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
		locationCol.setPrefWidth(150);

		TableColumn<Warehouse, Integer> capacityCol = new TableColumn<>("Capacity");
		capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));
		capacityCol.setPrefWidth(100);

		TableColumn<Warehouse, String> managerCol = new TableColumn<>("Manager");
		managerCol.setCellValueFactory(new PropertyValueFactory<>("stockManager"));
		managerCol.setPrefWidth(120);

		TableColumn<Warehouse, String> storeCol = new TableColumn<>("Store");
		storeCol.setCellValueFactory(e -> {
			OnlineStore store = e.getValue().getOnlineStore();
			return new javafx.beans.property.SimpleStringProperty(store != null ? store.getStoreName() : "N/A");
		});
		storeCol.setPrefWidth(150);

		table.getColumns().addAll(idCol, locationCol, capacityCol, managerCol, storeCol);

		HBox controlButtons = new HBox(10);
		controlButtons.setAlignment(Pos.CENTER);
		controlButtons.setPadding(new Insets(10, 0, 0, 0));

		Button deleteSelectedBtn = new Button("Delete Selected");
		deleteSelectedBtn.setStyle(
				"-fx-background-color: #FF4500; -fx-text-fill: white; " + "-fx-font-weight: bold; -fx-padding: 8 15;");

		table.setRowFactory(tv -> {
			TableRow<Warehouse> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && !row.isEmpty()) {
					Warehouse selected = row.getItem();
					currentWarehouseId = selected.getWarehouseId();
					stage.close();
					showWarehouseView();
				}
			});
			return row;
		});

		controlButtons.getChildren().addAll(deleteSelectedBtn);
		loadWarehousesToTable(table);
		container.getChildren().addAll(title, table, controlButtons);

		Scene scene = new Scene(container, 700, 500);
		stage.setScene(scene);
		stage.showAndWait();
	}

	private void loadWarehousesToTable(TableView<Warehouse> table) {

		new Thread(() -> {
			try {
				ArrayList<Warehouse> warehouses = warehouseDB.getAllWarehouses();
				Platform.runLater(() -> {
					ObservableList<Warehouse> data = FXCollections.observableArrayList(warehouses);
					table.setItems(data);
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void deleteWarehouseWithConfirmation(Warehouse warehouse) {

		String message = "Are you sure you want to delete warehouse:\n\n" + "Location: " + warehouse.getLocation()
				+ "\n" + "ID: " + warehouse.getWarehouseId() + "\n" + "Manager: " + warehouse.getStockManager() + "\n\n"
				+ "This action cannot be undone";

		if (AlertManager.showConfirmation(message)) {
			boolean success = warehouseDB.deleteWarehouse(warehouse.getWarehouseId());
			if (success) {
				AlertManager.showInformationMessage("Warehouse deleted successfully");

				ArrayList<Warehouse> warehouses = warehouseDB.getAllWarehouses();
				if (!warehouses.isEmpty()) {
					currentWarehouseId = warehouses.get(0).getWarehouseId();
				} else {
					currentWarehouseId = 1;
				}

				showWarehouseView();
				refreshData();
			} else {
				AlertManager.showError("Failed to delete warehouse\n It may contain inventory items ");
			}
		}
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

	private Button createQuickActionButton(String text, String color) {

		Button button = new Button(text);
		button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
				+ "-fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 5; "
				+ "-fx-padding: 5 10; -fx-cursor: hand;");

		button.setOnMouseEntered(
				e -> button.setStyle("-fx-background-color: derive(" + color + ", -20%); -fx-text-fill: white; "
						+ "-fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 5; "
						+ "-fx-padding: 5 10; -fx-cursor: hand;"));
		button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
				+ "-fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 5; "
				+ "-fx-padding: 5 10; -fx-cursor: hand;"));

		return button;
	}

	private Label createInfoLabel(String text) {

		Label label = new Label(text);
		label.setStyle("-fx-font-weight: bold; -fx-text-fill: #C71585; -fx-font-size: 12px;");
		return label;
	}

	private Label createInfoValue(String text) {

		Label label = new Label(text);
		label.setStyle("-fx-text-fill: #8B4513; -fx-font-size: 12px;");
		return label;
	}

	private VBox createStatCard(String title, String value, String color, String subtitle, double width) {

		VBox card = new VBox(10);
		card.setPadding(new Insets(20));
		card.setPrefWidth(width);
		card.setPrefHeight(150);
		card.setStyle("-fx-background-color: linear-gradient(to bottom right, " + color + ", " + lightenColor(color)
				+ "); " + "-fx-background-radius: 15; " + "-fx-border-radius: 15; "
				+ "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");

		Label titleLabel = new Label(title);
		titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

		Label valueLabel = new Label(value);
		valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

		Label subtitleLabel = new Label(subtitle);
		subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-opacity: 0.9;");
		subtitleLabel.setWrapText(true);

		card.getChildren().addAll(titleLabel, valueLabel, subtitleLabel);
		card.setAlignment(Pos.CENTER);

		return card;
	}

	private String lightenColor(String color) {
		switch (color) {
		case "#FF69B4":
			return "#FFB6C1";
		case "#DB7093":
			return "#F8C8DC";
		case "#FF1493":
			return "#FF69B4";
		case "#C71585":
			return "#DB7093";
		default:
			return "#FFFFFF";
		}
	}

	private Label createFormLabel(String text) {

		Label label = new Label(text);
		label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #8B4513;");
		label.setMinWidth(100);
		return label;

	}

	private TextField createFormTextField() {
		TextField field = new TextField();
		field.getStyleClass().add("form-text-field");
		return field;
	}

	private Button createFormButton(String text, String color) {

		Button button = new Button(text);
		button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; "
				+ "-fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 10; "
				+ "-fx-padding: 10 25; -fx-cursor: hand;");
		button.setPrefWidth(100);
		return button;
	}

	private boolean validateWarehouseForm(TextField locationField, TextField capacityField, TextField managerField,
			ComboBox<OnlineStore> storeComboBox) {

		if (locationField.getText().trim().isEmpty()) {
			AlertManager.showError("Please enter location");
			locationField.requestFocus();
			return false;
		}

		if (capacityField.getText().trim().isEmpty()) {
			AlertManager.showError("Please enter capacity");
			capacityField.requestFocus();
			return false;
		}

		if (managerField.getText().trim().isEmpty()) {
			AlertManager.showError("Please enter manager name");
			managerField.requestFocus();
			return false;
		}

		if (storeComboBox.getValue() == null) {
			AlertManager.showError("Please select a store");
			storeComboBox.requestFocus();
			return false;
		}

		try {
			int capacity = Integer.parseInt(capacityField.getText().trim());
			if (capacity <= 0) {
				AlertManager.showError("Capacity must be a positive number");
				capacityField.requestFocus();
				return false;
			}
		} catch (NumberFormatException e) {
			AlertManager.showError("Invalid capacity value \nPlease enter a number");
			capacityField.requestFocus();
			return false;
		}

		return true;
	}

	private void refreshData() {
		
		loadingLabel.setText("Refreshing data....");
		loadingLabel.setVisible(true);
		loadingLabel.setStyle("-fx-text-fill: #FF69B4;");
		progressBar.setVisible(true);

		loadWarehousesNavigation();
		loadWarehouseData(currentWarehouseId);
	}

	private int getTotalItems() {
		String query = "SELECT SUM(quantity) AS total FROM Inventory WHERE warehouse_id = ?";
		try (Connection conn = DataBaseConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, currentWarehouseId);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return rs.getInt("total");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
