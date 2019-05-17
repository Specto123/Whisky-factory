package Controllers;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.*;


public class MainController
{
    @FXML
    ComboBox <ComboBoxObject> Product;
    @FXML
    ComboBox <ComboBoxObject> Employee;
    @FXML
    TextField Sum;
    @FXML
    TextField Count;
    @FXML
    TextField Budjet;
    @FXML
    TextField FinalCount;
    @FXML
    TextField FinalSum;
    @FXML
    Button Buy;
    @FXML
    Button Sale;
    @FXML
    TableView <RawMaterial> Ruslan;
    @FXML
    Button Make;
    @FXML
    TableColumn<RawMaterial,Integer> ColumnID;
    @FXML
    TableColumn<RawMaterial,String> ColumnName;
    @FXML
    TableColumn<RawMaterial,Double> ColumnSum;
    @FXML
    TableColumn<RawMaterial,Double> ColumnCount;
    @FXML
    TableColumn<RawMaterial,Double> ColumnCount2;
    @FXML
    public void initialize() {
        populateComboBox(Product, "Select ID,Наименование from [Готовая продукция]");
        populateComboBox(Employee, "Select ID,Наименование from [Сотрудники]");
        ColumnID.setCellValueFactory(new PropertyValueFactory<RawMaterial, Integer>("id"));
        ColumnName.setCellValueFactory(new PropertyValueFactory<RawMaterial, String>("Name"));
        ColumnSum.setCellValueFactory(new PropertyValueFactory<RawMaterial, Double>("Sum"));
        ColumnCount.setCellValueFactory(new PropertyValueFactory<RawMaterial, Double>("Count"));
        ColumnCount2.setCellValueFactory(new PropertyValueFactory<RawMaterial, Double>("Count2"));
    }
    private void populateComboBox(ComboBox<ComboBoxObject> comboBox,String sqlQuery)
    {
        try
        {
            Connection connection = ConnectionDB.DB();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            ObservableList<ComboBoxObject> list = FXCollections.observableArrayList();
            while(resultSet.next())
            {
                ComboBoxObject comboBoxObject = new ComboBoxObject(resultSet.getInt("ID"),resultSet.getString("Наименование"));
                list.add(comboBoxObject);
            }
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
            comboBox.setItems(list);
        }
        catch(SQLException | ClassNotFoundException exception)
        {
            new ErrorAlert(exception.getMessage());
        }

    }
    public void afterUpdate()
    {
        try
        {
            int product = Product.getValue().getID();
            String sqlQuery = "{call SP_InfoProduct(?)}";
            Connection connection = ConnectionDB.DB();
            CallableStatement callableStatement = connection.prepareCall(sqlQuery);
            callableStatement.setInt(1,product);
            callableStatement.execute();
            ResultSet resultSet = callableStatement.getResultSet();
            ObservableList<RawMaterial> list = FXCollections.observableArrayList();
            while (resultSet.next())
            {
                Count.setText(String.valueOf(resultSet.getInt("Прод_кол")));
                Sum.setText(String.valueOf(resultSet.getInt("Прод_сумма")));
                Budjet.setText(String.valueOf(resultSet.getInt("Сумма_Бюджета")));
                RawMaterial rawMaterial = new RawMaterial(resultSet.getInt("ID_сырье"),
                        resultSet.getString("сырье_имя"),
                        resultSet.getDouble("Сырье_сумма"),
                        resultSet.getDouble("Сырье_кол"),
                        resultSet.getDouble("Количество"));
                list.add(rawMaterial);
            }
            Ruslan.setItems(list);
        }
        catch(SQLException|ClassNotFoundException exception)
        {
            new ErrorAlert(exception.getMessage());
        }
    }
    public void buy()
    {
        if (Ruslan.getSelectionModel().getSelectedItem()==null)
        {
            new ErrorAlert("Выберите сырье которое хотите купить?");
            return;
        }
        if(FinalSum.getText().equals(""))
        {
            new ErrorAlert("Введите сумму сырья!");
            FinalSum.requestFocus();
            return;
        }
        if (FinalCount.getText().equals(""))
        {
            new ErrorAlert("Введите количество сырья!");
            FinalCount.requestFocus();
            return;
        }
        if (Employee.getValue()==null)
        {
            new ErrorAlert("Введите сотрудника!");
            Employee.requestFocus();
            return;
        }
        double budjet = Double.valueOf(Budjet.getText());
        double amount = Double.valueOf(FinalSum.getText());
        int rawMaterial = Ruslan.getSelectionModel().getSelectedItem().getId();
        double quantity = Double.valueOf(FinalCount.getText());
        int employee = Employee.getValue().getID();
        String query = "{call Zakupka(?,?,?,?)}";
        if (budjet<amount)
        {
            new ErrorAlert("Неправильная сумма!");
            FinalSum.requestFocus();
            return;
        }
        try
        {
            Connection connection = ConnectionDB.DB();
            CallableStatement callableStatement = connection.prepareCall(query);
            callableStatement.setDouble(1,quantity);
            callableStatement.setInt(2,rawMaterial);
            callableStatement.setInt(3,employee);
            callableStatement.setDouble(4,amount);
            callableStatement.execute();
            afterUpdate();
            successOperation();
        }
        catch(SQLException|ClassNotFoundException exception)
        {
            new ErrorAlert(exception.getMessage());
        }
    }
    private void zero()
    {
        FinalCount.setText("");
        FinalSum.setText("");
        Employee.setValue(null);
    }
    private void successOperation()
    {
        zero();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Операция");
        alert.setHeaderText(null);
        alert.setContentText("Операция успешна проведена");
        alert.showAndWait();
    }
    public void sale()
    {
        if (FinalCount.getText().equals(""))
        {
            new ErrorAlert("Введите количество продукта!");
            FinalCount.requestFocus();
            return;
        }
        if (FinalSum.getText().equals(""))
        {
            new ErrorAlert("Отсутствуют значения в поле суммы!");
            Employee.requestFocus();
            return;
        }
        if (Employee.getValue()==null)
        {
            new ErrorAlert("Введите сотрудника!");
            Employee.requestFocus();
            return;
        }
        double amount = Double.valueOf(FinalSum.getText());
        int product = Product.getValue().getID();
        String query="{call Sale(?,?,?,?)}";
        double quantity = Double.valueOf(FinalCount.getText());
        int employee = Employee.getValue().getID();
        double productQuantity = Double.valueOf(Count.getText());
        if (quantity>productQuantity)
        {
            new ErrorAlert("Неправильное количество!");
            FinalCount.requestFocus();
            return;
        }
        try
        {
            Connection connection = ConnectionDB.DB();
            CallableStatement callableStatement = connection.prepareCall(query);
            callableStatement.setDouble(1,quantity);
            callableStatement.setInt(2,product);
            callableStatement.setInt(3,employee);
            callableStatement.setDouble(4,amount);
            callableStatement.execute();
            afterUpdate();
            successOperation();
        }
        catch(SQLException|ClassNotFoundException exception)
        {
            new ErrorAlert(exception.getMessage());
        }

    }
    public void afterUpdateQuantity()
    {
        double percent=0;
        if (!FinalCount.getText().equals(""))
        {
            try
            {
                Connection connection = ConnectionDB.DB();
                String sqlQuery = "select ProtsProd from [Бюджет] where ID=1";
                ResultSet resultSet= connection.createStatement().executeQuery(sqlQuery);
                if(resultSet.next())
                {
                    percent=resultSet.getInt("ProtsProd");
                }
            }
            catch(SQLException|ClassNotFoundException exception)
            {
                new ErrorAlert(exception.getMessage());
            }
            double quantity = Double.valueOf(FinalCount.getText());
            double amount = Double.valueOf(Sum.getText())/Double.valueOf(Count.getText());
            double result =amount * quantity +(amount*quantity*(percent/100));
            FinalSum.setText(String.valueOf(result));
        }
    }
    public void production()
    {
        if (FinalCount.getText().equals(""))
        {
            new ErrorAlert("Введите количество продукта!");
            FinalCount.requestFocus();
            return;
        }
        if (Employee.getValue()==null)
        {
            new ErrorAlert("Введите сотрудника!");
            Employee.requestFocus();
            return;
        }
        int product = Product.getValue().getID();
        String query="{call production(?,?,?)}";
        double quantity = Double.valueOf(FinalCount.getText());
        int employee = Employee.getValue().getID();
        for(int i=0;i<Ruslan.getItems().size();i++)
        {
            double firstQuantity = Double.valueOf(Ruslan.getColumns().get(3).getCellObservableValue(i).getValue().toString());
            double secondQuantity = Double.valueOf(Ruslan.getColumns().get(4).getCellObservableValue(i).getValue().toString());
            if(firstQuantity<secondQuantity*quantity)
            {
                new ErrorAlert("Недостаточно сырья для производства товара!");
                return;
            }
        }
        try
        {
            Connection connection = ConnectionDB.DB();
            CallableStatement callableStatement = connection.prepareCall(query);
            callableStatement.setDouble(1,quantity);
            callableStatement.setInt(2,product);
            callableStatement.setInt(3,employee);
            callableStatement.execute();
            afterUpdate();
            successOperation();
        }
        catch(SQLException|ClassNotFoundException exception)
        {
            new ErrorAlert(exception.getMessage());
        }

    }
    public void Salary()
    {
        try
        {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Views/Salary.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Зарплата");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch(IOException excpetion)
        {
             new ErrorAlert(excpetion.getMessage());
        }
    }
    public void credit()
    {
        try
        {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Views/credit.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Кредит");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch(IOException excpetion)
        {
            new ErrorAlert(excpetion.getMessage());
        }
    }
}
