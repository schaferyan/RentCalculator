package rentui;

import datamodel.HouseData;
import datamodel.Person;
import datamodel.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.util.InputMismatchException;

public class NewPersonController extends DialogController {
    @FXML
    public TextField essentialsBoughtField;
    @FXML
    public TextField rentPortionField;
    @FXML
    public TextField utilPortionField;
    @FXML
    public CheckBox prorateCheckbox;
    @FXML
    private TextField nameField;
    @FXML
    private ChoiceBox<Room> roomChoiceBox;

    Person person;


    public void initialize() {

        if (HouseData.getInstance().getRooms() != null) {
            ObservableList<Room> roomsList = FXCollections.observableArrayList();
            roomsList.addAll(HouseData.getInstance().getRooms());
            roomsList.add(null);
            roomChoiceBox.setItems(roomsList);
        }
    }

    @Override
    public boolean validateAndProcess() {
        String name = nameField.getText();
        Room room = roomChoiceBox.getSelectionModel().getSelectedItem();

        try {
            BigDecimal essentialsBought;

            String essStr = essentialsBoughtField.getText().replaceAll(HouseData.removeNonDigits, "");
            if(essStr.isBlank()){
                essStr = "0.00";
            }

            String rentPortStr = rentPortionField.getText().replaceAll(HouseData.removeNonDigits, "");
            if(rentPortStr.isBlank()) {
                rentPortStr = "100.00";
            }

            String utilPortStr = utilPortionField.getText().replaceAll(HouseData.removeNonDigits, "");
            if(utilPortStr.isBlank()){
                utilPortStr = "100";
            }

            if (!name.isBlank()) {
                if(person == null) {
                    person = new Person(name);
                    HouseData.getInstance().addPerson(person);
                }
                essentialsBought = new BigDecimal(essStr);
                person.setEssentialsBought(HouseData.formatMoney(essentialsBought, HouseData.getInstance().getLocale()));
                person.setCsIndex(Double.parseDouble(rentPortStr) / 100);
                person.setUtilityIndex(Double.parseDouble(utilPortStr) / 100);
                if (room != null) {
                    room.assign(person);
                } else {
                    person.setRoom(0);
                    person.setRoomFee("0");
                }
                return true;
            } else {
                return false;
            }
        }catch(NullPointerException | InputMismatchException | NumberFormatException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setFields(Object selected) {
        if(selected == null){
            rentPortionField.setText("100.00 %");
            utilPortionField.setText("100.00 %");
            return;
        }

        try {
            person = (Person) selected;
            nameField.setText(person.getName());
            roomChoiceBox.setValue(HouseData.getInstance().getRoomById(person.getRoom()));
            essentialsBoughtField.setText(String.valueOf(person.getEssentialsBought()));
            rentPortionField.setText(String.valueOf(person.getCsIndex()*100));
            utilPortionField.setText(String.valueOf(person.getUtilityIndex()*100));
        }catch (Exception e){
            System.out.println("Wrong object passed to setFields() method, or null value found");
            e.printStackTrace();
        }

    }

    public void handleProrate() {
        if(prorateCheckbox.isSelected()){
            rentPortionField.disableProperty().setValue(false);
            utilPortionField.disableProperty().setValue(false);
        }else{
            rentPortionField.disableProperty().setValue(true);
            utilPortionField.disableProperty().setValue(true);
        }
    }
}
