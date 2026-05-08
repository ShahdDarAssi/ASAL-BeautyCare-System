import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseOperationsProduct {
    
    public static boolean addProduct(Product product) {
     
    	String sql = "INSERT INTO Product (category_id, Product_name, price, brand, Product_description) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, product.getCategory().getCategoryId());
            pstmt.setString(2, product.getProduct_name());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setString(4, product.getBrand());
            pstmt.setString(5, product.getProduct_description());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setProduct_id(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            AlertManager.showError("Database Error: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean updateProduct(Product product) {
      
    	String sql = "UPDATE Product SET category_id = ?, Product_name = ?, price = ?, " +
                    "brand = ?, Product_description = ? WHERE product_id = ?"; 
    	
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
            AlertManager.showError("Database Error : " + e.getMessage());
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
            
            if (e.getSQLState().equals("23000")) {
                AlertManager.showError("Cannot delete product! There are related records in sales or other tables");
            } else {
                AlertManager.showError("Database Error: " + e.getMessage());
            }
            return false;
        }
    }
    
    public static ObservableList<Product> getAllProducts() {
      
    	ObservableList<Product> products = FXCollections.observableArrayList();
      
        String sql = "SELECT p.*, c.category_name, c.category_description " +
                    "FROM Product p " +
                    "LEFT JOIN Category c ON p.category_id = c.category_id " +
                    "ORDER BY p.product_id";
        
        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("Product_name"),
                    rs.getDouble("price"),
                    rs.getString("brand"),
                    rs.getString("Product_description"),
                    new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("category_description")
                    )
                );
                products.add(product);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            AlertManager.showError("Error loading products : " + e.getMessage());
        }
        return products;
    }
    
    public static Product getProductById(int productId) {
      
    	String sql = "SELECT p.*, c.category_name, c.category_description " +
                    "FROM Product p " +
                    "LEFT JOIN Category c ON p.category_id = c.category_id " +
                    "WHERE p.product_id = ?";
        
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Product(
                    rs.getInt("product_id"),
                    rs.getString("Product_name"),
                    rs.getDouble("price"),
                    rs.getString("brand"),
                    rs.getString("Product_description"),
                    new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("category_description")
                    )
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static ObservableList<Product> getProductsByName(String name) {
       
    	ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT p.*, c.category_name, c.category_description " +
                    "FROM Product p " +
                    "LEFT JOIN Category c ON p.category_id = c.category_id " +
                    "WHERE p.Product_name LIKE ? " +
                    "ORDER BY p.Product_name";
        
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("Product_name"),
                    rs.getDouble("price"),
                    rs.getString("brand"),
                    rs.getString("Product_description"),
                    new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("category_description")
                    )
                );
                products.add(product);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
     public static ObservableList<Product> getProductsByBrand(String brandName) {
       
    	 ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT p.*, c.category_name, c.category_description " +
                    "FROM Product p " +
                    "LEFT JOIN Category c ON p.category_id = c.category_id " +
                    "WHERE p.brand LIKE ? " +
                    "ORDER BY p.brand";
        
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + brandName + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("Product_name"),
                    rs.getDouble("price"),
                    rs.getString("brand"),
                    rs.getString("Product_description"),
                    new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("category_description")
                    )
                );
                products.add(product);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
   
     public static ObservableList<Product> getProductsByCategory(String categorySearch) {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT p.*, c.category_name, c.category_description " +
                    "FROM Product p " +
                    "LEFT JOIN Category c ON p.category_id = c.category_id " +
                    "WHERE c.category_name LIKE ? OR " +
                    "      c.category_id = ? OR " +
                    "      p.category_id = ? " +
                    "ORDER BY p.Product_name";
        
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + categorySearch + "%");
            
           try {
                int categoryId = Integer.parseInt(categorySearch);
                pstmt.setInt(2, categoryId);
                pstmt.setInt(3, categoryId);
            } catch (NumberFormatException e) {
                pstmt.setInt(2, -1); 
                pstmt.setInt(3, -1);
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("Product_name"),
                    rs.getDouble("price"),
                    rs.getString("brand"),
                    rs.getString("Product_description"),
                    new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("category_description")
                    )
                );
                products.add(product);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    public static int getProductCount() {
      
    	String sql = "SELECT COUNT(*) as count FROM Product";
        
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
    
    public static ObservableList<Product> searchProducts(String keyword) {
      
    	ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT p.*, c.category_name, c.category_description " +
                    "FROM Product p " +
                    "LEFT JOIN Category c ON p.category_id = c.category_id " +
                    "WHERE p.Product_name LIKE ? OR " +
                    "      p.brand LIKE ? OR " +
                    "      p.Product_description LIKE ? OR " +
                    "      c.category_name LIKE ? " +
                    "ORDER BY p.Product_name";
        
        try (Connection con = DataBaseConnection.getConnection();
           
        		PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("Product_name"),
                    rs.getDouble("price"),
                    rs.getString("brand"),
                    rs.getString("Product_description"),
                    new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("category_description")
                    )
                );
                products.add(product);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
      

}