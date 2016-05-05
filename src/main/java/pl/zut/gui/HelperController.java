package pl.zut.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Kontroler obsługuje pola w pomocy.
 */
public class HelperController {
    @FXML
    Hyperlink myPage = new Hyperlink();

    @FXML
    Hyperlink article = new Hyperlink();

    @FXML
    private void handleOpenMyWebsite(ActionEvent event) {
        try {
            Desktop.getDesktop().browse(new URI("www.piotrretmanczyk.pl"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenArticle(ActionEvent event) {
        try {
            Desktop.getDesktop().browse(new URI("http://www.knws.uz.zgora.pl/history/pdf/KNWS'12/Sesja_B_referat_4.pdf"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
