package datamodel;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Bill {
    SimpleStringProperty name = new SimpleStringProperty();
    SimpleStringProperty amount = new SimpleStringProperty();
    Person personPaying;

    public Bill(String name, String amount, Person personPaying) {
        setName(name);
        setAmount(amount);
        this.personPaying = personPaying;
    }

    public Bill(String name, String amount) {
        setName(name);
        setAmount(amount);
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

    public String getAmount() {
        return amount.get();
    }

    public SimpleStringProperty amountProperty() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount.set(amount);
    }

    public Person getPersonPaying() {
        return personPaying;
    }

    public void setPersonPaying(Person personPaying) {
        this.personPaying = personPaying;
    }
}
