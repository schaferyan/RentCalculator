package datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HouseData {
    private static final HouseData instance = new HouseData();

    private BigDecimal rentDue = new BigDecimal(0);
    private BigDecimal billsStoredTotal = new BigDecimal(0);
    private BigDecimal totalDue = new BigDecimal(0);

    private BigDecimal week2weekTotal = new BigDecimal(0);
    private BigDecimal roomFeesTotal = new BigDecimal(0);

    private BigDecimal billsPayedTotal = new BigDecimal(0);
    private BigDecimal utilitiesTotal = new BigDecimal(0);
    private BigDecimal essentialsTotal = new BigDecimal(0);
    private BigDecimal communitySpaceTotal = new BigDecimal(0);

    private BigDecimal peoplePayingUtil = new BigDecimal( 0);
    private BigDecimal peoplePayingRent = new BigDecimal(0);

    private final ObservableList<Person> people = FXCollections.observableArrayList();
    private final ObservableList<Room> rooms = FXCollections.observableArrayList();
    private final ObservableList<Bill> bills = FXCollections.observableArrayList();

    private final Map<Integer, Room> roomMap = new HashMap<>();
    private final Map<Integer, Person> personMap = new HashMap<>();
    private File directory = new File("temp");
    private boolean saved = false;

    private Locale locale = Locale.US;
    
    public static String removeNonDigits = "[^\\d.]";
    
    public static HouseData getInstance() {
        return instance;
    }


    private HouseData() {
    }

    public static String formatMoney(BigDecimal amount, Locale locale){
        NumberFormat usdCostFormat = NumberFormat.getCurrencyInstance(locale);
        usdCostFormat.setMinimumFractionDigits( 2 );
        usdCostFormat.setMaximumFractionDigits( 2 );
        return usdCostFormat.format(amount.setScale(2, RoundingMode.HALF_EVEN));
    }

    public Locale getLocale() {
        return locale;
    }

    public String getRentDue() {
        return formatMoney(rentDue, locale);
    }

    public String getWeek2weekTotal() {
        return formatMoney(week2weekTotal, locale);
    }

    public void setRentDue(String rentDue) {
        this.rentDue = new BigDecimal( rentDue);
    }
    public void setRentDue(BigDecimal rentDue) {
        this.rentDue = rentDue;
    }

    public void setWeek2weekTotal(String week2weekTotal) {
        this.week2weekTotal = new BigDecimal(week2weekTotal);
    }

    public void setWeek2weekTotal(BigDecimal week2week) {
        this.week2weekTotal = week2week;
    }

    public ObservableList<datamodel.Person> getPeople() {
        return people;
    }

    public ObservableList<Room> getRooms() {
        return rooms;
    }

    public ObservableList<Bill> getBills() {
        return bills;
    }

    public Room getRoomById(int id) {
        return roomMap.get(id);
    }

    public Person getPersonById(int id) {
        return personMap.get(id);
    }

    public void reset() {
        rentDue = new BigDecimal("0");
        billsStoredTotal = new BigDecimal("0");
        totalDue = new BigDecimal("0");
        week2weekTotal = new BigDecimal("0");
        roomFeesTotal = new BigDecimal("0");
        billsPayedTotal = new BigDecimal("0");
        utilitiesTotal = new BigDecimal("0");
        essentialsTotal = new BigDecimal("0");
        communitySpaceTotal = new BigDecimal("0");
        peoplePayingUtil = new BigDecimal("0");
        peoplePayingRent = new BigDecimal("0");
        people.clear();
        rooms.clear();
        bills.clear();
        roomMap.clear();
        personMap.clear();
    }


    public Person addPerson(String name) {
        int id = personMap.size() + 1;
        return addPerson(name, id);
    }

    public Person addPerson(String name, int id) {
        Person person = new Person(name, id);
        return addPerson(person);
    }
    public Person addPerson(Person person){
        if(person.getId() == 0){
            person.setId(personMap.size() + 1);
        }
        if (!personMap.containsValue(person)) {
            people.add(person);
            personMap.put(person.getId(), person);
            updateData();
            return person;
        }
        return null;
    }

    public Room addRoom(String name, String roomFee) {
        int id = roomMap.size() + 1;
        return addRoom(name, roomFee, id);
    }

    public Room addRoom(String name, String roomFee, int id) {
        Room room = new Room(name, roomFee, id);
        roomMap.put(id, room);
        rooms.add(room);
        updateData();
        return room;
    }

    public void addBill(Bill bill) {
        bills.add(bill);
        updateData();
    }

    public void removePerson(Person person) {
        people.remove(person);
        personMap.remove(person.getId());
        int room = person.getRoom();
        if (room > 0) {
            getRoomById(person.getRoom()).removePerson(person);
        }
        updateData();
    }

    public void removePerson(int id) {
        people.remove(personMap.remove(id));
        updateData();
    }

    public void removeRoom(Room room) {
        roomMap.remove(room);
        rooms.remove(room);
        updateData();
    }

    public void removeRoom(int id) {
        rooms.remove(roomMap.remove(id));
    }


    public void removeBill(Bill bill) {
        bills.remove(bill);
        updateData();
    }

    public void setTotalDue(double totalDue) {
        this.totalDue = BigDecimal.valueOf(totalDue);
    }

    public void updateData() {
        calculateTotals();
        calculateFees();
    }

    public void calculateTotals() {

        peoplePayingRent = new BigDecimal("0");
        peoplePayingUtil = new BigDecimal("0");
        roomFeesTotal = new BigDecimal("0");
        billsPayedTotal = new BigDecimal("0");
        essentialsTotal = new BigDecimal("0");
        billsStoredTotal = new BigDecimal("0");

        for (Person person : people) {
            person.setBillsPayed("0");
        }

        for (Bill bill : bills) {
            if (bill.getPersonPaying() == null) {
                billsStoredTotal = billsStoredTotal.add(new BigDecimal(bill.getAmount().replaceAll(removeNonDigits, "")));
            } else {
                bill.getPersonPaying().addToBillsPayed(new BigDecimal(bill.getAmount().replaceAll(removeNonDigits, "")));
            }
        }

        totalDue = rentDue.add(billsStoredTotal);

        for (Person person : people) {
            peoplePayingRent = peoplePayingRent.add(BigDecimal.valueOf(person.getCsIndex()));
            peoplePayingUtil = peoplePayingUtil.add(BigDecimal.valueOf(person.getUtilityIndex()));
            roomFeesTotal = roomFeesTotal.add(new BigDecimal(person.getRoomFee().replaceAll(removeNonDigits, "")));
            billsPayedTotal = billsPayedTotal.add(new BigDecimal(person.getBillsPayed().replaceAll(removeNonDigits, "")));
            essentialsTotal = essentialsTotal.add(new BigDecimal(person.getEssentialsBought().replaceAll(removeNonDigits, "")));
        }
        utilitiesTotal = billsPayedTotal.add(billsStoredTotal);
        communitySpaceTotal = rentDue.subtract(roomFeesTotal.add(week2weekTotal));
    }

    public void calculateFees() {
        for (Person person : people) {

            person.setCsFee(formatMoney((communitySpaceTotal
                    .divide(peoplePayingRent, RoundingMode.HALF_UP)
                    .multiply( BigDecimal
                            .valueOf(person
                                    .getCsIndex()
                            )
                    )), locale));

            person.setUtilityFee(formatMoney(utilitiesTotal
                    .divide(peoplePayingUtil, RoundingMode.HALF_UP)
                    .multiply( BigDecimal
                            .valueOf(person
                                    .getUtilityIndex()
                            )
                    ), locale));

            person.setEssentialsFee(formatMoney(essentialsTotal.divide(peoplePayingUtil, RoundingMode.HALF_UP)
                    .multiply(BigDecimal
                            .valueOf(person
                                    .getUtilityIndex()
                            )
                    ), locale));

            person.setTotalOwed(formatMoney(new BigDecimal(person.getCsFee().replaceAll(removeNonDigits, ""))
                    .add(new BigDecimal(person.getRoomFee().replaceAll(removeNonDigits, "")))
                    .add(new BigDecimal(person.getUtilityFee().replaceAll(removeNonDigits, "")))
                    .add(new BigDecimal(person.getEssentialsFee().replaceAll(removeNonDigits, "")))
                    .subtract(new BigDecimal(person.getBillsPayed().replaceAll(removeNonDigits, "")))
                    .subtract(new BigDecimal(person.getEssentialsBought().replaceAll(removeNonDigits, "")))
                    , locale
                )
            );
        }
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public void save() {
//        Save data for each person to a file
        boolean overwrite = (!directory.mkdir() && !directory.getName().equals("temp"));
        try (DataOutputStream peopleFile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(directory + "/people.dat")))) {
            for (Person person : people) {
                peopleFile.writeUTF(person.getName());
                peopleFile.writeInt(person.getId());
                peopleFile.writeInt((int) person.getCsIndex() * 100);
                peopleFile.writeInt((int) person.getUtilityIndex() * 100);
                peopleFile.writeInt(person.getRoom());
                peopleFile.writeUTF(person.getEssentialsBought());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Save each room to a second file
        try (DataOutputStream roomFile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(directory + "/rooms.dat")))) {
            for (Room room : roomMap.values()) {
                roomFile.writeInt(room.getId());
                roomFile.writeUTF(room.getName());
                roomFile.writeInt((int)Double.parseDouble(room.getRoomFee().replaceAll(removeNonDigits, "")) * 100);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        save each bill to a third file
        try (DataOutputStream billFile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(directory + "/bills.dat")))) {
            billFile.writeInt(rentDue.multiply(BigDecimal
                    .valueOf(100))
                    .intValue());
            billFile.writeInt(week2weekTotal.multiply(BigDecimal
                    .valueOf(100))
                    .intValue());
            for (Bill bill : bills) {
                billFile.writeUTF(bill.getName());
                billFile.writeInt((int) Double.parseDouble(bill.getAmount().replaceAll(removeNonDigits, "")) * 100);
                Person personPaying = bill.getPersonPaying();
                if (personPaying != null) {
                    billFile.writeInt(personPaying.getId());
                } else {
                    billFile.writeInt(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
//        load rooms first as we will need this to load people
        reset();
        try (DataInputStream roomFile = new DataInputStream(new BufferedInputStream(new FileInputStream(directory + "/rooms.dat")))) {
            boolean eof = false;
            while (!eof) {
                try {
                    int roomId = roomFile.readInt();
                    String roomName = roomFile.readUTF();
                    String roomFee = formatMoney( BigDecimal.valueOf(roomFile.readInt() / 100.0), locale);
                    addRoom(roomName, roomFee, roomId);

                } catch (EOFException e) {
                    eof = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//          load data for each person into memory
        try (DataInputStream peopleFile = new DataInputStream(new BufferedInputStream(new FileInputStream(directory + "/people.dat")))) {
            boolean eof = false;
            while (!eof) {
                try {
                    String name = peopleFile.readUTF();
                    int id = peopleFile.readInt();
                    double csIndex = peopleFile.readInt() / 100.0;
                    double utilityIndex = peopleFile.readInt() / 100.0;
                    int roomId = peopleFile.readInt();
                    String essentialsBought = peopleFile.readUTF();
                    addPerson(name, id);
                    Person person = getPersonById(id);
                    try {
                        getRoomById(roomId).assign(person);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    person.setUtilityIndex(utilityIndex);
                    person.setCsIndex(csIndex);
                    person.setEssentialsBought(essentialsBought);
                } catch (EOFException e) {
                    eof = true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (DataInputStream billFile = new DataInputStream(new BufferedInputStream(new FileInputStream(directory + "/bills.dat")))) {
            boolean eof = false;
            rentDue = BigDecimal.valueOf(billFile.readInt() / 100.0);
            week2weekTotal = BigDecimal.valueOf(billFile.readInt() / 100.0);
            while (!eof) {
                try {
                    String name = billFile.readUTF();
                    double amountDouble = billFile.readInt()/100.00;
                    BigDecimal amountBD = BigDecimal.valueOf(amountDouble);
                    String amount = formatMoney(amountBD, locale);
                    int personId = billFile.readInt();
                    if (personId != 0) {
                        Person person = getPersonById(personId);
                        addBill(new Bill(name, amount, person));
                    } else {
                        addBill(new Bill(name, amount));
                    }
                } catch (EOFException e) {
                    eof = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





}
