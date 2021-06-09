package datamodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.math.BigDecimal;

public class Person implements Serializable {

    private long serialVersionVID = 1L;
    private SimpleStringProperty name = new SimpleStringProperty("");
    private int roomId = 0;
    private int id = 0;
    
    private double utilityIndex =1;
    private double csIndex =1;

    private SimpleStringProperty roomFee = new SimpleStringProperty("0");
    private SimpleStringProperty essentialsFee = new SimpleStringProperty("0");
    private SimpleStringProperty utilityFee = new SimpleStringProperty("0");
    private SimpleStringProperty csFee = new SimpleStringProperty("0");

    private SimpleStringProperty billsPayed = new SimpleStringProperty("0");
    private SimpleStringProperty essentialsBought = new SimpleStringProperty("0");
    private SimpleStringProperty totalOwed = new SimpleStringProperty("0");

    public Person(String name, int id) {
        this.setName(name);
        this.id = id;
//        this.setRoomFee(0);
//        this.setRoomFee(0);
//        this.setCsFee(0);
//        this.setEssentialsFee(0);
//        this.setUtilityFee(0);
//        this.setBillsPayed(0);
//        this.setEssentialsBought(0);
    }

    public Person(String name, int id, int roomId) {
        this.setName(name);
        this.id = id;
        this.roomId = roomId;
        ;
    }

    public Person(String name){
        this.setName(name);
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

    
    public SimpleStringProperty roomFeeProperty() {
        return roomFee;
    }

    public String getEssentialsFee() {
        return essentialsFee.get();
    }

    public SimpleStringProperty essentialsFeeProperty() {
        return essentialsFee;
    }

    public void setEssentialsFee(String essentialsFee) {
        this.essentialsFee.set(essentialsFee);
    }

    public String getUtilityFee() {
        return utilityFee.get();
    }

    public SimpleStringProperty utilityFeeProperty() {
        return utilityFee;
    }

    public void setUtilityFee(String utilityFee) {
        this.utilityFee.set(utilityFee);
    }

    public String getCsFee() {
        return csFee.get();
    }

    public SimpleStringProperty csFeeProperty() {
        return csFee;
    }

    public void setCsFee(String csFee) {
        this.csFee.set(csFee);
    }

    public String getBillsPayed() {
        return billsPayed.get();
    }

    public SimpleStringProperty billsPayedProperty() {
        return billsPayed;
    }

    public void setBillsPayed(String billsPayed) {
        this.billsPayed.set(billsPayed);
    }

    public void addToBillsPayed(BigDecimal amount){
        setBillsPayed(HouseData.formatMoney(new BigDecimal(getBillsPayed().replaceAll(HouseData.removeNonDigits, ""))
                .add( amount)
                , HouseData.getInstance().getLocale())
        );
    }

    public String getEssentialsBought() {
        return essentialsBought.get();
    }

    public SimpleStringProperty essentialsBoughtProperty() {
        return essentialsBought;
    }

    public void setEssentialsBought(String essentialsBought) {
        this.essentialsBought.set(essentialsBought);
    }

    public String getTotalOwed() {
        return totalOwed.get();
    }

    public SimpleStringProperty totalOwedProperty() {
        return totalOwed;
    }

    public void setTotalOwed(String totalOwed) {
        this.totalOwed.set(totalOwed);
    }



    public String getRoomFee() {
        return roomFee.get();
    }

    public void setRoomFee(String roomFee) {
        this.roomFee.set(roomFee);
    }

    @Override
    public String toString() {
        return getName();
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public double getUtilityIndex() {
        return utilityIndex;
    }

    public void setUtilityIndex(double utillityIndex) {
        this.utilityIndex = utillityIndex;
    }

    public double getCsIndex() {
        return csIndex;
    }

    public void setCsIndex(double csIndex) {
        this.csIndex = csIndex;
    }

    public int getRoom() {
        return roomId;
    }

    public void setRoom(int roomId) {
        this.roomId = roomId;
    }
}
