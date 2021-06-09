package rentui;

import datamodel.HouseData;
import datamodel.Person;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.*;
import java.io.File;
import java.io.IOException;


public class Controller {

    @FXML
    private Button editPersonButton;
    @FXML
    private Button addPersonButton;
    @FXML
    private Button deletePersonButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button editHouseButton;
    @FXML
    private Button loadButton;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private TableView<Person> rentTable;
    @FXML
    private ContextMenu contextMenu;
    private File saveDir;
    private final HouseData houseData = HouseData.getInstance();



    public void initialize() {

        saveDir = new File(System.getProperty("user.home"), "rentcalculus/records");
        if (!saveDir.exists()) {
            if(!saveDir.mkdirs()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Couldn't find directory");
                alert.setContentText("Unable to find the home directory. You will be unable to save your work " +
                        "until you specify a default directory in settings");
                alert.show();
            }
        }

        UIHelper.setButtonIcon("/icons/icons8-save-64(1).png", saveButton);
        UIHelper.setButtonIcon("/icons/icons8-add-user-female-64.png", addPersonButton);
        UIHelper.setButtonIcon("/icons/icons8-edit-profile-64.png", editPersonButton);
        UIHelper.setButtonIcon("/icons/icons8-home-100.png", editHouseButton);
        UIHelper.setButtonIcon("/icons/icons8-load-cargo-64.png", loadButton );
        UIHelper.setButtonIcon("/icons/icons8-denied-64.png", deletePersonButton);
        houseData.load();
        houseData.updateData();
        rentTable.setItems(houseData.getPeople());

        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem save = new MenuItem("Save");
        MenuItem saveAs = new MenuItem("Save As");
        MenuItem load = new MenuItem("Load");
        MenuItem exit = new MenuItem("Exit");
        MenuItem print = new MenuItem("Print");
        fileMenu.getItems().addAll(save, saveAs, load, print, new SeparatorMenuItem(), exit);

        Menu houseMenu = new Menu("House");
        MenuItem addPerson = new MenuItem("Add new Person");
        MenuItem addRoom = new MenuItem("Add new Room");
        MenuItem addBill = new MenuItem("Add new Bill");

        houseMenu.getItems().addAll(addPerson, addRoom, addBill);

        Menu viewMenu = new Menu("View");
        MenuItem viewRooms = new MenuItem("Rooms");
        MenuItem viewBills = new MenuItem( "Bills");
        MenuItem viewHouse = new MenuItem("House Details");
        viewMenu.getItems().addAll(viewHouse, viewRooms, viewBills);

        Menu aboutMenu = new Menu("About");
        MenuItem aboutItem = new MenuItem("About");
        aboutMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, houseMenu, viewMenu, aboutMenu);

        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        addBill.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN));
        addRoom.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        addPerson.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        viewRooms.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.ALT_DOWN));
        viewBills.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.ALT_DOWN));
        viewHouse.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.ALT_DOWN));


        save.setOnAction(actionEvent -> handleSaveRequest());
        saveAs.setOnAction(actionEvent -> handleSaveAsRequest());
        load.setOnAction(actionEvent -> handleLoadRequest());

        exit.setOnAction(actionEvent -> handleExitRequest());
        addPerson.setOnAction(actionEvent -> showNewPersonDialog());
        addRoom.setOnAction(actionEvent -> addRoom());
        addBill.setOnAction(actionEvent -> addBill());

        viewHouse.setOnAction(this::handleEditHouseRequest);
        viewRooms.setOnAction(actionEvent -> {
            try {
                showRoomStage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        viewBills.setOnAction(actionEvent -> {
            try {
                showBillStage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        print.setOnAction(actionEvent -> handlePrintRequest());
        aboutMenu.setOnAction(actionEvent -> {
            try{
                showAboutStage();
            } catch (IOException e){
                e.printStackTrace();
            }
        });

        mainBorderPane.setTop(menuBar);

        contextMenu = new ContextMenu();
        MenuItem editPerson = new MenuItem("Edit person");
        MenuItem deletePerson = new MenuItem("Delete person");
        contextMenu.getItems().addAll(editPerson, deletePerson);
        editPerson.setOnAction(actionEvent -> handleEditPersonRequest());
        deletePerson.setOnAction(actionEvent -> handleDeleteRequest());



        TableColumn<Person, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(c -> c.getValue().nameProperty());

        TableColumn<Person, String> roomFeeColumn = new TableColumn<>("Room Fee");
        roomFeeColumn.setCellValueFactory(c -> c.getValue().roomFeeProperty());

        TableColumn<Person, String> csFeeCol = new TableColumn<>( "Community Space Fee");
        csFeeCol.setCellValueFactory(c -> c.getValue().csFeeProperty());

        TableColumn<Person, String> utilFeeCol = new TableColumn<>( "Utilities Fee");
        utilFeeCol.setCellValueFactory(c -> c.getValue().utilityFeeProperty());

        TableColumn<Person, String> essFeeCol = new TableColumn<>( "Essentials Fee");
        essFeeCol.setCellValueFactory(c -> c.getValue().essentialsFeeProperty());

        TableColumn<Person, String> billsPayedCol = new TableColumn<>( "Bills Payed");
        billsPayedCol.setCellValueFactory(c -> c.getValue().billsPayedProperty());

        TableColumn<Person, String> essBoughtCol = new TableColumn<>( "Essentials Bought");
        essBoughtCol.setCellValueFactory(c -> c.getValue().essentialsBoughtProperty());

        TableColumn<Person, String> totalCol = new TableColumn<>( "Total");
        totalCol.setCellValueFactory(c -> c.getValue().totalOwedProperty());
        rentTable.getColumns().setAll(nameColumn, csFeeCol, roomFeeColumn, utilFeeCol,
                essFeeCol, billsPayedCol, essBoughtCol, totalCol);
        rentTable.getSelectionModel().select(0);

        rentTable.setRowFactory(personTableView -> {
            TableRow<Person> row = new TableRow<>();
            row.setContextMenu(contextMenu);
            return row;
        });

        rentTable.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    handleEditPersonRequest();
                }
            }
        });

        rentTable.setPlaceholder(new Text("Add people to your document to see rent data"));
    }



    private void handlePrintRequest() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null)
        {
            return;
        }
        Printer printer = Printer.getDefaultPrinter();
        JobSettings settings = job.getJobSettings();
        settings.setCopies(1);
        settings.setPageRanges(new PageRange(1, 1));
        PageLayout layout = printer.createPageLayout(Paper.A4,
                PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM);
        settings.setPageLayout(layout);

