
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class SupplierManagement {

    public static TableView<Supplier> table;
    static ObservableList<Supplier> data;
    private Stage stage;
    private BorderPane root;

    private TextField searchField;
    private ComboBox<String> searchCombo;

    public SupplierManagement(BorderPane root, Stage stage) {
        this.root = root;
        this.stage = stage;
    }

    public void show() {
//to connect to css file
        root.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        root.getStyleClass().add("root-pane");
//to put the search in it
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10, 0, 10, 0));
        box.getStyleClass().add("search-box");

        searchCombo = new ComboBox<>();
        searchCombo.getItems().addAll("Id", "Name");
        searchCombo.setValue("Id");
        searchCombo.setPrefWidth(100);
        searchCombo.getStyleClass().add("search-combo");

        searchField = new TextField();
        searchField.setPromptText("enter id or Name...");
        searchField.setPrefWidth(200);
        searchField.getStyleClass().add("search-field");

        Button searchButton = new Button("Search");
        searchButton.getStyleClass().add("button-normal");

        Button showAll = new Button("show all");
        showAll.getStyleClass().add("button-normal");

        box.getChildren().addAll(new Label("Search by:"), searchCombo, searchField, searchButton,
                showAll);
//he buttons
        HBox buttonHBox = new HBox(20);
        buttonHBox.getStyleClass().add("button-box");


        Button add = createStyledButton("Add Supplier", "button-normal");
        Button edit = createStyledButton("Edit Supplier", "button-normal");
        Button delete = createStyledButton("Delete Supplier", "button-normal");
        Button purchase = createStyledButton("Purchase", "button-normal");
        Button back = createStyledButton("Back", "button-back");

        buttonHBox.getChildren().addAll(add, edit, delete, purchase, back);
//title
        Label titleLabel = new Label("Supplier Management");
        titleLabel.getStyleClass().add("title-label");

        VBox titleBox = new VBox(10);
        titleBox.getStyleClass().add("title-box");
        titleBox.getChildren().addAll(titleLabel);

        Label supplierLabel = new Label();
        supplierLabel.getStyleClass().add("supplier-count-label");
