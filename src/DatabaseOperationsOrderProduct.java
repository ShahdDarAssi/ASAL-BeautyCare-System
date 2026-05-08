import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseOperationsOrderProduct {

    public static boolean addOrderProduct(Order_Product op) {
        String sql = "INSERT INTO Order_Product (order_id, product_id, quantity, price_at_order) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, op.getOrderId());
            pstmt.setInt(2, op.getProductId());
            pstmt.setInt(3, op.getQuantity());
            pstmt.setDouble(4, op.getPriceAtOrder());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateOrderProduct(Order_Product op) {
        String sql = "UPDATE Order_Product SET quantity = ?, price_at_order = ? " +
                "WHERE order_id = ? AND product_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, op.getQuantity());
            pstmt.setDouble(2, op.getPriceAtOrder());
            pstmt.setInt(3, op.getOrderId());
            pstmt.setInt(4, op.getProductId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteOrderProduct(int orderId, int productId) {
        String sql = "DELETE FROM Order_Product WHERE order_id = ? AND product_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.setInt(2, productId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ObservableList<Order_Product> getAllOrderProducts() {
        ObservableList<Order_Product> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Order_Product ORDER BY order_id";

        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order_Product op = new Order_Product(
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("price_at_order"),
                        rs.getDouble("subtotal")
                );
                list.add(op);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Order_Product getOrderProduct(int orderId, int productId) {
        String sql = "SELECT * FROM Order_Product WHERE order_id = ? AND product_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.setInt(2, productId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Order_Product(
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("price_at_order"),
                        rs.getDouble("subtotal")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ObservableList<Order_Product> searchOrderProducts(String keyword) {
        ObservableList<Order_Product> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Order_Product WHERE " +
                "CAST(order_id AS CHAR) LIKE ? OR " +
                "CAST(product_id AS CHAR) LIKE ? " +
                "ORDER BY order_id";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Order_Product op = new Order_Product(
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("price_at_order"),
                        rs.getDouble("subtotal")
                );
                list.add(op);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ObservableList<Order_Product> getOrderProductsByOrderId(int orderId) {
        ObservableList<Order_Product> products = FXCollections.observableArrayList();

        String query = "SELECT * FROM Order_Product WHERE order_id = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price_at_order");
                double subtotal = rs.getDouble("subtotal");

                products.add(new Order_Product(orderId, productId, quantity, price, subtotal));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }
    public static ObservableList<Order_Product> getOrderProductsByProductId(int productId) {
        ObservableList<Order_Product> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Order_Product WHERE product_id = ? ORDER BY order_id";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Order_Product op = new Order_Product(
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("price_at_order"),
                        rs.getDouble("subtotal")
                );
                list.add(op);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static int getTotalQuantityByProduct(int productId) {
        String sql = "SELECT SUM(quantity) as total_quantity FROM Order_Product WHERE product_id = ?";

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

    public static double getTotalSalesByProduct(int productId) {
        String sql = "SELECT SUM(quantity * price_at_order) as total_sales FROM Order_Product WHERE product_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_sales");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static int getOrderCountByProduct(int productId) {
        String sql = "SELECT COUNT(DISTINCT order_id) as order_count FROM Order_Product WHERE product_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("order_count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static double getAveragePriceByProduct(int productId) {
        String sql = "SELECT AVG(price_at_order) as avg_price FROM Order_Product WHERE product_id = ?";

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

    public static boolean updateSubtotal(int orderId, int productId) {
        String sql = "UPDATE Order_Product SET subtotal = quantity * price_at_order WHERE order_id = ? AND product_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.setInt(2, productId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static ObservableList<Order_Product> getOrderProductsByOrderIdAndProductId(int orderId, int productId) {
        ObservableList<Order_Product> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM order_product WHERE order_id = ? AND product_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, productId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Order_Product(
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("price_at_order")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static double getTotalSubtotalByOrder(int orderId) {
        String sql = "SELECT SUM(subtotal) as total_subtotal FROM Order_Product WHERE order_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_subtotal");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    public static boolean addProductToOrder(Order_Product op) {
        String sql = "INSERT INTO Order_Product (order_id, product_id, quantity, price_at_order, subtotal) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, op.getOrderId());
            pstmt.setInt(2, op.getProductId());
            pstmt.setInt(3, op.getQuantity());
            pstmt.setDouble(4, op.getPriceAtOrder());
            pstmt.setDouble(5, op.getQuantity() * op.getPriceAtOrder());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}