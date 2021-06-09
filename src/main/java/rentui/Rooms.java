package rentui;

import datamodel.HouseData;
import datamodel.Person;
import datamodel.Room;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class Rooms {

    @FXML
    public TableView<Room> roomsTable;

    @FXML
    public VBox roomsPane;

    public void initialize(){
        roomsTable.setItems(HouseData.getInstance().getRooms());

        TableColumn<Room, String> nameColumn = new TableColumn<>("Room");
        nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Room, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Room, String> c) {
                return c.getValue().nameProperty();
            }
        });

        TableColumn<Room, String> roomFeeColumn = new TableColumn<>("Fee");
        roomFeeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Room, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Room, String> c) {
                return c.getValue().roomFeeProperty();
            }
        });

        TableColumn<Room, String> occupantsColumn = new TableColumn<>("Occupants");
        occupantsColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Room, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Room, String> c) {
                StringBuilder sb = new StringBuilder("");
                if(c.getValue().getOccupants().values().size() > 0) {
                    for (Person person : c.getValue().getOccupants().values()) {
                        sb.append(person.getName()).append(", ");
                    }
                    sb.delete(sb.length() - 2, sb.length() - 1);
                }
                SimpleStringProperty occupants = new SimpleStringProperty();
                occupants.set(sb.toString());
                return occupants;
            }
        });
        roomsTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        editRoom();
                    }
                }
            }
        });
        try {
            roomsTable.getColumns().addAll(nameColumn, roomFeeColumn, occupantsColumn);
            roomsTable.getSelectionModel().select(0);
        }catch(NullPointerException e){
            e.printStackTrace();
        }

    }

    public void addRoom() {
        UIHelper.dialogHelper("/newRoom.fxml", "Add new room", null, roomsPane);
        roomsTable.refresh();
    }

    public void removeRoom() {
        HouseData.getInstance().removeRoom(roomsTable.getSelectionModel().getSelectedItem());
        roomsTable.refresh();
    }

    public void editRoom() {
        UIHelper.dialogHelper("/newRoom.fxml", "Edit room",
                roomsTable.getSelectionModel().getSelectedItem(), roomsPane);
        roomsTable.refresh();
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        switch(keyEvent.getCode()){
            case DELETE:
                removeRoom();
                break;
            case E:
                editRoom();
                break;
            default:
                break;
        }
    }
}
