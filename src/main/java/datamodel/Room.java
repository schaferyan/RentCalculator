package datamodel;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Room {

    SimpleStringProperty name = new SimpleStringProperty();
    SimpleStringProperty roomFee = new SimpleStringProperty();

    int id;

    HashMap<Integer, Person> occupants = new HashMap<>();

    public Room(String name, String roomFee, int id) {
        setName(name);
        setRoomFee(roomFee);
        this.id = id;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getId() {
        return id;
    }

    public String getRoomFee() {
        return roomFee.get();
    }

    public SimpleStringProperty roomFeeProperty() {
        return roomFee;
    }

    public void setRoomFee(String roomFee) {
        this.roomFee.set(roomFee);
    }

    @Override
    public String toString() {
        return getName();
    }

    public void assign(Person person){
        if (person != null) {
            occupants.put(person.getId(), person);
            Room oldRoom = HouseData.getInstance().getRoomById(person.getRoom());
            person.setRoom(this.getId());
            updateRoomFees();
            if(oldRoom != null && oldRoom != this) {
                oldRoom.removePerson(person);
            }
        }
    }

    public void assign(Collection<Person> people){
//        need to compare list passed as parameter to occupants.values and remove room from Person object for any
//        not included in the people list passed as parameter
        for(Person occupant: occupants.values()){
            if (!people.contains(occupant)){
                occupant.setRoom(0);
                occupant.setRoomFee("0");
            }
        }
        occupants.clear();
        for(Person person : people){
            if(person!=null){
                assign(person);
            }
        }
    }

    public HashMap<Integer, Person> getOccupants() {
        return occupants;
    }

    public void updateRoomFees(){
        for(Person person : occupants.values()){
            person.setRoomFee(HouseData.formatMoney(new BigDecimal(getRoomFee().replaceAll("[^\\d.]", ""))
                    .divide( new BigDecimal(occupants.size()), RoundingMode.HALF_UP)
                    , HouseData.getInstance().getLocale()));
        }
    }

    public void removePerson(Person person) {
//        when using this method you must also change the person's room field, accept when this is called from the
//        assign() method
        occupants.remove(person.getId());
        updateRoomFees();
    }
}
