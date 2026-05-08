
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;

public class Scenes {
// to use it in all the classes on this project
    public static TableView<Product> table;
    static ObservableList<Product> data;
    Stage primaryStage;
    private BorderPane root;

    public Scenes(BorderPane root,Stage primaryStage) {
        this.root = root;
        this.primaryStage=primaryStage;
    }

    public void show() {
        root.setStyle("-fx-background-color: #FBEFEF; -fx-padding: 20;");

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 30, 0));

        Button addButton = createStyledButton("Add Product");
        Button editButton = createStyledButton("Edit Product");
        Button deleteButton = createStyledButton("Delete Product");
        Button backButton = createStyledButton("Back");

        buttonBox.getChildren().addAll(addButton, editButton, deleteButton, backButton);

        Label titleLabel = new Label("Products Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: #8B4513;");

        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().addAll(titleLabel);

        table = new TableView<>();
        table.setMaxSize(900, 500);
        table.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-border-color: #F5AFAF;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        );

        TableColumn<Product, Integer> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        id.setMinWidth(50);
        id.setStyle("-fx-alignment: CENTER;");

        TableColumn<Product, Integer> cat = new TableColumn<>("Category ID");
        cat.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getCategory().getCategoryId()).asObject()
        );
        cat.setMinWidth(80);
        cat.setStyle("-fx-alignment: CENTER;");

        TableColumn<Product, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        name.setMinWidth(150);

        TableColumn<Product, Double> price = new TableColumn<>("Price");
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        price.setMinWidth(80);
        price.setStyle("-fx-alignment: CENTER_RIGHT;");

        TableColumn<Product, String> brand = new TableColumn<>("Brand");
        brand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        brand.setMinWidth(100);

        TableColumn<Product, String> description = new TableColumn<>("Description");
        description.setCellValueFactory(new PropertyValueFactory<>("product_description"));
        description.setMinWidth(200);

        TableColumn<Product, Boolean> stock = new TableColumn<>("In Stock");
        stock.setCellValueFactory(new PropertyValueFactory<>("stockStatus"));
        stock.setMinWidth(80);
        stock.setStyle("-fx-alignment: CENTER;");

        table.getColumns().addAll(id, cat, name, price, brand, description, stock);

        data = FXCollections.observableArrayList();
        table.setItems(data);

        refreshTable();

        // Add Button
        addButton.setOnAction(c -> showAddScreen());

        // Edit Button
        editButton.setOnAction(n -> {
            Product product = table.getSelectionModel().getSelectedItem();
            if (product == null) {
                showAlert(Alert.AlertType.ERROR, "Please select a product to edit");
                return;
            }
            showEditScreen(product);
        });

        // Delete Button
        deleteButton.setOnAction(m -> {
            Product product = table.getSelectionModel().getSelectedItem();
            if (product == null) {
                showAlert(Alert.AlertType.ERROR, "Please select a product to delete");
                return;
            }

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Delete Product");
            confirmation.setHeaderText("Delete Product");
            confirmation.setContentText("Are you sure you want to delete product: " + product.getProduct_name() + "?");

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean success = DatabaseOperations.deleteProduct(product.getProduct_id());
                    if (success) {
                        refreshTable();
                        showAlert(Alert.AlertType.INFORMATION, "Product deleted successfully!");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Failed to delete product from database!");
                    }
                }
            });
        });

        // Back Button
        backButton.setOnAction(e -> {
            MainScene mainScene = new MainScene();
            try {
                mainScene.start((Stage) root.getScene().getWindow());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox mainContent = new VBox(20);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.getChildren().addAll(buttonBox, titleBox, table);

        root.setCenter(mainContent);
    }

    private void showAddScreen() {
        Add addScreen = new Add(root, this);
        addScreen.show();
    }

    private void showEditScreen(Product product) {
        Edit editScreen = new Edit(root, this, product);
        editScreen.show();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(
            "-fx-background-color: #F5AFAF;" +
            "-fx-text-fill: #333333;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14;" +
            "-fx-border-color: #B76E79;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #FFD6D6;" +
            "-fx-text-fill: #333333;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14;" +
            "-fx-border-color: #B76E79;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: #F5AFAF;" +
            "-fx-text-fill: #333333;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14;" +
            "-fx-border-color: #B76E79;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 10 20;" +
            "-fx-cursor: hand;"
        ));
        return button;
    }

    public void refreshTable() {
        try {
            ObservableList<Product> tempData = FXCollections.observableArrayList();

            Connection con = DataBaseConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Product");

            while (rs.next()) {
                tempData.add(new Product(
                    rs.getInt("product_id"),
                    rs.getString("Product_name"),
                    rs.getDouble("price"),
                    rs.getString("brand"),
                    rs.getString("Product_description"),
                    new Category(rs.getInt("category_id"), "", "")
                ));
            }
            con.close();

            Platform.runLater(() -> {
                data.clear();
                data.addAll(tempData);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Alert");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}















