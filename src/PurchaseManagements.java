import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PurchaseManagements {
    private Integer supplierId;
    public static TableView<Purchase> table;
    static ObservableList<Purchase> data;
    private Stage primaryStage;
    private TextField searchField;
    private ComboBox<String> searchCombo;
    private BorderPane root;
    public static ObservableList<Purchase> purchaseList = FXCollections.observableArrayList();

    private String selectedSupplierName;


    public PurchaseManagements(BorderPane root, Stage primaryStage) {
        this.root = root;
        this.primaryStage = primaryStage;
        this.supplierId = null;
    }
    public PurchaseManagements(BorderPane root, Stage primaryStage, Integer supplierId, String supplierName) {
        this.root = root;
        this.primaryStage = primaryStage;
        this.supplierId = supplierId;
        this.selectedSupplierName = supplierName;
    }

    public void show() {
        root.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        root.getStyleClass().add("root-pane");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10, 0, 10, 0));
        searchBox.getStyleClass().add("search-box");

        searchCombo = new ComboBox<>();
        searchCombo.getItems().addAll("purchase id", "supplier id", "date");
        searchCombo.setValue("Purchase ID");
        searchCombo.setPrefWidth(100);
        searchCombo.getStyleClass().add("search-combo");

        searchField = new TextField();
        searchField.setPromptText("enter purchase id or supplier id or date");
        searchField.setPrefWidth(200);
        searchField.getStyleClass().add("search-field");

        Button search= new Button("search");
        search.getStyleClass().add("button-normal");

        Button showAll = new Button("show all");
        showAll.getStyleClass().add("button-normal");

        searchBox.getChildren().addAll(new Label("Search by:"), searchCombo, searchField, search,
                showAll);
//buttons
        HBox buttonBox = new HBox(10);
        buttonBox.getStyleClass().add("button-box");
        buttonBox.setAlignment(Pos.CENTER);

        Button select = createStyledButton("select supplier", "button-normal");
        Button add = createStyledButton("Add Purchase", "button-normal");
        Button edit= createStyledButton("Edit Purchase", "button-normal");
        Button delete = createStyledButton("Delete Purchase", "button-normal");
        Button addItem = createStyledButton("Add item", "button-normal");
        Button back= createStyledButton("back", "button-normal");

        add.setDisable(supplierId == null);
        addItem.setDisable(supplierId == null);

        buttonBox.getChildren().addAll(select, add, edit, delete, addItem, back);
//title
        Label titleLabel = new Label("Purchase Management");
        titleLabel.getStyleClass().add("title-label");


        Label supplierLabel = new Label();
        supplierLabel.getStyleClass().add("selected-supplier-label");
        if (supplierId != null) {
            if (selectedSupplierName != null) {
                supplierLabel.setText("selected supplier: " + selectedSupplierName + " (id: " + supplierId + ")");
            } else {
                supplierLabel.setText("selected supplier id: " + supplierId);
            }
        } else {
            supplierLabel.setText("no supplier selected , please select a supplier first");
        }

        VBox titleBox = new VBox(10);
        titleBox.getStyleClass().add("title-box");
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().addAll(titleLabel, supplierLabel);

        Label purchaseCountLabel = new Label();
        purchaseCountLabel.getStyleClass().add("purchase-count-label");
//table
        table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Purchase, Integer> id = new TableColumn<>("Id");
        id.setCellValueFactory(new PropertyValueFactory<>("purchase_id"));
        id.setMinWidth(50);
        id.getStyleClass().add("column-center");

        TableColumn<Purchase, Integer> supplier = new TableColumn<>("Supplier Id");
        supplier.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getSupplier().getSupplierId()).asObject());
        supplier.setMinWidth(80);
        supplier.getStyleClass().add("column-center");

        TableColumn<Purchase, Date> date = new TableColumn<>("Date");
        date.setCellValueFactory(new PropertyValueFactory<>("Purchase_date"));
        date.setMinWidth(150);

        TableColumn<Purchase, Double> total_cost = new TableColumn<>("Total Cost");
        total_cost.setCellValueFactory(new PropertyValueFactory<>("total_cost"));
        total_cost.setMinWidth(80);
        total_cost.getStyleClass().add("column-right");

        table.getColumns().addAll(id, supplier, date, total_cost);

        data = FXCollections.observableArrayList();
        table.setItems(data);

        refreshTable();
        updatePurchaseLabel(purchaseCountLabel);
