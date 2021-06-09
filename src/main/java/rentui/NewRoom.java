package rentui;

import datamodel.HouseData;
import datamodel.Person;
import datamodel.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.util.*;

public class NewRoom extends DialogController {
    @FXML
    public TextField nameField;
    @FXML
    public TextField amountField;
    @FXML
    public GridPane gridPane;
    @FXML
    public Button addOccupantButton;
    @FXML
    public GridPane gridPaneChBx;


    Room room;
    List<ChoiceBox<Person>> choiceBoxes = new ArrayList<>();

    ObservableList<Person> choiceBoxItems = FXCollections.observableArrayList();
    List<Person> people = HouseData.getInstance().getPeople();
    HashMap<Integer, Person> occupants;
//    int gridRowsOnInit;
    int numOccupants;

    public void initialize() {
        choiceBoxItems.addAll(people);
        choiceBoxItems.add(null);

    }


    public boolean validateAndProcess() {
        try {
            String name = nameField.getText();
            String roomFee = amountField.getText().replaceAll(HouseData.removeNonDigits, "");
            if(roomFee.isBlank()){
                roomFee = "0";
            }
            roomFee = HouseData.formatMoney(new BigDecimal(roomFee), HouseData.getInstance().getLocale());
            if (room == null){
                room = HouseData.getInstance().addRoom(name, roomFee);
            }else{
                room.setName(name);
                room.setRoomFee(roomFee);
            }
            List<Person> list = new ArrayList<>();
            for(ChoiceBox<Person> choiceBox : choiceBoxes){
                list.add(choiceBox.getValue());
            }
            room.assign(list);
            return true;
        }catch (NumberFormatException | NullPointerException | InputMismatchException e){
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public void setFields(Object selected) {
        if(selected!=null && selected.getClass().equals(Room.class)){
            room = (Room) selected;
            occupants = room.getOccupants();
            nameField.setText(room.getName());
            amountField.setText(String.valueOf(room.getRoomFee()));


            ChoiceBox<Person> choiceBox;
            Iterator<Person> iterator;
            iterator = occupants.values().iterator();

            while (iterator.hasNext()) {
                numOccupants += 1;
                Person person = iterator.next();
                choiceBox = new ChoiceBox<>();
                choiceBox.setItems(choiceBoxItems);
                choiceBox.setValue(person);
                choiceBoxes.add(choiceBox);
                String labelText = "Occupant " + (numOccupants) + ".";
                Label label = new Label();
                label.setText(labelText);
                gridPaneChBx.add(label, 0, numOccupants );
                gridPaneChBx.add(choiceBox, 1, numOccupants );
            }
        }
    }

    public void addOccupant(ActionEvent actionEvent) {
        numOccupants += 1;
        ChoiceBox<Person> choiceBox = new ChoiceBox<>();
        choiceBox.setItems(choiceBoxItems);
        int curRows = gridPane.getRowCount();
        Label label = new Label("Occupant " + numOccupants );
        gridPaneChBx.add(label, 0, curRows + numOccupants);
        gridPaneChBx.add(choiceBox, 1, curRows + numOccupants);
        choiceBoxes.add(choiceBox);
    }
}
