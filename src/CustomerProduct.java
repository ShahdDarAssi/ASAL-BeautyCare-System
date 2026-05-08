
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Comparator;

public class CustomerProduct {

	private Stage primaryStage;
	private ObservableList<Product> products = FXCollections.observableArrayList();
	private ObservableList<Category> categories = FXCollections.observableArrayList();
	private ObservableList<String> brands = FXCollections.observableArrayList();
	private ObservableList<Product> cartItems = FXCollections.observableArrayList();
	private Label cartCountLabel = new Label("0");
	private Label cartTotalLabel = new Label("$0.00");
	private ComboBox<String> categoryFilter = new ComboBox<>();
	private ComboBox<String> brandFilter = new ComboBox<>();
	private TextField searchField = new TextField();
	private ComboBox<String> sortByCombo = new ComboBox<>();
	private FlowPane productsShow;
	private ScrollPane productsPane;

	private Label loadingLabel = new Label("Loading products.......");

	private int pageSize = 15;
	private int currentPage = 1;
	private int totalProducts = 0;
	private Label pageInfoLabel = new Label();
	private Button prevBtn = new Button("Previous");
	private Button nextBtn = new Button("Next");

	private ObservableList<Product> currentDisplayProducts = FXCollections.observableArrayList();

	public CustomerProduct(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void show() {

		HBox bottomSide = new HBox();
		bottomSide.setPadding(new Insets(10));
		bottomSide.setAlignment(Pos.CENTER);
		bottomSide.setStyle("-fx-background-color: #FFE4E1;");

		Label bottomText = new Label("Contact: +970598765432 | info@asal.com | 24 Hours");
		bottomText.setStyle("-fx-font-size: 11px; -fx-text-fill: #8B4513;");

		bottomSide.getChildren().add(bottomText);

		BorderPane root = new BorderPane();
		root.setStyle("-fx-background-color: #FFF0F5;");

		root.setTop(showHeader());
		root.setLeft(createLeftSide());
		root.setCenter(createCenterSide());
		root.setBottom(bottomSide);

		loadCategories();
		loadBrands();
		loadAllProducts();

		Scene scene = new Scene(root, 1200, 800);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Asal Beauty Store - Product Catalog");
		primaryStage.setMinWidth(1000);
		primaryStage.setMinHeight(700);
		primaryStage.show();
	}

	private HBox showHeader() {

		ImageView storeIcon = new ImageView(new Image("file:Photo/flower.png"));
		storeIcon.setFitWidth(25);
		storeIcon.setFitHeight(25);
		storeIcon.setPreserveRatio(true);

		Label storeName = new Label("Asal Beauty Store");
		storeName.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");

		HBox storeBox = new HBox();
		storeBox.setAlignment(Pos.CENTER_LEFT);
		storeBox.getChildren().addAll(storeIcon, storeName);
		HBox.setHgrow(storeBox, Priority.ALWAYS);

		ImageView cartIcon = new ImageView(new Image("file:Photo/cart.png"));
		cartIcon.setFitWidth(25);
		cartIcon.setFitHeight(25);
		cartIcon.setPreserveRatio(true);

		Button cartButton = new Button("Cart");
		cartButton.setGraphic(cartIcon);
		cartButton.setStyle("-fx-background-color: transparent; " + "-fx-text-fill: white; " + "-fx-font-size: 16px; "
				+ "-fx-font-weight: bold; " + "-fx-cursor: hand; " + "-fx-padding: 5 10;");
		cartButton.setOnAction(e -> showCart());

		HBox countBox = new HBox(3);
		countBox.setAlignment(Pos.CENTER_RIGHT);
		cartCountLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
		countBox.getChildren().addAll(cartCountLabel);

		HBox totalBox = new HBox(3);
		totalBox.setAlignment(Pos.CENTER_RIGHT);
		cartTotalLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
		totalBox.getChildren().addAll(cartTotalLabel);

		VBox cartInfo = new VBox(3);
		cartInfo.setAlignment(Pos.CENTER_RIGHT);
		cartInfo.getChildren().addAll(countBox, totalBox);

		HBox cartBox = new HBox(10);
		cartBox.setAlignment(Pos.CENTER_RIGHT);
		cartBox.getChildren().addAll(cartButton, cartInfo);

		HBox header = new HBox(15);
		header.setStyle("-fx-background-color: linear-gradient(to right, #FF69B4, #FF1493); " + "-fx-padding: 10 20;");
		header.setAlignment(Pos.CENTER_LEFT);
		header.getChildren().addAll(storeBox, cartBox);

		return header;
	}

	private VBox createLeftSide() {

		Label searchLabel = new Label("Search Products");
		searchLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #C71585;");

		HBox searchBox = new HBox(5);
		searchField.setPromptText("Search by name.....");
		searchField.setStyle("-fx-background-color: white; " + "-fx-border-color: #FFB6C1; " + "-fx-border-radius: 5; "
				+ "-fx-background-radius: 5; " + "-fx-padding: 5; " + "-fx-pref-width: 150px;");

		ImageView searchIcon = new ImageView(new Image("file:Photo/search.png"));
		searchIcon.setFitWidth(25);
		searchIcon.setFitHeight(25);
		searchIcon.setPreserveRatio(true);

		Button searchBtn = new Button();
		searchBtn.setStyle("-fx-background-color: transparent; " + "-fx-text-fill: white; " + "-fx-font-size: 12px; "
				+ "-fx-background-radius: 5; " + "-fx-padding: 5 8; " + "-fx-cursor: hand;");
		searchBtn.setGraphic(searchIcon);

		searchBtn.setOnAction(e -> {
			currentPage = 1;
			loadProductsBySearch();
		});

		ImageView clearIcon = new ImageView(new Image("file:Photo/X.png"));
		clearIcon.setFitWidth(25);
		clearIcon.setFitHeight(25);
		clearIcon.setPreserveRatio(true);

		Button clearSearchBtn = new Button();
		clearSearchBtn.setStyle("-fx-background-color: #F5AFAF; " + "-fx-text-fill: #8B4513; " + "-fx-font-size: 10px; "
				+ "-fx-background-radius: 5; " + "-fx-padding: 3 6; " + "-fx-cursor: hand;");
		clearSearchBtn.setGraphic(clearIcon);

		clearSearchBtn.setOnAction(e -> {
			searchField.clear();
			currentPage = 1;
			loadAllProducts();
		});

		searchBox.getChildren().addAll(searchField, searchBtn, clearSearchBtn);

		// Category filter
		Label categoriesLabel = new Label("Filter by Category");
		categoriesLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #C71585;");

		HBox categoryBox = new HBox(5);
		categoryFilter.setPromptText("All Categories");
		categoryFilter
				.setStyle("-fx-background-color: white; " + "-fx-border-color: #FFB6C1; " + "-fx-border-radius: 5; "
						+ "-fx-background-radius: 5; " + "-fx-pref-width: 150px; " + "-fx-font-size: 12px;");
		categoryFilter.setOnAction(e -> {
			currentPage = 1;
			loadProductsByCategory();
		});

		ImageView clearCategoryIcon = new ImageView(new Image("file:Photo/X.png"));
		clearCategoryIcon.setFitWidth(25);
		clearCategoryIcon.setFitHeight(25);
		clearCategoryIcon.setPreserveRatio(true);

		Button clearCategoryBtn = new Button();
		clearCategoryBtn.setStyle("-fx-background-color: #F5AFAF; " + "-fx-text-fill: #8B4513; "
				+ "-fx-font-size: 10px; " + "-fx-background-radius: 5; " + "-fx-padding: 3 6; " + "-fx-cursor: hand;");
		clearCategoryBtn.setGraphic(clearCategoryIcon);

		clearCategoryBtn.setOnAction(e -> {
			categoryFilter.setValue(null);
			currentPage = 1;
			loadAllProducts();
		});

		categoryBox.getChildren().addAll(categoryFilter, clearCategoryBtn);

		// Brand filter
		Label brandLabel = new Label("Filter by Brand");
		brandLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #C71585;");

		HBox brandBox = new HBox(5);
		brandFilter.setPromptText("All Brands");
		brandFilter.setStyle("-fx-background-color: white; " + "-fx-border-color: #FFB6C1; " + "-fx-border-radius: 5; "
				+ "-fx-background-radius: 5; " + "-fx-pref-width: 150px; " + "-fx-font-size: 12px;");
		brandFilter.setOnAction(e -> {
			currentPage = 1;
			loadProductsByBrand();
		});

		ImageView clearBrandIcon = new ImageView(new Image("file:Photo/X.png"));
		clearBrandIcon.setFitWidth(25);
		clearBrandIcon.setFitHeight(25);
		clearBrandIcon.setPreserveRatio(true);

		Button clearBrandBtn = new Button();
		clearBrandBtn.setStyle("-fx-background-color: #F5AFAF; " + "-fx-text-fill: #8B4513; " + "-fx-font-size: 10px; "
				+ "-fx-background-radius: 5; " + "-fx-padding: 3 6; " + "-fx-cursor: hand;");

		clearBrandBtn.setGraphic(clearBrandIcon);

		clearBrandBtn.setOnAction(e -> {
			brandFilter.setValue(null);
			currentPage = 1;
			loadAllProducts();
		});

		brandBox.getChildren().addAll(brandFilter, clearBrandBtn);

		// Sort options
		Label sortLabel = new Label("Sort Options");
		sortLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #C71585;");

		HBox sortBox = new HBox(5);
		sortByCombo.getItems().addAll("None", "Name A-Z", "Name Z-A", "Price Low-High", "Price High-Low");
		sortByCombo.setValue("None");
		sortByCombo.setStyle("-fx-background-color: white; " + "-fx-border-color: #FFB6C1; " + "-fx-border-radius: 5; "
				+ "-fx-background-radius: 5; " + "-fx-pref-width: 150px; " + "-fx-font-size: 12px;");
		sortByCombo.setOnAction(e -> sortCurrentProducts());

		ImageView clearSortIcon = new ImageView(new Image("file:Photo/X.png"));
		clearSortIcon.setFitWidth(25);
		clearSortIcon.setFitHeight(25);
		clearSortIcon.setPreserveRatio(true);

		Button clearSortBtn = new Button();
		clearSortBtn.setStyle("-fx-background-color: #F5AFAF; " + "-fx-text-fill: #8B4513; " + "-fx-font-size: 10px; "
				+ "-fx-background-radius: 5; " + "-fx-padding: 3 6; " + "-fx-cursor: hand;");
		clearSortBtn.setGraphic(clearSortIcon);

		clearSortBtn.setOnAction(e -> {
			sortByCombo.setValue("None");
			sortCurrentProducts();
		});

		sortBox.getChildren().addAll(sortByCombo, clearSortBtn);

		// Clear all filters
		Button clearAll = new Button("Clear All Filters");
		clearAll.setStyle("-fx-background-color: #F5AFAF; " + "-fx-text-fill: #8B4513; " + "-fx-font-weight: bold; "
				+ "-fx-font-size: 12px; " + "-fx-background-radius: 5; " + "-fx-padding: 8 15; " + "-fx-cursor: hand; "
				+ "-fx-max-width: infinity;");
		clearAll.setOnAction(e -> {
			clearAllFilters();
			currentPage = 1;
			loadAllProducts();
		});

		Button logoutBtn = new Button("Logout");
		logoutBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #FF6347, #FF4500); "
				+ "-fx-text-fill: white; " + "-fx-font-weight: bold; " + "-fx-font-size: 14px; "
				+ "-fx-background-radius: 5; " + "-fx-padding: 10 15; " + "-fx-cursor: hand; "
				+ "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2); " + "-fx-max-width: infinity;");

		logoutBtn.setOnMouseEntered(
				e -> logoutBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #FF4500, #DC143C); "
						+ "-fx-text-fill: white; " + "-fx-font-weight: bold; " + "-fx-font-size: 14px; "
						+ "-fx-background-radius: 5; " + "-fx-padding: 10 15; " + "-fx-cursor: hand; "
						+ "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 7, 0, 0, 3); "
						+ "-fx-max-width: infinity;"));

