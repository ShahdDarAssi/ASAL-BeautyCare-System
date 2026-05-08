
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ProductsManagement {

	private TableView<Product> table;
	private ObservableList<Product> productList;
	private Stage primaryStage;
	private BorderPane root;
	private TextField searchField;
	private ComboBox<String> searchTypeCombo;
	private ProgressBar progressBar;
	private Label loadingLabel;

	public ProductsManagement(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void show() {

		root = new BorderPane();

		try {
			root.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		} catch (Exception e) {
			System.out.println("CSS file not found using default styling");
		}
		root.getStyleClass().add("root-pane");

		VBox leftSide = new VBox(15);
		leftSide.setPadding(new Insets(25));
		leftSide.setPrefWidth(250);
		leftSide.setStyle("-fx-background-color: linear-gradient(to bottom, #FFE4E1, #FFF0F5);");
		leftSide.getStyleClass().add("sidebar");

		Label panelTitle = new Label("Product Operations");
		panelTitle
				.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #C71585; -fx-padding: 0 0 15 0;");

		Button addButton = createSideButton("Add New Product", "#FF69B4");
		addButton.setOnAction(e -> showAddScreen());

		Button editButton = createSideButton("Edit Product", "#FF69B4");
		editButton.setOnAction(e -> {
			Product product = table.getSelectionModel().getSelectedItem();
			if (product == null) {
				AlertManager.showError("Please select a product to edit");
				return;
			}
			showEditScreen(product);
		});

		Button deleteButton = createSideButton("Delete Product", "#FF69B4");
		deleteButton.setOnAction(e -> {
			Product product = table.getSelectionModel().getSelectedItem();
			if (product == null) {
				AlertManager.showError("Please select a product to delete");
				return;
			}

			if (AlertManager
					.showConfirmation("Are you sure you want to delete product: " + product.getProduct_name() + "?")) {
				boolean success = DatabaseOperationsProduct.deleteProduct(product.getProduct_id());
				if (success) {
					refreshTable();
					AlertManager.showInformationMessage("Product deleted successfully!");
				} else {
					AlertManager.showError("Failed to delete product from database!");
				}
			}
		});

		Button refreshButton = createSideButton("Refresh", "#FFB6C1");
		refreshButton.setOnAction(e -> refreshTable());

		Separator separator = new Separator();
		separator.setPadding(new Insets(15, 0, 15, 0));

		Button backButton = createSideButton("Back", "#DB7093");
		backButton.setOnAction(e -> {
			try {
				AdminScreen dashboard = new AdminScreen(primaryStage);
				dashboard.show();
			} catch (Exception ex) {
				ex.printStackTrace();
				AlertManager.showError("Error returning to Admin Screen : " + ex.getMessage());
			}
		});

		leftSide.getChildren().addAll(panelTitle, addButton, editButton, deleteButton, refreshButton, separator,
				backButton);

		VBox rightContent = new VBox(20);
		rightContent.setPadding(new Insets(25));
		rightContent.getStyleClass().add("center-content");

		HBox headerBox = new HBox(20);
		headerBox.setAlignment(Pos.CENTER_LEFT);

		Label titleLabel = new Label("Products Management");
		titleLabel.getStyleClass().add("welcome-title");

		HBox searchBox = new HBox(15);
		searchBox.setAlignment(Pos.CENTER_LEFT);
		searchBox.setPadding(new Insets(10, 0, 20, 0));
		searchBox.getStyleClass().add("search-box");

		searchTypeCombo = new ComboBox<>();
		searchTypeCombo.getItems().addAll("ID", "Name", "Brand", "Category");
		searchTypeCombo.setValue("Name");
		searchTypeCombo.setPrefWidth(120);
		searchTypeCombo.getStyleClass().add("search");

		searchField = new TextField();
		searchField.setPromptText("Enter product name ID or brand....");
		searchField.setPrefWidth(300);
		searchField.getStyleClass().add("search-field");

		Button searchButton = createStyledButton("Search", "button-normal");
		Button showAllButton = createStyledButton("Show All", "button-normal");

		searchBox.getChildren().addAll(new Label("Search by : "), searchTypeCombo, searchField, searchButton,
				showAllButton);

		Label productCountLabel = new Label();
		productCountLabel.getStyleClass().add("product-count-label");

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
		setupTableColumns();

		productList = FXCollections.observableArrayList();
		table.setItems(productList);

		HBox bottomButtons = new HBox(15);
		bottomButtons.setAlignment(Pos.CENTER);
		bottomButtons.setPadding(new Insets(15, 0, 0, 0));

		rightContent.getChildren().addAll(titleLabel, searchBox, productCountLabel, loadingLabel, progressBar, table,
				bottomButtons);

		root.setLeft(leftSide);
		root.setCenter(rightContent);

		loadProducts(productCountLabel);

		searchButton.setOnAction(e -> {
			search(productCountLabel);
		});

		showAllButton.setOnAction(e -> {
			searchField.clear();
			refreshTable();
		});

		searchField.setOnAction(e -> {
			search(productCountLabel);
		});

		Scene scene = new Scene(root, 1300, 800);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Products Management");
		primaryStage.show();
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

	private void setupTableColumns() {

		TableColumn<Product, Integer> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("product_id"));
		idCol.setPrefWidth(70);

		TableColumn<Product, Integer> catCol = new TableColumn<>("Category ID");
		catCol.setCellValueFactory(
				e -> new SimpleIntegerProperty(e.getValue().getCategory().getCategoryId()).asObject());
		catCol.setPrefWidth(100);

		TableColumn<Product, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("product_name"));
		nameCol.setPrefWidth(200);

		TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
		priceCol.setPrefWidth(100);
		priceCol.setCellFactory(e -> new TableCell<Product, Double>() {
			@Override
			protected void updateItem(Double price, boolean empty) {
				super.updateItem(price, empty);
				if (empty || price == null)
					setText(null);
				else
					setText(String.format("$%.2f", price));
			}
		});

		TableColumn<Product, String> brandCol = new TableColumn<>("Brand");
		brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
		brandCol.setPrefWidth(150);

		TableColumn<Product, String> descCol = new TableColumn<>("Description");
		descCol.setCellValueFactory(new PropertyValueFactory<>("product_description"));
		descCol.setPrefWidth(300);

		table.getColumns().addAll(idCol, catCol, nameCol, priceCol, brandCol, descCol);
	}

	private void loadProducts(Label countLabel) {

		loadingLabel.setText("Loading products....");
		loadingLabel.setVisible(true);
		progressBar.setVisible(true);
		progressBar.setProgress(-1);

		new Thread(() -> {
			ObservableList<Product> allProducts = DatabaseOperationsProduct.getAllProducts();
			int totalProducts = DatabaseOperationsProduct.getProductCount();

			Platform.runLater(() -> {
				productList.clear();
				productList.addAll(allProducts);
				countLabel.setText("Total Products: " + totalProducts);
				loadingLabel.setVisible(false);
				progressBar.setVisible(false);
			});
		}).start();

	}

	private void refreshTable() {
		loadingLabel.setText("Refreshing data......");
		loadingLabel.setVisible(true);
		progressBar.setVisible(true);
		progressBar.setProgress(-1);

		new Thread(() -> {
			ObservableList<Product> allProducts = DatabaseOperationsProduct.getAllProducts();
			int totalProducts = DatabaseOperationsProduct.getProductCount();

			Platform.runLater(() -> {
				productList.clear();
				productList.addAll(allProducts);

				for (var node : ((VBox) root.getCenter()).getChildren()) {
					if (node instanceof Label && ((Label) node).getStyleClass().contains("product-count-label")) {
						((Label) node).setText("Total Products: " + totalProducts);
						break;
					}
				}

				loadingLabel.setVisible(false);
				progressBar.setVisible(false);
			});
		}).start();
	}

	private void search(Label countLabel) {

		String keyword = searchField.getText().trim();
		String searchType = searchTypeCombo.getValue();

		if (keyword.isEmpty()) {
			refreshTable();
			return;
		}

		loadingLabel.setText("Searching products....");
		loadingLabel.setVisible(true);
		progressBar.setVisible(true);
		progressBar.setProgress(-1);

		new Thread(() -> {
			ObservableList<Product> searchResults;

			if ("ID".equals(searchType)) {
				try {
					int productId = Integer.parseInt(keyword);
					Product product = DatabaseOperationsProduct.getProductById(productId);
					searchResults = FXCollections.observableArrayList();
					if (product != null) {
						searchResults.add(product);
					}
				} catch (NumberFormatException e) {
					Platform.runLater(() -> {
						AlertManager.showError("Invalid ID \nPlease enter a valid numeric ID");
						loadingLabel.setVisible(false);
						progressBar.setVisible(false);
					});
					return;
				}
			} else if ("Name".equals(searchType)) {
				searchResults = DatabaseOperationsProduct.getProductsByName(keyword);
			} else if ("Brand".equals(searchType)) {
				searchResults = DatabaseOperationsProduct.getProductsByBrand(keyword);
			} else if ("Category".equals(searchType)) {
				searchResults = DatabaseOperationsProduct.getProductsByCategory(keyword);
			} else {
				searchResults = DatabaseOperationsProduct.getProductsByName(keyword);
			}

			Platform.runLater(() -> {
				productList.clear();
				productList.addAll(searchResults);
				countLabel.setText("Search Results: " + searchResults.size());
				loadingLabel.setVisible(false);
				progressBar.setVisible(false);

				if (searchResults.isEmpty()) {
					AlertManager.showInformationMessage("No products found for: " + keyword);
				}
			});
		}).start();
	}

	private Button createStyledButton(String text, String styleClass) {
		Button button = new Button(text);
		button.getStyleClass().add(styleClass);
		button.setPrefSize(120, 40);
		return button;
	}

	private void showAddScreen() {

		BorderPane addRoot = new BorderPane();
		try {
			addRoot.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		} catch (Exception e) {
			System.out.println("CSS file not found");
		}
		addRoot.getStyleClass().add("root-pane");

		VBox addBox = new VBox(15);
		addBox.setPadding(new Insets(30));
		addBox.setAlignment(Pos.CENTER);
		addBox.getStyleClass().add("add-edit-box");

		Label title = new Label("Add New Product");
		title.getStyleClass().add("add-edit-title");

		Label productIdLabel = new Label("Product ID will be assigned automatically");
		productIdLabel.getStyleClass().add("info-label");

		ComboBox<Category> categoryCombo = new ComboBox<>();
		categoryCombo.setPromptText("Select Category");
		categoryCombo.setPrefWidth(350);
		categoryCombo.setPrefHeight(40);

		new Thread(() -> {
			ObservableList<Category> categories = DatabaseOperationsCategory.getAllCategories();
			Platform.runLater(() -> {
				categoryCombo.getItems().addAll(categories);
				categoryCombo.setCellFactory(lv -> new ListCell<Category>() {
					@Override
					protected void updateItem(Category item, boolean empty) {
						super.updateItem(item, empty);
						setText(empty || item == null ? "" : item.getName() + " (ID: " + item.getCategoryId() + ")");
					}
				});
				categoryCombo.setButtonCell(new ListCell<Category>() {
					@Override
					protected void updateItem(Category item, boolean empty) {
						super.updateItem(item, empty);
						setText(empty || item == null ? "Select Category"
								: item.getName() + " (ID: " + item.getCategoryId() + ")");
					}
				});
			});
		}).start();

		TextField name = createStyledTextField("Product Name", "");
		TextField price = createStyledTextField("Price", "");
		TextField brand = createStyledTextField("Brand", "");
		TextArea desc = createStyledTextArea("Description", "");

		HBox buttons = new HBox(20);
		buttons.setAlignment(Pos.CENTER);

		Button save = createPinkButton("Save", "#ff69b4", "#ff1493");
		Button back = createPinkButton("Back", "#ffb6c1", "#db7093");

		buttons.getChildren().addAll(save, back);

		VBox formBox = new VBox(10);
		formBox.setAlignment(Pos.CENTER);
		formBox.getChildren().addAll(new Label("Category:"), categoryCombo, new Label("Product Name:"), name,
				new Label("Price:"), price, new Label("Brand:"), brand, new Label("Description:"), desc);

		addBox.getChildren().addAll(title, productIdLabel, formBox, buttons);

		BorderPane container = new BorderPane(addBox);
		container.getStyleClass().add("add-edit-container");
		addRoot.setCenter(container);

		back.setOnAction(e -> show());

		save.setOnAction(e -> {
			if (categoryCombo.getValue() == null) {
				AlertManager.showError("Please select a category!");
				return;
			}
			if (!validateInput(name, price, brand, desc)) {
				return;
			}

			Product newProduct = new Product(0, name.getText().trim(), Double.parseDouble(price.getText().trim()),
					brand.getText().trim(), desc.getText().trim(), categoryCombo.getValue());

			boolean success = DatabaseOperationsProduct.addProduct(newProduct);
			if (success) {
				AlertManager.showInformationMessage("Product Added Successfully!");
				show();
			} else {
				AlertManager.showError("Failed to add product!");
			}
		});

		Scene scene = new Scene(addRoot, 1300, 800);
		primaryStage.setScene(scene);
	}

	private void showEditScreen(Product product) {

		BorderPane editRoot = new BorderPane();
		try {
			editRoot.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		} catch (Exception e) {
			System.out.println("CSS file not found");
		}
		editRoot.getStyleClass().add("root-pane");

		VBox editBox = new VBox(15);
		editBox.setPadding(new Insets(30));
		editBox.setAlignment(Pos.CENTER);
		editBox.getStyleClass().add("add-edit-box");

		Label title = new Label("Edit Product");
		title.getStyleClass().add("add-edit-title");

		Label productIdLabel = new Label("Product ID : " + product.getProduct_id());
		productIdLabel.getStyleClass().add("info-label");

		ComboBox<Category> categoryCombo = new ComboBox<>();
		categoryCombo.setPromptText("Select Category");
		categoryCombo.setPrefWidth(350);
		categoryCombo.setPrefHeight(40);

		new Thread(() -> {
			ObservableList<Category> categories = DatabaseOperationsCategory.getAllCategories();
			Platform.runLater(() -> {
				categoryCombo.getItems().addAll(categories);
				categoryCombo.setCellFactory(lv -> new ListCell<Category>() {
					@Override
					protected void updateItem(Category item, boolean empty) {
						super.updateItem(item, empty);
						setText(empty || item == null ? "" : item.getName() + " (ID: " + item.getCategoryId() + ")");
					}
				});
				categoryCombo.setButtonCell(new ListCell<Category>() {
					@Override
					protected void updateItem(Category item, boolean empty) {
						super.updateItem(item, empty);
						setText(empty || item == null ? "Select Category"
								: item.getName() + " (ID: " + item.getCategoryId() + ")");
					}
				});

				for (Category cat : categories) {
					if (cat.getCategoryId() == product.getCategory().getCategoryId()) {
						categoryCombo.getSelectionModel().select(cat);
						break;
					}
				}
			});
		}).start();

		TextField name = createStyledTextField("Product Name", product.getProduct_name());
		TextField price = createStyledTextField("Price", String.valueOf(product.getPrice()));
		TextField brand = createStyledTextField("Brand", product.getBrand());
		TextArea desc = createStyledTextArea("Description", product.getProduct_description());

		HBox buttons = new HBox(20);
		buttons.setAlignment(Pos.CENTER);

		Button save = createPinkButton("Update", "#ff69b4", "#ff1493");
		Button back = createPinkButton("Cancel", "#ffb6c1", "#db7093");

		buttons.getChildren().addAll(save, back);

		VBox formBox = new VBox(10);
		formBox.setAlignment(Pos.CENTER);
		formBox.getChildren().addAll(new Label("Category : "), categoryCombo, new Label("Product Name: "), name,
				new Label("Price :"), price, new Label("Brand : "), brand, new Label("Description: "), desc);

		editBox.getChildren().addAll(title, productIdLabel, formBox, buttons);

		BorderPane container = new BorderPane(editBox);
		container.getStyleClass().add("add-edit-container");
		editRoot.setCenter(container);

		back.setOnAction(e -> show());

		save.setOnAction(e -> {
			if (categoryCombo.getValue() == null) {
				AlertManager.showError("Please select a category!");
				return;
			}
			if (!validateInput(name, price, brand, desc)) {
				return;
			}

			product.setProduct_name(name.getText().trim());
			product.setPrice(Double.parseDouble(price.getText().trim()));
			product.setBrand(brand.getText().trim());
			product.setProduct_description(desc.getText().trim());
			product.setCategory(categoryCombo.getValue());

			boolean success = DatabaseOperationsProduct.updateProduct(product);
			if (success) {
				AlertManager.showInformationMessage("Product updated successfully ");
				show();
			} else {
				AlertManager.showError("Failed to update product ");
			}
		});

		Scene scene = new Scene(editRoot, 1300, 800);
		primaryStage.setScene(scene);
	}

	private boolean validateInput(TextField name, TextField price, TextField brand, TextArea desc) {

		if (name.getText().trim().isEmpty() || price.getText().trim().isEmpty() || brand.getText().trim().isEmpty()
				|| desc.getText().trim().isEmpty()) {
			AlertManager.showError("Please fill in all fields");
			return false;
		}

		try {
			double priceValue = Double.parseDouble(price.getText().trim());
			if (priceValue <= 0) {
				AlertManager.showError("Invalid Price \nPrice must be greater than 0 ");
				return false;
			}
		} catch (NumberFormatException e) {
			AlertManager.showError("Invalid Price \nPrice must be a valid number ");
			return false;
		}

		if (name.getText().trim().length() > 100) {
			AlertManager.showError("Product name is too long!\nMaximum 100 characters allowed");
			return false;
		}

		if (brand.getText().trim().length() > 50) {
			AlertManager.showError("Brand name is too long!\nMaximum 50 characters allowed ");
			return false;
		}

		return true;
	}

	private Button createPinkButton(String text, String baseColor, String hoverColor) {
		Button button = new Button(text);
		button.setStyle("-fx-background-color: " + baseColor + "; " + "-fx-text-fill: white; -fx-font-size: 16px; "
				+ "-fx-padding: 12px 25px; -fx-font-weight: bold; "
				+ "-fx-background-radius: 20; -fx-border-radius: 20; "
				+ "-fx-cursor: hand; -fx-font-family: 'Arial Rounded MT Bold';");

		button.setOnMouseEntered(e -> {
			button.setStyle("-fx-background-color: " + hoverColor + "; " + "-fx-text-fill: white; -fx-font-size: 16px; "
					+ "-fx-padding: 12px 25px; -fx-font-weight: bold; "
					+ "-fx-background-radius: 20; -fx-border-radius: 20; "
					+ "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");
		});

		button.setOnMouseExited(e -> {
			button.setStyle("-fx-background-color: " + baseColor + "; " + "-fx-text-fill: white; -fx-font-size: 16px; "
					+ "-fx-padding: 12px 25px; -fx-font-weight: bold; "
					+ "-fx-background-radius: 20; -fx-border-radius: 20; " + "-fx-cursor: hand;");
		});

		button.setOnMousePressed(e -> {
			button.setStyle("-fx-background-color: #c71585; " + "-fx-text-fill: white; -fx-font-size: 16px; "
					+ "-fx-padding: 12px 25px; -fx-font-weight: bold; "
					+ "-fx-background-radius: 20; -fx-border-radius: 20; " + "-fx-cursor: hand; -fx-translate-y: 2px;");
		});

		button.setOnMouseReleased(e -> {
			button.setStyle("-fx-background-color: " + baseColor + "; " + "-fx-text-fill: white; -fx-font-size: 16px; "
					+ "-fx-padding: 12px 25px; -fx-font-weight: bold; "
					+ "-fx-background-radius: 20; -fx-border-radius: 20; " + "-fx-cursor: hand; -fx-translate-y: 0px;");
		});

		return button;
	}

	private TextField createStyledTextField(String promptText, String initialValue) {
		TextField textField = new TextField(initialValue);
		textField.setPromptText(promptText);
		textField.setMaxWidth(350);
		textField.setPrefHeight(40);
		textField.getStyleClass().add("styled-text-field");
		return textField;
	}

	private TextArea createStyledTextArea(String promptText, String initialValue) {
		TextArea textArea = new TextArea(initialValue);
		textArea.setPromptText(promptText);
		textArea.setMaxWidth(350);
		textArea.setPrefHeight(120);
		textArea.setWrapText(true);
		textArea.getStyleClass().add("styled-text-area");
		return textArea;
	}
}