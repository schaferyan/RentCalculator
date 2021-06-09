package rentui;

import datamodel.Bill;
import datamodel.HouseData;
import datamodel.Person;
import datamodel.Room;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.Optional;

public class EditHouseController extends DialogController {
    @FXML
    public TextField electricField;
    @FXML
    public TextField waterField;
    @FXML
    public TextField garbageField;
    @FXML
    public DialogPane editHouseDialogPane;
    @FXML
    public TableView<Room> roomsTable;
    @FXML
    private TextField rentField;
    @FXML
    private TextField w2wField;
    @FXML
    private TableView<Bill> billsTable;

    private HouseData houseData = HouseData.getInstance();

    public void initialize(){
        rentField.setText(String.valueOf(houseData.getRentDue()));
        w2wField.setText(String.valueOf(houseData.getWeek2weekTotal()));
    }

    @Override
    public boolean validateAndProcess() {
        try {
            String rentStr = rentField.getText().replaceAll(HouseData.removeNonDigits, "");
            BigDecimal rentDue = new BigDecimal(rentStr);
            String w2wStr = w2wField.getText().replaceAll(HouseData.removeNonDigits, "");
            BigDecimal week2week = new BigDecimal(w2wStr);
            houseData.setRentDue(rentDue);
            houseData.setWeek2weekTotal(week2week);
            return true;
        }catch(InputMismatchException | NullPointerException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setFields(Object selected) {
//        No data needs to be passed to fields in this dialog so this method can be empty
    }
}
