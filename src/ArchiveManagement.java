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

import java.sql.Date;
import java.util.ArrayList;

public class ArchiveManagement {
	
    private Stage primaryStage;
    private BorderPane root;
    private TextField searchField;
    private ComboBox<String> searchCombo;
    private ComboBox<String> archiveCombo;

    private TableView<Staff> staffTable;
    private TableView<Purchase> purchaseTable;
    private TableView<Returns> returnsTable;

    // Labels
    private Label archiveLabel;
    private Label titleLabel;

    private VBox table;

    private static ObservableList<Staff> staffArchive = FXCollections.observableArrayList();
    private static ObservableList<Purchase> purchaseArchive = FXCollections.observableArrayList();
    private static ObservableList<Returns> returnsArchive = FXCollections.observableArrayList();

    public ArchiveManagement(BorderPane root, Stage primaryStage) {
        this.root = root;
        this.primaryStage = primaryStage;
    }

    public void show() {
        root.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        root.getStyleClass().add("root-pane");

        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10, 0, 10, 0));
        box.getStyleClass().add("search-box");

        archiveCombo = new ComboBox<>();
        archiveCombo.getItems().addAll("Staff", "Purchases", "Returns");
        archiveCombo.setValue("Staff");
        archiveCombo.setPrefWidth(120);
        archiveCombo.getStyleClass().add("search-combo");
        archiveCombo.setOnAction(e -> switchArchiveType());

        searchCombo = new ComboBox<>();
        searchCombo.setPrefWidth(100);
        searchCombo.getStyleClass().add("search-combo");
        updateSearchOptions();

        searchField = new TextField();
        searchField.setPromptText("enter search keyword");
        searchField.setPrefWidth(200);
        searchField.getStyleClass().add("search-field");

        Button search = createStyledButton("Search", "button-normal");
        Button showAll= createStyledButton("show all", "button-normal");

        box.getChildren().addAll(new Label("Archive Type:"), archiveCombo, new Label("Search by:"), searchCombo, searchField,search, showAll);


        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);
        buttons.getStyleClass().add("button-box");

        Button restore= createStyledButton("Restore", "button-normal");
        Button delete = createStyledButton("Delete", "button-normal");
        Button back = createStyledButton("Back", "button-back");

        buttons.getChildren().addAll(restore, delete, back);
        titleLabel = new Label("Staff Archive");
        titleLabel.getStyleClass().add("title-label");

        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getStyleClass().add("title-box");
        titleBox.getChildren().addAll(titleLabel);


        archiveLabel = new Label();
        archiveLabel.getStyleClass().add("purchase-count-label");


        initializeTable();

        table = new VBox();
        table.getStyleClass().add("table-container");
        table.getChildren().add(staffTable);


        switchArchiveType();

        search.setOnAction(e -> performSearch());
        showAll.setOnAction(e -> refreshCurrentTable());
        searchField.setOnAction(e -> performSearch());

        restore.setOnAction(e -> restoreSelectedItem());
        delete.setOnAction(e -> deleteSelectedItem());

        back.setOnAction(e -> {
            try {
                AdminScreen dashboard = new AdminScreen(primaryStage);
                dashboard.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox main = new VBox(15);
        main.getStyleClass().add("main-content");
        main.getChildren().addAll(box, titleBox, archiveLabel, buttons, table);

        root.setCenter(main);
    }

    private void initializeTable() {
        staffTable = createStaffTable();
        purchaseTable = createPurchaseTable();
        returnsTable = createReturnsTable();
    }
    //to create the tables content
    private TableView<Staff> createStaffTable() {
        TableView<Staff> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Staff, Integer> id = new TableColumn<>("Id");
        id.setCellValueFactory(new PropertyValueFactory<>("staffId"));
        id.setMinWidth(80);
        id.getStyleClass().add("column-center");

        TableColumn<Staff, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("staffName"));
        name.setMinWidth(150);
        name.getStyleClass().add("column-center");

        TableColumn<Staff, String> position = new TableColumn<>("Position");
        position.setCellValueFactory(new PropertyValueFactory<>("position"));
        position.setMinWidth(120);
        position.getStyleClass().add("column-center");

        TableColumn<Staff, Double> salary = new TableColumn<>("Salary");
        salary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        salary.setMinWidth(100);
        salary.getStyleClass().add("column-right");

        TableColumn<Staff, Date> archivedDate = new TableColumn<>("Archived Date");
        archivedDate.setCellValueFactory(new PropertyValueFactory<>("archivedDate"));
        archivedDate.setMinWidth(120);
        archivedDate.getStyleClass().add("column-center");

        table.getColumns().addAll(id, name, position, salary, archivedDate);

        return table;
    }

    private TableView<Purchase> createPurchaseTable() {
        TableView<Purchase> table = new TableView<>();
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

        TableColumn<Purchase, Double> total_cost = new TableColumn<>("Cost");
        total_cost.setCellValueFactory(new PropertyValueFactory<>("total_cost"));
        total_cost.setMinWidth(80);
        total_cost.getStyleClass().add("column-right");


        TableColumn<Purchase, Date> archivedDate = new TableColumn<>("Archived Date");
        archivedDate.setCellValueFactory(new PropertyValueFactory<>("archivedDate"));
        archivedDate.setMinWidth(120);
        archivedDate.getStyleClass().add("column-center");

        table.getColumns().addAll(id, supplier, date, total_cost,archivedDate);
        return table;
    }

    private TableView<Returns> createReturnsTable() {
        TableView<Returns> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Returns, Integer> returnId = new TableColumn<>("Return Id");
        returnId.setCellValueFactory(new PropertyValueFactory<>("returnId"));
        returnId.setMinWidth(100);
        returnId.getStyleClass().add("column-center");

        TableColumn<Returns, Integer> orderId = new TableColumn<>("Order Id");
        orderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderId.setMinWidth(100);
        orderId.getStyleClass().add("column-center");

        TableColumn<Returns, Date> returnDate = new TableColumn<>("Return Date");
        returnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        returnDate.setMinWidth(120);
        returnDate.getStyleClass().add("column-center");

        TableColumn<Returns, Double> refund = new TableColumn<>("Refund");
        refund.setCellValueFactory(new PropertyValueFactory<>("refundAmount"));
        refund.setMinWidth(100);
        refund.getStyleClass().add("column-right");

        TableColumn<Returns, String> status = new TableColumn<>("Status");
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        status.setMinWidth(120);
        status.getStyleClass().add("column-center");

        TableColumn<Returns, Date> archivedDate = new TableColumn<>("Archived Date");
        archivedDate.setCellValueFactory(new PropertyValueFactory<>("archivedDate"));
        archivedDate.setMinWidth(120);
        archivedDate.getStyleClass().add("column-center");

        table.getColumns().addAll(returnId, orderId, returnDate, refund, status, archivedDate);
        return table;
    }
    //to get the data that are arvhived
    private void loadStaffArchive() {
        staffArchive.clear();
        ArrayList<Staff> list = DatabaseOperationsArchive.getArchivedStaff();
        staffArchive.addAll(list);
    }


    private void loadPurchaseArchive() {
        purchaseArchive.clear();
        ArrayList<Purchase> list = DatabaseOperationsArchive.getArchivedPurchases();
        purchaseArchive.addAll(list);
    }

    private void loadReturnsArchive() {
        returnsArchive.clear();
        ArrayList<Returns> list = DatabaseOperationsArchive.getArchivedReturns();
        returnsArchive.addAll(list);
    }

//to put the type for search
    private void updateSearchOptions() {
        String type = archiveCombo.getValue();
        searchCombo.getItems().clear();

        switch (type) {
            case "Staff":
                searchCombo.getItems().addAll("Id", "Name", "Position");
                searchCombo.setValue(" Staff Id");
                break;
            case "Purchases":
                searchCombo.getItems().addAll("Purchase Id", "Supplier Id");
                searchCombo.setValue("Purchase Id");
                break;
            case "Returns":
                searchCombo.getItems().addAll("Return Id", "Order Id", "Status");
                searchCombo.setValue("Return Id");
                break;
        }
    }

    private void switchArchiveType() {
        String type = archiveCombo.getValue();
        switch (type) {
            case "Staff":
                titleLabel.setText("Staff Archive");
                loadStaffArchive();
                staffTable.setItems(staffArchive);
                showTable(staffTable);
                break;
            case "Purchases":
                titleLabel.setText("Purchases Archive");
                loadPurchaseArchive();
                purchaseTable.setItems(purchaseArchive);
                showTable(purchaseTable);
                break;
            case "Returns":
                titleLabel.setText("Returns Archive");
                loadReturnsArchive();
                returnsTable.setItems(returnsArchive);
                showTable(returnsTable);
                break;
        }
        updateSearchOptions();
        updateLabel();
    }

    private void showTable(TableView<?> tables) {//? to make it accept the different values of classes
        Platform.runLater(() -> {
            table.getChildren().clear();
            table.getChildren().add(tables);
        });
    }


    private void restoreSelectedItem() {
        Object selected = getCurrentTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertManager.showError("Please select an item first");
            return;
        }

        boolean success = false;
        switch (archiveCombo.getValue()) {
            case "Staff":
                Staff s = (Staff) selected;
                success = DatabaseOperationsArchive.restoreStaff(s.getStaffId());
                if (success) staffArchive.remove(s);
                break;
            case "Purchases":
                Purchase p = (Purchase) selected;
                success = DatabaseOperationsArchive.restorePurchase(p.getPurchase_id());
                if (success) purchaseArchive.remove(p);
                break;
            case "Returns":
                Returns r = (Returns) selected;
                success = DatabaseOperationsArchive.restoreReturn(r.getReturnId());
                if (success) returnsArchive.remove(r);
                break;
        }

        if (success) {
            updateLabel();
            AlertManager.showInformationMessage("Restored successfully");
            refreshCurrentTable();
        } else {
            AlertManager.showError("Restore failed");
        }
    }

    private void deleteSelectedItem() {
        Object selected = getCurrentTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertManager.showError("Please select an item first");
            return;
        }

        if (!AlertManager.showConfirmation("Are you sure you want to permanently delete this item?")) {
            return;
        }

        boolean success = false;
        switch (archiveCombo.getValue()) {
            case "Staff":
                success = DatabaseOperationsArchive.deleteStaffPermanently(((Staff) selected).getStaffId());
                if (success) staffArchive.remove(selected);
                break;
            case "Purchases":
                success = DatabaseOperationsArchive.deletePurchase(((Purchase) selected).getPurchase_id());
                if (success) purchaseArchive.remove(selected);
                break;
            case "Returns":
                success = DatabaseOperationsArchive.deleteReturnPermanently(((Returns) selected).getReturnId());
                if (success) returnsArchive.remove(selected);
                break;
        }
        if (success) {
            refreshCurrentTable();
            updateLabel();
            AlertManager.showInformationMessage("item deleted");
        } else {
            AlertManager.showError("delete failed");
        }
    }

    private TableView<?> getCurrentTable() {
        switch (archiveCombo.getValue()) {
            case "Staff":
                return staffTable;
            case "Purchases":
                return purchaseTable;
            case "Returns":
                return returnsTable;
            default:
                return purchaseTable;
        }
    }

    private void refreshCurrentTable() {
        switchArchiveType();
        searchField.clear();
    }

    private void updateLabel() {
        int count = 0;
        switch (archiveCombo.getValue()) {
            case "Staff":
                count = staffArchive.size();
                break;
            case "Purchases":
                count = purchaseArchive.size();
                break;
            case "Returns":
                count = returnsArchive.size();
                break;
        }
        archiveLabel.setText("Total Archived " + archiveCombo.getValue() + ": " + count);
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        String searchType = searchCombo.getValue();
        String archiveType = archiveCombo.getValue();

        if (keyword.isEmpty()) {
            refreshCurrentTable();
            return;
        }

        ObservableList<?> searchResults = FXCollections.observableArrayList();

        try {
            switch (archiveType) {
                case "Staff":
                    searchResults = searchStaff(keyword, searchType);
                    break;
                case "Purchases":
                    searchResults = searchPurchases(keyword, searchType);
                    break;
                case "Returns":
                    searchResults = searchReturns(keyword, searchType);
                    break;
            }
        } catch (NumberFormatException e) {
            AlertManager.showError("invalid id \nplease enter a valid  id");
            return;
        }

        updateTableData(searchResults);

        if (searchResults.isEmpty()) {
            AlertManager.showInformationMessage("no items found of  : " + keyword);
        }
    }
