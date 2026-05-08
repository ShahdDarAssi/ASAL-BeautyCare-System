import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DatabaseOperationsPurchase {

	public static boolean addPurchase(Purchase purchase) {
		boolean success = false;
		String sql = "INSERT INTO Purchase (supplier_id, total_cost, purchase_date) VALUES (?, ?, ?)";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setInt(1, purchase.getSupplier().getSupplierId());
			pstmt.setDouble(2, purchase.getTotal_cost());

			if (purchase.getPurchase_date() != null) {
				pstmt.setDate(3, purchase.getPurchase_date());
			} else {
				pstmt.setDate(3, new Date(System.currentTimeMillis()));
			}

			int rowsAffected = pstmt.executeUpdate();
			success = rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return success;
	}

	public static boolean updatePurchase(Purchase purchase) {
		boolean success = false;
		String sql = "UPDATE Purchase SET supplier_id = ?, total_cost = ?, purchase_date = ? WHERE purchase_id = ?";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setInt(1, purchase.getSupplier().getSupplierId());
			pstmt.setDouble(2, purchase.getTotal_cost());

			if (purchase.getPurchase_date() != null) {
				pstmt.setDate(3, purchase.getPurchase_date());
			} else {
				pstmt.setDate(3, new Date(System.currentTimeMillis()));
			}

			pstmt.setInt(4, purchase.getPurchase_id());

			int rowsAffected = pstmt.executeUpdate();
			success = rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return success;
	}

	public static boolean deletePurchase(int purchaseId) {
		String insertArchiveSQL = "INSERT INTO purchase_archive "
				+ "(purchase_id, supplier_id, purchase_date, total_cost, archived_date) "
				+ "SELECT purchase_id, supplier_id, Purchase_date, total_cost, CURRENT_TIMESTAMP "
				+ "FROM Purchase WHERE purchase_id = ?";

		String deletePurchaseSQL = "DELETE FROM Purchase WHERE purchase_id = ?";

		try (Connection con = DataBaseConnection.getConnection()) {

			con.setAutoCommit(false);

			try (PreparedStatement pstmtArchive = con.prepareStatement(insertArchiveSQL)) {
				pstmtArchive.setInt(1, purchaseId);
				int inserted = pstmtArchive.executeUpdate();
				if (inserted == 0) {
					con.rollback();
					return false;
				}
			}

			try (PreparedStatement pstmtDelete = con.prepareStatement(deletePurchaseSQL)) {
				pstmtDelete.setInt(1, purchaseId);
				pstmtDelete.executeUpdate();
			}

			con.commit();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

//    public static int getLastInsertedPurchaseId() {
//        try {
//            ResultSet rs = DataBaseConnection.executeQuery("SELECT MAX(purchase_id) AS last_id FROM Purchase");
//            if (rs.next()) return rs.getInt("last_id");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }

	public static int getPurchaseCountBySupplier(int supplierId) {
		int count = 0;
		String sql = "SELECT COUNT(*) as count FROM Purchase WHERE supplier_id = ?";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setInt(1, supplierId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				count = rs.getInt("count");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	public static int getPurchaseCount() {
		String sql = "SELECT COUNT(*) as count FROM Purchase";

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

	public static ObservableList<Purchase> getPurchasesBySupplier(int supplierId) {
		ObservableList<Purchase> purchases = FXCollections.observableArrayList();

		String sql = "SELECT * FROM Purchase WHERE supplier_id = ?";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setInt(1, supplierId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Purchase purchase = new Purchase(rs.getInt("purchase_id"), rs.getDouble("total_cost"),
						new Supplier(supplierId, "", "", ""));
				if (rs.getDate("purchase_date") != null) {
					purchase.setPurchase_date(rs.getDate("purchase_date"));
				}
				purchases.add(purchase);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return purchases;
	}

	public static ObservableList<Purchase> getAllPurchase() {
		ObservableList<Purchase> purchases = FXCollections.observableArrayList();

		String sql = "SELECT * FROM Purchase";

		try (Connection con = DataBaseConnection.getConnection();
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				Purchase purchase = new Purchase(rs.getInt("purchase_id"), rs.getDouble("total_cost"),
						new Supplier(rs.getInt("supplier_id"), "", "", ""));
				if (rs.getDate("purchase_date") != null) {
					purchase.setPurchase_date(rs.getDate("purchase_date"));
				}
				purchases.add(purchase);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return purchases;
	}

	public static Purchase getPurchaseById(int purchaseId) {
		String sql = "SELECT * FROM Purchase WHERE purchase_id = ?";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setInt(1, purchaseId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				Purchase purchase = new Purchase(rs.getInt("purchase_id"), rs.getDouble("total_cost"),
						new Supplier(rs.getInt("supplier_id"), "", "", ""));

				if (rs.getDate("purchase_date") != null) {
					purchase.setPurchase_date(rs.getDate("purchase_date"));
				}
				return purchase;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ObservableList<Purchase> searchPurchasesByDate(String date) {
		ObservableList<Purchase> purchases = FXCollections.observableArrayList();
		String sql = "SELECT * FROM Purchase WHERE DATE(purchase_date) = ?";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, date);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Purchase purchase = new Purchase(rs.getInt("purchase_id"), rs.getDouble("total_cost"),
						new Supplier(rs.getInt("supplier_id"), "", "", ""));
				if (rs.getDate("purchase_date") != null) {
					purchase.setPurchase_date(rs.getDate("purchase_date"));
				}
				purchases.add(purchase);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return purchases;
	}

	public static double getTotalPurchaseCostBySupplier(int supplierId) {
		double total = 0.0;
		String sql = "SELECT SUM(total_cost) as total FROM Purchase WHERE supplier_id = ?";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setInt(1, supplierId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				total = rs.getDouble("total");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return total;
	}
}