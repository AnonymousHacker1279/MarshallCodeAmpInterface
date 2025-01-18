package tech.anonymoushacker1279.marshallcodeampinterface.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;
import tech.anonymoushacker1279.marshallcodeampinterface.amp.AmpConfig;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SavePresetToAmpDialogController implements Initializable {

	@FXML
	private ChoiceBox<String> presetChoiceBox;
	@FXML
	private TextField presetNameTextField;
	@FXML
	private Button saveButton;

	private static final FXMLLoader fxmlLoader = new FXMLLoader(CODEInterfaceApplication.class.getResource("views/save-to-amp.fxml"));
	private static Scene scene;
	private static Stage stage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		presetChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
			saveButton.setDisable(savePrevented());
		});

		// Enforce a character limit of 1-18 characters for the preset name
		presetNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.length() > 18) {
				presetNameTextField.setText(oldValue);
			}

			saveButton.setDisable(savePrevented());
		});

		saveButton.setOnAction(event -> {
			AmpConfig currentConfig = AmpConfig.getCurrentConfig(CODEInterfaceApplication.CONTROLLER);
			currentConfig.presetName = presetNameTextField.getText();
			currentConfig.presetNumber = presetChoiceBox.getSelectionModel().getSelectedIndex();
			CODEInterfaceApplication.INTERFACE.saveConfigToDevice(currentConfig, presetChoiceBox.getSelectionModel().getSelectedIndex());

			// Replace the preset in the list
			CODEInterfaceApplication.PRESETS.set(presetChoiceBox.getSelectionModel().getSelectedIndex(), currentConfig);
			CODEInterfaceApplication.CONTROLLER.presetListView.getItems().set(presetChoiceBox.getSelectionModel().getSelectedIndex(), currentConfig.presetName);

			stage.close();
		});
	}

	private boolean savePrevented() {
		return presetNameTextField.getText().isEmpty() || presetChoiceBox.getSelectionModel().getSelectedIndex() == -1;
	}

	public static void openDialog() {
		Platform.runLater(() -> {
			try {
				if (scene == null || stage == null) {
					CODEInterfaceApplication.LOGGER.debug("Loading save preset to amp scene");
					scene = new Scene(fxmlLoader.load());
					stage = new Stage();
					stage.setTitle("Save Preset to Amp");
					stage.setScene(scene);
					stage.setResizable(false);
					stage.getIcons().add(new Image(Objects.requireNonNull(CODEInterfaceApplication.class.getResourceAsStream("images/code50.png"))));
				}

				SavePresetToAmpDialogController controller = fxmlLoader.getController();
				controller.presetNameTextField.clear();
				controller.presetChoiceBox.getItems().clear();
				controller.saveButton.setDisable(true);

				int i = 0;
				for (AmpConfig config : CODEInterfaceApplication.PRESETS) {
					controller.presetChoiceBox.getItems().add(i + ". " + config.presetName);
					i++;
				}

				stage.show();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}
}