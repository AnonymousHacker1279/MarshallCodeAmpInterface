package tech.anonymoushacker1279.marshallcodeampinterface.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;

import java.io.IOException;
import java.util.Objects;

public class BTScanningInterfaceController {

	private static final FXMLLoader fxmlLoader = new FXMLLoader(CODEInterfaceApplication.class.getResource("views/bt-scanning.fxml"));
	private static Scene scene;
	private static Stage stage;

	public static void openDialog() {
		Platform.runLater(() -> {
			try {
				if (scene == null || stage == null) {
					CODEInterfaceApplication.LOGGER.debug("Loading Bluetooth Scanning scene");
					scene = new Scene(fxmlLoader.load());
					stage = new Stage();
					stage.setTitle("Bluetooth Scanning");
					stage.setScene(scene);
					stage.setAlwaysOnTop(true);
					stage.getIcons().add(new Image(Objects.requireNonNull(CODEInterfaceApplication.class.getResourceAsStream("images/code50.png"))));

					stage.setOnCloseRequest(event -> {
						event.consume();
						stage.hide();
					});
				}

				stage.show();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public static void closeDialog() {
		Platform.runLater(() -> stage.close());
	}
}