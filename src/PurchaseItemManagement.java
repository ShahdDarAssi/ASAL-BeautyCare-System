import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PurchaseItemManagement {
    private int purchaseId;
    private Stage primaryStage;
    private BorderPane root;
    private TableView<Purchase_item> table;
    private ObservableList<Purchase_item> data;
    private Label itemLabel;
    private ComboBox<String> searchCombo;
    private TextField searchField;

    public PurchaseItemManagement(BorderPane root, Stage stage, int purchaseId) {
        this.root = root;
        this.primaryStage = stage;
        this.purchaseId = purchaseId;
    }

    // main
    public void show() {
        root.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        root.getStyleClass().add("root-pane");

        // search
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10, 0, 10, 0));
        searchBox.getStyleClass().add("search-box");

        searchCombo = new ComboBox<>();
        searchCombo.getItems().addAll("Item Id", "Product Id");
        searchCombo.setValue("Item ID");
        searchCombo.setPrefWidth(100);
        searchCombo.getStyleClass().add("search-combo");

        searchField = new TextField();
        searchField.setPromptText("Enter search keyword...");
        searchField.setPrefWidth(200);
        searchField.getStyleClass().add("search-field");


        Button search = new Button("Search");
        search.getStyleClass().add("button-normal");

        Button showAll = new Button("Show All");
        showAll.getStyleClass().add("button-normal");

        searchBox.getChildren().addAll(
                new Label("Search by:"), searchCombo,
                searchField, search, showAll
        );

        // buttons
        HBox button = new HBox(20);
        button.getStyleClass().add("button-box");
        button.setAlignment(Pos.CENTER);

        Button add = createStyledButton("Add Item", "button-normal");
        Button edit= createStyledButton("Edit", "button-normal");
        Button delete = createStyledButton("Delete", "button-normal");
        Button back = createStyledButton("Back", "button-back");

        button.getChildren().addAll(add, edit, delete, back);

        // title
        Label titleLabel = new Label("Purchase Items (Purchase Id: " + purchaseId + ")");
        titleLabel.getStyleClass().add("title-label");

        VBox titleBox = new VBox(10);
        titleBox.getStyleClass().add("title-box");
        titleBox.getChildren().addAll(titleLabel);

        itemLabel = new Label();
        itemLabel.getStyleClass().add("purchase-count-label");

        // table
        table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Purchase_item, Integer> id = new TableColumn<>("Item Id");
        id.setCellValueFactory(new PropertyValueFactory<>("item_id"));
        id.setMinWidth(80);
        id.getStyleClass().add("column-center");

        TableColumn<Purchase_item, Integer> productId = new TableColumn<>("Product Id");
        productId.setCellValueFactory(c ->
                new SimpleIntegerProperty(
                        c.getValue().getProduct().getProduct_id()
                ).asObject()
        );
        productId.setMinWidth(100);
        productId.getStyleClass().add("column-center");

        TableColumn<Purchase_item, Integer> quantity = new TableColumn<>("Quantity");
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantity.setMinWidth(100);
        quantity.getStyleClass().add("column-center");

        TableColumn<Purchase_item, Integer> cost = new TableColumn<>("Cost / Unit");
        cost.setCellValueFactory(new PropertyValueFactory<>("cost_per_unit"));
        cost.setMinWidth(120);
        cost.getStyleClass().add("column-right");

        TableColumn<Purchase_item, Double> total = new TableColumn<>("Total Cost");
        total.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(
                        cellData.getValue().getQuantity() * cellData.getValue().getCost_per_unit()
                ).asObject()
        );
        total.setMinWidth(120);
        total.getStyleClass().add("column-right");

        table.getColumns().addAll(id, productId, quantity, cost, total);

        data = FXCollections.observableArrayList();
        table.setItems(data);

        refreshTable();
        updateItemLabel();

       //operations
        search.setOnAction(e -> {
            performSearch();
            updateItemLabel();
        });

        showAll.setOnAction(e -> {
            searchField.clear();
            refreshTable();
            updateItemLabel();
        });

        searchField.setOnAction(e -> {
            performSearch();
            updateItemLabel();
        });

        add.setOnAction(e -> addScreen());

        edit.setOnAction(e -> {
            Purchase_item selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertManager.showError("please select item first");
                return;
            }
            showEditScreen(selected);
        });

        delete.setOnAction(e -> {
            Purchase_item selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertManager.showError("please select item first");
                return;
            }

            if (AlertManager.showConfirmation(" do you want to delete selected item?")) {
                boolean success = DatabaseOperationsPurchaseItem.deletePurchase(selected.getItem_id());
                if (success) {
                    refreshTable();
                    updateItemLabel();
                    AlertManager.showInformationMessage("Item deleted successfully");
                } else {
                    AlertManager.showError("Failed to delete item");
                }
            }
        });

        back.setOnAction(e -> {
            PurchaseManagements p = new PurchaseManagements(root, primaryStage);
            p.show();
        });

        // main Content
        VBox main  = new VBox(15);
        main .getStyleClass().add("main-content");
        main .getChildren().addAll(searchBox, titleBox, itemLabel, button, table);

        root.setCenter(main );
    }


    private void updateItemLabel() {
        int count = data.size();
        itemLabel.setText("Total Items: " + count);
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        String searchType = searchCombo.getValue();

        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }

        ObservableList<Purchase_item> searchRes = FXCollections.observableArrayList();

        try {
            if ("Item Id".equals(searchType)) {
                int itemId = Integer.parseInt(keyword);
                Purchase_item item = DatabaseOperationsPurchaseItem.getPurchaseById(itemId);
                if (item != null && item.getPurchase().getPurchase_id() == purchaseId) {
                    searchRes.add(item);
                }
            } else if ("Product Id".equals(searchType)) {
                int productId = Integer.parseInt(keyword);
                ObservableList<Purchase_item> allItems = DatabaseOperationsPurchaseItem.getItemsByPurchaseId(purchaseId);
                for (Purchase_item item : allItems) {
                    if (item.getProduct().getProduct_id() == productId) {
                        searchRes.add(item);
                    }
                }
            }
        } catch (NumberFormatException e) {
            AlertManager.showError("Invalid ID \nPlease enter a valid numeric ID");
            return;
        }

        updateTableData(searchRes);

        if (searchRes.isEmpty()) {
            AlertManager.showInformationMessage("No items found for: " + keyword);
        }
    }

    private void updateTableData(ObservableList<Purchase_item> newData) {
        Platform.runLater(() -> {
            data.clear();
            data.addAll(newData);
        });
    }

    // add
    private void addScreen() {
        VBox addBox = new VBox(15);
        addBox.setPadding(new Insets(30));
        addBox.setAlignment(Pos.CENTER);
        addBox.getStyleClass().add("add-edit-box");

        Label title = new Label("Add Purchase Item");
        title.getStyleClass().add("add-edit-title");

        Label purchaseIdLabel = new Label("Purchase ID: " + purchaseId);
        purchaseIdLabel.getStyleClass().add("info-label");

        TextField productIdField = createStyledTextField("Product ID", "");
        TextField quantityField = createStyledTextField("Quantity", "");
        TextField costField = createStyledTextField("Cost per Unit", "");

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button update = createPinkButton("Update", "#ff69b4", "#ff1493");
        Button cancel = createPinkButton("Cancel", "#ffb6c1", "#db7093");

        buttons.getChildren().addAll(update, cancel);

        addBox.getChildren().addAll(title, purchaseIdLabel, productIdField, quantityField, costField, buttons);

        BorderPane container = new BorderPane(addBox);
        container.getStyleClass().add("add-edit-container");
        root.setCenter(container);

        cancel.setOnAction(e -> show());

        update.setOnAction(e -> {
            if (!validateInput(productIdField, quantityField, costField)) return;

            Purchase_item item = new Purchase_item(
                    0,
                    Integer.parseInt(quantityField.getText().trim()),
                    Integer.parseInt(costField.getText().trim()),
                    new Purchase(purchaseId, null,  null),
                    new Product(
                            Integer.parseInt(productIdField.getText().trim()),
                            "", 0, "", "", null
                    )
            );

            boolean success = DatabaseOperationsPurchaseItem.addPurchase(item);
            if (success) {
                AlertManager.showInformationMessage("item added successfully");
                refreshTable();
                show();
            } else {
                AlertManager.showError("failed to add item");
            }
        });
    }

    // edit
    private void showEditScreen(Purchase_item item) {
        VBox editBox = new VBox(15);
        editBox.setPadding(new Insets(30));
        editBox.setAlignment(Pos.CENTER);
        editBox.getStyleClass().add("add-edit-box");

        Label title = new Label("Edit Purchase Item");
        title.getStyleClass().add("add-edit-title");

        Label itemIdLabel = new Label("Item Id: " + item.getItem_id());
        itemIdLabel.getStyleClass().add("info-label");

        TextField productIdField = createStyledTextField("Product Id",
                String.valueOf(item.getProduct().getProduct_id()));
        TextField quantityField = createStyledTextField("Quantity",
                String.valueOf(item.getQuantity()));
        TextField costField = createStyledTextField("Cost per Unit",
                String.valueOf(item.getCost_per_unit()));
//button to save the update and cancle
        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button update = createPinkButton("Update", "#ff69b4", "#ff1493");
        Button cancel = createPinkButton("Cancel", "#ffb6c1", "#db7093");

        buttons.getChildren().addAll(update, cancel);

        editBox.getChildren().addAll(title, itemIdLabel, productIdField, quantityField, costField, buttons);

        BorderPane container = new BorderPane(editBox);
        container.getStyleClass().add("add-edit-container");
        root.setCenter(container);

        cancel.setOnAction(e -> show());

        update.setOnAction(e -> {
            if (!validateInput(productIdField, quantityField, costField)) return;

            item.setQuantity(Integer.parseInt(quantityField.getText().trim()));
            item.setCost_per_unit(Integer.parseInt(costField.getText().trim()));
            item.setProduct(new Product(
                    Integer.parseInt(productIdField.getText().trim()),
                    "", 0, "", "", null
            ));

            boolean success = DatabaseOperationsPurchaseItem.updatePurchase(item);
            if (success) {
                AlertManager.showInformationMessage("item updated successfully");
                refreshTable();
                show();
            } else {
                AlertManager.showError("failed to update item");
            }
        });
    }

 //validate
    private boolean validateInput(TextField productId, TextField quantity, TextField cost) {
        try {
            int prodId = Integer.parseInt(productId.getText().trim());
            int qty = Integer.parseInt(quantity.getText().trim());
            int cst = Integer.parseInt(cost.getText().trim());

            if (prodId <= 0) {
                AlertManager.showError("product id must be greater than 0");
                return false;
            }

            if (qty <= 0) {
                AlertManager.showError("quantity must be greater than 0");
                return false;
            }

            if (cst <= 0) {
                AlertManager.showError("cost must be greater than 0");
                return false;
            }

            return true;
        } catch (Exception e) {
            AlertManager.showError("Invalid input please enter valid numbers");
            return false;
        }
    }

    private void refreshTable() {
        ObservableList<Purchase_item> items = DatabaseOperationsPurchaseItem.getItemsByPurchaseId(purchaseId);
        updateTableData(items);
    }
//styled buttons
    private Button createStyledButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        return button;
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
}