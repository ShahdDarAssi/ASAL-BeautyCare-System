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
import javafx.stage.Stage;

public class Order_ProductManagement {
    public static TableView<Order_Product> productsTable;
    static ObservableList<Order_Product> data;
    private Stage primaryStage;
    private BorderPane root;
    private TextField searchField;
    private ComboBox<String> searchCombo;
    private Integer orderId;


    public Order_ProductManagement(BorderPane root, Stage primaryStage, int orderId) {
        this.root = root;
        this.primaryStage = primaryStage;
        this.orderId = orderId;
    }


    public Order_ProductManagement(BorderPane root, Stage primaryStage) {
        this.root = root;
        this.primaryStage = primaryStage;
        this.orderId = null;
    }
//show all the components
    public void show() {
        root.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        root.getStyleClass().add("root-pane");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10, 0, 10, 0));
        searchBox.getStyleClass().add("search-box");

        searchCombo = new ComboBox<>();
        searchCombo.getItems().addAll("Product Id");
        searchCombo.setValue("Product Id");
        searchCombo.setPrefWidth(150);
        searchCombo.getStyleClass().add("search-combo");

        searchField = new TextField();
        if (orderId != null) {
            searchField.setText(String.valueOf(orderId));
            searchField.setDisable(true);
        }
        searchField.setPromptText("Enter search keyword");
        searchField.setPrefWidth(200);
        searchField.getStyleClass().add("search-field");

        Button search = createStyledButton("Search", "button-normal");
        Button showAll = createStyledButton("show all", "button-normal");

        search.setOnAction(e -> searchs());
        showAll.setOnAction(e -> {
            searchField.clear();
            refreshProductsTable();
        });
        searchField.setOnAction(e -> searchs());

        searchBox.getChildren().addAll(
                new Label("search by:"),
                searchCombo,
                searchField,
                search,
                showAll
        );

        HBox buttonBox = new HBox(20);
        buttonBox.setStyle("-fx-padding: 10;");
        buttonBox.setAlignment(Pos.CENTER);

        Button backButton = createStyledButton("Back", "button-normal");

        backButton.setOnAction(e -> {
            OrderManagement om = new OrderManagement(root, primaryStage);
            om.show();
        });

        buttonBox.getChildren().addAll(backButton);
        Label titleLabel;
        if (orderId != null) {
            titleLabel = new Label("Order Products  Order ID: " + orderId);
        } else {
            titleLabel = new Label("all order products");
        }
        titleLabel.getStyleClass().add("title-label");

        VBox titleBox = new VBox(10, titleLabel);
        titleBox.setAlignment(Pos.CENTER);

        Label productLabel = new Label();
        productLabel.getStyleClass().add("staff-count-label");
//tables
        productsTable = new TableView<>();
        productsTable.getStyleClass().add("table-view");
        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Order_Product, Integer> order_id = new TableColumn<>("Order Id");
        order_id.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        order_id.setMinWidth(80);

        TableColumn<Order_Product, Integer> productId = new TableColumn<>("Product Id");
        productId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productId.setMinWidth(80);

        TableColumn<Order_Product, Integer> quantity = new TableColumn<>("Quantity");
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantity.setMinWidth(80);

        TableColumn<Order_Product, Double> price = new TableColumn<>("Price at Order");
        price.setCellValueFactory(new PropertyValueFactory<>("priceAtOrder"));
        price.setMinWidth(100);

        TableColumn<Order_Product, Double> subtotal = new TableColumn<>("Subtotal");
        subtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        subtotal.setMinWidth(120);

        productsTable.getColumns().addAll(order_id, productId, quantity, price, subtotal);

        data = FXCollections.observableArrayList();
        productsTable.setItems(data);

        refreshProductsTable();
        updateProductLabel(productLabel);
//main content
        VBox main = new VBox(15);
        main.getStyleClass().add("main-content");
        main.setPadding(new Insets(10));
        main.getChildren().addAll(
                searchBox,
                buttonBox,
                titleBox,
                productLabel,
                productsTable
        );

        root.setCenter(main);
    }



    private void updateProductLabel(Label label) {
        label.setText("Total Products: " + data.size());
    }
    //refresh
    private void refreshProductsTable() {
        ObservableList<Order_Product> orderProducts = DatabaseOperationsOrderProduct.getOrderProductsByOrderId(orderId);
        updateProductsTable(orderProducts);
    }


    private void updateProductsTable(ObservableList<Order_Product> newData) {
        Platform.runLater(() -> {//to make a safe update first clear the table afteer this put the new data
            data.clear();
            data.addAll(newData);
        });
    }
//search
    private void searchs() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            refreshProductsTable();
            return;
        }

        try {
            int productId = Integer.parseInt(keyword);

            ObservableList<Order_Product> results =
                    DatabaseOperationsOrderProduct
                            .getOrderProductsByOrderIdAndProductId(orderId, productId);

            updateProductsTable(results);

            if (results.isEmpty()) {
                AlertManager.showInformationMessage("no product found in this order");
            }

        } catch (NumberFormatException e) {
            AlertManager.showError("invalid product id");
        }
    }

    //styled buttons
    private Button createStyledButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        button.setPrefWidth(120);
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