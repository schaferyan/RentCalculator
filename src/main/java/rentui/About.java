package rentui;

import javafx.fxml.FXML;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class About {

    @FXML
    private Text title;

    public void initialize(){
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 32));
    }

}