// Show the print setup dialog
        boolean proceed = job.showPrintDialog(mainBorderPane.getScene().getWindow());
        if (proceed)
        {
            print(job);
        }


    }

    private void print(PrinterJob job) {
        JobSettings settings = job.getJobSettings();
        PageLayout layout = settings.getPageLayout();
        double pw = layout.getPrintableWidth();
        double ph = layout.getPrintableHeight();
        if(rentTable.getWidth() > pw || rentTable.getHeight() > ph){
            rentTable.setMaxSize(pw, ph);
        }

        boolean printed = job.printPage(rentTable);

        if (printed)
        {
            job.endJob();
        }
    }


    private void showBillStage() throws IOException {
        Stage billStage = new Stage();
        billStage.setTitle("Bills");
        billStage.initModality(Modality.NONE);
        billStage.initStyle(StageStyle.UTILITY);
        Parent parent = FXMLLoader.load(getClass().getResource("/bills.fxml"));
        billStage.setScene(new Scene(parent));
        billStage.initOwner(mainBorderPane.getScene().getWindow());
        billStage.show();
    }

    private void showRoomStage() throws IOException {
        Stage roomStage = new Stage();
        roomStage.setTitle("Rooms");
        roomStage.initModality(Modality.NONE);
        roomStage.initStyle(StageStyle.UTILITY);
        Parent parent = FXMLLoader.load(getClass().getResource("/rooms.fxml"));
        roomStage.setScene(new Scene(parent));
        roomStage.initOwner(mainBorderPane.getScene().getWindow());
        roomStage.show();
    }

    private void showAboutStage() throws IOException {
        Stage aboutStage = new Stage();
        aboutStage.setTitle("About");
        aboutStage.initModality(Modality.NONE);
        aboutStage.initStyle(StageStyle.UTILITY);
        Parent parent = FXMLLoader.load(getClass().getResource("/about.fxml"));
        aboutStage.setScene(new Scene(parent));
        aboutStage.initOwner(mainBorderPane.getScene().getWindow());
        aboutStage.show();
    }

    private void handleExitRequest() {
//        replace with prefab alert
        if(houseData.isSaved()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit");
            alert.setHeaderText("");
            alert.setHeight(50);
            alert.setContentText("Are you sure you want to exit?");
            alert.show();
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit");
            alert.setHeaderText("");
            alert.setHeight(50);
            alert.setContentText("Quit without saving?");
            alert.show();
            ButtonType saveAndQuit = new ButtonType("Save and quit");
            alert.getButtonTypes().add(saveAndQuit);
            Button button = (Button) alert.getDialogPane().lookupButton(saveAndQuit);
            button.setOnAction(actionEvent -> {
                houseData.save();
                Platform.exit();
            });
        }


    }

    public void addRoom() {
        dialogHelper("/newRoom.fxml", "Add new room");
        rentTable.refresh();
    }

    public void dialogHelper(String fxmlFileName, String title, Object selected){
        UIHelper.dialogHelper(fxmlFileName, title, selected, mainBorderPane);
    }

    public void dialogHelper(String fxmlFileName, String title){
        dialogHelper(fxmlFileName, title, null);
    }

    public void addBill() {
        UIHelper.dialogHelper("/newBill.fxml", "New Bill", null, mainBorderPane);
        rentTable.refresh();
    }

    public void showNewPersonDialog() {
        dialogHelper("/newPersonDialog.fxml", "Add new person");
        rentTable.refresh();
    }

    public void handleSaveRequest() {
        if(!houseData.isSaved()){

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(saveDir);
            File file = fileChooser.showSaveDialog(mainBorderPane.getScene().getWindow());
            if (file != null) {
                houseData.setDirectory(file);
                houseData.setSaved(true);
            }
        }
        houseData.save();
    }

    public void handleSaveAsRequest() {
            houseData.setSaved(false);
            handleSaveRequest();
    }

    public void handleLoadRequest(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose folder to load data from");
        directoryChooser.setInitialDirectory(saveDir);
        File file = directoryChooser.showDialog(mainBorderPane.getScene().getWindow());
        if (file != null) {
            houseData.setDirectory(file);
            houseData.reset();
            houseData.load();
            houseData.setSaved(true);
        }
    }

    public void handleDeleteRequest() {
        houseData.removePerson(rentTable.getSelectionModel().getSelectedItem());
        rentTable.refresh();
    }

    public void handleEditPersonRequest() {
        dialogHelper("/newPersonDialog.fxml", "Edit Person", rentTable.getSelectionModel().getSelectedItem());
        rentTable.refresh();
    }

    public void handleEditHouseRequest(ActionEvent actionEvent) {
        dialogHelper("/editHouseDialog.fxml", "Edit House Details");
        rentTable.refresh();
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        Person person = rentTable.getSelectionModel().getSelectedItem();

        switch(keyEvent.getCode()){
            case DELETE:
                houseData.removePerson(person);
                break;
            case E:
                handleEditPersonRequest();
                break;
            case Z:
                System.out.println("Undo last action");
                break;
            default:
                break;
        }
    }
}
