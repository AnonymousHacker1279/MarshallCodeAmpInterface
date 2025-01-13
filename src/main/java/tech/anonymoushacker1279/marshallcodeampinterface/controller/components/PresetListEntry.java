package tech.anonymoushacker1279.marshallcodeampinterface.controller.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;

import java.io.IOException;
import java.util.function.Supplier;

public class PresetListEntry extends HBox {

	@FXML
	private ImageView coverImageView;
	@FXML
	private Text presetNameText;
	@FXML
	private Text trackNameText;
	@FXML
	private Text artistNameText;
	@FXML
	private Text downloadsText;
	@FXML
	private Button usePresetButton;

	public PresetListEntry(String presetName, String trackName, String artistName, int downloads, String coverImageURL, Supplier<?> downloadAction) {
		FXMLLoader fxmlLoader = new FXMLLoader(CODEInterfaceApplication.class.getResource("views/components/preset-list-entry.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		presetNameText.setText(presetName);
		trackNameText.setText(trackName);
		artistNameText.setText(artistName);
		downloadsText.setText(String.valueOf(downloads));
		if (coverImageURL != null && !coverImageURL.isEmpty()) {
			coverImageView.setImage(new Image(coverImageURL));
		}

		usePresetButton.setOnAction((event) -> {
			downloadAction.get();
		});
	}
}