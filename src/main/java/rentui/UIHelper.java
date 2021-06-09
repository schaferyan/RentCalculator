package rentui;

import datamodel.HouseData;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Optional;

public class UIHelper {

    public static void dialogHelper(String fxmlFileName, String title, Object selected, Pane pane) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(pane.getScene().getWindow());
        dialog.setTitle(title);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Controller.class.getResource(fxmlFileName));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        DialogController controller = fxmlLoader.getController();
        controller.setFields(selected);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (!controller.validateAndProcess()) {
                event.consume();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid input");
                alert.setHeaderText("");
                alert.setHeight(50);
                alert.setContentText("Invalid input, cannot process request. " +
                        "Please enter a valid input and try again.");
                alert.show();
            }
        });
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            HouseData.getInstance().updateData();
        }else{
        }
    }

    public static SimpleStringProperty formatDouble(double d){
        return new SimpleStringProperty("$ " + String.format("%.2f", d));
    }

    public static void setButtonIcon(String imgUrl, Button button){
        try {
            button.setGraphic(new ImageView(new Image(UIHelper.class.getResourceAsStream(imgUrl), 50, 50, false, false)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
