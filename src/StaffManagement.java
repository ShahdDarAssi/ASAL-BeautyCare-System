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

public class StaffManagement {
    public static TableView<Staff> table;
    static ObservableList<Staff> data;
    private Stage primaryStage;
    private BorderPane root;

    private TextField searchField;
    private ComboBox<String> searchCombo;

    public static ObservableList<Staff> staff = FXCollections.observableArrayList();

    public StaffManagement(BorderPane root, Stage stage) {
        this.root = root;
        this.primaryStage = stage;
    }

    public void show() {

        root.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        root.getStyleClass().add("root-pane");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10, 0, 10, 0));
        searchBox.getStyleClass().add("search-box");

        searchCombo = new ComboBox<>();
        searchCombo.getItems().addAll("id", "name", "position");
        searchCombo.setValue("id");
        searchCombo.setPrefWidth(100);
        searchCombo.getStyleClass().add("search-combo");

        searchField = new TextField();
        searchField.setPromptText("Enter id or name or position...");
        searchField.setPrefWidth(200);
        searchField.getStyleClass().add("search-field");

        Button search = new Button("Search");
        search.getStyleClass().add("button-normal");

        Button showAll = new Button("show all");
        showAll.getStyleClass().add("button-normal");

        searchBox.getChildren().addAll(
                new Label("Search by:"),
                searchCombo,
                searchField,
                search,
                showAll
        );
        HBox buttonHBox = new HBox(20);
        buttonHBox.getStyleClass().add("button-box");

        Button add = createStyledButton("Add Staff", "button-normal");
        Button edit = createStyledButton("Edit Staff", "button-normal");
        Button delete = createStyledButton("Deactivate Staff", "button-normal");
        Button back = createStyledButton("Back", "button-back");

        buttonHBox.getChildren().addAll(add, edit, delete, back);

        Label titleL = new Label("Staff Management");
        titleL.getStyleClass().add("title-label");

        VBox title = new VBox(10);
        title.getStyleClass().add("title-box");
        title.getChildren().addAll(titleL);

        Label staffLabel = new Label();
        staffLabel.getStyleClass().add("staff-count-label");

