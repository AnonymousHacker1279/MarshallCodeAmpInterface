package tech.anonymoushacker1279.marshallcodeampinterface;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutDialogController implements Initializable {

	@FXML Hyperlink nameHyperlink;

	private HostServices hostServices;

	public void setHostServices(HostServices hostServices) {
		this.hostServices = hostServices;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nameHyperlink.setOnAction(event -> hostServices.showDocument("https://github.com/AnonymousHacker1279/MarshallCodeAmpInterface"));
	}
}