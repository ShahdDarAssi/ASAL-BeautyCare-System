
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class AlertManager {

	// Method to show a confirmation message
	public static boolean showConfirmation(String message) {
		// Create an alert of type CONFORMATION
		Alert alert = new Alert(AlertType.CONFIRMATION);
		// Set the title of the alert
		alert.setTitle(" Confirm the process  ");
		// No header text for the alert
		alert.setHeaderText(null);
		// Set the content of the message
		alert.setContentText(message);
		// Show the alert and wait for the user's choice (OK or CANCEL)
		ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
		// Return true if the user clicks OK, otherwise return false
		return result == ButtonType.OK;

	}

	// Method to show an error message to the user
	public static void showError(String message) {
		// Create an alert of type ERROR
		Alert alert = new Alert(AlertType.ERROR);
		// Set the title of the alert
		alert.setTitle("Error!!");
		// No header text for the alert
		alert.setHeaderText(null);
		// Set the content of the message
		alert.setContentText(message);
	   alert.showAndWait();

	}	

	// Method to show an informational message to the user
	public static void showInformationMessage(String message) {
		// Create an alert of type INFORMATION
		Alert alert = new Alert(AlertType.INFORMATION);
		// Set the title of the alert
		alert.setTitle(" Advertisement ");
		// No header text for the alert
		alert.setHeaderText(null);
		// Set the content of the message
		alert.setContentText(message);
		alert.showAndWait();

	}

}