		logoutBtn.setOnMouseExited(
				e -> logoutBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #FF6347, #FF4500); "
						+ "-fx-text-fill: white; " + "-fx-font-weight: bold; " + "-fx-font-size: 14px; "
						+ "-fx-background-radius: 5; " + "-fx-padding: 10 15; " + "-fx-cursor: hand; "
						+ "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2); "
						+ "-fx-max-width: infinity;"));

		logoutBtn.setOnAction(e -> logout());

		VBox leftSide = new VBox(10);
		leftSide.setStyle("-fx-background-color: linear-gradient(to bottom, #FFE4E1, #FFF0F5); "
				+ "-fx-border-color: #FFB6C1; " + "-fx-border-width: 0 2px 0 0; " + "-fx-padding: 15;");
		leftSide.setPrefWidth(250);

		Region spacer = new Region();
		VBox.setVgrow(spacer, Priority.ALWAYS);

		leftSide.getChildren().addAll(searchLabel, searchBox, new Separator(), categoriesLabel, categoryBox,
				new Separator(), brandLabel, brandBox, new Separator(), sortLabel, sortBox, new Separator(), clearAll,
				spacer, new Separator(), logoutBtn);

		return leftSide;
	}

	private VBox createCenterSide() {

		productsPane = new ScrollPane();
		productsPane.setStyle("-fx-background: transparent; " + "-fx-background-color: #FFFAFA;");
		productsPane.setFitToWidth(true);
		productsPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		productsShow = new FlowPane();
		productsShow.setPadding(new Insets(15));
		productsShow.setHgap(20);
		productsShow.setVgap(20);
		productsShow.setAlignment(Pos.TOP_CENTER);

		loadingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FF69B4; -fx-font-style: italic;");
		loadingLabel.setVisible(false);

		VBox centerContainer = new VBox(loadingLabel, productsShow);
		centerContainer.setAlignment(Pos.TOP_CENTER);
		centerContainer.setPadding(new Insets(5));

		productsPane.setContent(centerContainer);

		HBox boxControl = new HBox(10);
		boxControl.setAlignment(Pos.CENTER);
		boxControl.setPadding(new Insets(10));
		boxControl.setStyle("-fx-background-color: #FFFAFA;");

		ImageView prevIcon = new ImageView(new Image("file:Photo/Next.png"));
		prevIcon.setFitWidth(25);
		prevIcon.setFitHeight(25);
		prevIcon.setPreserveRatio(true);
		prevIcon.setRotate(180);

		ImageView nextIcon = new ImageView(new Image("file:Photo/Next.png"));
		nextIcon.setFitWidth(25);
		nextIcon.setFitHeight(25);
		nextIcon.setPreserveRatio(true);

		prevBtn.setGraphic(prevIcon);
		nextBtn.setGraphic(nextIcon);
		nextBtn.setContentDisplay(ContentDisplay.RIGHT);

		prevBtn.setStyle("-fx-background-color: #F5AFAF; " + "-fx-text-fill: #8B4513; " + "-fx-font-weight: bold; "
				+ "-fx-background-radius: 5; " + "-fx-padding: 5 15; " + "-fx-cursor: hand;");

		nextBtn.setStyle("-fx-background-color: #FF69B4; " + "-fx-text-fill: white; " + "-fx-font-weight: bold; "
				+ "-fx-background-radius: 5; " + "-fx-padding: 5 15; " + "-fx-cursor: hand;");

		pageInfoLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #C71585;");

		// go to previous page
		prevBtn.setOnAction(e -> {
			if (currentPage > 1) {
				currentPage--;
				// load products for new page
				loadCurrentFilter();
			}
		});

		// go to next page
		nextBtn.setOnAction(e -> {
			if (currentPage * pageSize < totalProducts) {
				currentPage++;
				loadCurrentFilter();
			}
		});

		boxControl.getChildren().addAll(prevBtn, pageInfoLabel, nextBtn);

		VBox centerBox = new VBox(productsPane, boxControl);
		VBox.setVgrow(productsPane, Priority.ALWAYS);

		return centerBox;

	}

	private void loadCategories() {

		// show loading message
		loadingLabel.setVisible(true);

		try {

			// get all categories from database
			ObservableList<Category> categoryList = DatabaseOperationsCategory.getAllCategories();
			ObservableList<String> categoryNames = FXCollections.observableArrayList();

			for (Category cat : categoryList) {
				categoryNames.add(cat.getName());
			}

			// update category filter
			categoryFilter.getItems().clear();
			categoryFilter.getItems().addAll(categoryNames);

			categories.clear();
			categories.addAll(categoryList);

		} catch (Exception e) {
			AlertManager.showError("Error loading categories : " + e.getMessage());
		} finally {
			loadingLabel.setVisible(false);
		}
	}

	private void loadBrands() {

		loadingLabel.setVisible(true);

		// get all products from database
		ObservableList<Product> allProducts = DatabaseOperationsProduct.getAllProducts();
		ObservableList<String> brandsList = FXCollections.observableArrayList();

		for (int i = 0; i < allProducts.size(); i++) {

			String brand = allProducts.get(i).getBrand();
			if (brand != null && !brand.trim().isEmpty() && !brandsList.contains(brand)) {
				brandsList.add(brand);
			}
		}

		FXCollections.sort(brandsList);

		brands.clear();
		brands.addAll(brandsList);
		brandFilter.getItems().clear();
		brandFilter.getItems().addAll(brands);

		loadingLabel.setVisible(false);
	}

	private void clearAllFilters() {
		searchField.clear();
		categoryFilter.setValue(null);
		brandFilter.setValue(null);
		sortByCombo.setValue("None");
	}

	private void loadAllProducts() {

		loadingLabel.setVisible(true);
		productsShow.getChildren().clear();

		try {

			ObservableList<Product> allProducts = DatabaseOperationsProduct.getAllProducts();
			// total number of products
			totalProducts = allProducts.size();

			// which products to show on current page
			int startIndex = (currentPage - 1) * pageSize;
			int endIndex = Math.min(startIndex + pageSize, totalProducts);

			// list for products on current page only
			ObservableList<Product> productsList = FXCollections.observableArrayList();
			for (int i = startIndex; i < endIndex; i++) {
				productsList.add(allProducts.get(i));
			}

			loadingLabel.setVisible(false);
			displayProducts(productsList);

		} catch (Exception e) {
			e.printStackTrace();
			loadingLabel.setVisible(false);
			AlertManager.showError("Error loading products : " + e.getMessage());
		}
	}

	private void loadProductsByCategory() {

		String catName = categoryFilter.getValue();

		// if no category select
		if (catName == null || catName.isEmpty()) {
			loadAllProducts();
			return;
		}

		loadingLabel.setVisible(true);
		productsShow.getChildren().clear();

		try {
			// find the category object from the category name
			Category selectedCategory = null;
			for (Category cat : categories) {
				if (cat.getName().equals(catName)) {
					selectedCategory = cat;
					break;
				}
			}
			// if category not found show all products
			if (selectedCategory == null) {
				loadAllProducts();
				return;
			}

			// load product for the selected category from database
			ObservableList<Product> categoryProducts = DatabaseOperationsProduct
					.getProductsByCategory(selectedCategory.getName());

			totalProducts = categoryProducts.size();

			// which product to show current page
			int startIndex = (currentPage - 1) * pageSize;
			int endIndex = Math.min(startIndex + pageSize, totalProducts);

			ObservableList<Product> productsList = FXCollections.observableArrayList();
			for (int i = startIndex; i < endIndex; i++) {
				productsList.add(categoryProducts.get(i));
			}

			loadingLabel.setVisible(false);
			displayProducts(productsList);

		} catch (Exception e) {
			e.printStackTrace();
			loadingLabel.setVisible(false);
			AlertManager.showError("Error loading products by category: " + e.getMessage());
		}
	}

	private void loadProductsByBrand() {

		// get selected brand from filter
		String selectedBrand = brandFilter.getValue();
		if (selectedBrand == null || selectedBrand.isEmpty()) {
			loadAllProducts();
			return;
		}

		loadingLabel.setVisible(true);
		productsShow.getChildren().clear();

		try {

			ObservableList<Product> allProducts = DatabaseOperationsProduct.getAllProducts();
			ObservableList<Product> brandProducts = FXCollections.observableArrayList();

			for (int i = 0; i < allProducts.size(); i++) {
				Product product = allProducts.get(i);
				if (selectedBrand.equals(product.getBrand())) {
					brandProducts.add(product);
				}
			}

			totalProducts = brandProducts.size();

			int startIndex = (currentPage - 1) * pageSize;
			int endIndex = Math.min(startIndex + pageSize, totalProducts);

			ObservableList<Product> productsList = FXCollections.observableArrayList();
			for (int i = startIndex; i < endIndex; i++) {
				productsList.add(brandProducts.get(i));
			}

			loadingLabel.setVisible(false);
			displayProducts(productsList);

		} catch (Exception e) {
			e.printStackTrace();
			loadingLabel.setVisible(false);
			AlertManager.showError("Error loading products by brand : " + e.getMessage());
		}
	}

	private void loadProductsBySearch() {

		String keyword = searchField.getText().trim();
		if (keyword.isEmpty()) {
			loadAllProducts();
			return;
		}

		loadingLabel.setVisible(true);
		productsShow.getChildren().clear();

		try {

			ObservableList<Product> searchedProducts = DatabaseOperationsProduct.searchProducts(keyword);
			totalProducts = searchedProducts.size();

			int startIndex = (currentPage - 1) * pageSize;
			int endIndex = Math.min(startIndex + pageSize, totalProducts);

			ObservableList<Product> productsList = FXCollections.observableArrayList();
			for (int i = startIndex; i < endIndex; i++) {
				productsList.add(searchedProducts.get(i));
			}

			loadingLabel.setVisible(false);
			displayProducts(productsList);

		} catch (Exception e) {
			e.printStackTrace();
			loadingLabel.setVisible(false);
			AlertManager.showError("Error searching products : " + e.getMessage());
		}
	}

	private void loadCurrentFilter() {

		if (!searchField.getText().isEmpty()) {
			loadProductsBySearch();
		} else if (categoryFilter.getValue() != null) {
			loadProductsByCategory();
		} else if (brandFilter.getValue() != null) {
			loadProductsByBrand();
		} else {
			loadAllProducts();
		}
	}

	private void sortCurrentProducts() {

		// get selected sort option
		String sortOption = sortByCombo.getValue();
		if (sortOption.equals("None") || currentDisplayProducts.isEmpty()) {
			return;
		}

		ObservableList<Product> sortedProducts = FXCollections.observableArrayList(currentDisplayProducts);

		// sort by product name ascending (A to Z)
		if (sortOption.equals("Name A-Z")) {
			sortedProducts.sort(Comparator.comparing(Product::getProduct_name));
		} else if (sortOption.equals("Name Z-A")) {
			// sort by product name descending (Z to A)
			sortedProducts.sort(Comparator.comparing(Product::getProduct_name).reversed());

		} else if (sortOption.equals("Price Low-High")) {
			// sort by price from lowest to highest
			sortedProducts.sort(Comparator.comparingDouble(Product::getPrice));

		} else if (sortOption.equals("Price High-Low")) {
			// sort by price from highest to lowest
			sortedProducts.sort(Comparator.comparingDouble(Product::getPrice).reversed());
		}

		displayProducts(sortedProducts);
	}

	private void displayProducts(ObservableList<Product> productsList) {

		productsShow.getChildren().clear();
		currentDisplayProducts = FXCollections.observableArrayList(productsList);

		if (productsList.isEmpty()) {

			Label noProductsLabel = new Label("No products found matching your criteria.");
			noProductsLabel.setStyle("-fx-font-size: 16px; " + "-fx-text-fill: #C71585; " + "-fx-padding: 40px;");
			productsShow.getChildren().add(noProductsLabel);

		} else {
			for (Product p : productsList) {
				createProductCard(p);
			}
		}

		updatePag();
	}

	private void updatePag() {
		int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
		pageInfoLabel.setText("Page " + currentPage + " of " + totalPages);
		prevBtn.setDisable(currentPage <= 1);
		nextBtn.setDisable(currentPage >= totalPages || totalProducts <= pageSize);
	}

	private void createProductCard(Product product) {

		VBox card = new VBox(10);
		card.setStyle("-fx-background-color: white; " + "-fx-border-color: #FFB6C1; " + "-fx-border-radius: 10; "
				+ "-fx-background-radius: 10; " + "-fx-padding: 15; " + "-fx-pref-width: 200px; "
				+ "-fx-pref-height: 170px; " + "-fx-effect: dropshadow(gaussian, rgba(255,182,193,0.2), 5, 0, 0, 2);");
		card.setUserData(product);

		// product name
		Label nameLabel = new Label(product.getProduct_name());
		nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #8B4513;");
		nameLabel.setWrapText(true);
		nameLabel.setMaxWidth(170);

		// product details
		VBox detailsBox = new VBox(5);

		Label brandLabel = new Label(product.getBrand());
		brandLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #696969;");

		Label categoryLabel = new Label(product.getCategory().getName());
		categoryLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #696969;");

		detailsBox.getChildren().addAll(brandLabel, categoryLabel);

		// price
		Label priceLabel = new Label(String.format("$%.2f", product.getPrice()));
		priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #C71585;");

		// add to cart
		Button addToCartBtn = new Button("Add to Cart");
		addToCartBtn.setStyle("-fx-background-color: #FF69B4; " + "-fx-text-fill: white; " + "-fx-font-weight: bold; "
				+ "-fx-font-size: 12px; " + "-fx-background-radius: 5; " + "-fx-padding: 6 10; " + "-fx-cursor: hand; "
				+ "-fx-max-width: infinity;");
		addToCartBtn.setOnAction(e -> addToCart(product));

		VBox.setVgrow(nameLabel, Priority.ALWAYS);
		VBox.setVgrow(detailsBox, Priority.ALWAYS);

		card.getChildren().addAll(nameLabel, detailsBox, priceLabel, addToCartBtn);
		productsShow.getChildren().add(card);
	}

	private void updateCartDisplay() {

		int itemCount = cartItems.size();
		double total = 0;
		for (Product p : cartItems) {
			total += p.getPrice();
		}

		cartCountLabel.setText(String.valueOf(itemCount));
		cartTotalLabel.setText(String.format("$%.2f", total));

	}

	private void showCart() {

		new CartManagemnet().show(primaryStage);
	}

	private void addToCart(Product product) {

		cartItems.add(product);
		CartItem item = new CartItem(product.getProduct_id(), product.getProduct_name(), product.getPrice(), 1);
		CartManagemnet.cartItems.add(item);
		updateCartDisplay();
		AlertManager
				.showInformationMessage("Added to Cart " + product.getProduct_name() + " has been added to your cart");

	}

	private void logout() {
		try {
			new LoginScene(primaryStage).showLoginScene();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}