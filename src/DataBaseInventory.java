import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataBaseInventory {

	public static ObservableList<Inventory> loadInventory() {
		ObservableList<Inventory> list = FXCollections.observableArrayList();

		String sql = """
				SELECT i.warehouse_id, i.product_id, i.quantity,
				       p.product_name, p.price, p.brand, p.product_description,
				       w.location, w.capacity, w.stock_manager
				FROM Inventory i
				LEFT JOIN Product p ON i.product_id = p.product_id
				LEFT JOIN Warehouse w ON i.warehouse_id = w.warehouse_id
				ORDER BY i.warehouse_id, i.product_id
				""";

		try (Connection conn = DataBaseConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Warehouse warehouse = new Warehouse();
				warehouse.setWarehouseId(rs.getInt("warehouse_id"));
				warehouse.setLocation(rs.getString("location"));
				warehouse.setCapacity(rs.getInt("capacity"));
				warehouse.setStockManager(rs.getString("stock_manager"));

				Product product = new Product();
				product.setProduct_id(rs.getInt("product_id"));
				product.setProduct_name(rs.getString("product_name"));
				product.setPrice(rs.getDouble("price"));
				product.setBrand(rs.getString("brand"));
				product.setProduct_description(rs.getString("product_description"));

				Inventory inventory = new Inventory(rs.getInt("quantity"), warehouse, product);
				list.add(inventory);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			AlertManager.showError("Error loading inventory: " + e.getMessage());
		}

		return list;
	}

	public static String getInventoryStatistics() {

		StringBuilder stats = new StringBuilder();

		String totalQuery = "SELECT SUM(quantity) FROM Inventory";
		String productQuery = "SELECT COUNT(DISTINCT product_id) FROM Inventory";
		String warehouseQuery = "SELECT COUNT(DISTINCT warehouse_id) FROM Inventory";
		String lowStockQuery = "SELECT COUNT(*) FROM Inventory WHERE quantity < 10";
		String outOfStockQuery = "SELECT COUNT(*) FROM Inventory WHERE quantity = 0";

		try (Connection conn = DataBaseConnection.getConnection()) {

			// total items
			try (PreparedStatement ps = conn.prepareStatement(totalQuery); ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					int total = rs.getInt(1);
					stats.append("Total Items: ").append(total);
				}
			}

			// total products
			try (PreparedStatement ps = conn.prepareStatement(productQuery); ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					int products = rs.getInt(1);
					stats.append(" | Products: ").append(products);
				}
			}

			// total warehouses
			try (PreparedStatement ps = conn.prepareStatement(warehouseQuery); ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					int warehouses = rs.getInt(1);
					stats.append(" | Warehouses: ").append(warehouses);
				}
			}

			// low stock
			try (PreparedStatement ps = conn.prepareStatement(lowStockQuery); ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					int lowStock = rs.getInt(1);
					stats.append(" | Low Stock: ").append(lowStock);
				}
			}

			// out of stock
			try (PreparedStatement ps = conn.prepareStatement(outOfStockQuery); ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					int outOfStock = rs.getInt(1);
					stats.append(" | Out of Stock : ").append(outOfStock);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			stats.append("Error loading statistics");
		}

		return stats.toString();
	}

	public static ObservableList<Inventory> searchByProductId(int productId) {

		ObservableList<Inventory> list = FXCollections.observableArrayList();

		String sql = """
				SELECT i.warehouse_id, i.product_id, i.quantity,
				       p.product_name, p.price, p.brand, p.product_description,
				       w.location, w.capacity, w.stock_manager
				FROM Inventory i
				LEFT JOIN Product p ON i.product_id = p.product_id
				LEFT JOIN Warehouse w ON i.warehouse_id = w.warehouse_id
				WHERE i.product_id = ?
				ORDER BY i.warehouse_id
				""";

		try (Connection conn = DataBaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, productId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Warehouse warehouse = new Warehouse();
				warehouse.setWarehouseId(rs.getInt("warehouse_id"));
				warehouse.setLocation(rs.getString("location"));
				warehouse.setCapacity(rs.getInt("capacity"));
				warehouse.setStockManager(rs.getString("stock_manager"));

				Product product = new Product();
				product.setProduct_id(rs.getInt("product_id"));
				product.setProduct_name(rs.getString("product_name"));
				product.setPrice(rs.getDouble("price"));
				product.setBrand(rs.getString("brand"));
				product.setProduct_description(rs.getString("product_description"));

				list.add(new Inventory(rs.getInt("quantity"), warehouse, product));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			AlertManager.showError("Search Error: " + e.getMessage());
		}

		return list;
	}

	public static ObservableList<Inventory> searchByWarehouseId(int warehouseId) {

		ObservableList<Inventory> list = FXCollections.observableArrayList();

		String sql = """
				SELECT i.warehouse_id, i.product_id, i.quantity,
				       p.product_name, p.price, p.brand, p.product_description,
				       w.location, w.capacity, w.stock_manager
				FROM Inventory i
				LEFT JOIN Product p ON i.product_id = p.product_id
				LEFT JOIN Warehouse w ON i.warehouse_id = w.warehouse_id
				WHERE i.warehouse_id = ?
				ORDER BY i.product_id
				""";

		try (Connection conn = DataBaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, warehouseId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Warehouse warehouse = new Warehouse();
				warehouse.setWarehouseId(rs.getInt("warehouse_id"));
				warehouse.setLocation(rs.getString("location"));
				warehouse.setCapacity(rs.getInt("capacity"));
				warehouse.setStockManager(rs.getString("stock_manager"));

				Product product = new Product();
				product.setProduct_id(rs.getInt("product_id"));
				product.setProduct_name(rs.getString("product_name"));
				product.setPrice(rs.getDouble("price"));
				product.setBrand(rs.getString("brand"));
				product.setProduct_description(rs.getString("product_description"));

				list.add(new Inventory(rs.getInt("quantity"), warehouse, product));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			AlertManager.showError("Search Error : " + e.getMessage());
		}

		return list;
	}

	public static ObservableList<Inventory> searchByProductName(String productName) {

		ObservableList<Inventory> list = FXCollections.observableArrayList();

		String sql = """
				SELECT i.warehouse_id, i.product_id, i.quantity,
				       p.product_name, p.price, p.brand, p.product_description,
				       w.location, w.capacity, w.stock_manager
				FROM Inventory i
				LEFT JOIN Product p ON i.product_id = p.product_id
				LEFT JOIN Warehouse w ON i.warehouse_id = w.warehouse_id
				WHERE p.product_name LIKE ?
				ORDER BY p.product_name
				""";

		try (Connection conn = DataBaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, "%" + productName + "%");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Warehouse warehouse = new Warehouse();
				warehouse.setWarehouseId(rs.getInt("warehouse_id"));
				warehouse.setLocation(rs.getString("location"));
				warehouse.setCapacity(rs.getInt("capacity"));
				warehouse.setStockManager(rs.getString("stock_manager"));

				Product product = new Product();
				product.setProduct_id(rs.getInt("product_id"));
				product.setProduct_name(rs.getString("product_name"));
				product.setPrice(rs.getDouble("price"));
				product.setBrand(rs.getString("brand"));
				product.setProduct_description(rs.getString("product_description"));

				list.add(new Inventory(rs.getInt("quantity"), warehouse, product));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			AlertManager.showError("Search Error: " + e.getMessage());
		}

		return list;
	}

	public static ObservableList<Inventory> getLowStockItems(int threshold) {

		ObservableList<Inventory> list = FXCollections.observableArrayList();

		String sql = """
				SELECT i.warehouse_id, i.product_id, i.quantity,
				       p.product_name, p.price, p.brand, p.product_description,
				       w.location, w.capacity, w.stock_manager
				FROM Inventory i
				LEFT JOIN Product p ON i.product_id = p.product_id
				LEFT JOIN Warehouse w ON i.warehouse_id = w.warehouse_id
				WHERE i.quantity < ? AND i.quantity > 0
				ORDER BY i.quantity
				""";

		try (Connection conn = DataBaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, threshold);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Warehouse warehouse = new Warehouse();
				warehouse.setWarehouseId(rs.getInt("warehouse_id"));
				warehouse.setLocation(rs.getString("location"));
				warehouse.setCapacity(rs.getInt("capacity"));
				warehouse.setStockManager(rs.getString("stock_manager"));

				Product product = new Product();
				product.setProduct_id(rs.getInt("product_id"));
				product.setProduct_name(rs.getString("product_name"));
				product.setPrice(rs.getDouble("price"));
				product.setBrand(rs.getString("brand"));
				product.setProduct_description(rs.getString("product_description"));

				list.add(new Inventory(rs.getInt("quantity"), warehouse, product));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			AlertManager.showError("Database Error : " + e.getMessage());
		}

		return list;
	}

	public static ObservableList<Inventory> getOutOfStockItems() {

		ObservableList<Inventory> list = FXCollections.observableArrayList();

		String sql = """
				SELECT i.warehouse_id, i.product_id, i.quantity,
				       p.product_name, p.price, p.brand, p.product_description,
				       w.location, w.capacity, w.stock_manager
				FROM Inventory i
				LEFT JOIN Product p ON i.product_id = p.product_id
				LEFT JOIN Warehouse w ON i.warehouse_id = w.warehouse_id
				WHERE i.quantity = 0
				ORDER BY p.product_name
				""";

		try (Connection conn = DataBaseConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Warehouse warehouse = new Warehouse();
				warehouse.setWarehouseId(rs.getInt("warehouse_id"));
				warehouse.setLocation(rs.getString("location"));
				warehouse.setCapacity(rs.getInt("capacity"));
				warehouse.setStockManager(rs.getString("stock_manager"));

				Product product = new Product();
				product.setProduct_id(rs.getInt("product_id"));
				product.setProduct_name(rs.getString("product_name"));
				product.setPrice(rs.getDouble("price"));
				product.setBrand(rs.getString("brand"));
				product.setProduct_description(rs.getString("product_description"));

				list.add(new Inventory(rs.getInt("quantity"), warehouse, product));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			AlertManager.showError("Database Error : " + e.getMessage());
		}

		return list;
	}

	public static boolean deleteStock(int warehouseId, int productId) {

		String sql = "DELETE FROM Inventory WHERE warehouse_id = ? AND product_id = ?";

		try (Connection conn = DataBaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, warehouseId);
			ps.setInt(2, productId);
			int rowsAffected = ps.executeUpdate();
			return rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			AlertManager.showError("Database Error : " + e.getMessage());
			return false;
		}
	}

	public static boolean addStock(int warehouseId, int productId, int quantity) {

		String sqlCheck = "SELECT * FROM Inventory WHERE warehouse_id = ? AND product_id = ?";
		String sqlInsert = "INSERT INTO Inventory (warehouse_id, product_id, quantity) VALUES (?, ?, ?)";

		try (Connection con = DataBaseConnection.getConnection();
				PreparedStatement checkStmt = con.prepareStatement(sqlCheck)) {

			checkStmt.setInt(1, warehouseId);
			checkStmt.setInt(2, productId);
			ResultSet rs = checkStmt.executeQuery();

			if (rs.next()) {

				int currentQuantity = rs.getInt("quantity");
				return updateStock(warehouseId, productId, currentQuantity + quantity);
			}

			try (PreparedStatement insertStmt = con.prepareStatement(sqlInsert)) {
				insertStmt.setInt(1, warehouseId);
				insertStmt.setInt(2, productId);
				insertStmt.setInt(3, quantity);

				int affectedRows = insertStmt.executeUpdate();
				return affectedRows > 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			AlertManager.showError("Database Error: " + e.getMessage());
			return false;
		}
	}

	public static boolean updateStock(int warehouseId, int productId, int quantity) {
		String sqlUpdate = "UPDATE Inventory SET quantity = ? WHERE warehouse_id = ? AND product_id = ?";

		try (Connection con = DataBaseConnection.getConnection();
				PreparedStatement stmt = con.prepareStatement(sqlUpdate)) {

			stmt.setInt(1, quantity);
			stmt.setInt(2, warehouseId);
			stmt.setInt(3, productId);

			int affectedRows = stmt.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			AlertManager.showError("Database Error: " + e.getMessage());
			return false;
		}
	}

	public static ObservableList<Product> loadProducts() {
		ObservableList<Product> products = FXCollections.observableArrayList();
		String sql = "SELECT * FROM Product ORDER BY product_name";

		try (Connection conn = DataBaseConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Product product = new Product();
				product.setProduct_id(rs.getInt("product_id"));
				product.setProduct_name(rs.getString("product_name"));
				product.setPrice(rs.getDouble("price"));
				product.setBrand(rs.getString("brand"));
				product.setProduct_description(rs.getString("product_description"));
				products.add(product);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			AlertManager.showError("Error loading products : " + e.getMessage());
		}
		return products;
	}

	public static ObservableList<Warehouse> loadWarehouses() {

		ObservableList<Warehouse> warehouses = FXCollections.observableArrayList();
		String sql = "SELECT warehouse_id, location, capacity, stock_manager FROM Warehouse ORDER BY location";

		try (Connection conn = DataBaseConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Warehouse warehouse = new Warehouse();
				warehouse.setWarehouseId(rs.getInt("warehouse_id"));
				warehouse.setLocation(rs.getString("location"));
				warehouse.setCapacity(rs.getInt("capacity"));
				warehouse.setStockManager(rs.getString("stock_manager"));
				warehouses.add(warehouse);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			AlertManager.showError("Error loading warehouses : " + e.getMessage());
		}
		return warehouses;
	}

	public static boolean productExists(int productId) {

		String sql = "SELECT COUNT(*) FROM Product WHERE product_id = ?";
		try (Connection conn = DataBaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, productId);
			ResultSet rs = ps.executeQuery();
			return rs.next() && rs.getInt(1) > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean warehouseExists(int warehouseId) {

		String sql = "SELECT COUNT(*) FROM Warehouse WHERE warehouse_id = ?";
		try (Connection conn = DataBaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, warehouseId);
			ResultSet rs = ps.executeQuery();
			return rs.next() && rs.getInt(1) > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean productInWarehouse(int warehouseId, int productId) {
		String sql = "SELECT COUNT(*) FROM Inventory WHERE warehouse_id = ? AND product_id = ?";
		try (Connection conn = DataBaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, warehouseId);
			ps.setInt(2, productId);
			ResultSet rs = ps.executeQuery();
			return rs.next() && rs.getInt(1) > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}