import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class DatabaseOperationsPurchaseItem {

    public static boolean addPurchase(Purchase_item item) {
        boolean success = false;
        String sql = "INSERT INTO Purchase_Item (purchase_id, product_id, quantity, cost_per_unit) VALUES (?, ?, ?, ?)";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, item.getPurchase().getPurchase_id());
            pstmt.setInt(2, item.getProduct().getProduct_id());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setInt(4, item.getCost_per_unit());

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

            if (success) {
                updatePurchaseTotalCost(item.getPurchase().getPurchase_id());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public static boolean updatePurchase(Purchase_item item) {
        boolean success = false;
        String sql = "UPDATE Purchase_Item SET product_id = ?, quantity = ?, cost_per_unit = ? WHERE item_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, item.getProduct().getProduct_id());
            pstmt.setInt(2, item.getQuantity());
            pstmt.setInt(3, item.getCost_per_unit());
            pstmt.setInt(4, item.getItem_id());

            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;

            if (success) {
                updatePurchaseTotalCost(item.getPurchase().getPurchase_id());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public static boolean deletePurchase(int itemId) {
        boolean success = false;
        String sql = "DELETE FROM Purchase_Item WHERE item_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            int purchaseId = getPurchaseIdByItemId(itemId);

            pstmt.setInt(1, itemId);
            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0;


            if (success) {
                updatePurchaseTotalCost(purchaseId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    private static int getPurchaseIdByItemId(int itemId) {
        int purchaseId = 0;
        String sql = "SELECT purchase_id FROM Purchase_Item WHERE item_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                purchaseId = rs.getInt("purchase_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchaseId;
    }

    public static ObservableList<Purchase_item> getAllPurchase() {
        ObservableList<Purchase_item> items = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Purchase_Item ORDER BY item_id";

        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Purchase_item item = new Purchase_item(
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        rs.getInt("cost_per_unit"),
                        new Purchase(rs.getInt("purchase_id"), 0.0, null),
                        new Product(rs.getInt("product_id"), "", 0.0, "", "", null)
                );
                items.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public static Purchase_item getPurchaseById(int itemId) {
        String sql = "SELECT * FROM Purchase_Item WHERE item_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Purchase_item(
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        rs.getInt("cost_per_unit"),
                        new Purchase(rs.getInt("purchase_id"), 0.0, null),
                        new Product(rs.getInt("product_id"), "", 0.0, "", "", null)
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ObservableList<Purchase_item> getItemsByPurchaseId(int purchaseId) {
        ObservableList<Purchase_item> items = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Purchase_Item WHERE purchase_id = ? ORDER BY item_id";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, purchaseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Purchase_item item = new Purchase_item(
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        rs.getInt("cost_per_unit"),
                        new Purchase(rs.getInt("purchase_id"), 0.0, null),
                        new Product(rs.getInt("product_id"), "", 0.0, "", "", null)
                );
                items.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public static Purchase_item getItemByProductAndPurchase(int productId, int purchaseId) {
        String sql = "SELECT * FROM Purchase_Item WHERE product_id = ? AND purchase_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            pstmt.setInt(2, purchaseId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Purchase_item(
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        rs.getInt("cost_per_unit"),
                        new Purchase(rs.getInt("purchase_id"), 0.0, null),
                        new Product(rs.getInt("product_id"), "", 0.0, "", "", null)
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ObservableList<Purchase_item> searchPurchaseItems(String searchType, String keyword) {
        ObservableList<Purchase_item> items = FXCollections.observableArrayList();

        try (Connection con = DataBaseConnection.getConnection()) {

            switch (searchType) {
                case "Item ID":
                    try {
                        int itemId = Integer.parseInt(keyword);
                        Purchase_item item = getPurchaseById(itemId);
                        if (item != null) {
                            items.add(item);
                        }
                    } catch (NumberFormatException e) {
                        return items;
                    }
                    break;

                case "Product ID":
                    try {
                        int productId = Integer.parseInt(keyword);
                        String sql = "SELECT * FROM Purchase_Item WHERE product_id = ?";
                        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                            pstmt.setInt(1, productId);
                            ResultSet rs = pstmt.executeQuery();
                            while (rs.next()) {
                                items.add(createPurchaseItemFromResultSet(rs));
                            }
                        }
                    } catch (NumberFormatException e) {
                        return items;
                    }
                    break;

                case "Purchase ID":
                    try {
                        int purchaseId = Integer.parseInt(keyword);
                        items = getItemsByPurchaseId(purchaseId);
                    } catch (NumberFormatException e) {
                        return items;
                    }
                    break;

                default:
                    String sql = "SELECT * FROM Purchase_Item WHERE " +
                            "CAST(item_id AS CHAR) LIKE ? OR " +
                            "CAST(purchase_id AS CHAR) LIKE ? OR " +
                            "CAST(product_id AS CHAR) LIKE ? " +
                            "ORDER BY item_id";
                    try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                        String searchPattern = "%" + keyword + "%";
                        pstmt.setString(1, searchPattern);
                        pstmt.setString(2, searchPattern);
                        pstmt.setString(3, searchPattern);

                        ResultSet rs = pstmt.executeQuery();
                        while (rs.next()) {
                            items.add(createPurchaseItemFromResultSet(rs));
                        }
                    }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public static int getTotalQuantityByProduct(int productId) {
        String sql = "SELECT SUM(quantity) as total_quantity FROM Purchase_Item WHERE product_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total_quantity");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double getTotalCostByProduct(int productId) {
        String sql = "SELECT SUM(quantity * cost_per_unit) as total_cost FROM Purchase_Item WHERE product_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_cost");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static double getTotalCostByPurchase(int purchaseId) {
        String sql = "SELECT SUM(quantity * cost_per_unit) as total_cost FROM Purchase_Item WHERE purchase_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, purchaseId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_cost");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static int getPurchaseCount() {
        String sql = "SELECT COUNT(*) as count FROM Purchase_Item";

        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getPurchaseCountByPurchaseId(int purchaseId) {
        String sql = "SELECT COUNT(*) as count FROM Purchase_Item WHERE purchase_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, purchaseId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean purchaseItemExists(int itemId) {
        String sql = "SELECT COUNT(*) as count FROM Purchase_Item WHERE item_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean productExistsInPurchase(int productId, int purchaseId) {
        String sql = "SELECT COUNT(*) as count FROM Purchase_Item WHERE product_id = ? AND purchase_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            pstmt.setInt(2, purchaseId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updatePurchaseTotalCost(int purchaseId) {
        String sql = "UPDATE Purchase SET total_cost = ? WHERE purchase_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            double totalCost = getTotalCostByPurchase(purchaseId);
            pstmt.setDouble(1, totalCost);
            pstmt.setInt(2, purchaseId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Purchase_item createPurchaseItemFromResultSet(ResultSet rs) throws SQLException {
        return new Purchase_item(
                rs.getInt("item_id"),
                rs.getInt("quantity"),
                rs.getInt("cost_per_unit"),
                new Purchase(rs.getInt("purchase_id"), 0.0, null),
                new Product(rs.getInt("product_id"), "", 0.0, "", "", null)
        );
    }

    public static boolean deleteAllItemsByPurchaseId(int purchaseId) {
        String sql = "DELETE FROM Purchase_Item WHERE purchase_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, purchaseId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                updatePurchaseTotalCost(purchaseId);
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Purchase_item getDetailedPurchaseItem(int itemId) {
        String sql = "SELECT pi.*, p.product_name, p.brand, c.category_name " +
                "FROM Purchase_Item pi " +
                "JOIN Product p ON pi.product_id = p.product_id " +
                "LEFT JOIN Category c ON p.category_id = c.category_id " +
                "WHERE pi.item_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        0.0,
                        rs.getString("brand"),
                        "",
                        new Category(0, rs.getString("category_name"), "")
                );

                return new Purchase_item(
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        rs.getInt("cost_per_unit"),
                        new Purchase(rs.getInt("purchase_id"), 0.0, null),
                        product
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ObservableList<ProductQuantity> getProductQuantities() {
        ObservableList<ProductQuantity> quantities = FXCollections.observableArrayList();
        String sql = "SELECT product_id, SUM(quantity) as total_quantity " +
                "FROM Purchase_Item GROUP BY product_id ORDER BY total_quantity DESC";

        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ProductQuantity pq = new ProductQuantity(
                        rs.getInt("product_id"),
                        rs.getInt("total_quantity")
                );
                quantities.add(pq);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quantities;
    }

    public static double getAveragePurchasePrice(int productId) {
        String sql = "SELECT AVG(cost_per_unit) as avg_price FROM Purchase_Item WHERE product_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("avg_price");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static boolean checkStockAvailability(int productId, int requestedQuantity) {
        String sql = "SELECT stock_quantity FROM Product WHERE product_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int stockQuantity = rs.getInt("stock_quantity");
                return stockQuantity >= requestedQuantity;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean updateProductStock(int productId, int purchasedQuantity) {
        String sql = "UPDATE Product SET stock_quantity = stock_quantity + ? WHERE product_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, purchasedQuantity);
            pstmt.setInt(2, productId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static ObservableList<Purchase_item> getItemsWithProductDetails(int purchaseId) {
        ObservableList<Purchase_item> items = FXCollections.observableArrayList();
        String sql = "SELECT pi.*, p.product_name, p.brand, p.price as selling_price " +
                "FROM Purchase_Item pi " +
                "JOIN Product p ON pi.product_id = p.product_id " +
                "WHERE pi.purchase_id = ? ORDER BY pi.item_id";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, purchaseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getDouble("selling_price"),
                        rs.getString("brand"),
                        "",
                        null
                );

                Purchase_item item = new Purchase_item(
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        rs.getInt("cost_per_unit"),
                        new Purchase(rs.getInt("purchase_id"), 0.0, null),
                        product
                );
                items.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}
