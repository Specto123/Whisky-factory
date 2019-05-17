package Controllers;

import javafx.scene.control.Alert;

public class ErrorAlert
{
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    public ErrorAlert(String message)
    {
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
