import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Random;

public class CartManagemnet  {

    private Stage primaryStage;
    private BorderPane root;
    private int currentStep = 1;
    private final int TOTAL_STEPS = 5;

    static ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private TableView<CartItem> cartTable;

    private TextField customerNameField, customerPhoneField;
    private TextArea customerAddressArea, shippingAddressArea;

    private ComboBox<String> paymentCombo;
    private TextField cardField, expiryField, cvvField;

    private HBox stepIndicators;
    private StackPane mainContent;
    private HBox navigation;

    private Label subtotalLabel, discountLabel, shippingLabel, taxLabel, totalLabel;

    private double subtotal = 0;
    private double discount = 0;
    private double shipping = 10;
    private double tax = 0;
    private double total = 0;


    public void show(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.root = new BorderPane();
        root.getStyleClass().add("root-pane");

        main();

        Scene scene = new Scene(root, 1100, 700);
        primaryStage.setFullScreen(true);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        primaryStage.setTitle("Asal BeautyCare  Checkout Process");
        primaryStage.setScene(scene);
        primaryStage.show();

        loadSampleData();
        updateStep(1);
    }

    private void main() {
        setupStepIndicators();
        setupMainContent();
        setupNavigationButtons();

        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.getStyleClass().add("main-content");
        mainLayout.getChildren().addAll(stepIndicators, mainContent, navigation);

        root.setCenter(mainLayout);
    }

    private void setupStepIndicators() {
        stepIndicators = new HBox(0);
        stepIndicators.setAlignment(Pos.CENTER);
        stepIndicators.setPadding(new Insets(0, 0, 20, 0));

        String[] stepTitles = {"1. Review Cart", "2. Customer Info", "3. Shipping", "4. Payment", "5. Confirmation"};
        ImageView[] stepIcons = {
                new ImageView(new Image("file:Photo/shopping-cart.png")),
                new ImageView(new Image("file:Photo/information (1).png")),
                new ImageView(new Image("file:Photo/truck.png")),
                new ImageView(new Image("file:Photo/credit-card.png")),
                new ImageView(new Image("file:Photo/confirmation.png"))
        };

        for (int i = 0; i < TOTAL_STEPS; i++) {
            VBox stepBox = createStepIndicator(i + 1, stepIcons[i], stepTitles[i]);
            stepIndicators.getChildren().add(stepBox);

            if (i < TOTAL_STEPS - 1) {
                Label arrow = new Label("➡");
                arrow.setFont(Font.font(20));
                arrow.setStyle("-fx-text-fill: #FFB6C1; -fx-padding: 0 15px;");
                stepIndicators.getChildren().add(arrow);
            }
        }
    }

    private VBox createStepIndicator(int stepNumber, ImageView icon, String title) {
        VBox stepBox = new VBox(5);
        stepBox.setAlignment(Pos.CENTER);
        stepBox.setPadding(new Insets(10));
        stepBox.setPrefWidth(180);
        stepBox.setStyle("-fx-cursor: hand;");

        StackPane circlePane = new StackPane();
        circlePane.setPrefSize(50, 50);
        circlePane.getStyleClass().add("step-circle");


        icon.setFitWidth(20);
        icon.setFitHeight(20);

        circlePane.getChildren().add(icon);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font(12));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #C71585;");

        stepBox.getChildren().addAll(circlePane, titleLabel);

        stepBox.setOnMouseClicked(e -> {
            if (stepNumber <= currentStep) {
                updateStep(stepNumber);
            }
        });

