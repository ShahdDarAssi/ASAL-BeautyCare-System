
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class WarehouseDB {

	public Warehouse getWarehouse(int warehouseId) {

		String query = "SELECT w.warehouse_id, w.location, w.capacity, w.stock_manager, "
				+ "s.store_id, s.store_name, s.website, s.contact_email, s.contact_phone, s.manager_name "
				+ "FROM Warehouse w " + "LEFT JOIN OnlineStore s ON w.store_id = s.store_id "
				+ "WHERE w.warehouse_id = ?";

		try (Connection conn = DataBaseConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {

			ps.setInt(1, warehouseId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {

				OnlineStore store = new OnlineStore(rs.getInt("store_id"), rs.getString("store_name"),
						rs.getString("website"), rs.getString("contact_email"), rs.getString("contact_phone"),
						rs.getString("manager_name"));

				return new Warehouse(rs.getInt("warehouse_id"), store, rs.getString("location"), rs.getInt("capacity"),
						rs.getString("stock_manager"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean addWarehouse(String location, int capacity, String stockManager, String storeId) {

		String query = "INSERT INTO Warehouse (store_id, location, capacity, stock_manager) VALUES (?, ?, ?, ?)";

		try (Connection conn = DataBaseConnection.getConnection();

				PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

			int storeIdInt = Integer.parseInt(storeId.trim());

			if (!isStoreExists(storeIdInt)) {
				AlertManager.showError("Store with ID " + storeIdInt + " does not exist!");
				return false;
			}

			ps.setInt(1, storeIdInt);
			ps.setString(2, location.trim());
			ps.setInt(3, capacity);
			ps.setString(4, stockManager.trim());

			int rowsAffected = ps.executeUpdate();

			if (rowsAffected > 0) {
				try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						int generatedId = generatedKeys.getInt(1);
					}
				}
				return true;
			}

		} catch (NumberFormatException e) {
			AlertManager.showError("Invalid store ID format: " + storeId);
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			AlertManager.showError("Error adding warehouse: " + e.getMessage());
		}

		return false;
	}

	public boolean updateWarehouse(Warehouse warehouse) {

		String query = "UPDATE Warehouse SET store_id = ?, location = ?, capacity = ?, stock_manager = ? "
				+ "WHERE warehouse_id = ?";

		try (Connection conn = DataBaseConnection.getConnection();

				PreparedStatement ps = conn.prepareStatement(query)) {

			int storeId = warehouse.getOnlineStore().getStoreId();

			if (!isStoreExists(storeId)) {
				AlertManager.showError("Store with ID " + storeId + " does not exist");
				return false;
			}

			ps.setInt(1, storeId);
			ps.setString(2, warehouse.getLocation().trim());
			ps.setInt(3, warehouse.getCapacity());
			ps.setString(4, warehouse.getStockManager().trim());
			ps.setInt(5, warehouse.getWarehouseId());

			int rowsAffected = ps.executeUpdate();

			if (rowsAffected > 0) {
				AlertManager.showError("Warehouse updated successfully! ID: " + warehouse.getWarehouseId());
				return true;
			} else {
				AlertManager.showError("No warehouse found with ID: " + warehouse.getWarehouseId());
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			AlertManager.showError("Error updating warehouse: " + e.getMessage());
		}

		return false;
	}

	private boolean isStoreExists(int storeId) {
		String query = "SELECT COUNT(*) FROM OnlineStore WHERE store_id = ?";

		try (Connection conn = DataBaseConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {

			ps.setInt(1, storeId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) > 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public ArrayList<Warehouse> getAllWarehouses() {

		ArrayList<Warehouse> warehouses = new ArrayList<>();

		String query = "SELECT w.warehouse_id, w.location, w.capacity, w.stock_manager, "
				+ "s.store_id, s.store_name, s.website, s.contact_email, s.contact_phone, s.manager_name "
				+ "FROM Warehouse w " + "LEFT JOIN OnlineStore s ON w.store_id = s.store_id "
				+ "ORDER BY w.warehouse_id";

		try (Connection conn = DataBaseConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(query);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				OnlineStore store = new OnlineStore(rs.getInt("store_id"), rs.getString("store_name"),
						rs.getString("website"), rs.getString("contact_email"), rs.getString("contact_phone"),
						rs.getString("manager_name"));

				Warehouse warehouse = new Warehouse(rs.getInt("warehouse_id"), store, rs.getString("location"),
						rs.getInt("capacity"), rs.getString("stock_manager"));

				warehouses.add(warehouse);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return warehouses;
	}

	public boolean deleteWarehouse(int warehouseId) {

		if (hasInventory(warehouseId)) {

			AlertManager.showError(
					"Cannot delete warehouse with ID " + warehouseId + " because it contains inventory items");

			return false;
		}

		String query = "DELETE FROM Warehouse WHERE warehouse_id = ?";

		try (Connection conn = DataBaseConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {

			ps.setInt(1, warehouseId);
			int rowsAffected = ps.executeUpdate();

			if (rowsAffected > 0) {

				AlertManager.showError("Warehouse deleted successfully! ID: " + warehouseId);
				return true;
			} else {

				AlertManager.showError("No warehouse found with ID: " + warehouseId);
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			AlertManager.showError("Error deleting warehouse: " + e.getMessage());

		}

		return false;
	}

	private boolean hasInventory(int warehouseId) {
		String query = "SELECT COUNT(*) FROM Inventory WHERE warehouse_id = ?";

		try (Connection conn = DataBaseConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {

			ps.setInt(1, warehouseId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) > 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public ArrayList<Warehouse> searchWarehousesByLocation(String location) {
		ArrayList<Warehouse> warehouses = new ArrayList<>();
		String query = "SELECT w.warehouse_id, w.location, w.capacity, w.stock_manager, "
				+ "s.store_id, s.store_name, s.website, s.contact_email, s.contact_phone, s.manager_name "
				+ "FROM Warehouse w " + "LEFT JOIN OnlineStore s ON w.store_id = s.store_id "
				+ "WHERE w.location LIKE ? " + "ORDER BY w.warehouse_id";

		try (Connection conn = DataBaseConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {

			ps.setString(1, "%" + location.trim() + "%");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				OnlineStore store = new OnlineStore(rs.getInt("store_id"), rs.getString("store_name"),
						rs.getString("website"), rs.getString("contact_email"), rs.getString("contact_phone"),
						rs.getString("manager_name"));

				Warehouse warehouse = new Warehouse(rs.getInt("warehouse_id"), store, rs.getString("location"),
						rs.getInt("capacity"), rs.getString("stock_manager"));

				warehouses.add(warehouse);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return warehouses;
	}

	public ArrayList<OnlineStore> getAllStores() {
        ArrayList<OnlineStore> stores = new ArrayList<>();
        String query = "SELECT * FROM OnlineStore ORDER BY store_id";
        
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                OnlineStore store = new OnlineStore(
                    rs.getInt("store_id"),
                    rs.getString("store_name"),
                    rs.getString("website"),
                    rs.getString("contact_email"),
                    rs.getString("contact_phone"),
                    rs.getString("manager_name")
                );
                stores.add(store);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return stores;
    }
	
	public static int getProductCount(int warehouseId) {
	 
		String query = "SELECT COUNT(DISTINCT product_id) as product_count FROM Inventory WHERE warehouse_id = ?";
	    
	    try (Connection conn = DataBaseConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(query)) {
	        
	        ps.setInt(1, warehouseId);
	        ResultSet rs = ps.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getInt("product_count");
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return 0;
	}
	
}