//operation
        search.setOnAction(e -> {
            performSearch();
            updatePurchaseLabel(purchaseCountLabel);
        });

        showAll.setOnAction(e -> {
            searchField.clear();
            refreshTable();
            updatePurchaseLabel(purchaseCountLabel);
        });

        searchField.setOnAction(e -> {
            performSearch();
            updatePurchaseLabel(purchaseCountLabel);
        });

        select.setOnAction(e -> {
            showSupplierSelection();
        });

        addItem.setOnAction(e -> {
            Purchase selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertManager.showError("please select a purchase first");
                return;
            }

            if (supplierId == null) {
                AlertManager.showError("Please select a supplier first");
                return;
            }

            int purchaseId = selected.getPurchase_id();

            PurchaseItemManagement p = new PurchaseItemManagement(root, primaryStage, purchaseId);
            p.show();
        });


        add.setOnAction(c -> addScreen());

        edit.setOnAction(n -> {
            Purchase item = table.getSelectionModel().getSelectedItem();
            if (item == null) {
                AlertManager.showError("please select a purchase to edit");
                return;
            }
            showEditScreen(item);
        });

        delete.setOnAction(m -> {
            Purchase purchaseItem = table.getSelectionModel().getSelectedItem();
            if (purchaseItem == null) {
                AlertManager.showError("Please select a purchase to delete");
                return;
            }

            if (AlertManager.showConfirmation(
                    "Are you sure you want to delete purchase: " + purchaseItem.getPurchase_id() + "?")) {

                boolean success = DatabaseOperationsArchive.deletePurchase(purchaseItem.getPurchase_id());

                if (success) {
                    refreshTable();
                    updatePurchaseLabel(purchaseCountLabel);
                    AlertManager.showInformationMessage("Purchase deleted successfully!");
                } else {
                    AlertManager.showError("Failed to delete purchase from database!");
                }
            }
        });

        back.setOnAction(e -> {
            try {
                AdminScreen dashboard = new AdminScreen(primaryStage);
                dashboard.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox mainContent = new VBox(15);
        mainContent.getStyleClass().add("main-content");
        mainContent.setPadding(new Insets(10));
        mainContent.getChildren().addAll(searchBox, titleBox, purchaseCountLabel, buttonBox, table);

        root.setCenter(mainContent);
    }
// dialog to show supplier for the operation
    private void showSupplierSelection() {
        Dialog<Pair<Integer, String>> dialog = new Dialog<>();
        dialog.setTitle("select Supplier");
        dialog.setHeaderText("choose a supplier for purchase operations");

        ButtonType selectButtonType = new ButtonType("select", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> supplierCombo = new ComboBox<>();
        supplierCombo.setPrefWidth(250);

        ObservableList<Supplier> suppliers = DataBaseOperationSupplier.getAllSuppliers();
        ObservableList<String> supplierNames = FXCollections.observableArrayList();

      //i used to for faster perfurmanse and easy to find supplier
        Map<String, Integer> supplierMap = new HashMap<>();

        if (suppliers.isEmpty()) {
            supplierCombo.setPromptText("no suppliers available");
            supplierCombo.setDisable(true);
        } else {
            for (Supplier supplier : suppliers) {
                String displayText = "Id: " + supplier.getSupplierId() + " - " + supplier.getSupplierName() +
                        " (" + supplier.getContactInfo() + ")";
                supplierNames.add(displayText);
                supplierMap.put(displayText, supplier.getSupplierId());
            }
            supplierCombo.setItems(supplierNames);

            if (supplierId != null && selectedSupplierName != null) {
                for (Map.Entry<String, Integer> entry : supplierMap.entrySet()) {
                    if (entry.getValue().equals(supplierId)) {
                        supplierCombo.setValue(entry.getKey());
                        break;
                    }
                }
            }
        }

        grid.add(new Label("supplier:"), 0, 0);
        grid.add(supplierCombo, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> supplierCombo.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == selectButtonType) {
                String selected = supplierCombo.getValue();
                if (selected != null && !selected.isEmpty()) {
                    Integer id = supplierMap.get(selected);
                    String name = selected.split(" - ")[1].split(" \\(")[0];
                    return new Pair<>(id, name);
                }
            }
            return null;
        });

     // to take result from user and show in table,take id and name then refresh
        Optional<Pair<Integer, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            supplierId = pair.getKey();
            selectedSupplierName = pair.getValue();
            refreshTable();
            show();
        });
    }

    private void updatePurchaseLabel(Label label) {
        int totalPurchase;
        if (supplierId != null) {
            totalPurchase = DatabaseOperationsPurchase.getPurchaseCountBySupplier(supplierId);
        } else {
            totalPurchase = DatabaseOperationsPurchase.getPurchaseCount();
        }
        String countText = "Total Purchases: " + totalPurchase;
        label.setText(countText);
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        String searchType = searchCombo.getValue();

        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }

        ObservableList<Purchase> searchResults = FXCollections.observableArrayList();

        try {
            if ("Purchase Id".equals(searchType)) {
                int purchaseId = Integer.parseInt(keyword);
                Purchase purchase = DatabaseOperationsPurchase.getPurchaseById(purchaseId);
                if (purchase != null) {
                    if (supplierId == null || purchase.getSupplier().getSupplierId() == supplierId) {
                        searchResults.add(purchase);
                    }
                }
            } else if ("Supplier Id".equals(searchType)) {
                int supId = Integer.parseInt(keyword);
                if (supplierId == null || supId == supplierId) {
                    searchResults = DatabaseOperationsPurchase.getPurchasesBySupplier(supId);
                }
            } else if ("Date".equals(searchType)) {
                searchResults = searchPurchasesByDate(keyword);
            }
        } catch (NumberFormatException e) {
            AlertManager.showError("invalid id. please enter a valid  id");
            return;
        }

        updateTableData(searchResults);

        if (searchResults.isEmpty()) {
            AlertManager.showInformationMessage("no purchases found for: " + keyword);
        }
    }

    private ObservableList<Purchase> searchPurchasesByDate(String keyword) {
        ObservableList<Purchase> allPurchases;
        if (supplierId != null) {
            allPurchases = DatabaseOperationsPurchase.getPurchasesBySupplier(supplierId);
        } else {
            allPurchases = DatabaseOperationsPurchase.getAllPurchase();
        }

        ObservableList<Purchase> filteredPurchases = FXCollections.observableArrayList();

        for (Purchase purchase : allPurchases) {
            if (purchase.getPurchase_date().toString().contains(keyword)) {
                filteredPurchases.add(purchase);
            }
        }

        return filteredPurchases;
    }

    private void updateTableData(ObservableList<Purchase> newData) {
        Platform.runLater(() -> {
            data.clear();
            data.addAll(newData);
        });
    }

    private Button createStyledButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        button.setMinWidth(Region.USE_PREF_SIZE);
        button.setPrefWidth(Region.USE_COMPUTED_SIZE);
        return button;
    }

    // add
    private void addScreen() {
        if (supplierId == null) {
            AlertManager.showError("please select a supplier first");
            return;
        }

        VBox addBox = new VBox(15);
        addBox.setPadding(new Insets(30));
        addBox.setAlignment(Pos.CENTER);
        addBox.getStyleClass().add("add-edit-box");

        Label title = new Label("Add New Purchase");
        title.getStyleClass().add("add-edit-title");

        Label itemId = new Label("purchase id will be  automatically");
        itemId.getStyleClass().add("info-label");

        Label supplierInfo= new Label();
        if (selectedSupplierName != null) {
            supplierInfo.setText("Supplier: " + selectedSupplierName + " (id: " + supplierId + ")");
        } else {
            supplierInfo.setText("Supplier Id: " + supplierId);
        }
        supplierInfo.getStyleClass().add("info-label");

        Button change= new Button("change supplier");
        change.getStyleClass().add("button-small");
        change.setOnAction(e -> {
            showSupplierSelection();
        });

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Purchase Date");
        datePicker.setMaxWidth(350);
        datePicker.setPrefHeight(40);
        datePicker.getStyleClass().add("styled-text-field");

        Label note= new Label("note: Total cost will be calculated automatically after adding items");
        note.getStyleClass().add("info-label");
        note.setStyle("-fx-text-fill: #007bff; -fx-font-weight: bold;");

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button save = createPinkButton("Save", "#ff69b4", "#ff1493");
        Button back = createPinkButton("Back", "#ffb6c1", "#db7093");

        buttons.getChildren().addAll(save, back);

        addBox.getChildren().addAll(title, itemId, supplierInfo,
                change, datePicker, note, buttons);

        BorderPane container = new BorderPane(addBox);
        container.getStyleClass().add("add-edit-container");
        root.setCenter(container);

        back.setOnAction(e -> show());

        save.setOnAction(e -> {
            if (datePicker.getValue() == null) {
                AlertManager.showError("please select a purchase date");
                return;
            }
            savePurchase(datePicker);
        });
    }

    private void showEditScreen(Purchase purchase) {
        VBox editBox = new VBox(15);
        editBox.setPadding(new Insets(30));
        editBox.setAlignment(Pos.CENTER);
        editBox.getStyleClass().add("add-edit-box");

        Label title = new Label("Edit Purchase");
        title.getStyleClass().add("add-edit-title");

        Label purchaseId= new Label("Purchase ID: " + purchase.getPurchase_id());
        purchaseId.getStyleClass().add("info-label");

        Label supplierInfo = new Label();
        if (selectedSupplierName != null) {
            supplierInfo.setText("Supplier: " + selectedSupplierName + " (ID: " + supplierId + ")");
        } else {
            supplierInfo.setText("Supplier ID: " + supplierId);
        }
        supplierInfo.getStyleClass().add("info-label");

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(purchase.getPurchase_date().toLocalDate());
        datePicker.setMaxWidth(350);
        datePicker.setPrefHeight(40);
        datePicker.getStyleClass().add("styled-text-field");

        Label costLabel = new Label("Current Total Cost: $" + purchase.getTotal_cost());
        costLabel.getStyleClass().add("info-label");
        costLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #28a745;");

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button save = createPinkButton("Update", "#ff69b4", "#ff1493");
        Button back = createPinkButton("Cancel", "#ffb6c1", "#db7093");

        buttons.getChildren().addAll(save, back);

        editBox.getChildren().addAll(title, purchaseId, supplierInfo,
                datePicker, costLabel, buttons);

        BorderPane container = new BorderPane(editBox);
        container.getStyleClass().add("add-edit-container");
        root.setCenter(container);

        back.setOnAction(e -> show());

        save.setOnAction(e -> {
            if (datePicker.getValue() == null) {
                AlertManager.showError("please select a purchase date!");
                return;
            }
            updatePurchaseDate(purchase, datePicker);
        });
    }

    private void savePurchase(DatePicker datePicker) {
        Date purchaseDate = Date.valueOf(datePicker.getValue());

        Purchase newPurchase = new Purchase(
                0,
                0.0,
                new Supplier(supplierId, selectedSupplierName, "", "")
        );

        boolean success = DatabaseOperationsPurchase.addPurchase(newPurchase);

        if (success) {
            AlertManager.showInformationMessage("purchase added successfully for supplier: " + selectedSupplierName +
                    "\nnow you can add items to this purchase");
            refreshTable();
            show();
        } else {
            AlertManager.showError("failed to add purchase");
        }
    }

    private void updatePurchaseDate(Purchase purchase, DatePicker datePicker) {
        purchase.setPurchase_date(Date.valueOf(datePicker.getValue()));
        boolean success = DatabaseOperationsPurchase.updatePurchase(purchase);

        if (success) {
            AlertManager.showInformationMessage("purchase date updated successfully");
            refreshTable();
            show();
        } else {
            AlertManager.showError("failed to update purchase");
        }
    }
//to refresh data after edit
    public void refreshTable() {
        ObservableList<Purchase> allPurchases;
        if (supplierId != null) {
            allPurchases = DatabaseOperationsPurchase.getPurchasesBySupplier(supplierId);
        } else {
            allPurchases = DatabaseOperationsPurchase.getAllPurchase();
        }
        updateTableData(allPurchases);
    }
//styled buttons
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

        return button;
    }


}