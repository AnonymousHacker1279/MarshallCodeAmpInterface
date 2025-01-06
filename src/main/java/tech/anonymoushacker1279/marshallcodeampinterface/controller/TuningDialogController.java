package tech.anonymoushacker1279.marshallcodeampinterface.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class TuningDialogController implements Initializable {

	@FXML
	TextField tunerTextField;
	@FXML
	Line tuningLine;

	private static final FXMLLoader fxmlLoader = new FXMLLoader(CODEInterfaceApplication.class.getResource("views/tuner.fxml"));
	private static Scene scene;
	private static Stage stage;
	private final Rotate tuningLineRotate = new Rotate();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tuningLine.getTransforms().add(tuningLineRotate);
		tuningLineRotate.setPivotX(tuningLine.getStartX());
		tuningLineRotate.setPivotY(tuningLine.getStartY());
	}

	/**
	 * Update the tuner display. The note is the MIDI note number and the accuracy is a value between 0 and 5, with 0
	 * being "very flat" and 5 being "very sharp".
	 *
	 * @param note     the MIDI note number
	 * @param accuracy the accuracy value
	 */
	public void updateTuner(int note, int accuracy) {
		tunerTextField.setText(midiToNote(note));

		double angle = 0;
		switch (accuracy) {
			case 0 -> angle = -50;
			case 1 -> angle = -25;
			case 2 -> angle = 0;
			case 3 -> angle = 25;
			case 4 -> angle = 50;
		}

		tuningLineRotate.setAngle(angle);
	}

	private String midiToNote(int note) {
		String[] notes = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
		return notes[note % 12] + (note / 12 - 1);
	}

	public static void openDialog(Consumer<TuningDialogController> onControllerLoaded) {
		Platform.runLater(() -> {
			try {
				if (scene == null || stage == null) {
					scene = new Scene(fxmlLoader.load());
					stage = new Stage();
					stage.setTitle("Tuner");
					stage.setScene(scene);
					stage.setResizable(false);
					stage.getIcons().add(new Image(Objects.requireNonNull(CODEInterfaceApplication.class.getResourceAsStream("images/code50.png"))));

					stage.setOnCloseRequest(event -> {
						event.consume();
						CODEInterfaceApplication.INTERFACE.toggleTuner(false);
						stage.hide();
					});
				}

				stage.show();

				onControllerLoaded.accept(fxmlLoader.getController());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public static void closeDialog() {
		Platform.runLater(() -> stage.close());
	}
}