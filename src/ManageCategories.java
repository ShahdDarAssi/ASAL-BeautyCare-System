
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ManageCategories {

	private Stage primaryStage;
	private BorderPane root;
	private ObservableList<Category> categoryList;
	//input for search
	private TextField searchField;
	//search type
	private ComboBox<String> searchTypeCombo;

	public ManageCategories(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void show() {

		root = new BorderPane();

		// load CSS styling
		try {
			root.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		} catch (Exception e) {
			AlertManager.showError("CSS file not found using default styling");
		}

		root.getStyleClass().add("root-pane");

		searchTypeCombo = new ComboBox<>();
		searchTypeCombo.getItems().addAll("ID", "Name");
		searchTypeCombo.setValue("ID");
		searchTypeCombo.setPrefWidth(100);
		searchTypeCombo.getStyleClass().add("search-combo");

		searchField = new TextField();
		searchField.setPromptText("Enter ID or Name.....");
		searchField.setPrefWidth(200);
		searchField.getStyleClass().add("search-field");

		Button searchButton = new Button("Search");
		searchButton.getStyleClass().add("button-normal");

		Button showAllButton = new Button("Show All");
		showAllButton.getStyleClass().add("button-normal");

		HBox searchBox = new HBox(10);
		searchBox.setAlignment(Pos.CENTER);
		searchBox.setPadding(new Insets(10, 0, 10, 0));
		searchBox.getStyleClass().add("search-box");
		searchBox.getChildren().addAll(new Label("Search by : "), searchTypeCombo, searchField, searchButton,
				showAllButton);

		// HBox for action buttons Add,Edit,Delete,Back
		HBox buttonBox = new HBox(20);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(10, 0, 20, 0));
		buttonBox.getStyleClass().add("button-box");

		Button addButton = createStyledButton("Add Category", "button-normal");
		Button editButton = createStyledButton("Edit Category", "button-normal");
		Button deleteButton = createStyledButton("Delete Category", "button-normal");
		Button backButton = createStyledButton("Back", "button-back");

		buttonBox.getChildren().addAll(addButton, editButton, deleteButton, backButton);

		//Title label
		Label titleLabel = new Label("Categories Management");
		titleLabel.getStyleClass().add("title-label");

		VBox titleBox = new VBox(10);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.getStyleClass().add("title-box");
		titleBox.getChildren().addAll(titleLabel);

		//label to show number of categories
		Label categoryCountLabel = new Label();
		categoryCountLabel.getStyleClass().add("product-count-label");

		// table to show categories
		TableView<Category> categoryTable = new TableView<>();
		categoryTable.getStyleClass().add("table-view");
		categoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		//columns for table
		TableColumn<Category, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
		idColumn.setMinWidth(50);
		idColumn.getStyleClass().add("column-center");

		TableColumn<Category, String> nameColumn = new TableColumn<>("Category Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setMinWidth(200);

		TableColumn<Category, String> descriptionColumn = new TableColumn<>("Description");
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
		descriptionColumn.setMinWidth(300);

		categoryTable.getColumns().addAll(idColumn, nameColumn, descriptionColumn);

		categoryList = FXCollections.observableArrayList();
		categoryTable.setItems(categoryList);

		refreshTable();
		updateCategoryCountLabel(categoryCountLabel);

		searchButton.setOnAction(e -> {
			search();
			updateCategoryCountLabel(categoryCountLabel);
		});

		showAllButton.setOnAction(e -> {
			searchField.clear();
			refreshTable();
			updateCategoryCountLabel(categoryCountLabel);
		});

		searchField.setOnAction(e -> {
			search();
			updateCategoryCountLabel(categoryCountLabel);
		});

		addButton.setOnAction(e -> showAddScreen());

		editButton.setOnAction(e -> {

			Category category = categoryTable.getSelectionModel().getSelectedItem();
			if (category == null) {
				AlertManager.showError("Please select a category to edit ");
				return;
			}
			showEditScreen(category);
		});

		deleteButton.setOnAction(e -> {

			Category category = categoryTable.getSelectionModel().getSelectedItem();
			if (category == null) {
				AlertManager.showError("Please select a category to delete ");
				return;
			}

			if (AlertManager
					.showConfirmation("Are you sure you want to delete category : " + category.getName() + " ?")) {

				boolean done = DatabaseOperationsCategory.deleteCategory(category.getCategoryId());
				if (done) {
					refreshTable();
					updateCategoryCountLabel(categoryCountLabel);
					AlertManager.showInformationMessage("Category deleted successfully");
				} else {
					AlertManager.showError("Failed to delete category from database");
				}
			}
		});

		backButton.setOnAction(e -> {
			try {
				AdminScreen screen = new AdminScreen(primaryStage);
				screen.show();
			} catch (Exception ex) {

				AlertManager.showError("Error returning to dashboard : " + ex.getMessage());
			}
		});

		VBox mainVBox = new VBox(15);
		mainVBox.setPadding(new Insets(20));
		mainVBox.getStyleClass().add("main-content");
		mainVBox.getChildren().addAll(searchBox, buttonBox, titleBox, categoryCountLabel, categoryTable);

		root.setCenter(mainVBox);

		Scene scene = new Scene(root, 1200, 800);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Categories Management");
		primaryStage.show();

	}

	private void updateCategoryCountLabel(Label label) {
		
		int totalCategories = DatabaseOperationsCategory.getCategoryCount();
		String countText = "Total Categories : " + totalCategories;
		label.setText(countText);
	}

	private void search() {

		String searchWord = searchField.getText().trim();
		String searchType = searchTypeCombo.getValue();

		if (searchWord.isEmpty()) {
			refreshTable();
			return;
		}

		ObservableList<Category> searchResults;

		if (searchType.equals("ID")) {
			try {
				int categoryId = Integer.parseInt(searchWord);
				Category category = DatabaseOperationsCategory.getCategoryById(categoryId);
				searchResults = FXCollections.observableArrayList();
				if (category != null) {
					searchResults.add(category);
				} else {
					AlertManager.showInformationMessage("No category found with ID : " + searchWord);
				}
			} catch (NumberFormatException e) {
				AlertManager.showError("Invalid ID \nPlease enter a valid numeric ID");
				return;
			}
		} else {
			searchResults = DatabaseOperationsCategory.searchCategories(searchWord);
		}

		updateTable(searchResults);

		if (searchResults.isEmpty() && !searchType.equals("ID")) {
			AlertManager.showInformationMessage("No categories found for : " + searchWord);
		}
	}

	private Button createStyledButton(String text, String styleClass) {
		Button button = new Button(text);
		button.getStyleClass().add(styleClass);
		button.setPrefSize(150, 40);
		return button;
	}

	private void showAddScreen() {

		BorderPane rootAddScreen = new BorderPane();
		try {
			rootAddScreen.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		} catch (Exception e) {
			AlertManager.showError("CSS file not found");
		}

		rootAddScreen.getStyleClass().add("root-pane");

		VBox addBox = new VBox(15);
		addBox.setPadding(new Insets(30));
		addBox.setAlignment(Pos.CENTER);
		addBox.getStyleClass().add("add-edit-box");

		Label title = new Label("Add New Category");
		title.getStyleClass().add("add-edit-title");

		Label categoryIdLabel = new Label("Category ID will be assigned automatically");
		categoryIdLabel.getStyleClass().add("info-label");

		TextField nameTextField = createStyledTextField("Category Name", "");
		TextArea descTextField = createStyledTextArea("Description", "");

		HBox buttons = new HBox(20);
		buttons.setAlignment(Pos.CENTER);

		Button saveButton = createPinkButton("Save", "#ff69b4", "#ff1493");
		Button backButton = createPinkButton("Back", "#ffb6c1", "#db7093");

		buttons.getChildren().addAll(saveButton, backButton);

		addBox.getChildren().addAll(title, categoryIdLabel, nameTextField, descTextField, buttons);

		BorderPane container = new BorderPane(addBox);
		container.getStyleClass().add("add-edit-container");
		rootAddScreen.setCenter(container);

		backButton.setOnAction(e -> show());

		saveButton.setOnAction(e -> {
			if (!validateInput(nameTextField, descTextField)) {
				return;
			}

			if (DatabaseOperationsCategory.isCategoryNameExists(nameTextField.getText().trim(), 0)) {
				AlertManager.showError("Category name already exists \nPlease choose a different name");
				return;
			}

			saveCategory(nameTextField, descTextField);
		});

		Scene scene = new Scene(rootAddScreen, 1200, 800);
		primaryStage.setScene(scene);
	}

	private void showEditScreen(Category category) {

		BorderPane rootEditScreen = new BorderPane();
		try {
			rootEditScreen.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		} catch (Exception e) {
			AlertManager.showError("CSS file not found");
		}
		rootEditScreen.getStyleClass().add("root-pane");

		VBox editBox = new VBox(15);
		editBox.setPadding(new Insets(30));
		editBox.setAlignment(Pos.CENTER);
		editBox.getStyleClass().add("add-edit-box");

		Label title = new Label("Edit Category");
		title.getStyleClass().add("add-edit-title");

		Label categoryIdLabel = new Label("Category ID: " + category.getCategoryId());
		categoryIdLabel.getStyleClass().add("info-label");

		TextField nameTextField = createStyledTextField("Category Name", category.getName());
		TextArea descTextField = createStyledTextArea("Description", category.getDescription());

		HBox buttons = new HBox(20);
		buttons.setAlignment(Pos.CENTER);

		Button saveButton = createPinkButton("Update", "#ff69b4", "#ff1493");
		Button backButton = createPinkButton("Cancel", "#ffb6c1", "#db7093");

		buttons.getChildren().addAll(saveButton, backButton);

		editBox.getChildren().addAll(title, categoryIdLabel, nameTextField, descTextField, buttons);

		BorderPane borderPane = new BorderPane(editBox);
		borderPane.getStyleClass().add("add-edit-container");
		rootEditScreen.setCenter(borderPane);

		backButton.setOnAction(e -> show());

		saveButton.setOnAction(e -> {
			if (!validateInput(nameTextField, descTextField)) {
				return;
			}

			if (DatabaseOperationsCategory.isCategoryNameExists(nameTextField.getText().trim(),
					category.getCategoryId())) {
				AlertManager.showError("Category name already exists!\nPlease choose a different name");
				return;
			}

			updateCategory(category, nameTextField, descTextField);
		});

		Scene scene = new Scene(rootEditScreen, 1200, 800);
		primaryStage.setScene(scene);
	}

	private boolean validateInput(TextField name, TextArea description) {

		String categoryName = name.getText().trim();
		String categoryDescription = description.getText().trim();

		if (categoryName.isEmpty()) {
			AlertManager.showError("Please enter category name!");
			name.requestFocus();
			return false;
		}

		if (categoryDescription.isEmpty()) {
			AlertManager.showError("Please enter category description!");
			description.requestFocus();
			return false;
		}

		if (categoryName.length() > 100) {
			AlertManager.showError("Category name is too long!\nMaximum 100 characters allowed.");
			name.requestFocus();
			return false;
		}

		return true;
	}

	private void saveCategory(TextField name, TextArea description) {

		Category newCategory = new Category(0, name.getText().trim(), description.getText().trim());

		boolean success = DatabaseOperationsCategory.addCategory(newCategory);

		if (success) {
			String message = "Category Added Successfully!\n" + "Category Details:\n" + "Category ID : "
					+ newCategory.getCategoryId() + "\n" + "Category Name : " + newCategory.getName() + "\n"
					+ "Description : " + newCategory.getDescription();

			AlertManager.showInformationMessage(message);
			refreshTable();
			show();
		} else {
			AlertManager.showError("Error to add category \n Please try again");
		}
	}

	private void updateCategory(Category originalCategory, TextField name, TextArea desc) {
		originalCategory.setName(name.getText().trim());
		originalCategory.setDescription(desc.getText().trim());

		boolean success = DatabaseOperationsCategory.updateCategory(originalCategory);

		if (success) {
			AlertManager.showInformationMessage("Category Updated Successfully!");
			refreshTable();
			show();
		} else {
			AlertManager.showError("Error to update category \n Please try again");
		}
	}

	public void refreshTable() {
		ObservableList<Category> allCategories = DatabaseOperationsCategory.getAllCategories();
		updateTable(allCategories);
	}

	private void updateTable(ObservableList<Category> newData) {
		Platform.runLater(() -> {
			categoryList.clear();
			categoryList.addAll(newData);
		});
	}

	private Button createPinkButton(String text, String baseColor, String hoverColor) {
		Button button = new Button(text);
		button.setStyle("-fx-background-color: " + baseColor + "; " + "-fx-text-fill: white; -fx-font-size: 14px; "
				+ "-fx-padding: 10px 20px; -fx-font-weight: bold; "
				+ "-fx-background-radius: 15; -fx-border-radius: 15; "
				+ "-fx-cursor: hand; -fx-font-family: 'Arial Rounded MT Bold';");

		button.setOnMouseEntered(e -> {
			button.setStyle("-fx-background-color: " + hoverColor + "; " + "-fx-text-fill: white; -fx-font-size: 14px; "
					+ "-fx-padding: 10px 20px; -fx-font-weight: bold; "
					+ "-fx-background-radius: 15; -fx-border-radius: 15; "
					+ "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");
		});

		button.setOnMouseExited(e -> {
			button.setStyle("-fx-background-color: " + baseColor + "; " + "-fx-text-fill: white; -fx-font-size: 14px; "
					+ "-fx-padding: 10px 20px; -fx-font-weight: bold; "
					+ "-fx-background-radius: 15; -fx-border-radius: 15; " + "-fx-cursor: hand;");
		});

		button.setOnMousePressed(e -> {
			button.setStyle("-fx-background-color: #c71585; " + "-fx-text-fill: white; -fx-font-size: 14px; "
					+ "-fx-padding: 10px 20px; -fx-font-weight: bold; "
					+ "-fx-background-radius: 15; -fx-border-radius: 15; " + "-fx-cursor: hand; -fx-translate-y: 2px;");
		});

		button.setOnMouseReleased(e -> {
			button.setStyle("-fx-background-color: " + baseColor + "; " + "-fx-text-fill: white; -fx-font-size: 14px; "
					+ "-fx-padding: 10px 20px; -fx-font-weight: bold; "
					+ "-fx-background-radius: 15; -fx-border-radius: 15; " + "-fx-cursor: hand; -fx-translate-y: 0px;");
		});

		return button;
	}

	private TextField createStyledTextField(String promptText, String initialValue) {
		TextField textField = new TextField(initialValue);
		textField.setPromptText(promptText);
		textField.setMaxWidth(400);
		textField.setPrefHeight(40);
		textField.getStyleClass().add("styled-text-field");
		return textField;
	}

	private TextArea createStyledTextArea(String promptText, String initialValue) {
		TextArea textArea = new TextArea(initialValue);
		textArea.setPromptText(promptText);
		textArea.setMaxWidth(400);
		textArea.setPrefHeight(150);
		textArea.setWrapText(true);
		textArea.getStyleClass().add("styled-text-area");
		return textArea;
	}
}