        return stepBox;
    }

    //for the steps
    private void setupMainContent() {
        mainContent = new StackPane();
        mainContent.setPrefHeight(450);
        mainContent.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 12px; " +
                "-fx-border-color: #FFB6C1; -fx-border-width: 3px; " +
                "-fx-effect: dropshadow(gaussian, rgba(255, 182, 193, 0.2), 15, 0, 0, 0);");


        VBox step1 = createStep1();
        VBox step2 = createStep2();
        VBox step3 = createStep3();
        VBox step4 = createStep4();
        VBox step5 = createStep5();

        mainContent.getChildren().addAll(step1, step2, step3, step4, step5);
    }

    private VBox createStep1() {
        VBox step1Content = new VBox(15);
        step1Content.setPadding(new Insets(25));
        step1Content.setId("step1");

        Label title = new Label("Review Your Cart");
        title.getStyleClass().add("add-edit-title");
        title.setStyle("-fx-font-size: 28px;");

        HBox countBox = new HBox();
        countBox.setAlignment(Pos.CENTER_LEFT);

        Label countLabel = new Label();
        countLabel.textProperty().bind(
                javafx.beans.binding.Bindings.createStringBinding(() ->
                                "Items in Cart: " + cartItems.size(),
                        cartItems
                )
        );
        countLabel.getStyleClass().add("product-count-label");
        countBox.getChildren().add(countLabel);

        cartTable = createCartTable();
        HBox summaryBox = createOrderSummaryBox();

        Button backButtons = new Button(" Back to Cart");
        backButtons.setStyle(
                "-fx-background-color: #F5AFAF;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8px 20px;" +
                        "-fx-background-radius: 8;"
        );

        backButtons.setOnAction(e -> {
            CustomerProduct c = new CustomerProduct(primaryStage);
            c.show();
        });

        step1Content.getChildren().addAll(title, countBox, cartTable, summaryBox, backButtons);
        return step1Content;
    }

    //the table for the items
    private TableView<CartItem> createCartTable() {
        TableView<CartItem> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setItems(cartItems);
        table.setPrefHeight(450);
        table.setPrefWidth(1000);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<CartItem, String> name = new TableColumn<>("Product");
        name.setCellValueFactory(new PropertyValueFactory<>("productName"));
        name.setPrefWidth(400);

        TableColumn<CartItem, Double> price = new TableColumn<>("Price");
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        price.setPrefWidth(150);

        TableColumn<CartItem, Integer> qty = new TableColumn<>("Quantity");
        qty.setCellFactory(col -> new TableCell<>() {
            private final Spinner<Integer> spinner = new Spinner<>(1, 100, 1);

            {
                spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    if (item != null) {
                        item.setQuantity(newVal);
                    }
                });
            }

            @Override
            protected void updateItem(Integer value, boolean empty) {
                super.updateItem(value, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CartItem item = getTableView().getItems().get(getIndex());
                    spinner.getValueFactory().setValue(item.getQuantity());
                    setGraphic(spinner);
                }
            }
        });

        TableColumn<CartItem, String> subtotal = new TableColumn<>("Subtotal");
        subtotal.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        String.format("$%.2f", cellData.getValue().getPrice() * cellData.getValue().getQuantity())
                )
        );

        TableColumn<CartItem, Void> delete = new TableColumn<>("Delete");
        delete.setCellFactory(col -> new TableCell<>() {
            Button button = new Button();
            ImageView im = new ImageView(new Image("file:C:/Users/User/Downloads/x.png"));

            {
                im.setFitWidth(16);
                im.setFitHeight(16);
                im.setPreserveRatio(true);
                button.setGraphic(im);

                button.setStyle(" -fx-text-fill: white;");
                button.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    cartItems.remove(item);
                    updateOrderSummary();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });
        table.setItems(cartItems);
        table.getColumns().addAll(name, price, qty, subtotal, delete);
        return table;
    }

    private HBox createOrderSummaryBox() {
        HBox summaryBox = new HBox(20);
        summaryBox.setPadding(new Insets(10));
        summaryBox.setStyle("-fx-background-color: #FFE4E1; -fx-border-radius: 10px; " +
                "-fx-border-color: #FFB6C1; -fx-border-width: 2px;");

        VBox left = new VBox(5);
        left.setPrefWidth(80);

        Label subtotalLbl = new Label("Subtotal:");
        Label discountLbl = new Label("Discount:");
        Label shippingLbl = new Label("Shipping:");
        Label taxLbl = new Label("Tax (16%):");
        Label totalLbl = new Label("TOTAL:");

        left.getChildren().addAll(subtotalLbl, discountLbl, shippingLbl, taxLbl, totalLbl);

        VBox right = new VBox(5);
        right.setPrefWidth(100);

        subtotalLabel = new Label("$0.00");
        discountLabel = new Label("$0.00");
        shippingLabel = new Label("$10.00");
        taxLabel = new Label("$0.00");
        totalLabel = new Label("$0.00");

        subtotalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #8B4513; -fx-font-size: 12px;");
        discountLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2E7D32; -fx-font-size: 12px;");
        shippingLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #8B4513; -fx-font-size: 12px;");
        taxLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #8B4513; -fx-font-size: 12px;");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #C71585;");

        right.getChildren().addAll(subtotalLabel, discountLabel, shippingLabel, taxLabel, totalLabel);
        summaryBox.getChildren().addAll(left, right);


        return summaryBox;
    }

    private VBox createStep2() {
        VBox step2Content = new VBox(20);
        step2Content.setPadding(new Insets(30));
        step2Content.setId("step2");

        Label title = new Label(" Customer Information");
        title.getStyleClass().add("add-edit-title");
        title.setStyle("-fx-font-size: 28px;");

        VBox form = new VBox(15);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #FFFAFA; -fx-border-radius: 10px; " +
                "-fx-border-color: #FFB6C1; -fx-border-width: 2px;");

        Label nameLabel = new Label("Full Name:");
        nameLabel.getStyleClass().add("info-label");
        customerNameField = new TextField();
        customerNameField.getStyleClass().add("styled-text-field");
        customerNameField.setPromptText("Enter your full name");
        customerNameField.setPrefWidth(300);


        Label phoneLabel = new Label("Phone Number:");
        phoneLabel.getStyleClass().add("info-label");
        customerPhoneField = new TextField();
        customerPhoneField.getStyleClass().add("styled-text-field");
        customerPhoneField.setPromptText("Enter your phone number");
        customerPhoneField.setPrefWidth(300);

        Label addressLabel = new Label("Home Address:");
        addressLabel.getStyleClass().add("info-label");
        customerAddressArea = new TextArea();
        customerAddressArea.getStyleClass().add("styled-text-area");
        customerAddressArea.setPromptText("Enter your home address...");
        customerAddressArea.setPrefRowCount(3);
        customerAddressArea.setPrefWidth(500);


        Label note = new Label("note: This information will be saved for future purchases");
        note.setStyle("-fx-font-size: 12px; -fx-text-fill: #696969;");

        form.getChildren().addAll(nameLabel, customerNameField, phoneLabel, customerPhoneField,
                addressLabel, customerAddressArea, note);
        step2Content.getChildren().addAll(title, form);

        return step2Content;
    }

    private VBox createStep3() {
        VBox step3Content = new VBox(20);
        step3Content.setPadding(new Insets(30));
        step3Content.setId("step3");

        Label title = new Label(" Shipping Information");
        title.getStyleClass().add("add-edit-title");
        title.setStyle("-fx-font-size: 28px;");

        VBox form = new VBox(15);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #FFFAFA; -fx-border-radius: 10px; " +
                "-fx-border-color: #FFB6C1; -fx-border-width: 2px;");


        Label addressLabel = new Label("Shipping Address:");
        addressLabel.getStyleClass().add("info-label");

        shippingAddressArea = new TextArea();
        shippingAddressArea.getStyleClass().add("styled-text-area");
        shippingAddressArea.setPromptText("Enter your complete shipping address...");
        shippingAddressArea.setPrefRowCount(4);
        shippingAddressArea.setPrefWidth(500);

        HBox addressHelper = new HBox(10);
        addressHelper.setAlignment(Pos.CENTER_LEFT);
        Button copyAddressBtn = new Button(" Use My Home Address");
        copyAddressBtn.setStyle("-fx-background-color: #FFB6C1; -fx-text-fill: white; -fx-padding: 5px 15px;");
        copyAddressBtn.setOnAction(e -> {
            if (!customerAddressArea.getText().trim().isEmpty()) {
                shippingAddressArea.setText(customerAddressArea.getText());
            }
        });
        addressHelper.getChildren().add(copyAddressBtn);

        Label methodLabel = new Label("Shipping Method:");
        methodLabel.getStyleClass().add("info-label");

        HBox shippingMethods = new HBox(15);
        shippingMethods.setAlignment(Pos.CENTER_LEFT);

        ToggleGroup shippingGroup = new ToggleGroup();
        RadioButton standard = new RadioButton("Standard ($10) - 5-7 days");
        RadioButton express = new RadioButton("Express ($20) - 2-3 days");
        RadioButton pickup = new RadioButton("Store Pickup ($0)");

        standard.setToggleGroup(shippingGroup);
        express.setToggleGroup(shippingGroup);
        pickup.setToggleGroup(shippingGroup);
        standard.setSelected(true);

        String radioStyle = "-fx-font-weight: bold; -fx-text-fill: #8B4513;";
        standard.setStyle(radioStyle);
        express.setStyle(radioStyle);
        pickup.setStyle(radioStyle);

        shippingMethods.getChildren().addAll(standard, express, pickup);

        shippingGroup.selectedToggleProperty().addListener((obs, old, newToggle) -> {
            if (newToggle == standard) shipping = 10;
            else if (newToggle == express) shipping = 20;
            else shipping = 0;
            shippingLabel.setText(String.format("$%.2f", shipping));
            updateOrderSummary();
        });

        form.getChildren().addAll(addressLabel, addressHelper, shippingAddressArea, methodLabel, shippingMethods);
        step3Content.getChildren().addAll(title, form);

        return step3Content;
    }

    private VBox createStep4() {
        VBox step4Content = new VBox(20);
        step4Content.setPadding(new Insets(30));
        step4Content.setId("step4");

        Label title = new Label("Payment Details");
        title.getStyleClass().add("add-edit-title");
        title.setStyle("-fx-font-size: 28px;");

        VBox form = new VBox(15);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #FFFAFA; -fx-border-radius: 10px; " +
                "-fx-border-color: #FFB6C1; -fx-border-width: 2px;");

        Label methodLabel = new Label("Payment Method:");
        methodLabel.getStyleClass().add("info-label");

        paymentCombo = new ComboBox<>();
        paymentCombo.getItems().addAll("Credit/Debit Card", "Cash on Delivery", "Bank Transfer");
        paymentCombo.setValue("Credit/Debit Card");
        paymentCombo.setPrefWidth(250);
        paymentCombo.getStyleClass().add("search-combo");

        VBox cardDetails = new VBox(10);
        cardDetails.setId("cardDetails");

        Label cardLabel = new Label("Card Number:");
        cardLabel.getStyleClass().add("info-label");

        cardField = new TextField();
        cardField.getStyleClass().add("styled-text-field");
        cardField.setPromptText("1234 5678 9012 3456");
        cardField.setPrefWidth(250);

        HBox cardExtra = new HBox(15);
        cardExtra.setAlignment(Pos.CENTER_LEFT);

        Label expiryLabel = new Label("Expiry Date:");
        expiryLabel.getStyleClass().add("info-label");

        expiryField = new TextField();
        expiryField.getStyleClass().add("styled-text-field");
        expiryField.setPromptText("MM/YY");
        expiryField.setPrefWidth(100);

        Label cvvLabel = new Label("CVV:");
        cvvLabel.getStyleClass().add("info-label");

        cvvField = new TextField();
        cvvField.getStyleClass().add("styled-text-field");
        cvvField.setPromptText("123");
        cvvField.setPrefWidth(80);

        cardExtra.getChildren().addAll(expiryLabel, expiryField, cvvLabel, cvvField);
        cardDetails.getChildren().addAll(cardLabel, cardField, cardExtra);

        paymentCombo.valueProperty().addListener((obs, old, newVal) -> {
            boolean isCard = newVal.equals("Credit/Debit Card");
            cardDetails.setVisible(isCard);
            cardDetails.setManaged(isCard);
        });

        form.getChildren().addAll(methodLabel, paymentCombo, cardDetails);
        step4Content.getChildren().addAll(title, form);

        return step4Content;
    }

    private VBox createStep5() {
        VBox step5Content = new VBox(20);
        step5Content.setPadding(new Insets(30));
        step5Content.setId("step5");

        Label title = new Label("Order Confirmation");
        title.getStyleClass().add("add-edit-title");
        title.setStyle("-fx-font-size: 28px;");

        VBox confirmationBox = new VBox(15);
        confirmationBox.setPadding(new Insets(25));
        confirmationBox.setStyle("-fx-background-color: #FFFAFA; -fx-border-radius: 10px; " +
                "-fx-border-color: #FFB6C1; -fx-border-width: 2px;");

        Label thankYou = new Label("Thank you for your order!");
        thankYou.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #C71585;");

        VBox orderDetails = new VBox(15);
        orderDetails.setPadding(new Insets(15));

        Label orderSummaryLabel = new Label("Order Summary:");
        orderSummaryLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #8B4513;");

        VBox details = new VBox(8);
        details.setPadding(new Insets(10, 0, 0, 20));

        Label orderIdLabel = new Label("Order Id: ASAL-" + System.currentTimeMillis());
        Label customerLabel = new Label("Customer: " + (customerNameField.getText().isEmpty() ? "Not provided" : customerNameField.getText()));
        Label phoneLabel = new Label(" Phone: " + (customerPhoneField.getText().isEmpty() ? "Not provided" : customerPhoneField.getText()));
        Label totalLabel = new Label(" Total Amount: " + String.format("$%.2f", this.total));
        Label shippingLabel = new Label(" Shipping: " + String.format("$%.2f", shipping));
        Label statusLabel = new Label(" Status: Pending Processing");

        orderIdLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #8B4513;");
        customerLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #8B4513;");
        phoneLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #8B4513;");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #C71585; -fx-font-size: 16px;");
        shippingLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #8B4513;");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        details.getChildren().addAll(orderIdLabel, customerLabel, phoneLabel, totalLabel, shippingLabel, statusLabel);

        Label message = new Label("you will receive a confirmation email shortly.\nThank you for shopping with Asal BeautyCare");
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #696969; -fx-wrap-text: true;");
        message.setPadding(new Insets(15, 0, 0, 0));

        orderDetails.getChildren().addAll(orderSummaryLabel, details, message);
        confirmationBox.getChildren().add(orderDetails);

        step5Content.getChildren().addAll(title, confirmationBox);

        return step5Content;
    }

    private void setupNavigationButtons() {
        navigation = new HBox(20);
        navigation.setAlignment(Pos.CENTER);
        navigation.setPadding(new Insets(20, 0, 0, 0));

        Button backButton = new Button("Back");
        backButton.getStyleClass().add("button-back");
        backButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px 25px;");
        backButton.setOnAction(e -> goToPreviousStep());

        Button nextButton = new Button("Next");
        nextButton.getStyleClass().add("button-normal");
        nextButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px 30px;");
        nextButton.setOnAction(e -> goToNextStep());

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("button-back");
        cancelButton.setStyle("-fx-background-color: #F5AFAF; -fx-font-size: 14px; -fx-padding: 10px 25px;");
        cancelButton.setOnAction(e -> {
            CustomerProduct c = new CustomerProduct(primaryStage);
            c.show();
        });


        navigation.getChildren().addAll(backButton, nextButton, cancelButton);
        updateNavigationButtons();
    }

    private void updateStep(int step) {
        currentStep = step;

        for (int i = 0; i < mainContent.getChildren().size(); i++) {
            mainContent.getChildren().get(i).setVisible(false);
        }

        if (step >= 1 && step <= mainContent.getChildren().size()) {
            mainContent.getChildren().get(step - 1).setVisible(true);
        }

        updateStepIndicators();
        updateNavigationButtons();

        if (step == 1) {
            updateOrderSummary();
        }
    }

    private void updateStepIndicators() {
        for (int i = 0; i < stepIndicators.getChildren().size(); i += 2) {
            VBox stepBox = (VBox) stepIndicators.getChildren().get(i);
            StackPane circle = (StackPane) stepBox.getChildren().get(0);
            Label title = (Label) stepBox.getChildren().get(1);

            int stepNumber = i / 2 + 1;

            if (stepNumber == currentStep) {
                circle.setStyle("-fx-background-color: #FF69B4; -fx-border-color: #FF69B4;");
                title.setStyle("-fx-text-fill: #FF1493; -fx-font-weight: bold;");
            } else if (stepNumber < currentStep) {
                circle.setStyle("-fx-background-color: #4CAF50; -fx-border-color: #4CAF50;");
                title.setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold;");
            } else {
                circle.setStyle("-fx-background-color: white; -fx-border-color: #FFB6C1;");
                title.setStyle("-fx-text-fill: #C71585;");
            }
        }
    }

    private void updateNavigationButtons() {
        Button backs = (Button) navigation.getChildren().get(0);
        Button next = (Button) navigation.getChildren().get(1);

        if (currentStep == TOTAL_STEPS) {
            next.setText("Place Order");
            next.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 30px;");
        } else {
            next.setText("Next");
            next.setStyle("-fx-background-color: #FF69B4; -fx-text-fill: white; " +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 30px;");
        }

        backs.setVisible(currentStep > 1);
    }

    private void goToPreviousStep() {
        if (currentStep > 1) {
            updateStep(currentStep - 1);
        }
    }

    private void goToNextStep() {
        if (!validateCurrentStep()) {
            return;
        }

        if (currentStep < TOTAL_STEPS) {
            updateStep(currentStep + 1);
        } else {
            placeOrder();
        }
    }

    private boolean validateCurrentStep() {
        switch (currentStep) {
            case 1:
                if (cartItems.isEmpty()) {
                    AlertManager.showError("your cart is empty please add items to your cart before proceeding");
                    return false;
                }
                break;

            case 2:
                if (customerNameField.getText().trim().isEmpty()) {
                    AlertManager.showError("name required please enter your full name");
                    return false;
                }
                if (customerPhoneField.getText().trim().isEmpty()) {
                    AlertManager.showError("phone required please enter your phone number");
                    return false;
                }
                if (customerAddressArea.getText().trim().isEmpty()) {
                    AlertManager.showError("address required please enter your home address");
                    return false;
                }
                break;

            case 3:
                if (shippingAddressArea.getText().trim().isEmpty()) {
                    AlertManager.showError("shipping address required please enter your shipping address");
                    return false;
                }
                break;

            case 4:
                String method = paymentCombo.getValue();
                if (method.equals("Credit/Debit Card")) {
                    if (cardField.getText().trim().isEmpty() ||
                            expiryField.getText().trim().isEmpty() ||
                            cvvField.getText().trim().isEmpty()) {
                        AlertManager.showError("payment information please enter all card details");
                        return false;
                    }

                    if (!cardField.getText().replaceAll("\\s", "").matches("\\d{16}")) {
                        AlertManager.showError("Invalid Card Please enter a valid 16-digit card number.");
                        return false;
                    }
                }
                break;
        }
        return true;
    }


    private void updateOrderSummary() {
        subtotal = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        Random random = new Random();
        double discountPercent = 5 + (10 * random.nextDouble());
        discount = subtotal * (discountPercent / 100.0);

        tax = (subtotal - discount) * 0.16;


        total = subtotal - discount + shipping + tax;

        if (subtotalLabel != null) {
            subtotalLabel.setText(String.format("$%.2f", subtotal));
            discountLabel.setText(String.format("-$%.2f", discount));
            shippingLabel.setText(String.format("$%.2f", shipping));
            taxLabel.setText(String.format("$%.2f", tax));
            totalLabel.setText(String.format("$%.2f", total));
        }
    }


    private void loadSampleData() {
        updateOrderSummary();
    }

    private void placeOrder() {

        if (cartItems.isEmpty()) {
            AlertManager.showError("Your cart is empty. Please add items before placing an order.");
            return;
        }
        if (customerNameField.getText().trim().isEmpty() ||
                customerPhoneField.getText().trim().isEmpty() ||
                customerAddressArea.getText().trim().isEmpty()) {
            AlertManager.showError("Please fill in all customer information.");
            return;
        }

        Customer customer = new Customer();
        customer.setCustomerName(customerNameField.getText());
        customer.setCustomerPhone(customerPhoneField.getText());
        customer.setCustomerAddress(customerAddressArea.getText());


        int customerId = DatabaseOperationsCustomer.addOrGetCustomer(customer);
        customer.setCustomerId(customerId);


        Orders order = new Orders();
        order.setCustomerId(customerId);
        order.setOrderDate(new java.sql.Date(System.currentTimeMillis()));
        order.setStatus("Pending");
        order.setPaymentStatus("Unpaid");
        order.setTotalAmount(subtotal);
        order.setDiscount(discount);
        order.setFinalAmount(total);
        order.setCost(0);
        order.setNotes("New order");

        DatabaseOperationsOrder.addOrder(order);


        for (CartItem item : cartItems) {
            DatabaseOperationsOrderProduct.addProductToOrder(
                    new Order_Product(
                            order.getOrderId(),
                            item.getProductId(),
                            item.getQuantity(),
                            item.getPrice()
                    )
            );
        }

    }
}
