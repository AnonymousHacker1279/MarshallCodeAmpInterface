package tech.anonymoushacker1279.marshallcodeampinterface.controller;

import atlantafx.base.controls.RingProgressIndicator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;
import tech.anonymoushacker1279.marshallcodeampinterface.controller.components.PresetListEntry;
import tech.anonymoushacker1279.marshallcodeampinterface.gateway.GatewayPresetEntry;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Supplier;

public class GatewayPresetBrowserController implements Initializable {

	@FXML
	private TextField searchTextField;
	@FXML
	private AnchorPane presetAnchorPane;
	@FXML
	private RingProgressIndicator ringProgressIndicator;

	private static final FXMLLoader browserFXMLLoader = new FXMLLoader(CODEInterfaceApplication.class.getResource("views/preset-browser.fxml"));
	private static Scene scene;
	private static Stage stage;

	public static GatewayPresetBrowserController openDialog() {
		Platform.runLater(() -> {
			try {
				if (scene == null || stage == null) {
					CODEInterfaceApplication.LOGGER.debug("Loading Gateway Preset Browser scene");
					scene = new Scene(browserFXMLLoader.load());
					stage = new Stage();
					stage.setTitle("Gateway Preset Browser");
					stage.setScene(scene);
					stage.setResizable(false);
					stage.getIcons().add(new Image(Objects.requireNonNull(CODEInterfaceApplication.class.getResourceAsStream("images/code50.png"))));
				}

				stage.show();

				if (!CODEInterfaceApplication.GATEWAY_API_HANDLER.isLoggedIn()) {
					GatewayLoginDialogController.openDialog();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		return browserFXMLLoader.getController();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		searchTextField.setOnAction((event) -> {
			// Clear the previous presets
			presetAnchorPane.getChildren().clear();
			presetAnchorPane.setPrefHeight(300);

			ringProgressIndicator.setVisible(true);

			// Run the preset fetching in a background thread
			new Thread(() -> {
				List<GatewayPresetEntry> presets = CODEInterfaceApplication.GATEWAY_API_HANDLER.searchPresets(searchTextField.getText());

				Platform.runLater(() -> {
					ringProgressIndicator.setVisible(false);

					for (GatewayPresetEntry preset : presets) {
						addPreset(preset.presetName(), preset.trackName(), preset.artistName(), preset.downloadCount(), preset.coverImageURL(), preset.downloadAction());
					}
				});
			}, "Gateway Preset Search").start();
		});
	}

	public void addPreset(String presetName, String trackName, String artistName, int downloads, String coverUrl, Supplier<?> downloadAction) {
		// Add custom elements to the pane
		PresetListEntry presetListEntry = new PresetListEntry(presetName, trackName, artistName, downloads, coverUrl, downloadAction);

		// Position the preset in the pane
		presetListEntry.setLayoutY(presetAnchorPane.getChildren().size() * 100);

		presetAnchorPane.getChildren().add(presetListEntry);

		// Update the preferred height of the AnchorPane
		presetAnchorPane.setPrefHeight(presetAnchorPane.getChildren().size() * 100);
	}
}