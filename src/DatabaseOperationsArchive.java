import java.sql.*;
import java.util.ArrayList;

public class DatabaseOperationsArchive {



	public static ArrayList<Staff> getArchivedStaff() {
		
		ArrayList<Staff> list = new ArrayList<>();
		String sql = "SELECT * FROM staff_archive";

		try (Connection con = DataBaseConnection.getConnection();
			 Statement st = con.createStatement();
			 ResultSet rs = st.executeQuery(sql)) {

			while (rs.next()) {
				Staff s = new Staff(
						rs.getInt("staff_id"),
						rs.getString("staff_name"),
						rs.getString("position"),
						rs.getDouble("salary"),
						rs.getDate("archived_date")

				);
				list.add(s);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static boolean restoreStaff(int staffId) {

		String insert = """
                INSERT INTO Staff (staff_id, staff_name, position, salary)
                SELECT staff_id, staff_name, position, salary
                FROM staff_archive WHERE staff_id = ?
                """;

		String delete = "DELETE FROM staff_archive WHERE staff_id = ?";

		try (Connection con = DataBaseConnection.getConnection()) {
			con.setAutoCommit(false);

			PreparedStatement ps1 = con.prepareStatement(insert);
			ps1.setInt(1, staffId);
			ps1.executeUpdate();

			PreparedStatement ps2 = con.prepareStatement(delete);
			ps2.setInt(1, staffId);
			ps2.executeUpdate();

			con.commit();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean deleteStaffPermanently(int staffId) {

		String deleteSql = "DELETE FROM staff_archive WHERE staff_id = ?";

		try (Connection con = DataBaseConnection.getConnection();
			 PreparedStatement ps = con.prepareStatement(deleteSql)) {

			ps.setInt(1, staffId);
			int rows = ps.executeUpdate();

			return rows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


	public static boolean deletePurchase(int purchaseId) {

		String selectSql = "SELECT supplier_id, purchase_date, total_cost FROM Purchase WHERE purchase_id = ?";
		String checkArchiveSql = "SELECT 1 FROM purchase_archive WHERE purchase_id = ?";
		String archiveSql = """
            INSERT INTO purchase_archive 
            (purchase_id, supplier_id, purchase_date, total_cost, archived_date)
            VALUES (?, ?, ?, ?, ?)
            """;
		String deleteItemsSql = "DELETE FROM purchase_item WHERE purchase_id = ?";
		String deletePurchaseSql = "DELETE FROM Purchase WHERE purchase_id = ?";

		try (Connection con = DataBaseConnection.getConnection()) {
			con.setAutoCommit(false);


			try (PreparedStatement checkStmt = con.prepareStatement(checkArchiveSql)) {
				checkStmt.setInt(1, purchaseId);
				ResultSet rsCheck = checkStmt.executeQuery();
				if (rsCheck.next()) {
					con.rollback();
					return false;
				}
			}

			int supplierId;
			Date purchaseDate;
			double totalCost;

					try (PreparedStatement ps = con.prepareStatement(selectSql)) {
				ps.setInt(1, purchaseId);
				ResultSet rs = ps.executeQuery();
				if (!rs.next()) {
					con.rollback();
					return false;
				}

				supplierId = rs.getInt("supplier_id");
				purchaseDate = rs.getDate("purchase_date");
				totalCost = rs.getDouble("total_cost");
			}

			try (PreparedStatement ps = con.prepareStatement(archiveSql)) {
				ps.setInt(1, purchaseId);
				ps.setInt(2, supplierId);
				ps.setDate(3, purchaseDate);
				ps.setDouble(4, totalCost);
				ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
				ps.executeUpdate();
			}

			try (PreparedStatement ps = con.prepareStatement(deleteItemsSql)) {
				ps.setInt(1, purchaseId);
				ps.executeUpdate();
			}

			try (PreparedStatement ps = con.prepareStatement(deletePurchaseSql)) {
				ps.setInt(1, purchaseId);
				ps.executeUpdate();
			}

			con.commit();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


	public static boolean restorePurchase(int purchaseId) {
		String selectArchiveSql = "SELECT supplier_id, purchase_date, total_cost FROM purchase_archive WHERE purchase_id = ?";
		String insertSql = "INSERT INTO Purchase (supplier_id, purchase_date, total_cost) VALUES (?, ?, ?)";
		String deleteArchiveSql = "DELETE FROM purchase_archive WHERE purchase_id = ?";

		try (Connection con = DataBaseConnection.getConnection()) {
			con.setAutoCommit(false);

			int supplierId;
			Timestamp purchaseTimestamp;
			double totalCost;

			try (PreparedStatement selectStmt = con.prepareStatement(selectArchiveSql)) {
				selectStmt.setInt(1, purchaseId);
				ResultSet rs = selectStmt.executeQuery();
				if (!rs.next()) {
					con.rollback();
					return false;
				}
				supplierId = rs.getInt("supplier_id");
				purchaseTimestamp = rs.getTimestamp("purchase_date");
				totalCost = rs.getDouble("total_cost");
			}


			try (PreparedStatement insertStmt = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
				insertStmt.setInt(1, supplierId);
				insertStmt.setTimestamp(2, purchaseTimestamp);
				insertStmt.setDouble(3, totalCost);
				insertStmt.executeUpdate();

				ResultSet generatedKeys = insertStmt.getGeneratedKeys();
				if (generatedKeys.next()) {
					int newId = generatedKeys.getInt(1);
					System.out.println("Restored Purchase with new ID: " + newId);
				}
			}

			try (PreparedStatement deleteStmt = con.prepareStatement(deleteArchiveSql)) {
				deleteStmt.setInt(1, purchaseId);
				deleteStmt.executeUpdate();
			}

			con.commit();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static ArrayList<Purchase> getArchivedPurchases() {
		ArrayList<Purchase> list = new ArrayList<>();
		String sql = "SELECT * FROM purchase_archive";
		try (Connection con = DataBaseConnection.getConnection();
			 Statement st = con.createStatement();
			 ResultSet rs = st.executeQuery(sql)) {

			while (rs.next()) {
				Purchase p = new Purchase(
						rs.getInt("purchase_id"),
						rs.getDouble("total_cost"),
						new Supplier(rs.getInt("supplier_id"), null, null, null)
				);
				p.setPurchase_date(rs.getDate("purchase_date"));
				p.setArchivedDate(rs.getDate("archived_date"));
				list.add(p);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static ArrayList<Returns> getArchivedReturns() {
		ArrayList<Returns> list = new ArrayList<>();
		String sql = "SELECT * FROM returns_archive";

		try (Connection con = DataBaseConnection.getConnection();
			 Statement st = con.createStatement();
			 ResultSet rs = st.executeQuery(sql)) {

			while (rs.next()) {

				Returns r = new Returns(
						rs.getInt("return_id"),
						rs.getInt("order_id"),
						rs.getDate("return_date"),
						rs.getString("reason"),
						rs.getString("status"),
						rs.getDouble("refund_amount"),
						rs.getDate("archived_date")
				);
				list.add(r);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static boolean restoreReturn(int returnId) {
		String insert = """
            INSERT INTO Returns (return_id, order_id, return_date, reason, status, refund_amount)
            SELECT return_id, order_id, return_date, reason, status, refund_amount
            FROM returns_archive
            WHERE return_id = ?
            """;

		String delete = "DELETE FROM returns_archive WHERE return_id = ?";

		try (Connection con = DataBaseConnection.getConnection()) {
			con.setAutoCommit(false);

			PreparedStatement ps1 = con.prepareStatement(insert);
			ps1.setInt(1, returnId);
			ps1.executeUpdate();

			PreparedStatement ps2 = con.prepareStatement(delete);
			ps2.setInt(1, returnId);
			ps2.executeUpdate();

			con.commit();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public static boolean deleteReturnPermanently(int returnId) {
		String insertArchive = """
            INSERT INTO returns_archive
            (return_id, order_id, return_date, reason, status, refund_amount, archived_date)
            SELECT return_id, order_id, return_date, reason, status, refund_amount, CURRENT_TIMESTAMP
            FROM Returns
            WHERE return_id = ?
        """;

		String deleteOriginal = "DELETE FROM Returns WHERE return_id = ?";

		try (Connection con = DataBaseConnection.getConnection()) {
			con.setAutoCommit(false);

			PreparedStatement ps1 = con.prepareStatement(insertArchive);
			ps1.setInt(1, returnId);
			ps1.executeUpdate();

			PreparedStatement ps2 = con.prepareStatement(deleteOriginal);
			ps2.setInt(1, returnId);
			ps2.executeUpdate();

			con.commit();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public static boolean deletePurchasePermanently(int purchaseId) {
		String sql = "DELETE FROM purchase_archive WHERE purchase_id = ?";

		try (Connection con = DataBaseConnection.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, purchaseId);
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}