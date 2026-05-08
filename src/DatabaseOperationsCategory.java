import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseOperationsCategory {

	public static ObservableList<Category> getAllCategories() {

		// list that will store all categories
		ObservableList<Category> categories = FXCollections.observableArrayList();

		// SQL query to select all categories ordered by id
		String query = "SELECT * FROM Category ORDER BY category_id";

		// open a connection
		try (Connection conn = DataBaseConnection.getConnection();

				Statement stmt = conn.createStatement();
				ResultSet resultSet = stmt.executeQuery(query)) {

			while (resultSet.next()) {
				// create a new Category object
				Category category = new Category(resultSet.getInt("category_id"), resultSet.getString("category_name"),
						resultSet.getString("category_description"));
				categories.add(category);
			}

		} catch (SQLException e) {

			AlertManager.showError("Error retrieving data from the database "+e.getMessage());
		} catch (Exception e) {

			AlertManager.showError("An unexpected error occurred "+e.getMessage());
		}

		return categories;
	}

	public static Category getCategoryById(int categoryId) {

		String query = "SELECT * FROM Category WHERE category_id = ?";

		// open a connection
		try (Connection conn = DataBaseConnection.getConnection();

			PreparedStatement preparedStatement = conn.prepareStatement(query)) {

			preparedStatement.setInt(1, categoryId);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return new Category(resultSet.getInt("category_id"), resultSet.getString("category_name"),
						resultSet.getString("category_description"));
			}

		} catch (SQLException e) {
			
			AlertManager.showError("Error fetching category by ID "+e.getMessage());
		}

		return null;
	}

	public static boolean addCategory(Category category) {

		String query = "INSERT INTO Category (category_name, category_description) VALUES (?, ?)";

		try (Connection conn = DataBaseConnection.getConnection();

			PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			// send the values from the Category object into the SQL query
			pstmt.setString(1, category.getName());
			pstmt.setString(2, category.getDescription());

			// if one or more rows were inserted
			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {

				// Get the auto generated ID from the database
				ResultSet generatedKeys = pstmt.getGeneratedKeys();
				if (generatedKeys.next()) {
					category.setCategoryId(generatedKeys.getInt(1));
				}
				return true;
			}

		} catch (SQLException e) {

			AlertManager.showError("Error adding category : " + e.getMessage());
		}

		return false;
	}

	public static boolean updateCategory(Category category) {

		String query = "UPDATE Category SET category_name = ?, category_description = ? WHERE category_id = ?";

		try (Connection conn = DataBaseConnection.getConnection();

			PreparedStatement pstmt = conn.prepareStatement(query)) {
			// send new values to the SQL query
			pstmt.setString(1, category.getName());
			pstmt.setString(2, category.getDescription());
			pstmt.setInt(3, category.getCategoryId());

			// if at least one row changed update was successful
			int affectedRows = pstmt.executeUpdate();
			return affectedRows > 0;

		} catch (SQLException e) {

			AlertManager.showError("Error updating category : " + e.getMessage());

		}

		return false;
	}

	public static boolean deleteCategory(int categoryId) {

		String query = "DELETE FROM Category WHERE category_id = ?";

		try (Connection conn = DataBaseConnection.getConnection();

				PreparedStatement pstmt = conn.prepareStatement(query)) {
			// replace the ? with the category ID
			pstmt.setInt(1, categoryId);
			int affectedRows = pstmt.executeUpdate();
			return affectedRows > 0;

		} catch (SQLException e) {

			AlertManager.showError("Error deleting category : " + e.getMessage());
		}

		return false;
	}

	public static ObservableList<Category> searchCategories(String searchWord) {

		// create an empty list to store the results
		ObservableList<Category> categories = FXCollections.observableArrayList();

		// SQL query
		String query = "SELECT * FROM Category WHERE category_name LIKE ? OR category_description LIKE ? ORDER BY category_id";

		try (Connection conn = DataBaseConnection.getConnection();

				PreparedStatement pstmt = conn.prepareStatement(query)) {

			// make the searchWord flexible so it can match anywhere in the name or
			// description
			String searchPattern = "%" + searchWord + "%";
			pstmt.setString(1, searchPattern);
			pstmt.setString(2, searchPattern);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Category category = new Category(rs.getInt("category_id"), rs.getString("category_name"),
						rs.getString("category_description"));
				categories.add(category);
			}

		} catch (SQLException e) {
			AlertManager.showError("Error searching categories : " + e.getMessage());
		}

		return categories;
	}

	public static int getCategoryCount() {

		String query = "SELECT COUNT(*) as count FROM Category";

		try (Connection conn = DataBaseConnection.getConnection();
				
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			if (rs.next()) {
				return rs.getInt("count");
			}

		} catch (SQLException e) {

			AlertManager.showError("Error getting category count : " + e.getMessage());
		}

		return 0;
	}

	public static boolean isCategoryNameExists(String categoryName, int excludeId) {

		// SQL query to check if the category name exists
		String query = "SELECT COUNT(*) as count FROM Category WHERE category_name = ? AND category_id != ?";

		try (Connection conn = DataBaseConnection.getConnection();

			PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, categoryName);
			pstmt.setInt(2, excludeId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("count") > 0;
			}

		} catch (SQLException e) {

			AlertManager.showError("Error checking category existence : " + e.getMessage());
		}

		return false;
	}

}