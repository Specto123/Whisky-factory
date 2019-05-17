package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class creditController {
    @FXML
    ComboBox<ComboBoxObject> clientComboBox;
    @FXML
    TextField percentTextField;
    @FXML
    TextField penaltiesTextField;
    @FXML
    TextField termTextField;
    @FXML
    TextField amountTextField;
    @FXML
    TextField dateTextField;
    @FXML
    TableView<Credit> paymentsTableView;
    @FXML
    TableColumn<Credit, Integer> creditIDColumn;
    @FXML
    TableColumn<Credit, Double> paidColumn;
    @FXML
    TableColumn<Credit, Double> percentColumn;
    @FXML
    TableColumn<Credit, Double> penaltiesColumn;
    @FXML
    TableColumn<Credit, Double> totalColumn;
    @FXML
    TableColumn<Credit, Integer> expiredColumn;
    @FXML
    TableColumn<Credit, Double> restColumn;
    @FXML
    TableColumn<Credit, Date> dateColumn;
    @FXML
    TextField paidDateTextField;

    @FXML
    void initialize() {
        creditIDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        paidColumn.setCellValueFactory(new PropertyValueFactory<>("paid"));
        percentColumn.setCellValueFactory(new PropertyValueFactory<>("percent"));
        penaltiesColumn.setCellValueFactory(new PropertyValueFactory<>("penalties"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        expiredColumn.setCellValueFactory(new PropertyValueFactory<>("expired"));
        restColumn.setCellValueFactory(new PropertyValueFactory<>("rest"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        ConnectionDB.populateComboBox("[Клиенты]", clientComboBox);
    }

    @FXML
    void afterUpdate() {
        paymentsTableView.getItems().clear();
        try {
            int client = clientComboBox.getValue().getID();
            String sqlQuery = "{CALL showCredits(?)}";
            Connection connection = ConnectionDB.DB();
            CallableStatement callableStatement = connection.prepareCall(sqlQuery);
            callableStatement.setInt(1, client);
            callableStatement.execute();
            ResultSet resultSet = callableStatement.getResultSet();
            if (resultSet.next()) {
                percentTextField.setText(String.valueOf(resultSet.getInt("percent")));
                penaltiesTextField.setText(String.valueOf(resultSet.getInt("penalties")));
                termTextField.setText(String.valueOf(resultSet.getInt("term")));
                amountTextField.setText(String.valueOf(resultSet.getDouble("creditAmount")));
                dateTextField.setText(String.valueOf(resultSet.getDate("date")));
            }
            if (!percentTextField.getText().equals("")) {
                sqlQuery = "{CALL showCreditHistory(?)}";
                callableStatement = connection.prepareCall(sqlQuery);
                callableStatement.setInt(1, client);
                callableStatement.execute();
                resultSet = callableStatement.getResultSet();
                while (resultSet.next()) {
                    paymentsTableView.getItems().add(new Credit(
                            resultSet.getInt("creditID"),
                            resultSet.getDouble("paid"),
                            resultSet.getDouble("percent"),
                            resultSet.getDouble("penalties"),
                            resultSet.getDouble("total"),
                            resultSet.getInt("expired"),
                            resultSet.getDouble("rest"),
                            resultSet.getDate("date")
                    ));
                }
            }
        } catch (SQLException | ClassNotFoundException exception) {
            exception.printStackTrace();
            new ErrorAlert(exception.getMessage());
        }
    }

    @FXML
    void loan(){
        if (percentTextField.getText().equals("")){
            new ErrorAlert(  "У этого клиента уже есть активный кредит!");
            percentTextField.requestFocus();
            return;
        }
        if (clientComboBox.getValue() == null){
            new ErrorAlert(  "Выберите клиента!");
            clientComboBox.requestFocus();
            return;
        }
        if (percentTextField.getText().equals("")){
            new ErrorAlert(  "Введите процент!");
            percentTextField.requestFocus();
            return;
        }
        if (penaltiesTextField.getText().equals("")) {
            new ErrorAlert(  "Введите пения!");
            percentTextField.requestFocus();
            return;
        }
        if (termTextField.getText().equals("")){
            new ErrorAlert(  "Введите срок (в годах)!");
            termTextField.requestFocus();
            return;
        }
        if (amountTextField.getText().equals("")) {
            new ErrorAlert(  "Введите сумму кредита!");
            amountTextField.requestFocus();
            return;
        }
        try{
            int client = clientComboBox.getValue().getID();
            int percent = Integer.valueOf(percentTextField.getText());
            int penalties = Integer.valueOf(penaltiesTextField.getText());
            int term = Integer.valueOf(termTextField.getText());
            double amount = Double.valueOf(amountTextField.getText());
            String sqlQuery = "{CALL addCredit(?,?,?,?,?)}";
            Connection connection = ConnectionDB.DB();
            CallableStatement callableStatement = connection.prepareCall(sqlQuery);
            callableStatement.setInt(1, client);
            callableStatement.setDouble(2, amount);
            callableStatement.setInt(3, percent);
            callableStatement.setInt(4, penalties);
            callableStatement.setInt(5, term);
            callableStatement.execute();
            afterUpdate();
           Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Операция");
            alert.setContentText("Операция успешно завершена");
            alert.showAndWait();
        } catch (SQLException | ClassNotFoundException exception) {
            new ErrorAlert(  exception.getMessage());
        }
    }

    @FXML
    void pay() {
        if (paidDateTextField.getText().equals("")){
            new ErrorAlert(  "Введите дату оплаты!");
            paidDateTextField.requestFocus();
            return;
        }
        try{
            int client = clientComboBox.getValue().getID();
            Date date = Date.valueOf(paidDateTextField.getText());
            String sqlQuery = "{CALL addPayment(?, ?)}";
            Connection connection = ConnectionDB.DB();
            CallableStatement callableStatement = connection.prepareCall(sqlQuery);
            callableStatement.setInt(1, client);
            callableStatement.setDate(2, date);
            callableStatement.execute();
            afterUpdate();
           Alert alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Операция");
            alert.setContentText("Операция успешно завершена");
            alert.showAndWait();
        }catch (SQLException | ClassNotFoundException exception) {
            new ErrorAlert(  exception.getMessage());
        }
    }
}
