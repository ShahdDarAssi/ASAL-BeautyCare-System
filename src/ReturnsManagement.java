
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Date;

public class ReturnsManagement {
    private Stage primaryStage;
    private BorderPane root;

    // tables
    private TableView<Returns> returnTable;
    private TableView<Returns_Product> returnProductsTable;

    // data
    private ObservableList<Returns> returnsData;
    private ObservableList<Returns_Product> returnProductsData;

    private Label totalReturnsLabel;
    private ComboBox<String> statusFilterCom;
    private TextField searchField;
    private Label statsLabel;

    public ReturnsManagement(BorderPane root, Stage primaryStage) {
        this.root = root;
        this.primaryStage = primaryStage;
        this.returnsData = FXCollections.observableArrayList();
        this.returnProductsData = FXCollections.observableArrayList();
    }

    public void show() {
        root.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        root.getStyleClass().add("root-pane");



        // search and filter
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10, 0, 10, 0));
        searchBox.getStyleClass().add("search-box");

        // status filter
        statusFilterCom = new ComboBox<>();
        statusFilterCom.getItems().addAll("all", "Requested", "Approved", "Rejected", "Processed");
        statusFilterCom.setValue("All");
        statusFilterCom.setPrefWidth(120);
        statusFilterCom.getStyleClass().add("search-combo");
        statusFilterCom.setOnAction(e -> filterReturns());

        // search
        searchField = new TextField();
        searchField.setPromptText("Search by Order id or Return id");
        searchField.setPrefWidth(250);
        searchField.getStyleClass().add("search-field");

        Button search = createStyledButton("Search", "button-normal");
        Button refresh = createStyledButton("show all", "button-normal");

        searchBox.getChildren().addAll(
                new Label("Status:"), statusFilterCom,
                new Label("Search:"), searchField,
                search, refresh
        );

        //  buttons
        HBox action = new HBox(15);
        action.setAlignment(Pos.CENTER);
        action.getStyleClass().add("button-box");

        Button viewDetails = createStyledButton("View Details", "button-normal");
        Button update = createStyledButton("Update", "button-normal");;
        Button delete = createStyledButton("Delete", "button-normal");
        Button back = createStyledButton("Back", "button-normal");

        action.getChildren().addAll(viewDetails, update, delete, back);

        // title
        Label titleLabel = new Label("Returns Management");
        titleLabel.getStyleClass().add("title-label");

        totalReturnsLabel = new Label();
        totalReturnsLabel.getStyleClass().add("purchase-count-label");

        statsLabel = new Label();
        statsLabel.getStyleClass().add("info-label");

        updateStatistics();

        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getStyleClass().add("title-box");
        titleBox.getChildren().addAll(titleLabel, totalReturnsLabel, statsLabel);
        //return table
        returnTable = createReturnsTable();
        loadReturnsData();

        returnProductsTable = createReturnProductsTable();
        returnProductsTable.setVisible(false);

        Label productsTitle = new Label("Returned Product");
        productsTitle.getStyleClass().add("subtitle-label");
        productsTitle.setVisible(false);

        VBox productsBox = new VBox(10);
        productsBox.getChildren().addAll(productsTitle, returnProductsTable);
        productsBox.setVisible(false);

        search.setOnAction(e -> performSearch());
        refresh.setOnAction(e -> {
            loadReturnsData();
            updateStatistics();
        });

        viewDetails.setOnAction(e -> viewReturnDetails());
        update.setOnAction(e -> {
            Returns returns = returnTable.getSelectionModel().getSelectedItem();
            if (returns == null) {
                AlertManager.showError("please select a supplier ");
                return;
            }
            editReturnScreen(returns);
        });
        delete.setOnAction(e -> deleteReturn());
        back.setOnAction(e -> {
            try {
                AdminScreen dashboard = new AdminScreen(primaryStage);
                dashboard.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        //main content
        VBox main = new VBox(15);
        main.getStyleClass().add("main-content");
        main.getChildren().addAll(
                searchBox, titleBox, action, returnTable, productsBox
        );

        root.setCenter(main);
    }

    // returns
    private TableView<Returns> createReturnsTable() {
        TableView<Returns> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);

        TableColumn<Returns, Integer> id = new TableColumn<>("Id");
        id.setCellValueFactory(new PropertyValueFactory<>("returnId"));
        id.setMinWidth(60);
        id.getStyleClass().add("column-center");


        TableColumn<Returns, Integer> order = new TableColumn<>("Order Id");
        order.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        order.setMinWidth(80);
        order.getStyleClass().add("column-center");

        TableColumn<Returns, Date> date = new TableColumn<>("Return Date");
        date.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        date.setMinWidth(100);
        date.getStyleClass().add("column-center");



        TableColumn<Returns, String> status = new TableColumn<>("Status");
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        status.setMinWidth(100);
        status.getStyleClass().add("column-center");

        TableColumn<Returns, Double> refund = new TableColumn<>("Refund Amount");
        refund.setCellValueFactory(new PropertyValueFactory<>("refundAmount"));
        refund.setMinWidth(100);
        refund.getStyleClass().add("column-right");


        TableColumn<Returns, String> reason = new TableColumn<>("Reason");
        reason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        reason.setMinWidth(200);
        reason.getStyleClass().add("column-left");

        table.getColumns().addAll(id, order, date, status, refund, reason);
        table.setItems(returnsData);

        return table;
    }

    private TableView<Returns_Product> createReturnProductsTable() {
        TableView<Returns_Product> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(200);


        TableColumn<Returns_Product, Integer> product = new TableColumn<>("Product Id");
        product.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getProduct().getProduct_id()).asObject()
        );
        product.setMinWidth(80);
        product.getStyleClass().add("column-center");

        TableColumn<Returns_Product, Integer> qty = new TableColumn<>("Quantity");
        qty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qty.setMinWidth(80);
        qty.getStyleClass().add("column-center");

        TableColumn<Returns_Product, Double> price = new TableColumn<>("Unit Price");
        price.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getProduct().getPrice()).asObject()
        );
        price.setMinWidth(90);
        price.getStyleClass().add("column-right");


        TableColumn<Returns_Product, Double> totals = new TableColumn<>("Total");
        totals.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(
                        cellData.getValue().getQuantity() * cellData.getValue().getProduct().getPrice()
                ).asObject()
        );
        totals.setMinWidth(90);
        totals.getStyleClass().add("column-right");

        table.getColumns().addAll(product, qty, price, totals);
        table.setItems(returnProductsData);

        return table;
    }

    // data
    private void loadReturnsData() {
        returnsData.clear();
        ObservableList<Returns> returns = DatabaseOperationsReturns.getAllReturns();
        returnsData.addAll(returns);
        updateTotalLabel();
    }

    private void loadReturnProducts(int returnId) {
        returnProductsData.clear();
        ObservableList<Returns_Product> products = DatabaseOperationsReturns.getReturnProducts(returnId);
        returnProductsData.addAll(products);
    }

    private void updateTotalLabel() {
        int total = returnsData.size();
        totalReturnsLabel.setText("Total Returns: " + total);
    }

    private void updateStatistics() {
        int requested = DatabaseOperationsReturns.getReturnCountByStatus("requested");
        int approved = DatabaseOperationsReturns.getReturnCountByStatus("approved");
        int processed = DatabaseOperationsReturns.getReturnCountByStatus("processed");
        double totalRefunds = DatabaseOperationsReturns.getTotalRefunds();

        statsLabel.setText(String.format(
                "Requested: %d | Approved: %d | Processed: %d | Total Refunds: $%.2f",
                requested, approved, processed, totalRefunds
        ));
    }

    // filter and search
    private void filterReturns() {
        String status = statusFilterCom.getValue();
        if (status.equals("all")) {
            loadReturnsData();
        } else {
            returnsData.clear();
            ObservableList<Returns> filtered = DatabaseOperationsReturns.getReturnsByStatus(status);
            returnsData.addAll(filtered);
        }
        updateTotalLabel();
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadReturnsData();
            return;
        }

        returnsData.clear();
        ObservableList<Returns> results = DatabaseOperationsReturns.searchReturns(keyword);
        returnsData.addAll(results);
        updateTotalLabel();

        if (results.isEmpty()) {
            AlertManager.showInformationMessage("no returns found of: " + keyword);
        }
    }
    //edit
    private void editReturnScreen(Returns returns) {
        VBox editBox = new VBox(15);
        editBox.setPadding(new Insets(30));
        editBox.setAlignment(Pos.CENTER);
        editBox.getStyleClass().add("add-edit-box");

        Label title = new Label("Edit Return");
        title.getStyleClass().add("add-edit-title");

        Label returnIdLabel = new Label("Return ID: " + returns.getReturnId());
        returnIdLabel.getStyleClass().add("info-label");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Requested", "Approved", "Rejected", "Processed");
        statusCombo.setValue(returns.getStatus());
        statusCombo.getStyleClass().add("styled-combo");

        TextArea reasonArea = new TextArea(returns.getReason());
        reasonArea.setPromptText("Reason");
        reasonArea.setPrefRowCount(3);
        reasonArea.getStyleClass().add("styled-textarea");

        TextField refundField = new TextField(String.valueOf(returns.getRefundAmount()));
        refundField.setPromptText("Refund Amount");
        refundField.getStyleClass().add("styled-textfield");

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button save = createPinkButton("Update", "#ff69b4", "#ff1493");
        Button back = createPinkButton("Cancel", "#ffb6c1", "#db7093");

        buttons.getChildren().addAll(save, back);

        editBox.getChildren().addAll(title, returnIdLabel,
                new Label("Status:"), statusCombo,
                new Label("Reason:"), reasonArea,
                new Label("Refund Amount:"), refundField,
                buttons);

        BorderPane container = new BorderPane(editBox);
        container.getStyleClass().add("add-edit-container");
        root.setCenter(container);

        back.setOnAction(e -> show()
        );


        save.setOnAction(e -> {
            if (!validateReturnInput(statusCombo, reasonArea, refundField)) {
                return;
            }
            try {
                double refund = Double.parseDouble(refundField.getText().trim());
                if (refund < 0) {
                    AlertManager.showError("refund amount cannot be negative");
                    return;
                }
                returns.setStatus(statusCombo.getValue());
                returns.setReason(reasonArea.getText().trim());
                returns.setRefundAmount(refund);

                boolean success = DatabaseOperationsReturns.updateReturn(returns);
                if (success) {
                        AlertManager.showInformationMessage("return updated successfully");
                       show();
                } else {
                    AlertManager.showError("failed to update return");
                }
            } catch (NumberFormatException ex) {
                AlertManager.showError("please enter a valid refund amount");
            }

        });
    }
    private boolean validateReturnInput(ComboBox<String> status, TextArea reason, TextField refund) {
        if (status.getValue() == null || status.getValue().isEmpty()) {
            AlertManager.showError("please select a status");
            return false;
        }
        if (reason.getText().trim().isEmpty()) {
            AlertManager.showError("reason cannot be empty");
            return false;
        }
        if (refund.getText().trim().isEmpty()) {
            AlertManager.showError("refund amount cannot be empty");
            return false;
        }
        return true;
    }


    private void processRefund() {
        Returns selected = returnTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertManager.showError("please select a return first");
            return;
        }

        if (!selected.getStatus().equals("approved")) {
            AlertManager.showError("only approved returns can be processed");
            return;
        }

        if (AlertManager.showConfirmation("rocess refund of " + selected.getRefundAmount() + "?")) {
            boolean success = DatabaseOperationsReturns.processRefund(selected.getReturnId());
            if (success) {
                AlertManager.showInformationMessage("Refund processed successfully!");
                loadReturnsData();
                updateStatistics();
            } else {
                AlertManager.showError("Failed to process refund");
            }
        }
    }

    private void deleteReturn() {
        Returns selected = returnTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertManager.showError("Please select a return first");
            return;
        }

        if (AlertManager.showConfirmation("Delete return ID: " + selected.getReturnId() + "?")) {
            boolean success = DatabaseOperationsReturns.deleteReturn(selected.getReturnId());
            if (success) {
                AlertManager.showInformationMessage("return deleted successfully");
                loadReturnsData();
                updateStatistics();
            } else {
                AlertManager.showError("failed to delete return");
            }
        }
    }

    //to view the data in return product table
    private void viewReturnDetails() {
        Returns selected = returnTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertManager.showError("please select a return first");
            return;
        }

        loadReturnProducts(selected.getReturnId());

        VBox box = (VBox) returnProductsTable.getParent();
        box.setVisible(true);

        for (javafx.scene.Node n : box.getChildren()) {
            n.setVisible(true);
        }
    }

    //styled buttons
    private Button createStyledButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);

        button.setMinWidth(Region.USE_PREF_SIZE);
        button.setPrefWidth(Region.USE_COMPUTED_SIZE);

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


}