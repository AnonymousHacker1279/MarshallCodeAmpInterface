package tech.anonymoushacker1279.marshallcodeampinterface.controller;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AboutDialogController implements Initializable {

	@FXML
	Hyperlink nameHyperlink;

	private HostServices hostServices;
	private static final FXMLLoader fxmlLoader = new FXMLLoader(CODEInterfaceApplication.class.getResource("about-view.fxml"));
	private static Scene scene;
	private static Stage stage;

	public void setHostServices(HostServices hostServices) {
		this.hostServices = hostServices;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nameHyperlink.setOnAction(event -> hostServices.showDocument("https://github.com/AnonymousHacker1279/MarshallCodeAmpInterface"));
	}

	public static void openDialog(HostServices services) {
		Platform.runLater(() -> {
			try {
				if (scene == null || stage == null) {
					CODEInterfaceApplication.LOGGER.debug("Loading About scene");
					scene = new Scene(fxmlLoader.load(), 600, 400);
					stage = new Stage();
					stage.setTitle("About Marshall CODE Interface");
					stage.setScene(scene);
					stage.setResizable(false);
				}

				stage.show();

				AboutDialogController aboutDialogController = fxmlLoader.getController();
				aboutDialogController.setHostServices(services);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}
}