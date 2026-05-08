import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseOperationsReturns {
//add
	public static boolean addReturn(Returns returns) {
		String sql = "INSERT INTO Returns (order_id, return_date, reason, status, refund_amount) "
				+ "VALUES (?, ?, ?, ?, ?)";

		try (Connection con = DataBaseConnection.getConnection();
				PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setInt(1, returns.getOrderId());
			pstmt.setDate(2, returns.getReturnDate());
			pstmt.setString(3, returns.getReason());
			pstmt.setString(4, returns.getStatus());
			pstmt.setDouble(5, returns.getRefundAmount());

			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet generatedKeys = pstmt.getGeneratedKeys();
				if (generatedKeys.next()) {
					returns.setReturnId(generatedKeys.getInt(1));
				}
				return true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

//add to reyurn product
	public static boolean addReturnProduct(int returnId, int productId, int quantity, double refundPerUnit) {
		String sql = "INSERT INTO Return_Product (return_id, product_id, quantity, refund_per_unit) "
				+ "VALUES (?, ?, ?, ?)";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setInt(1, returnId);
			pstmt.setInt(2, productId);
			pstmt.setInt(3, quantity);
			pstmt.setDouble(4, refundPerUnit);

			return pstmt.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static ObservableList<Returns> getAllReturns() {
		ObservableList<Returns> returnsList = FXCollections.observableArrayList();
		String sql = "SELECT * FROM Returns ORDER BY return_date DESC";

		try (Connection con = DataBaseConnection.getConnection();
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				Returns returns = new Returns(rs.getInt("return_id"), rs.getInt("order_id"), rs.getDate("return_date"),
						rs.getString("reason"), rs.getString("status"), rs.getDouble("refund_amount"));
				returnsList.add(returns);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnsList;
	}

	public static Returns getReturnById(int returnId) {
		String sql = "SELECT * FROM Returns WHERE return_id = ?";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setInt(1, returnId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return new Returns(rs.getInt("return_id"), rs.getInt("order_id"), rs.getDate("return_date"),
						rs.getString("reason"), rs.getString("status"), rs.getDouble("refund_amount"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ObservableList<Returns> getReturnsByStatus(String status) {
		ObservableList<Returns> returnsList = FXCollections.observableArrayList();
		String sql = "SELECT * FROM Returns WHERE status = ? ORDER BY return_date DESC";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, status);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Returns returns = new Returns(rs.getInt("return_id"), rs.getInt("order_id"), rs.getDate("return_date"),
						rs.getString("reason"), rs.getString("status"), rs.getDouble("refund_amount"));
				returnsList.add(returns);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnsList;
	}

	public static ObservableList<Returns> getReturnsByOrderId(int orderId) {
		ObservableList<Returns> returnsList = FXCollections.observableArrayList();
		String sql = "SELECT * FROM Returns WHERE order_id = ? ORDER BY return_date DESC";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setInt(1, orderId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Returns returns = new Returns(rs.getInt("return_id"), rs.getInt("order_id"), rs.getDate("return_date"),
						rs.getString("reason"), rs.getString("status"), rs.getDouble("refund_amount"));
				returnsList.add(returns);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnsList;
	}

	public static boolean updateReturn(Returns returns) {
		String sql = "UPDATE Returns SET status = ?, reason = ?, refund_amount = ? WHERE return_id = ?";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, returns.getStatus());
			pstmt.setString(2, returns.getReason());
			pstmt.setDouble(3, returns.getRefundAmount());
			pstmt.setInt(4, returns.getReturnId());

			return pstmt.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean updateReturnStatus(int returnId, String newStatus) {
		String sql = "UPDATE Returns SET status = ? WHERE return_id = ?";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setString(1, newStatus);
			pstmt.setInt(2, returnId);

			return pstmt.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean processRefund(int returnId) {
		String sql = "UPDATE Returns SET status = 'processed' WHERE return_id = ? AND status = 'approved'";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setInt(1, returnId);
			return pstmt.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteReturn(int returnId) {
		String insertArchiveSQL = "INSERT INTO returns_archive "
				+ "(return_id, order_id, return_date, reason, status, refund_amount, archived_date) "
				+ "SELECT return_id, order_id, return_date, reason, status, refund_amount, CURRENT_TIMESTAMP "
				+ "FROM Returns WHERE return_id = ?";

		String deleteReturnProductsSQL = "DELETE FROM Return_Product WHERE return_id = ?";
		String deleteReturnSQL = "DELETE FROM Returns WHERE return_id = ?";

		try (Connection con = DataBaseConnection.getConnection()) {
			con.setAutoCommit(false);

			try (PreparedStatement pstmtArchive = con.prepareStatement(insertArchiveSQL)) {
				pstmtArchive.setInt(1, returnId);
				int inserted = pstmtArchive.executeUpdate();
				if (inserted == 0) {
					con.rollback();
					return false;
				}
			}

			try (PreparedStatement pstmtDeleteProducts = con.prepareStatement(deleteReturnProductsSQL)) {
				pstmtDeleteProducts.setInt(1, returnId);
				pstmtDeleteProducts.executeUpdate();
			}

			try (PreparedStatement pstmtDeleteReturn = con.prepareStatement(deleteReturnSQL)) {
				pstmtDeleteReturn.setInt(1, returnId);
				pstmtDeleteReturn.executeUpdate();
			}

			con.commit();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static ObservableList<Returns> searchReturns(String keyword) {
		ObservableList<Returns> returnsList = FXCollections.observableArrayList();
		String sql = "SELECT * FROM Returns WHERE " + "CAST(return_id AS CHAR) LIKE ? OR "
				+ "CAST(order_id AS CHAR) LIKE ? OR " + "reason LIKE ? OR " + "status LIKE ? "
				+ "ORDER BY return_date DESC";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			String pattern = "%" + keyword + "%";
			pstmt.setString(1, pattern);
			pstmt.setString(2, pattern);
			pstmt.setString(3, pattern);
			pstmt.setString(4, pattern);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Returns returns = new Returns(rs.getInt("return_id"), rs.getInt("order_id"), rs.getDate("return_date"),
						rs.getString("reason"), rs.getString("status"), rs.getDouble("refund_amount"));
				returnsList.add(returns);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnsList;
	}

	public static int getReturnCount() {
		String sql = "SELECT COUNT(*) as count FROM Returns";

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

	public static int getReturnCountByStatus(String status) {
		String sql = "SELECT COUNT(*) as count FROM Returns WHERE status = ?";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

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

	public static double getTotalRefunds() {
		String sql = "SELECT SUM(refund_amount) as total FROM Returns WHERE status = 'processed'";

		try (Connection con = DataBaseConnection.getConnection();
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			if (rs.next()) {
				return rs.getDouble("total");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	public static double calculateTotalRefundFromProducts(int returnId) {
		String sql = "SELECT SUM(quantity * refund_per_unit) as total FROM Return_Product WHERE return_id = ?";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setInt(1, returnId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getDouble("total");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	public static ObservableList<MonthlyReturnStat> getMonthlyReturnStats() {
		ObservableList<MonthlyReturnStat> stats = FXCollections.observableArrayList();
		String sql = "SELECT MONTH(return_date) as month, YEAR(return_date) as year, "
				+ "COUNT(*) as count, SUM(refund_amount) as total_refund " + "FROM Returns WHERE status = 'processed' "
				+ "GROUP BY YEAR(return_date), MONTH(return_date) " + "ORDER BY year DESC, month DESC";

		try (Connection con = DataBaseConnection.getConnection();
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				MonthlyReturnStat stat = new MonthlyReturnStat(rs.getInt("year"), rs.getInt("month"),
						rs.getInt("count"), rs.getDouble("total_refund"));
				stats.add(stat);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stats;
	}

	public static ObservableList<ProductReturnStat> getMostReturnedProducts(int limit) {
		ObservableList<ProductReturnStat> stats = FXCollections.observableArrayList();
		String sql = "SELECT p.product_id, p.product_name, " + "COUNT(rp.return_product_id) as return_count, "
				+ "SUM(rp.quantity) as total_quantity " + "FROM Return_Product rp "
				+ "JOIN Product p ON rp.product_id = p.product_id " + "GROUP BY p.product_id, p.product_name "
				+ "ORDER BY return_count DESC " + "LIMIT ?";

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setInt(1, limit);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				ProductReturnStat stat = new ProductReturnStat(rs.getInt("product_id"), rs.getString("product_name"),
						rs.getInt("return_count"), rs.getInt("total_quantity"));
				stats.add(stat);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stats;
	}

	public static ObservableList<Returns_Product> getReturnProducts(int returnId) {
		ObservableList<Returns_Product> products = FXCollections.observableArrayList();

		String sql = "SELECT rp.return_id, rp.product_id, rp.quantity, " + "p.product_name, p.price "
				+ "FROM Return_Product rp " + "JOIN Product p ON rp.product_id = p.product_id "
				+ "WHERE rp.return_id = ?";

		Returns returns = getReturnById(returnId);
		if (returns == null) {
			return products;
		}

		try (Connection con = DataBaseConnection.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {

			pstmt.setInt(1, returnId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {

				Product product = new Product(rs.getInt("product_id"), rs.getString("product_name"),
						rs.getDouble("price"), "", "", null);

				Returns_Product rp = new Returns_Product(returnId, rs.getInt("quantity"), returns, product);

				products.add(rp);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return products;
	}

}