//table view
        table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Supplier, Integer> id = new TableColumn<>("Id");
        id.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        id.setMinWidth(50);
        id.getStyleClass().add("column-center");

        TableColumn<Supplier, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        name.setMinWidth(150);

        TableColumn<Supplier, String> contact = new TableColumn<>("Contact_Info");
        contact.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
        contact.setMinWidth(80);
        contact.getStyleClass().add("column-right");

        TableColumn<Supplier, String> country = new TableColumn<>("Country");
        country.setCellValueFactory(new PropertyValueFactory<>("country"));
        country.setMinWidth(100);

        table.getColumns().addAll(id, name, contact, country);

        data = FXCollections.observableArrayList();
        table.setItems(data);

        refreshTable();
        updateSupplierLabel(supplierLabel);

        searchButton.setOnAction(e -> {
            performSearch();
            updateSupplierLabel(supplierLabel);
        });

        showAll.setOnAction(e -> {
            searchField.clear();
            refreshTable();
            updateSupplierLabel(supplierLabel);
        });

        searchField.setOnAction(e -> {
            performSearch();
            updateSupplierLabel(supplierLabel);
        });

        add.setOnAction(c -> addScreen());

        edit.setOnAction(n -> {
            Supplier supplier = table.getSelectionModel().getSelectedItem();
            if (supplier == null) {
                AlertManager.showError("please select a supplier ");
                return;
            }
            editScreen(supplier);
        });


        delete.setOnAction(m -> {
            Supplier supplier = table.getSelectionModel().getSelectedItem();
            if (supplier == null) {
                AlertManager.showError("please select a supplier first");
                return;
            }
            if (AlertManager.showConfirmation("are you sure you want to delete this  supplier: " + supplier.getSupplierName() + "?")) {
                boolean success = DataBaseOperationSupplier.deleteSupplier(supplier.getSupplierId());
                if (success) {
                    refreshTable();
                    updateSupplierLabel(supplierLabel);
                    AlertManager.showInformationMessage("supplier deleted successfully");
                } else {
                    AlertManager.showError("failed to delete supplier from database");
                }
            }
        });

        purchase.setOnAction(e -> {
            PurchaseManagements purchaseScreen = new PurchaseManagements(root, stage);
            purchaseScreen.show();
        });


        back.setOnAction(e -> {
            try {
                AdminScreen dashboard = new AdminScreen(stage);
                dashboard.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox main = new VBox(15);
        main.getStyleClass().add("main-content");
        main.getChildren().addAll(box, buttonHBox, titleBox, supplierLabel, table);

        root.setCenter(main);
    }

    private void updateSupplierLabel(Label label) {
        int totalSupplier = DataBaseOperationSupplier.getSupplierCount();
        String countText = "Total Suppliers: " + totalSupplier;
        label.setText(countText);
    }

    //the search ,the user will enter  keyword to search then click on the search button
    private void performSearch() {
        String keyword = searchField.getText().trim();
        String searchType = searchCombo.getValue();

        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }

        ObservableList<Supplier> searchResults;
//to search by id
        if ("Id".equals(searchType)) {
            try {
                int supplierid = Integer.parseInt(keyword);
                Supplier supplier = DataBaseOperationSupplier.getSupplierById(supplierid);
                searchResults = FXCollections.observableArrayList();
                if (supplier != null) {
                    searchResults.add(supplier);
                }
            } catch (NumberFormatException e) {
                AlertManager.showError("invalid id \n please enter a valid id");
                return;
            }
        } else {
            searchResults = searchSupplierByName(keyword);
        }

        updateTableData(searchResults);

        if (searchResults.isEmpty()) {
            AlertManager.showInformationMessage("no supplier found of: " + keyword);
        }
    }

    //to search by name
    private ObservableList<Supplier> searchSupplierByName(String keyword) {
        ObservableList<Supplier> allSupplier = DataBaseOperationSupplier.getAllSupplier();
        ObservableList<Supplier> filteredSupplier = FXCollections.observableArrayList();

        for (Supplier supplier : allSupplier) {
            if (supplier.getSupplierName().toLowerCase().contains(keyword.toLowerCase())) {
                filteredSupplier.add(supplier);
            }
        }

        return filteredSupplier;
    }

    private void updateTableData(ObservableList<Supplier> newData) {
        Platform.runLater(() -> {
            data.clear();
            data.addAll(newData);
        });
    }

    //to get styled button
    private Button createStyledButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        return button;
    }

    // add screen
    private void addScreen() {
        VBox addBox = new VBox(15);
        addBox.setPadding(new Insets(30));
        addBox.setAlignment(Pos.CENTER);
        addBox.getStyleClass().add("add-edit-box");

        Label title = new Label("Add New Supplier");
        title.getStyleClass().add("add-edit-title");

        Label supplierIdLabel = new Label("supplier id will be  automatically");
        supplierIdLabel.getStyleClass().add("info-label");


        TextField name = createStyledTextField("Supplier name", "");
        TextField contact = createStyledTextField("Contact_info", "");
        TextField country = createStyledTextField("Country", "");


        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button save = createPinkButton("Save", "#ff69b4", "#ff1493");
        Button back = createPinkButton("Back", "#ffb6c1", "#db7093");

        buttons.getChildren().addAll(save, back);

        addBox.getChildren().addAll(title, supplierIdLabel, name, contact, country, buttons);

        BorderPane container = new BorderPane(addBox);
        container.getStyleClass().add("add-edit-container");
        root.setCenter(container);

        back.setOnAction(e -> show());

        save.setOnAction(e -> {
            if (!validateInput(name, contact, country)) {
                return;
            }
            saveSupplier(name, contact, country);
        });
    }

    // edit screen
    private void editScreen(Supplier supplier) {
        VBox editBox = new VBox(15);
        editBox.setPadding(new Insets(30));
        editBox.setAlignment(Pos.CENTER);
        editBox.getStyleClass().add("add-edit-box");

        Label title = new Label("Edit Supplier ");
        title.getStyleClass().add("add-edit-title");

        Label supplierIdLabel = new Label("Supplier id: " + supplier.getSupplierId());
        supplierIdLabel.getStyleClass().add("info-label");

        TextField name = createStyledTextField("Supplier name", supplier.getSupplierName());
        TextField contact = createStyledTextField("Contact-info", supplier.getContactInfo());
        TextField country = createStyledTextField("Country", supplier.getCountry());

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button save = createPinkButton("Update", "#ff69b4", "#ff1493");
        Button back = createPinkButton("Cancel", "#ffb6c1", "#db7093");

        buttons.getChildren().addAll(save, back);

        editBox.getChildren().addAll(title, supplierIdLabel, name, contact, country, buttons);

        BorderPane container = new BorderPane(editBox);
        container.getStyleClass().add("add-edit-container");
        root.setCenter(container);


        back.setOnAction(e -> show());

        save.setOnAction(e -> {
            if (!validateInput(name, contact, country)) {
                return;
            }
            updateSupplier(supplier, name, contact, country);
        });
    }

    private boolean validateInput(TextField name, TextField contact, TextField country) {
        if (name.getText().trim().isEmpty() || contact.getText().trim().isEmpty() || country.getText().trim().isEmpty()) {
            AlertManager.showError("please fill in all fields first");
            return false;
        }
        return true;
    }

    private void saveSupplier(TextField name, TextField contact, TextField country) {
        Supplier newSupplier = new Supplier(0, name.getText().trim(), contact.getText().trim(),
                country.getText().trim());

        boolean success = DataBaseOperationSupplier.addSupplier(newSupplier);

        if (success) {
            String message = "Supplier added successfully\n\n" + "Supplier details:\n" + "• Supplier id: "
                    + newSupplier.getSupplierId() + "\n" + "• Supplier name: " + newSupplier.getSupplierName() + "\n"
                    + "• Contact: "
                    + newSupplier.getContactInfo() + "\n" + "• Country: " + newSupplier.getCountry();

            AlertManager.showInformationMessage(message);
            refreshTable();
            show();
        } else {
            AlertManager.showError("failed to add supplier");
        }
    }

    private void updateSupplier(Supplier originalSupplier, TextField name, TextField contact, TextField country) {
        originalSupplier.setSupplierName(name.getText().trim());
        originalSupplier.setContactInfo(contact.getText().trim());
        originalSupplier.setCountry(country.getText().trim());

        boolean success = DataBaseOperationSupplier.updateSupplier(originalSupplier);

        if (success) {
            AlertManager.showInformationMessage("supplier updated successfully");
            refreshTable();
            show();
        } else {
            AlertManager.showError("failed to update supplier");
        }
    }

    //to refresh the data after edit it
    public void refreshTable() {
        ObservableList<Supplier> allSuppliers = DataBaseOperationSupplier.getAllSupplier();
        updateTableData(allSuppliers);
    }

    //to edit button to get styled when use it
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


}