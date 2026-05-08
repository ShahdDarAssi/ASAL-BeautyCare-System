import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;


public class DatabaseOperationsStaff {
    //add
    public static boolean addStaff(Staff staff) {
        String sql = "INSERT INTO Staff ( Staff_name, position, salary) " +
                "VALUES ( ?, ?, ?)";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {


            pstmt.setString(1, staff.getStaffName());
            pstmt.setString(2, staff.getPosition());
            pstmt.setDouble(3, staff.getSalary());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        staff.setStaffId(generatedKeys.getInt(1));
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
//update
    public static boolean updateStaff(Staff staff) {
        String sql = "UPDATE Staff SET Staff_name = ?, position = ?, salary = ? " +
                "WHERE staff_id = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1,staff.getStaffName());
            pstmt.setString(2, staff.getPosition());
            pstmt.setDouble(3, staff.getSalary());

            pstmt.setInt(4, staff.getStaffId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
//delete the staff and add it to the archived file
    public static boolean deleteStaff(int staffId) {
        String selectSql = "SELECT * FROM Staff WHERE staff_id = ?";
        String insertArchiveSql = "INSERT INTO  staff_archive (staff_id, staff_name, position, salary, archived_date) VALUES (?, ?, ?, ?, ?)";
        String deleteSql = "DELETE FROM Staff WHERE staff_id = ?";

        try (Connection con = DataBaseConnection.getConnection()) {

            Staff staffToArchive = null;

            try (PreparedStatement selectStmt = con.prepareStatement(selectSql)) {
                selectStmt.setInt(1, staffId);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    staffToArchive = new Staff(
                            rs.getInt("staff_id"),
                            rs.getString("staff_name"),
                            rs.getString("position"),
                            rs.getDouble("salary")
                    );
                } else {
                    return false;
                }
            }

            try (PreparedStatement archiveStmt = con.prepareStatement(insertArchiveSql)) {
                archiveStmt.setInt(1, staffToArchive.getStaffId());
                archiveStmt.setString(2, staffToArchive.getStaffName());
                archiveStmt.setString(3, staffToArchive.getPosition());
                archiveStmt.setDouble(4, staffToArchive.getSalary());
                archiveStmt.setDate(5, new java.sql.Date(System.currentTimeMillis()));
                archiveStmt.executeUpdate();
            }

            try (PreparedStatement deleteStmt = con.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, staffId);
                int rowsAffected = deleteStmt.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static ObservableList<Staff> getAllStaff() {
            ObservableList<Staff> staff = FXCollections.observableArrayList();
            String sql = "SELECT * FROM Staff ORDER BY staff_id";

            try (Connection con = DataBaseConnection.getConnection();
                 Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Staff staffs = new Staff(
                            rs.getInt("staff_id"),
                            rs.getString("Staff_name"),
                            rs.getString("position"),
                            rs.getDouble("salary")
                    );
                    staff.add(staffs);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return staff;
        }

        public static Staff getStaffById(int staff_id) {
            String sql = "SELECT * FROM Staff WHERE staff_id = ?";

            try (Connection con = DataBaseConnection.getConnection();
                 PreparedStatement pstmt = con.prepareStatement(sql)) {

                pstmt.setInt(1, staff_id);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return new Staff(
                            rs.getInt("staff_id"),
                            rs.getString("Staff_name"),
                            rs.getString("position"),
                            rs.getDouble("salary")

                    );
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

    public static ObservableList<Staff> searchStaffByPosition(String keyword) {
        ObservableList<Staff> staff = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Staff WHERE position LIKE ? ORDER BY Staff_name";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Staff staffs = new Staff(
                        rs.getInt("staff_id"),
                        rs.getString("Staff_name"),
                        rs.getString("position"),
                        rs.getDouble("salary")
                );
                staff.add(staffs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staff;
    }
        public static int getStaffCount() {
            String sql = "SELECT COUNT(*) as count FROM Staff";

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
    }
