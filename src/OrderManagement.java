import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Date;

public class OrderManagement {
    public static ObservableList<Orders> orders = FXCollections.observableArrayList();
    public static TableView<Orders> orderTable;
    static ObservableList<Orders> data;
    private Stage stage;
    private BorderPane root;


    private TextField searchField;
    private ComboBox<String> searchCombo;

    public OrderManagement(BorderPane root, Stage stage) {
        this.root = root;
        this.stage = stage;
        root.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        root.getStyleClass().add("root-pane");
    }
   //show
    public void show() {
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10, 0, 10, 0));
        searchBox.getStyleClass().add("search-box");


        searchCombo = new ComboBox<>();
        searchCombo.getItems().addAll("order id", "status", "payment status", "customer id");
        searchCombo.setValue("Order ID");
        searchCombo.setPrefWidth(150);
        searchCombo.getStyleClass().add("search-combo");

        searchField = new TextField();
        searchField.setPromptText("Enter search keywords");
        searchField.setPrefWidth(200);
        searchField.getStyleClass().add("search-field");

        Button search= createStyledButton("Search", "button-normal");
        Button showAll = createStyledButton("show all", "button-normal");

        search.setOnAction(e -> performSearch());
        showAll.setOnAction(e -> {
            searchField.clear();
            refreshOrderTable();
        });
        searchField.setOnAction(e -> performSearch());

        searchBox.getChildren().addAll(
                new Label("Search by:"), searchCombo, searchField, search, showAll
        );
        HBox button = new HBox(15);
        button.setStyle("-fx-padding: 4;");
        button.setAlignment(Pos.CENTER);



        Button view = createStyledButton("View Details", "button-normal");
        Button updateStatus = createStyledButton("Update Status", "button-normal");
        Button product = createStyledButton("View Products", "button-normal");
        Button back = createStyledButton("Back", "button-back");

        view.setOnAction(e -> {
            Orders order = orderTable.getSelectionModel().getSelectedItem();
            if (order == null) {
                AlertManager.showError("please select order");
                return;
            }
            showView(order);
        });

        updateStatus.setOnAction(e -> {
            Orders order = orderTable.getSelectionModel().getSelectedItem();
            if (order == null) {
                AlertManager.showError("Please select an order to update status");
                return;
            }
            UpdateStatus(order);
        });

        product.setOnAction(e -> {
            Orders order = orderTable.getSelectionModel().getSelectedItem();
            if (order == null) {
                AlertManager.showError("Please select an order first");
                return;
            }
            Order_ProductManagement p = new Order_ProductManagement(root, stage, order.getOrderId());
            p.show();
        });

        back.setOnAction(e -> {
            try {
                AdminScreen dashboard = new AdminScreen(stage);
                dashboard.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        button.getChildren().addAll(view, updateStatus, product, back);

        Label titleL = new Label("Order Management");
        titleL.getStyleClass().add("title-label");
        VBox titleBox = new VBox(10, titleL);
        titleBox.setAlignment(Pos.CENTER);
        Label orderCountLabel = new Label();
        orderCountLabel.getStyleClass().add("staff-count-label");
        OrderTable();
        data = FXCollections.observableArrayList();
        orderTable.setItems(data);
        refreshOrderTable();
        updateLabel(orderCountLabel);
        VBox main= new VBox(15);
        main.getStyleClass().add("main-content");
        main.setPadding(new Insets(10));
        main.getChildren().addAll(searchBox, button, titleBox, orderCountLabel, orderTable);
        root.setCenter(main);
        VBox.setVgrow(orderTable, Priority.ALWAYS);
        orderTable.setMaxHeight(Double.MAX_VALUE);
        main.setFillWidth(true);
    }
//tables
    private void OrderTable() {
        orderTable = new TableView<>();
        orderTable.getStyleClass().add("table-view");

        TableColumn<Orders, Integer> orderId= new TableColumn<>("Order Id");
        orderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderId.setMinWidth(80);

        TableColumn<Orders, Date> orderDate = new TableColumn<>("Order Date");
        orderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        orderDate.setMinWidth(120);

        TableColumn<Orders, Integer> customerId = new TableColumn<>("Customer Id");
        customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerId.setMinWidth(100);

        TableColumn<Orders, String> status = new TableColumn<>("Status");
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        status.setMinWidth(100);

        TableColumn<Orders, String> paymentStatus= new TableColumn<>("Payment Status");
        paymentStatus.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        paymentStatus.setMinWidth(130);

        TableColumn<Orders, Double> totalAmount= new TableColumn<>("Total Amount");
        totalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        totalAmount.setMinWidth(120);

        TableColumn<Orders, Double> discount = new TableColumn<>("Discount");
        discount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        discount.setMinWidth(100);

        TableColumn<Orders, Double> finalAmount= new TableColumn<>("Final Amount");
        finalAmount.setCellValueFactory(new PropertyValueFactory<>("finalAmount"));
        finalAmount.setMinWidth(120);

        TableColumn<Orders, Double> cost = new TableColumn<>("Cost");
        cost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        cost.setMinWidth(100);

        TableColumn<Orders, String> notes = new TableColumn<>("Notes");
        notes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        notes.setMinWidth(150);


        orderTable.getColumns().addAll(
                orderId, orderDate, customerId, status, paymentStatus,
                totalAmount, discount, finalAmount, cost, notes
        );
    }


    private void updateLabel(Label label) {
        int totalOrders = DatabaseOperationsOrder.getOrderCount();
        label.setText("Total Orders: " + totalOrders);
    }
//to refresh data after edit
    private void refreshOrderTable() {
        ObservableList<Orders> allOrders = DatabaseOperationsOrder.getAllOrders();
        updateOrderTable(allOrders);
    }

    private void updateOrderTable(ObservableList<Orders> newData) {
        Platform.runLater(() -> {
            data.clear();
           data.addAll(newData);
        });
    }
//search
    private void performSearch() {
        String keyword = searchField.getText().trim();
        String searchType = searchCombo.getValue();

        if (keyword.isEmpty()) {
            refreshOrderTable();
            return;
        }

        ObservableList<Orders> searchResults = FXCollections.observableArrayList();
        try {
            switch (searchType) {
                case "Order ID":
                    int orderId = Integer.parseInt(keyword);
                    Orders order = DatabaseOperationsOrder.getOrderById(orderId);
                    if (order != null) searchResults.add(order);
                    break;
                case "Status":
                    searchResults = DatabaseOperationsOrder.searchOrdersByStatus(keyword);
                    break;
                case "Payment Status":
                    searchResults = DatabaseOperationsOrder.searchOrdersByPaymentStatus(keyword);
                    break;
                case "Customer ID":
                    int customerId = Integer.parseInt(keyword);
                    searchResults = DatabaseOperationsOrder.getOrdersByCustomerId(customerId);
                    break;
            }
        } catch (NumberFormatException e) {
            AlertManager.showError("invalid format");
            return;
        }

        updateOrderTable(searchResults);
        if (searchResults.isEmpty()) {
            AlertManager.showInformationMessage("no orders found  of : " + keyword);
        }
    }

    private void showView(Orders order) {
        VBox viewBox = new VBox(15);
        viewBox.setPadding(new Insets(30));
        viewBox.setAlignment(Pos.CENTER);
        viewBox.getStyleClass().add("add-edit-box");

        Label title = new Label("Order Details");
        title.getStyleClass().add("add-edit-title");

        Label orderId = new Label("order id: " + order.getOrderId());
        orderId.getStyleClass().add("info-label");

        Label date = new Label("order date: " + order.getOrderDate());
        date.getStyleClass().add("info-label");

        Label customer= new Label("customer id: " + order.getCustomerId());
        customer.getStyleClass().add("info-label");

        Label status = new Label("status: " + order.getStatus());
        status.getStyleClass().add("info-label");

        Label payment = new Label("payment status: " + order.getPaymentStatus());
        payment.getStyleClass().add("info-label");

        Label totalL= new Label("total amount: " + order.getTotalAmount());
        totalL.getStyleClass().add("info-label");

        Label discount = new Label("discount: " + order.getDiscount());
        discount.getStyleClass().add("info-label");

        Label finalL = new Label("final Amount: " + order.getFinalAmount());
        finalL.getStyleClass().add("info-label");

        Label cost= new Label("cost: " + order.getCost());
        cost.getStyleClass().add("info-label");

        Label notes= new Label("notes: " + order.getNotes());
        notes.getStyleClass().add("info-label");

        Button back = createPinkButton("Back", "#ffb6c1", "#db7093");

        viewBox.getChildren().addAll(
                title, orderId, date, customer,
                status, payment, totalL, discount,
                finalL, cost, notes, back
        );

        BorderPane container = new BorderPane(viewBox);
        container.getStyleClass().add("add-edit-container");
        root.setCenter(container);

        back.setOnAction(e -> show());
    }

    private void UpdateStatus(Orders order) {
        VBox updateBox = new VBox(15);
        updateBox.setPadding(new Insets(30));
        updateBox.setAlignment(Pos.CENTER);
        updateBox.getStyleClass().add("add-edit-box");

        Label title = new Label("update order status");
        title.getStyleClass().add("add-edit-title");

        Label orderIdLabel = new Label("order id: " + order.getOrderId());
        orderIdLabel.getStyleClass().add("info-label");

        Label currentStatusLabel = new Label("current status: " + order.getStatus());
        currentStatusLabel.getStyleClass().add("info-label");

        ComboBox<String> statusCom = new ComboBox<>();
        statusCom.getItems().addAll("pending", "processing", "shipped", "delivered", "cancelled");
        statusCom.setValue(order.getStatus());
        statusCom.setPrefWidth(200);

        ComboBox<String> paymentCom = new ComboBox<>();
        paymentCom.getItems().addAll("pending", "paid", "partially paid", "refunded");
        paymentCom.setValue(order.getPaymentStatus());
        paymentCom.setPrefWidth(200);

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);
        Button updates = createPinkButton("Update", "#ff69b4", "#ff1493");
        Button cancel= createPinkButton("Cancel", "#ffb6c1", "#db7093");

        buttons.getChildren().addAll(updates, cancel);

        updateBox.getChildren().addAll(
                title, orderIdLabel, currentStatusLabel,
                new Label("new status:"), statusCom,
                new Label("payment status:"), paymentCom,
                buttons
        );

        BorderPane bp = new BorderPane(updateBox);
        bp.getStyleClass().add("add-edit-container");
        root.setCenter(bp);

        cancel.setOnAction(e -> show());

        updates.setOnAction(e -> {
            order.setStatus(statusCom.getValue());
            order.setPaymentStatus(paymentCom.getValue());

            boolean succes = DatabaseOperationsOrder.updateOrderStatus(
                    order.getOrderId(),
                    statusCom.getValue(),
                    paymentCom.getValue()
            );

            if (succes) {
                AlertManager.showInformationMessage("order status updated successfully");
                refreshOrderTable();
                show();
            } else {
                AlertManager.showError("failed to update order status");
            }
        });
    }
//styled buttons
    private Button createStyledButton(String text, String styleClass) {
        Button button = new Button(text);
        button.setPrefWidth(150);
        button.setPrefHeight(35);
        button.getStyleClass().add(styleClass);
        return button;
    }

    private Button createPinkButton(String text, String baseColor, String hoverColor) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + baseColor + "; " + "-fx-text-fill: white; -fx-font-size: 14px; "
                + "-fx-padding: 10px 20px; -fx-font-weight: bold; "
                + "-fx-background-radius: 20; -fx-border-radius: 20; "
                + "-fx-cursor: hand;");

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: " + hoverColor + "; " + "-fx-text-fill: white; -fx-font-size: 14px; "
                    + "-fx-padding: 10px 20px; -fx-font-weight: bold; "
                    + "-fx-background-radius: 20; -fx-border-radius: 20; "
                    + "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + baseColor + "; " + "-fx-text-fill: white; -fx-font-size: 14px; "
                    + "-fx-padding: 10px 20px; -fx-font-weight: bold; "
                    + "-fx-background-radius: 20; -fx-border-radius: 20; " + "-fx-cursor: hand;");
        });

        return button;
    }
}