//to search for the type of data
    private ObservableList<Staff> searchStaff(String keyword, String searchType) {
        ObservableList<Staff> results = FXCollections.observableArrayList();
        for (Staff staff : staffArchive) {
            switch (searchType) {
                case "ID":
                    if (String.valueOf(staff.getStaffId()).contains(keyword)) {
                        results.add(staff);
                    }
                    break;
                case "Name":
                    if (staff.getStaffName().toLowerCase().contains(keyword.toLowerCase())) {
                        results.add(staff);
                    }
                    break;
                case "Position":
                    if (staff.getPosition().toLowerCase().contains(keyword.toLowerCase())) {
                        results.add(staff);
                    }
                    break;
            }
        }
        return results;
    }


    private ObservableList<Purchase> searchPurchases(String keyword, String searchType) {
        ObservableList<Purchase> results = FXCollections.observableArrayList();
        for (Purchase purchase : purchaseArchive) {
            switch (searchType) {
                case "Purchase ID":
                    if (String.valueOf(purchase.getPurchase_id()).contains(keyword)) {
                        results.add(purchase);
                    }
                    break;
                case "Supplier ID":
                    if (String.valueOf(purchase.getSupplier().getSupplierId()).contains(keyword)) {
                        results.add(purchase);
                    }
                    break;
            }
        }
        return results;
    }

    private ObservableList<Returns> searchReturns(String keyword, String searchType) {
        ObservableList<Returns> results = FXCollections.observableArrayList();
        for (Returns returns : returnsArchive) {
            switch (searchType) {
                case "Return ID":
                    if (String.valueOf(returns.getReturnId()).contains(keyword)) {
                        results.add(returns);
                    }
                    break;
                case "Order ID":
                    if (String.valueOf(returns.getOrderId()).contains(keyword)) {
                        results.add(returns);
                    }
                    break;
                case "Status":
                    if (returns.getStatus().toLowerCase().contains(keyword.toLowerCase())) {
                        results.add(returns);
                    }
                    break;
            }
        }
        return results;
    }
//to update table data after choose type
    private void updateTableData(ObservableList<?> newData) {
        Platform.runLater(() -> {
            TableView<?> currentTable = getCurrentTable();
            if (currentTable == staffTable) {
                staffArchive.clear();
                staffArchive.addAll((ObservableList<Staff>) newData);
            } else if (currentTable == purchaseTable) {
                purchaseArchive.clear();
                purchaseArchive.addAll((ObservableList<Purchase>) newData);
            } else if (currentTable == returnsTable) {
                returnsArchive.clear();
                returnsArchive.addAll((ObservableList<Returns>) newData);
            }
        });
    }

//for buttons
    private Button createStyledButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        return button;
    }
}