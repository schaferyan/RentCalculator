package rentui;

import datamodel.Bill;
import datamodel.HouseData;
import datamodel.Person;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.util.InputMismatchException;

public class NewBill extends DialogController {
    @FXML
    public TextField nameField;
    @FXML
    public ChoiceBox<Person> personChoiceBox;
    @FXML
    public RadioButton radioPerson;
    @FXML
    public RadioButton radioCollected;
    @FXML
    public TextField amountField;

    ToggleGroup toggleGroup;
    Bill bill;

    public void initialize(){
        toggleGroup = new ToggleGroup();
        radioCollected.setToggleGroup(toggleGroup);
        radioPerson.setToggleGroup(toggleGroup);
        radioCollected.setSelected(true);
        personChoiceBox.setDisable(true);
        radioPerson.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                personChoiceBox.setDisable(toggleGroup.getSelectedToggle() != radioPerson);
            }
        });
        radioCollected.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                personChoiceBox.setDisable(toggleGroup.getSelectedToggle() != radioPerson);
            }
        });
        personChoiceBox.setItems(HouseData.getInstance().getPeople());
    }


    public boolean validateAndProcess(){

        try{
            String name = nameField.getText();
            String amount = amountField.getText().replaceAll(HouseData.removeNonDigits, "");
            if(amount.isBlank()){
                amount = "0";
            }
            amount = HouseData.formatMoney(new BigDecimal(amount), HouseData.getInstance().getLocale());
            Person person = personChoiceBox.getValue();
                if(bill == null){
                    bill = new Bill(name, amount, person);
                    HouseData.getInstance().addBill(bill);
                }else{
                    bill.setName(name);
                    bill.setAmount(amount);
                }
                if(radioPerson.isSelected()) {
                    bill.setPersonPaying(person);
//                    person.setBillsPayed(person.getBillsPayed() + amount);
//                    this is handled in Housedata class calculateTotals()
                }else{
                    if(person != null){
//                        person.setBillsPayed(person.getBillsPayed() - amount);
//                        this is handled in HouseData.calculateTotals()

                        bill.setPersonPaying(null);
                    }
                }
            return true;
        }catch (NumberFormatException | NullPointerException | InputMismatchException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setFields(Object selected) {
        if(selected != null) {
            bill = (Bill) selected;
            Person person = bill.getPersonPaying();
            String name = bill.getName();
            String amount = bill.getAmount();
            nameField.setText(name);
            amountField.setText(String.valueOf(amount));
            if (person != null) {
                radioPerson.setSelected(true);
                personChoiceBox.setDisable(false);
                personChoiceBox.setValue(person);
            } else {
                radioCollected.setSelected(true);
                personChoiceBox.setDisable(true);
            }
        }
    }


}
