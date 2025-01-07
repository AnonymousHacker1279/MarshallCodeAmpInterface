package tech.anonymoushacker1279.marshallcodeampinterface.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ErrorDialogController implements Initializable {

	private static final FXMLLoader fxmlLoader = new FXMLLoader(CODEInterfaceApplication.class.getResource("views/error.fxml"));
	private static Scene scene;
	private static Stage stage;

	@FXML
	private TextArea errorTextArea;
	@FXML
	private Button openLogFolderButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		openLogFolderButton.setOnAction(event -> {
			try {
				String[] command = new String[]{"explorer.exe", CODEInterfaceApplication.getRootDataPath() + "\\logs"};
				Runtime.getRuntime().exec(command);
			} catch (IOException e) {
				CODEInterfaceApplication.LOGGER.error("Failed to open log folder", e);
			}
		});
	}

	public static void openDialog(String message, String exception) {
		Platform.runLater(() -> {
			try {
				if (scene == null || stage == null) {
					CODEInterfaceApplication.LOGGER.debug("Loading error dialog scene");
					scene = new Scene(fxmlLoader.load());
					stage = new Stage();
					stage.setTitle("Fatal Application Error");
					stage.setScene(scene);
					stage.setAlwaysOnTop(true);
					stage.getIcons().add(new Image(Objects.requireNonNull(CODEInterfaceApplication.class.getResourceAsStream("images/code50.png"))));

					ErrorDialogController controller = fxmlLoader.getController();
					String preparedMessage = message + "\n\n" + exception;
					controller.errorTextArea.setText(preparedMessage);

					stage.setOnCloseRequest(event -> {
						event.consume();
						stage.hide();

						Platform.runLater(() -> {
							CODEInterfaceApplication.LOGGER.fatal("Exiting application due to fatal error");
							System.exit(1);
						});
					});
				}

				stage.show();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}
}