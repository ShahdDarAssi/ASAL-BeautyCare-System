import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReturnDetails {

    private Stage stage;
    private BorderPane root;

    private Returns selectedReturn;
    private TableView<Returns_Product> productsTable;
    private ObservableList<Returns_Product> productsData;

    public ReturnDetails(Returns selectedReturn) {
        this.selectedReturn = selectedReturn;
        this.productsData = FXCollections.observableArrayList();
    }

    public void show() {
        stage = new Stage();
        root = new BorderPane();

        root.getStylesheets().add(
                getClass().getResource("styles.css").toExternalForm()
        );
        root.getStyleClass().add("root-pane");

        // title
        Label titleLabel = new Label("Return Details");
        titleLabel.getStyleClass().add("title-label");

        // info
        Label infoLabel = new Label(
                "Return Id: " + selectedReturn.getReturnId() +
                        "  Order Id: " + selectedReturn.getOrderId() +
                        "  Date: " + selectedReturn.getReturnDate() +
                        "  Status: " + selectedReturn.getStatus() +
                        "  Refund: " + selectedReturn.getRefundAmount()
        );
        infoLabel.getStyleClass().add("info-label");

        VBox headerBox = new VBox(10, titleLabel, infoLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(15));

        // =product table
        productsTable = createReturnProductsTable();
        loadReturnProducts();

        // buttons
        Button close = createStyledButton("Close");
        close.setOnAction(e -> stage.close());

        HBox box = new HBox(close);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.getStyleClass().add("button-box");

        // main
        VBox main = new VBox(15, headerBox, productsTable, box);
        main.setPadding(new Insets(20));

        root.setCenter(main);

        stage.setTitle("Return " + selectedReturn.getReturnId());
        stage.setScene(new Scene(root, 850, 500));
        stage.show();
    }

    private TableView<Returns_Product> createReturnProductsTable() {
        TableView<Returns_Product> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);

        TableColumn<Returns_Product, Integer> productId =
                new TableColumn<>("Product Id");
        productId.setCellValueFactory(cell ->
                new SimpleIntegerProperty(
                        cell.getValue().getProduct().getProduct_id()
                ).asObject()
        );


        TableColumn<Returns_Product, String> name =
                new TableColumn<>("Product Name");
        name.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getProduct().getProduct_name()
                )
        );

        TableColumn<Returns_Product, Integer> qty=
                new TableColumn<>("Quantity");
        qty.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );

        TableColumn<Returns_Product, Double> price =
                new TableColumn<>("Unit Price");
        price.setCellValueFactory(cell ->
                new SimpleDoubleProperty(
                        cell.getValue().getProduct().getPrice()
                ).asObject()
        );

        TableColumn<Returns_Product, Double> total =
                new TableColumn<>("Total");
        total.setCellValueFactory(cell ->
                new SimpleDoubleProperty(
                        cell.getValue().getQuantity() *
                                cell.getValue().getProduct().getPrice()
                ).asObject()
        );

        table.getColumns().addAll(
                productId, name, qty , price, total
        );

        table.setItems(productsData);
        return table;
    }
    private void loadReturnProducts() {
        productsData.clear();

        int returnId = selectedReturn.getReturnId();
        System.out.println("Loading products for return ID: " + returnId);

        ObservableList<Returns_Product> list = DatabaseOperationsReturns.getReturnProducts(returnId);

        System.out.println("Number of products fetched: " + list.size());
        for (Returns_Product rp : list) {
            System.out.println("Product: " + rp.getProduct().getProduct_name() + " Qty: " + rp.getQuantity());
        }

        productsData.addAll(list);
    }


    //button
    private Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("button-normal");
        btn.setPrefWidth(120);
        return btn;
    }
}