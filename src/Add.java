import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Add {

    private BorderPane root;
    private Scenes scenes;

    public Add(BorderPane root, Scenes scenes) {
        this.root = root;
        this.scenes = scenes;
    }

    public void show() {

        VBox addBox = new VBox(15);
        addBox.setPadding(new Insets(30));
        addBox.setAlignment(Pos.CENTER);
        addBox.setStyle("-fx-background-color: linear-gradient(to bottom right, #fff0f5, #ffe4e1); " +
                       "-fx-background-radius: 15; -fx-border-radius: 15; " +
                       "-fx-effect: dropshadow(three-pass-box, rgba(219, 112, 147, 0.3), 10, 0, 0, 5);");

        Label title = new Label("Add New Product");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; " +
                      "-fx-text-fill: #db7093; -fx-font-family: 'Arial Rounded MT Bold';");

        Label productIdLabel = new Label("Product ID will be assigned automatically");
        productIdLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #c71585; " +
                               "-fx-font-weight: bold; -fx-padding: 5px;");

        TextField category_id = createStyledTextField("Category ID");

        TextField name = createStyledTextField("Product Name");

        TextField price = createStyledTextField("Price");

        TextField brand = createStyledTextField("Brand");

        TextArea desc = createStyledTextArea("Description");

        CheckBox stock = new CheckBox("In Stock");
        stock.setStyle("-fx-font-size: 16px; -fx-text-fill: #c71585; " +
                      "-fx-font-weight: bold; -fx-padding: 5px;");
        stock.setSelected(true);

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button save = createPinkButton("Save", "#ff69b4", "#ff1493");
        Button back = createPinkButton("Back", "#ffb6c1", "#db7093");

        buttons.getChildren().addAll(save, back);

        addBox.getChildren().addAll(title, productIdLabel, category_id, name, price, brand, desc, stock, buttons);

        BorderPane container = new BorderPane(addBox);
        container.setStyle("-fx-background-color: #fffaf0; -fx-padding: 20;");
        root.setCenter(container);

        back.setOnAction(e -> scenes.show());

        save.setOnAction(e -> {
            if (!validateInput(category_id, name, price, brand, desc)) {
                return;
            }

            saveProduct(category_id, name, price, brand, desc, stock);
        });
    }

    private int getNextProductIdFromDatabase() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DataBaseConnection.getConnection();

            String autoIncrementSQL = "SELECT AUTO_INCREMENT " +
                                     "FROM information_schema.tables " +
                                     "WHERE table_name = 'Product' " +
                                     "AND table_schema = DATABASE()";

            ps = con.prepareStatement(autoIncrementSQL);
            rs = ps.executeQuery();

            if (rs.next()) {
                int autoIncrement = rs.getInt("AUTO_INCREMENT");
                if (!rs.wasNull() && autoIncrement > 0) {
                    return autoIncrement;
                }
            }

          rs.close();
            ps.close();

            String maxSQL = "SELECT IFNULL(MAX(product_id), 0) + 1 as next_id FROM Product";
            ps = con.prepareStatement(maxSQL);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("next_id");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return getSimpleNextId();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 1;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return 1;
    }

    private int getSimpleNextId() {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = DataBaseConnection.getConnection();
            stmt = con.createStatement();

            String sql = "SELECT MAX(product_id) as max_id FROM Product";
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                if (!rs.wasNull()) {
                    return maxId + 1;
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return 1;
    }

    private TextField createStyledTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText( promptText );
        textField.setMaxWidth(350);
        textField.setPrefHeight(40);
        textField.setStyle("-fx-padding: 10px 15px; -fx-font-size: 14px; " +
                          "-fx-background-color: white; -fx-background-radius: 10; " +
                          "-fx-border-color: #ffb6c1; -fx-border-radius: 10; " +
                          "-fx-border-width: 2px; -fx-text-fill: #8b4513; " +
                          "-fx-font-family: 'Segoe UI';");

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                textField.setStyle("-fx-padding: 10px 15px; -fx-font-size: 14px; " +
                                 "-fx-background-color: #fffafa; -fx-background-radius: 10; " +
                                 "-fx-border-color: #ff69b4; -fx-border-radius: 10; " +
                                 "-fx-border-width: 3px; -fx-text-fill: #8b4513; " +
                                 "-fx-effect: dropshadow(gaussian, rgba(255,105,180,0.3), 5, 0, 0, 2);");
            } else {
                textField.setStyle("-fx-padding: 10px 15px; -fx-font-size: 14px; " +
                                 "-fx-background-color: white; -fx-background-radius: 10; " +
                                 "-fx-border-color: #ffb6c1; -fx-border-radius: 10; " +
                                 "-fx-border-width: 2px; -fx-text-fill: #8b4513;");
            }
        });

        return textField;
    }

    private TextArea createStyledTextArea(String promptText) {
        TextArea textArea = new TextArea();
        textArea.setPromptText( promptText );
        textArea.setMaxWidth(350);
        textArea.setPrefHeight(120);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-padding: 10px 15px; -fx-font-size: 14px; " +
                         "-fx-background-color: white; -fx-background-radius: 10; " +
                         "-fx-border-color: #ffb6c1; -fx-border-radius: 10; " +
                         "-fx-border-width: 2px; -fx-text-fill: #8b4513; " +
                         "-fx-font-family: 'Segoe UI';");

        textArea.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                textArea.setStyle("-fx-padding: 10px 15px; -fx-font-size: 14px; " +
                                "-fx-background-color: #fffafa; -fx-background-radius: 10; " +
                                "-fx-border-color: #ff69b4; -fx-border-radius: 10; " +
                                "-fx-border-width: 3px; -fx-text-fill: #8b4513; " +
                                "-fx-effect: dropshadow(gaussian, rgba(255,105,180,0.3), 5, 0, 0, 2);");
            } else {
                textArea.setStyle("-fx-padding: 10px 15px; -fx-font-size: 14px; " +
                                "-fx-background-color: white; -fx-background-radius: 10; " +
                                "-fx-border-color: #ffb6c1; -fx-border-radius: 10; " +
                                "-fx-border-width: 2px; -fx-text-fill: #8b4513;");
            }
        });

        return textArea;
    }

    private Button createPinkButton(String text, String baseColor, String hoverColor) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + baseColor + "; " +
                       "-fx-text-fill: white; -fx-font-size: 16px; " +
                       "-fx-padding: 12px 25px; -fx-font-weight: bold; " +
                       "-fx-background-radius: 20; -fx-border-radius: 20; " +
                       "-fx-cursor: hand; -fx-font-family: 'Arial Rounded MT Bold';");

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: " + hoverColor + "; " +
                           "-fx-text-fill: white; -fx-font-size: 16px; " +
                           "-fx-padding: 12px 25px; -fx-font-weight: bold; " +
                           "-fx-background-radius: 20; -fx-border-radius: 20; " +
                           "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + baseColor + "; " +
                           "-fx-text-fill: white; -fx-font-size: 16px; " +
                           "-fx-padding: 12px 25px; -fx-font-weight: bold; " +
                           "-fx-background-radius: 20; -fx-border-radius: 20; " +
                           "-fx-cursor: hand;");
        });

        button.setOnMousePressed(e -> {
            button.setStyle("-fx-background-color: #c71585; " +
                           "-fx-text-fill: white; -fx-font-size: 16px; " +
                           "-fx-padding: 12px 25px; -fx-font-weight: bold; " +
                           "-fx-background-radius: 20; -fx-border-radius: 20; " +
                           "-fx-cursor: hand; -fx-translate-y: 2px;");
        });

        button.setOnMouseReleased(e -> {
            button.setStyle("-fx-background-color: " + baseColor + "; " +
                           "-fx-text-fill: white; -fx-font-size: 16px; " +
                           "-fx-padding: 12px 25px; -fx-font-weight: bold; " +
                           "-fx-background-radius: 20; -fx-border-radius: 20; " +
                           "-fx-cursor: hand; -fx-translate-y: 0px;");
        });

        return button;
    }

    private boolean validateInput(TextField categoryId, TextField name, TextField price,
                                  TextField brand, TextArea desc) {

        if (categoryId.getText().trim().isEmpty() ||
            name.getText().trim().isEmpty() ||
            price.getText().trim().isEmpty() ||
            brand.getText().trim().isEmpty() ||
            desc.getText().trim().isEmpty()) {

            showStyledAlert(Alert.AlertType.WARNING,
                "⚠️ Please fill in all fields! ⚠️",
                "All fields are required to add a new product.");
            return false;
        }

        try {
            Integer.parseInt(categoryId.getText().trim());
        } catch (NumberFormatException e) {
            showStyledAlert(Alert.AlertType.ERROR,
                " Invalid Category ID ",
                "Category ID must be a valid number!");
            return false;
        }

        try {
            double priceValue = Double.parseDouble(price.getText().trim());
            if (priceValue <= 0) {
                showStyledAlert(Alert.AlertType.ERROR,
                    " Invalid Price ",
                    "Price must be greater than 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            showStyledAlert(Alert.AlertType.ERROR,
                "❌ Invalid Price ❌",
                "Price must be a valid number!");
            return false;
        }

        return true;
    }

    private void showStyledAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #fff0f5; " +
                           "-fx-border-color: #ff69b4; -fx-border-width: 2px; " +
                           "-fx-border-radius: 10; -fx-background-radius: 10;");
        dialogPane.setHeader(null);

        for (ButtonType buttonType : dialogPane.getButtonTypes()) {
            Button button = (Button) dialogPane.lookupButton(buttonType);
            button.setStyle("-fx-background-color: #ff69b4; " +
                           "-fx-text-fill: white; -fx-font-weight: bold; " +
                           "-fx-background-radius: 15; -fx-border-radius: 15; " +
                           "-fx-padding: 5px 15px;");
        }

        alert.showAndWait();
    }

    private void saveProduct(TextField categoryId, TextField name, TextField price,
                             TextField brand, TextArea desc, CheckBox stock) {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DataBaseConnection.getConnection();

            String sql = "INSERT INTO Product (category_id, Product_name, price, brand, Product_description, stock_status) VALUES (?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, Integer.parseInt(categoryId.getText().trim()));
            ps.setString(2, name.getText().trim());
            ps.setDouble(3, Double.parseDouble(price.getText().trim()));
            ps.setString(4, brand.getText().trim());
            ps.setString(5, desc.getText().trim());
            ps.setBoolean(6, stock.isSelected());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                rs = ps.getGeneratedKeys();
                int generatedId = 0;
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }

                String message = "Product Added Successfully! ✅\n\n" +
                               " Product Details:\n" +
                               "• Product ID: " + generatedId + "\n" +
                               "• Product Name: " + name.getText().trim() + "\n" +
                               "• Category ID: " + categoryId.getText().trim() + "\n" +
                               "• Price: $" + price.getText().trim() + "\n" +
                               "• Brand: " + brand.getText().trim() + "\n" +
                               "• Stock Status: " + (stock.isSelected() ? "In Stock" : "Out of Stock");

                showStyledAlert(Alert.AlertType.INFORMATION,
                    "Product Added Successfully! ",
                    message);

                scenes.refreshTable();
                scenes.show();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showStyledAlert(Alert.AlertType.ERROR,
                "❌ Database Error ❌",
                "Error saving product: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            showStyledAlert(Alert.AlertType.ERROR,
                "❌ Error ❌",
                "An unexpected error occurred: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}