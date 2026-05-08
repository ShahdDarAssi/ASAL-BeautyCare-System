import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseOperationsOrder {

    public static boolean addOrder(Orders order) {
        String sql = "INSERT INTO Orders (customer_id, order_date, status, total_amount, discount, final_amount, payment_status, cost, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, order.getCustomerId());
            pstmt.setDate(2, (Date) order.getOrderDate());
            pstmt.setString(3, order.getStatus());
            pstmt.setDouble(4, order.getTotalAmount());
            pstmt.setDouble(5, order.getDiscount());
            pstmt.setDouble(6, order.getFinalAmount());
            pstmt.setString(7, order.getPaymentStatus());
            pstmt.setDouble(8, order.getCost());
            pstmt.setString(9, order.getNotes());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    order.setOrderId(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateOrder(Orders order) {
        String sql = "UPDATE Orders SET customer_id = ?, order_date = ?, status = ?, total_amount = ?, " +
                "discount = ?, final_amount = ?, payment_status = ?, cost = ?, notes = ? " +
                "WHERE order_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, order.getCustomerId());
            pstmt.setDate(2, (Date) order.getOrderDate());
            pstmt.setString(3, order.getStatus());
            pstmt.setDouble(4, order.getTotalAmount());
            pstmt.setDouble(5, order.getDiscount());
            pstmt.setDouble(6, order.getFinalAmount());
            pstmt.setString(7, order.getPaymentStatus());
            pstmt.setDouble(8, order.getCost());
            pstmt.setString(9, order.getNotes());
            pstmt.setInt(10, order.getOrderId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean updateOrderStatus(int orderId, String status, String paymentStatus) {
        String sql = "UPDATE Orders SET status = ?, payment_status = ?, last_updated = CURRENT_TIMESTAMP WHERE order_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setString(2, paymentStatus);
            pstmt.setInt(3, orderId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteOrder(int orderId) {
        String sql = "DELETE FROM Orders WHERE order_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ObservableList<Orders> getAllOrders() {
        ObservableList<Orders> orders = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Orders ORDER BY order_id DESC";

        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Orders order = new Orders(
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getDate("order_date"),
                        rs.getString("status"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("discount"),
                        rs.getDouble("final_amount"),
                        rs.getString("payment_status"),
                        rs.getDouble("cost"),
                        rs.getString("notes"),
                        rs.getString("created_at"),
                        rs.getString("last_updated")
                );
                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public static Orders getOrderById(int orderId) {
        String sql = "SELECT * FROM Orders WHERE order_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Orders(
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getDate("order_date"),
                        rs.getString("status"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("discount"),
                        rs.getDouble("final_amount"),
                        rs.getString("payment_status"),
                        rs.getDouble("cost"),
                        rs.getString("notes"),
                        rs.getString("created_at"),
                        rs.getString("last_updated")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ObservableList<Orders> searchOrdersByStatus(String status) {
        ObservableList<Orders> orders = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Orders WHERE status LIKE ? ORDER BY order_date DESC";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, "%" + status + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Orders order = new Orders(
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getDate("order_date"),
                        rs.getString("status"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("discount"),
                        rs.getDouble("final_amount"),
                        rs.getString("payment_status"),
                        rs.getDouble("cost"),
                        rs.getString("notes"),
                        rs.getString("created_at"),
                        rs.getString("last_updated")
                );
                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public static ObservableList<Orders> searchOrdersByPaymentStatus(String paymentStatus) {
        ObservableList<Orders> orders = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Orders WHERE payment_status LIKE ? ORDER BY order_date DESC";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, "%" + paymentStatus + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Orders order = new Orders(
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getDate("order_date"),
                        rs.getString("status"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("discount"),
                        rs.getDouble("final_amount"),
                        rs.getString("payment_status"),
                        rs.getDouble("cost"),
                        rs.getString("notes"),
                        rs.getString("created_at"),
                        rs.getString("last_updated")
                );
                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

       public static ObservableList<Orders> getOrdersByCustomerId(int customerId) {
        ObservableList<Orders> orders = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Orders WHERE customer_id = ? ORDER BY order_date DESC";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Orders order = new Orders(
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getDate("order_date"),
                        rs.getString("status"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("discount"),
                        rs.getDouble("final_amount"),
                        rs.getString("payment_status"),
                        rs.getDouble("cost"),
                        rs.getString("notes"),
                        rs.getString("created_at"),
                        rs.getString("last_updated")
                );
                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }


    public static int getOrderCount() {
        String sql = "SELECT COUNT(*) as count FROM Orders";

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

     public static int getOrderCountByStatus(String status) {
        String sql = "SELECT COUNT(*) as count FROM Orders WHERE status = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static int getOrderCountByPaymentStatus(String paymentStatus) {
        String sql = "SELECT COUNT(*) as count FROM Orders WHERE payment_status = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, paymentStatus);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static double getTotalSales() {
        String sql = "SELECT SUM(final_amount) as total_sales FROM Orders WHERE status NOT IN ('Cancelled', 'Refunded')";

        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total_sales");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static double getTotalProfit() {
        String sql = "SELECT SUM(final_amount - cost) as total_profit FROM Orders WHERE status NOT IN ('Cancelled', 'Refunded')";

        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total_profit");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }


    public static ObservableList<Orders> getOrdersByDateRange(Date startDate, Date endDate) {
        ObservableList<Orders> orders = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Orders WHERE order_date BETWEEN ? AND ? ORDER BY order_date DESC";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Orders order = new Orders(
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getDate("order_date"),
                        rs.getString("status"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("discount"),
                        rs.getDouble("final_amount"),
                        rs.getString("payment_status"),
                        rs.getDouble("cost"),
                        rs.getString("notes"),
                        rs.getString("created_at"),
                        rs.getString("last_updated")
                );
                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public static ObservableList<Order_Product> getProductsByOrderId(int orderId) {
        ObservableList<Order_Product> products = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Order_Product WHERE order_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Order_Product op = new Order_Product(
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("price_at_order")
                );
                products.add(op);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public static boolean updateOrderAmounts(int orderId) {
        String sql = "UPDATE Orders o SET total_amount = " +
                "(SELECT COALESCE(SUM(subtotal), 0) FROM Order_Product WHERE order_id = ?), " +
                "final_amount = (SELECT COALESCE(SUM(subtotal), 0) FROM Order_Product WHERE order_id = ?) - discount " +
                "WHERE order_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.setInt(2, orderId);
            pstmt.setInt(3, orderId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}