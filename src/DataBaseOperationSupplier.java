import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.sql.*;

public class DataBaseOperationSupplier {


//add
    public static boolean addSupplier(Supplier supplier) {
        String sql = "INSERT INTO Supplier(Supplier_name, contact_info, country) " +
                "VALUES (?, ?, ?)";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, supplier.getSupplierName());
            pstmt.setString(2, supplier.getContactInfo());
            pstmt.setString(3, supplier.getCountry());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        supplier.setSupplierId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
//updtae
    public static boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE Supplier SET  Supplier_name = ?, contact_info = ?, " +
                "country = ?" +
                "WHERE supplier_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {


            pstmt.setString(1, supplier.getSupplierName());
            pstmt.setString(2, supplier.getContactInfo());
            pstmt.setString(3, supplier.getCountry());

            pstmt.setInt(4, supplier.getSupplierId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
//delete
    public static boolean deleteSupplier(int supplierIds) {
        String sql = "DELETE FROM Supplier WHERE supplier_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, supplierIds);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static ObservableList<Supplier> getAllSuppliers() {
        ObservableList<Supplier> list = FXCollections.observableArrayList();
        try {
            ResultSet rs = DataBaseConnection.executeQuery("SELECT * FROM supplier");
            while (rs.next()) {
                list.add(new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("Supplier_name"),
                        rs.getString("contact_info"),
                        rs.getString("country")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ObservableList<Supplier> getAllSupplier() {
        ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Supplier ORDER BY supplier_id";

        try (Connection con = DataBaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Supplier supplier = new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("Supplier_name"),
                        rs.getString("contact_info"),
                        rs.getString("country")
                );
                suppliers.add(supplier);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    public static Supplier getSupplierById(int supplierId) {
        String sql = "SELECT * FROM Supplier WHERE supplier_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, supplierId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("Supplier_name"),
                        rs.getString("contact_info"),
                        rs.getString("country")

                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ObservableList<Supplier> searchSuppliers(String keyword) {
        ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Supplier WHERE " +
                "Supplier_name LIKE ? OR " +
                "country LIKE ? OR " +
                "ORDER BY Supplier_name";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Supplier supplier = new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("Supplier_name"),
                        rs.getString("contact_info"),
                        rs.getString("country")

                );
                suppliers.add(supplier);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    public static int getSupplierCount() {
        String sql = "SELECT COUNT(*) as count FROM Supplier";

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
    public static int getPurchaseCountBySupplier(int supplierId) {
        int count = 0;

        String query = "SELECT COUNT(*) as purchase_count FROM purchases WHERE supplier_id = ? AND is_deleted = 0";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, supplierId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("purchase_count");
            }
        } catch (Exception e) {
            System.err.println("Error getting purchase count for supplier ID " + supplierId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return count;
    }
    public static int getPurchaseCount() {
        int count = 0;

        String query = "SELECT COUNT(*) as total_count FROM purchases WHERE is_deleted = 0";

        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("total_count");
            }
        } catch (Exception e) {
            System.err.println("Error getting total purchase count: " + e.getMessage());
            e.printStackTrace();
        }

        return count;
    }


}
