package rentui;

import datamodel.HouseData;
import datamodel.Bill;
import datamodel.Person;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class Bills {

    @FXML
    public TableView<Bill> billsTable;

    @FXML
    public VBox billsPane;

    public void initialize(){
        billsTable.setItems(HouseData.getInstance().getBills());

        TableColumn<Bill, String> nameColumn = new TableColumn<>("Bill");
        nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bill, String> c) {
                return c.getValue().nameProperty();
            }
        });

        TableColumn<Bill, String> billAmountColumn = new TableColumn<>("Cost");
        billAmountColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bill, String> c) {
                return  c.getValue().amountProperty();
            }
        });

        TableColumn<Bill, String> personColumn = new TableColumn<>("Person Paying");
        personColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bill, String> c) {
                Person person = c.getValue().getPersonPaying();
                if(person != null) {
                    return person.nameProperty();
                }else{
                    return null;
                }
            }
        });
        try {
            billsTable.getColumns().addAll(nameColumn, billAmountColumn, personColumn);
            billsTable.getSelectionModel().select(0);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        billsTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        editBill();
                    }
                }
            }
        });
    }

    public void addBill(ActionEvent actionEvent) {
        UIHelper.dialogHelper("/newBill.fxml", "Add new bill", null, billsPane);
        billsTable.refresh();
    }

    public void removeBill(ActionEvent actionEvent) {
        HouseData.getInstance().removeBill(billsTable.getSelectionModel().getSelectedItem());
        billsTable.refresh();
    }

    public void editBill() {
        UIHelper.dialogHelper("/newBill.fxml", "Edit bill", billsTable.getSelectionModel().getSelectedItem(), billsPane);
        billsTable.refresh();
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        Bill bill = billsTable.getSelectionModel().getSelectedItem();

        switch(keyEvent.getCode()){
            case DELETE:
                HouseData.getInstance().removeBill(bill);
                break;
            case E:
                editBill();
                break;
            case Z:
                System.out.println("Undo last action");
                break;
            default:
                break;
        }
    }
}
