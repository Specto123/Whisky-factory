package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

public class Autorization {
    @FXML
    TextField login;
    @FXML
    PasswordField password;
    @FXML
    Button autorization;
    @FXML
    void EnterDB(ActionEvent event)
    {
        if (login.getText().equals(""))
        {
            new ErrorAlert("Поле логин не может быть пустым!");
            login.requestFocus();
        }
        if (password.getText().equals(""))
        {
            new ErrorAlert("Поле пароль не может быть пустым!");
            password.requestFocus();
        }
        try
        {
            ConnectionDB.set(login.getText(),password.getText());
            ConnectionDB.DB();
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Views/mainForm.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Главная");
            stage.setScene(new Scene(root));
            stage.show();
            ((Node)(event.getSource())).getScene().getWindow().hide();
        }
        catch(SQLException|ClassNotFoundException | IOException exception)
        {
            new ErrorAlert(exception.getMessage());
        }
    }
}

