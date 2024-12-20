package tech.anonymoushacker1279.marshallcodeampinterface.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;

import java.io.IOException;

public class BTScanningInterfaceController {

	private static final FXMLLoader fxmlLoader = new FXMLLoader(CODEInterfaceApplication.class.getResource("bt-scanning-view.fxml"));
	private static Scene scene;
	private static Stage stage;

	public static void openDialog() {
		Platform.runLater(() -> {
			try {
				if (scene == null || stage == null) {
					scene = new Scene(fxmlLoader.load());
					stage = new Stage();
					stage.setTitle("Bluetooth Scanning");
					stage.setScene(scene);
					stage.setAlwaysOnTop(true);

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