import java.sql.*;

public class DatabaseOperationsCustomer {

    public static int addOrGetCustomer(Customer customer) {

        String checkSql = "SELECT customer_id FROM customer WHERE customer_phone = ?";
        String insertSql = """
            INSERT INTO customer (customer_name, customer_phone, customer_address)
            VALUES (?, ?, ?)
        """;

        try (Connection con = DataBaseConnection.getConnection()) {

            PreparedStatement check = con.prepareStatement(checkSql);
            check.setString(1, customer.getCustomerPhone());
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                return rs.getInt("customer_id");
            }
            PreparedStatement insert = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            insert.setString(1, customer.getCustomerName());
            insert.setString(2, customer.getCustomerPhone());
            insert.setString(3, customer.getCustomerAddress());
            insert.executeUpdate();

            ResultSet keys = insert.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