//table
        table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Staff, Integer> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("staffId"));
        id.setMinWidth(50);
        id.getStyleClass().add("column-center");

        TableColumn<Staff, String> name = new TableColumn<>("Staff Name");
        name.setCellValueFactory(new PropertyValueFactory<>("staffName"));
        name.setMinWidth(200);

        TableColumn<Staff, String> position = new TableColumn<>("Position");
        position.setCellValueFactory(new PropertyValueFactory<>("position"));
        position.setMinWidth(300);

        TableColumn<Staff, Double> salary = new TableColumn<>("Salary");
        salary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        salary.setMinWidth(80);
        salary.getStyleClass().add("column-center");

        table.getColumns().addAll(id, name, position, salary);

        data = FXCollections.observableArrayList();
        table.setItems(data);

        refreshTable();
        updateStaffLabel(staffLabel);

        search.setOnAction(e -> {
            performSearch();
            updateStaffLabel(staffLabel);
        });

        showAll.setOnAction(e -> {
            searchField.clear();
            refreshTable();
            updateStaffLabel(staffLabel);
        });

        searchField.setOnAction(e -> {
            performSearch();
            updateStaffLabel(staffLabel);
        });

        add.setOnAction(e -> addScreen());

        edit.setOnAction(e -> {
            Staff staff = table.getSelectionModel().getSelectedItem();
            if (staff == null) {
                AlertManager.showError("Please select a staff to edit");
                return;
            }
            editScreen(staff);
        });

        delete.setOnAction(e -> {
            Staff staff = table.getSelectionModel().getSelectedItem();
            if (staff == null) {
                AlertManager.showError("Please select a staff to delete");
                return;
            }

            if (AlertManager.showConfirmation(
                    "Are you sure you want to delete staff: " + staff.getStaffName() + "?")) {

                boolean success = DatabaseOperationsStaff.deleteStaff(staff.getStaffId());
                if (success) {
                    refreshTable();
                    updateStaffLabel(staffLabel);
                    AlertManager.showInformationMessage("staff deleted successfully");
                } else {
                    AlertManager.showError("failed to delete staff from database");
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
        mainContent.getChildren().addAll(
                searchBox,
                buttonHBox,
                title,
                staffLabel,
                table
        );

        root.setCenter(mainContent);

    }

    private void updateStaffLabel(Label label) {
        int totalStaffs = DatabaseOperationsStaff.getStaffCount();
        String countText = "Total Staffs: " + totalStaffs;
        label.setText(countText);
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        String searchType = searchCombo.getValue();

        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }

        ObservableList<Staff> searchResults;

        if ("id".equals(searchType)) {
            try {
                int staffId = Integer.parseInt(keyword);
                Staff staff = DatabaseOperationsStaff.getStaffById(staffId);
                searchResults = FXCollections.observableArrayList();
                if (staff != null) {
                    searchResults.add(staff);
                }
            } catch (NumberFormatException e) {
                AlertManager.showError("Invalid ID \n Please enter a valid numeric ID");
                return;
            }
        } else if ("Position".equals(searchType)) {
            try {
                String staffPosition = keyword;
                ObservableList<Staff> staffList = DatabaseOperationsStaff.searchStaffByPosition(staffPosition);
                searchResults = FXCollections.observableArrayList();

                if (staffList != null && !staffList.isEmpty()) {
                    searchResults.addAll(staffList);
                }

            } catch (Exception e) {
                AlertManager.showError("Invalid position \n Please enter a valid position");
                return;
            }

        } else {
            searchResults = searchStaffsByName(keyword);
        }

        updateTableData(searchResults);

        if (searchResults.isEmpty()) {
            AlertManager.showInformationMessage("No staff found for: " + keyword);
        }
    }

    private ObservableList<Staff> searchStaffsByName(String keyword) {
        ObservableList<Staff> allStaff = DatabaseOperationsStaff.getAllStaff();
        ObservableList<Staff> filteredAllStaffs = FXCollections.observableArrayList();

        for (Staff staff : allStaff) {
            if (staff.getStaffName().toLowerCase().contains(keyword.toLowerCase()) ||
                    staff.getPosition().toLowerCase().contains(keyword.toLowerCase())) {
                filteredAllStaffs.add(staff);
            }
        }

        return filteredAllStaffs;
    }

    private void updateTableData(ObservableList<Staff> newData) {
        Platform.runLater(() -> {
            data.clear();
            data.addAll(newData);
        });
    }

    private Button createStyledButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        return button;
    }

    //add screen
    private void addScreen() {
        VBox addBox = new VBox(15);
        addBox.setPadding(new Insets(30));
        addBox.setAlignment(Pos.CENTER);
        addBox.getStyleClass().add("add-edit-box");

        Label title = new Label("Add New Staff");
        title.getStyleClass().add("add-edit-title");

        Label staffIdLabel = new Label("Staff ID will be assigned automatically");
        staffIdLabel.getStyleClass().add("info-label");

        TextField name = createStyledTextField("Staff Name", "");
        TextField position = createStyledTextField("Position", "");
        TextField salary = createStyledTextField("Salary", "");


        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button save = createPinkButton("Save", "#ff69b4", "#ff1493");
        Button back = createPinkButton("Back", "#ffb6c1", "#db7093");

        buttons.getChildren().addAll(save, back);

        addBox.getChildren().addAll(title, staffIdLabel, name, position, salary, buttons);

        BorderPane container = new BorderPane(addBox);
        container.getStyleClass().add("add-edit-container");
        root.setCenter(container);

        back.setOnAction(e -> show());

        save.setOnAction(e -> {
            if (!validateInput(name, position, salary)) {
                return;
            }
            saveStaff(name, position, salary);
        });
    }

    //edit screen
    private void editScreen(Staff staff) {
        VBox editBox = new VBox(15);
        editBox.setPadding(new Insets(30));
        editBox.setAlignment(Pos.CENTER);
        editBox.getStyleClass().add("add-edit-box");

        Label title = new Label("Edit Staff ");
        title.getStyleClass().add("add-edit-title");

        Label satffIdLabel = new Label("Staff ID: " + staff.getStaffId());
        satffIdLabel.getStyleClass().add("info-label");

        TextField name = createStyledTextField("Staff Name", staff.getStaffName());
        TextField position = createStyledTextField("Position", staff.getPosition());
        TextField salary = createStyledTextField("Salary", String.valueOf(staff.getSalary()));

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);

        Button updates = createPinkButton("Update", "#ff69b4", "#ff1493");
        Button back = createPinkButton("Cancel", "#ffb6c1", "#db7093");

        buttons.getChildren().addAll(updates, back);

        editBox.getChildren().addAll(title, satffIdLabel, name, position, salary, buttons);

        BorderPane container = new BorderPane(editBox);
        container.getStyleClass().add("add-edit-container");
        root.setCenter(container);

        back.setOnAction(e -> show());

        updates.setOnAction(e -> {
            if (!validateInput(name, position, salary)) {
                return;
            }
            updateStaff(staff, name, position, salary);
        });

    }

    //validation method to make sure that all alright
    private boolean validateInput(TextField name, TextField position, TextField salary) {
        if (name.getText().trim().isEmpty() || position.getText().trim().isEmpty() || salary.getText().trim().isEmpty()) {
            AlertManager.showError("please fill in all fields");
            return false;
        }

        return true;
    }

    //to save it after add
    private void saveStaff(TextField name, TextField position, TextField salary) {
        Staff newStaff = new Staff(
                0,
                name.getText().trim(),
                position.getText().trim(),
                Double.parseDouble(salary.getText().trim())
        );

        boolean success = DatabaseOperationsStaff.addStaff(newStaff);

        if (success) {
            String message = "Staff added successfully\n\n" +
                    "Staff details:\n" +
                    "• Staff id: " + newStaff.getStaffId() + "\n" +
                    "• Staff name: " + newStaff.getStaffName() + "\n" +
                    "• Staff position : " + newStaff.getPosition() +
                    "• Staff salary : " + newStaff.getSalary();

            AlertManager.showInformationMessage(message);
            refreshTable();
            show();
        } else {
            AlertManager.showError("failed to add staff");
        }
    }

    //update method
    private void updateStaff(Staff originalStaff, TextField name, TextField position, TextField salary) {
        originalStaff.setStaffName(name.getText().trim());
        originalStaff.setPosition(position.getText().trim());
        originalStaff.setSalary(Double.parseDouble(salary.getText().trim()));

        boolean success = DatabaseOperationsStaff.updateStaff(originalStaff);

        if (success) {
            AlertManager.showInformationMessage("staff updated successfully");
            refreshTable();
            show();
        } else {
            AlertManager.showError("failed to update staff");
        }
    }

    //to refresh data after edit
    public void refreshTable() {
        ObservableList<Staff> allStaff = DatabaseOperationsStaff.getAllStaff();
        updateTableData(allStaff);
    }

    //styled buttons
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

    private TextField createStyledTextField(String promptText, String initialValue) {
        TextField textField = new TextField(initialValue);
        textField.setPromptText(promptText);
        textField.setMaxWidth(350);
        textField.setPrefHeight(40);
        textField.getStyleClass().add("styled-text-field");
        return textField;
    }


}