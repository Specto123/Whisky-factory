package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.sql.*;
import java.util.Calendar;

public class Salary {
    @FXML
    TableColumn<Employee,Integer> ID;
    @FXML
    TableColumn<Employee,String> FIO;
    @FXML
    TableColumn<Employee,Double> Oklad;
    @FXML
    TableColumn<Employee,Double> Premiya;
    @FXML
    TableColumn<Employee,Double> Itog;
    @FXML
    TableColumn<Employee,Double> procent;
    @FXML
    TableColumn<Employee,Double> kolvo_prodaj;
    @FXML
    TableColumn<Employee,Double> kolvo_pokupok;
    @FXML
    TableColumn<Employee,Double> kolvo_proizvodstva;
    @FXML
    TableColumn<Employee,Double> Obwiy;
    @FXML
    ComboBox <ComboBoxObject> month;
    @FXML
    ComboBox <Integer> year;
    @FXML
    TableView<Employee> Table;
    @FXML
    public void initialize()
    {
        ID.setCellValueFactory(new PropertyValueFactory<>("id"));
        FIO.setCellValueFactory(new PropertyValueFactory<>("name"));
        Oklad.setCellValueFactory(new PropertyValueFactory<>("oklad"));
        Premiya.setCellValueFactory(new PropertyValueFactory<>("premiya"));
        Itog.setCellValueFactory(new PropertyValueFactory<>("itog"));
        procent.setCellValueFactory(new PropertyValueFactory<>("procent"));
        kolvo_prodaj.setCellValueFactory(new PropertyValueFactory<>("kolvo_prodaj"));
        kolvo_proizvodstva.setCellValueFactory(new PropertyValueFactory<>("kolvo_proizvodstva"));
        kolvo_pokupok.setCellValueFactory(new PropertyValueFactory<>("kolvo_pokupok"));
        Obwiy.setCellValueFactory(new PropertyValueFactory<>("obwiy"));
        month.getItems().addAll(new ComboBoxObject(1,"Январь"),
                new ComboBoxObject(2,"Февраль"),
                new ComboBoxObject(3,"Март"),
                new ComboBoxObject(4,"Апрель"),
                new ComboBoxObject(5,"Май"),
                new ComboBoxObject(6,"Июнь"),
                new ComboBoxObject(7,"Июль"),
                new ComboBoxObject(8,"Август"),
                new ComboBoxObject(9,"Сентябрь"),
                new ComboBoxObject(10,"Октябрь"),
                new ComboBoxObject(11,"Ноябрь"),
                new ComboBoxObject(12,"Декабрь"));
        int y = Calendar.getInstance().get(Calendar.YEAR);
        year.getItems().addAll(y-2,y-1,y,y+1,y+2);
        month.setConverter(new StringConverter<ComboBoxObject>() {
            @Override
            public String toString(ComboBoxObject object) {
                return object.getName();
            }

            @Override
            public ComboBoxObject fromString(String string) {
                return month.getItems().stream().filter(ap->ap.getName().equals(string)).findFirst().orElse(null);
            }
        });
    }
    public void afterUpdate()
    {
        if(month.getValue()==null)
        {
            new ErrorAlert("Выберите месяц");
            month.requestFocus();
            return;
        }
        if(year.getValue()==null)
        {
            new ErrorAlert("Выберите год");
            year.requestFocus();
            return;
        }
        String sqlQuery= "Select ID from Сотрудники";
        try {
            Connection connection = ConnectionDB.DB();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            ObservableList<Integer> employees = FXCollections.observableArrayList();
            while (resultSet.next()) {
                int a=resultSet.getInt("ID");
                employees.add(a);
            }
            ObservableList<Employee> list=FXCollections.observableArrayList();
            for(int employee:employees)
            {
                sqlQuery="{call Salary(?,?,?)}";
                CallableStatement callableStatement = connection.prepareCall(sqlQuery);
                callableStatement.setDouble(1,employee);
                callableStatement.setInt(2,month.getValue().getID());
                callableStatement.setInt(3,year.getValue());
                callableStatement.execute();
                resultSet=callableStatement.getResultSet();
                if (resultSet.next())
                {
                    double oklad1= resultSet.getDouble("Оклад");
                    double procent1=resultSet.getDouble("Процент_с_продаж");
                    double countSale = resultSet.getDouble("Количество_продаж");
                    double countBuy = resultSet.getDouble("Количество_покупок");
                    double countProduction = resultSet.getDouble("Количество_производства");
                    double total = countSale+countBuy+countProduction;
                    double premiya1= oklad1*procent1/100*total;
                    double salary=premiya1+oklad1;
                    list.add(
                    new Employee(resultSet.getInt("ID"),
                            resultSet.getString("Наименование"),
                            oklad1,
                            premiya1,
                            salary,
                            procent1,
                            countSale,
                            countBuy,
                            countProduction,
                            total
                            )
                    );
                }
            }
            Table.setItems(list);
        }
        catch(SQLException | ClassNotFoundException exception)
        {
            new ErrorAlert(exception.getMessage());
            exception.printStackTrace();
        }
    }
    public void Salary()
    {
        if(month.getValue()==null)
        {
            new ErrorAlert("Выберите месяц");
            month.requestFocus();
            return;
        }
        if(year.getValue()==null)
        {
            new ErrorAlert("Выберите год");
            year.requestFocus();
            return;
        }
        if(Table.getSelectionModel().getSelectedItem()==null)
        {
            new ErrorAlert("Выберите сотрудника");
            return;
        }
        try
        {
            Connection connection= ConnectionDB.DB();
            String sqlQuery="{call NASDAQ(?,?,?,?,?,?)}";
            CallableStatement callableStatement = connection.prepareCall(sqlQuery);
            callableStatement.setDouble(1,Table.getSelectionModel().getSelectedItem().getId());
            callableStatement.setDouble(2, Table.getSelectionModel().getSelectedItem().getOklad());
            callableStatement.setDouble(3,Table.getSelectionModel().getSelectedItem().getPremiya());
            callableStatement.setDouble(4,Table.getSelectionModel().getSelectedItem().getItog());
            callableStatement.setInt(5,month.getValue().getID());
            callableStatement.setInt(6,year.getValue());
            callableStatement.execute();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Операция");
            alert.setHeaderText(null);
            alert.setContentText("Операция успешна проведена");
            alert.showAndWait();
        }
        catch(SQLException | ClassNotFoundException exception)
        {
            new ErrorAlert(exception.getMessage());
            exception.printStackTrace();
        }

    }
}

//TODO написать передачу в конструктор, написать на кнопку выдачу зарплату, написать запрос insert МАУНС