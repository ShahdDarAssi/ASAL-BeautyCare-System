import java.sql.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseOperations {
    
    public static boolean addProduct(Product product) {
        String sql = "INSERT INTO Product (product_id, category_id, Product_name, price, brand, Product_description, stock_status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setInt(1, product.getProduct_id());
            pstmt.setInt(2, product.getCategory().getCategoryId());
            pstmt.setString(3, product.getProduct_name());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setString(5, product.getBrand());
            pstmt.setString(6, product.getProduct_description());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
     public static boolean updateProduct(Product product) {
        String sql = "UPDATE Product SET category_id = ?, Product_name = ?, price = ?, " +
                    "brand = ?, Product_description = ?, stock_status = ? " +
                    "WHERE product_id = ?";
        
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setInt(1, product.getCategory().getCategoryId());
            pstmt.setString(2, product.getProduct_name());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setString(4, product.getBrand());
            pstmt.setString(5, product.getProduct_description());
            pstmt.setInt(6, product.getProduct_id());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteProduct(int productId) {
        String sql = "DELETE FROM Product WHERE product_id = ?";
        
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static ObservableList<Category> getAllCategories() {
        ObservableList<Category> categories = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Category ORDER BY category_name";
        
        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Category cat = new Category(
                    rs.getInt("category_id"),
                    rs.getString("Category_name"),
                    rs.getString("Category_description")
                );
                categories.add(cat);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
    
    public static int getTodaysOrders() {
        String sql = "SELECT COUNT(*) AS today_orders FROM Orders WHERE order_date = CURDATE()";
        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("today_orders");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public static int getTotalInventory() {
    	
        String sql = "SELECT SUM(quantity) AS total_inventory FROM Inventory"; 
        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int total = rs.getInt("total_inventory");
                if (rs.wasNull()) { 
                    return 0;
                }
                return total;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    
    public static int getActiveUsers() {
    	
        String sql = "SELECT COUNT(DISTINCT customer_id) AS active_users FROM Orders";
        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("active_users");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public static double getRevenueToday() {
    	
        String sql = "SELECT SUM(final_amount) AS revenue_today " +
                     "FROM Orders WHERE order_date = CURDATE() AND payment_status = 'paid'";
        
        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                double revenue = rs.getDouble("revenue_today");
                if (rs.wasNull()) {  
                    return 0.0;
                }
                return revenue;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }


    public static int getCompletedOrders() {
        String sql = "SELECT COUNT(*) AS completed_orders FROM Orders WHERE status = 'completed' OR status = 'delivered'";
        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("completed_orders");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static String getMostSoldProduct() {
     
    	String sql = "SELECT p.Product_name, SUM(op.quantity) AS total_sold " +
                     "FROM Order_Product op JOIN Product p ON p.product_id = op.product_id " +
                     "GROUP BY p.product_id ORDER BY total_sold DESC LIMIT 1";
        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getString("Product_name");
        } catch (SQLException e) { e.printStackTrace(); }
        return "N/A";
    }

    public static String getMostUsedSupplier() {
        String sql = "SELECT s.Supplier_name, COUNT(p.purchase_id) AS purchase_count " +
                     "FROM Purchase p JOIN Supplier s ON s.supplier_id = p.supplier_id " +
                     "GROUP BY s.supplier_id ORDER BY purchase_count DESC LIMIT 1";
        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getString("Supplier_name");
        } catch (SQLException e) { e.printStackTrace(); }
        return "N/A";
    }

    
}
