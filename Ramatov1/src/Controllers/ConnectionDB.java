package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionDB {
    static private String login;
    static private String password;
    static public Connection DB()throws SQLException,ClassNotFoundException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String url="jdbc:sqlserver://localhost:1433;databaseName=RamatovSamost";
        Connection connection = DriverManager.getConnection(url,login,password);
        return connection;
    }
    static public void set(String _login, String _password)
    {
        login = _login;
        password = _password;
    }
    static void populateComboBox(String table, ComboBox<ComboBoxObject> comboBox)
    {
        try
        {
            Connection connection=DB();
            String sqlQuery ="SELECT ID, name FROM"+table;
            ResultSet resultSet=connection.createStatement().executeQuery(sqlQuery);
            ObservableList<ComboBoxObject> list= FXCollections.observableArrayList();
            while(resultSet.next())
            {
                ComboBoxObject object = new ComboBoxObject(resultSet.getInt("ID"),
                        resultSet.getString("name"));
                list.add(object);
            }
            comboBox.setItems(list);
            comboBox.setConverter(new StringConverter<ComboBoxObject>() {
                @Override
                public String toString(ComboBoxObject object) {
                    return object.getName();
                }

                @Override
                public ComboBoxObject fromString(String string) {
                    return comboBox.getItems().stream().filter(ap->ap.getName().equals(string)).findFirst().orElse(null);
                }
            });
            resultSet.close();
        }
        catch (SQLException | ClassNotFoundException exception)
        {
            new ErrorAlert(exception.getMessage());
        }
    }
}
