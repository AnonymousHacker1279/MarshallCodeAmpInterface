package tech.anonymoushacker1279.marshallcodeampinterfacev2;

import javafx.application.HostServices;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tech.anonymoushacker1279.marshallcodeampinterfacev2.amp.AmpConfig;

import javax.sound.midi.InvalidMidiDataException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CODEInterfaceController implements Initializable {

	@FXML public ToggleButton ampToggleButton;
	@FXML public ListView<String> ampListView;
	@FXML public Slider gainSlider;
	@FXML public TextField gainTextField;
	@FXML public Slider volumeSlider;
	@FXML public TextField volumeTextField;
	@FXML public Slider gateSlider;
	@FXML public TextField gateTextField;
	@FXML public Slider bassSlider;
	@FXML public TextField bassTextField;
	@FXML public Slider middleSlider;
	@FXML public TextField middleTextField;
	@FXML public Slider trebleSlider;
	@FXML public TextField trebleTextField;
	@FXML public ToggleButton powerToggleButton;
	@FXML public ListView<String> powerListView;
	@FXML public Slider presenceSlider;
	@FXML public TextField presenceTextField;
	@FXML public Slider resonanceSlider;
	@FXML public TextField resonanceTextField;
	@FXML public ToggleButton cabToggleButton;
	@FXML public ListView<String> cabListView;
	@FXML public ToggleButton preFXToggleButton;
	@FXML public TabPane preFXTabPane;
	@FXML public Slider compressorToneSlider;
	@FXML public TextField compressorToneTextField;
	@FXML public Slider compressorRatioSlider;
	@FXML public TextField compressorRatioTextField;
	@FXML public Slider compressorCompSlider;
	@FXML public TextField compressorCompTextField;
	@FXML public Slider compressorLevelSlider;
	@FXML public TextField compressorLevelTextField;
	@FXML public ListView<String> distortionModeListView;
	@FXML public Slider distortionDriveSlider;
	@FXML public TextField distortionDriveTextField;
	@FXML public Slider distortionToneSlider;
	@FXML public TextField distortionToneTextField;
	@FXML public Slider distortionLevelSlider;
	@FXML public TextField distortionLevelTextField;
	@FXML public ListView<String> autoWahModeListView;
	@FXML public Slider autoWahFreqSlider;
	@FXML public TextField autoWahFreqTextField;
	@FXML public Slider autoWahSensitivitySlider;
	@FXML public TextField autoWahSensitivityTextField;
	@FXML public Slider autoWahResSlider;
	@FXML public TextField autoWahResTextField;
	@FXML public Slider pitchShifterSemitoneSlider;
	@FXML public TextField pitchShifterSemitoneTextField;
	@FXML public Slider pitchShifterFineSlider;
	@FXML public TextField pitchShifterFineTextField;
	@FXML public Slider pitchShifterRegenSlider;
	@FXML public TextField pitchShifterRegenTextField;
	@FXML public Slider pitchShifterMixSlider;
	@FXML public TextField pitchShifterMixTextField;
	@FXML public ToggleButton modulationToggleButton;
	@FXML public TabPane modulationTabPane;
	@FXML public ListView<String> chorusModeListView;
	@FXML public Slider chorusSpeedSlider;
	@FXML public TextField chorusSpeedTextField;
	@FXML public Slider chorusDepthSlider;
	@FXML public TextField chorusDepthTextField;
	@FXML public Slider chorusToneSlider;
	@FXML public TextField chorusToneTextField;
	@FXML public ListView<String> flangerModeListView;
	@FXML public Slider flangerSpeedSlider;
	@FXML public TextField flangerSpeedTextField;
	@FXML public Slider flangerDepthSlider;
	@FXML public TextField flangerDepthTextField;
	@FXML public Slider flangerRegenSlider;
	@FXML public TextField flangerRegenTextField;
	@FXML public ListView<String> phaserModeListView;
	@FXML public Slider phaserSpeedSlider;
	@FXML public TextField phaserSpeedTextField;
	@FXML public Slider phaserDepthSlider;
	@FXML public TextField phaserDepthTextField;
	@FXML public Slider phaserRegenSlider;
	@FXML public TextField phaserRegenTextField;
	@FXML public ListView<String> tremoloModeListView;
	@FXML public Slider tremoloSpeedSlider;
	@FXML public TextField tremoloSpeedTextField;
	@FXML public Slider tremoloDepthSlider;
	@FXML public TextField tremoloDepthTextField;
	@FXML public Slider tremoloSkewSlider;
	@FXML public TextField tremoloSkewTextField;
	@FXML public ToggleButton delayToggleButton;
	@FXML public TabPane delayTabPane;
	@FXML public Slider studioTimeSlider;
	@FXML public TextField studioTimeTextField;
	@FXML public Slider studioFeedbackSlider;
	@FXML public TextField studioFeedbackTextField;
	@FXML public Slider studioFreqSlider;
	@FXML public TextField studioFreqTextField;
	@FXML public Slider studioLevelSlider;
	@FXML public TextField studioLevelTextField;
	@FXML public Slider vintageTimeSlider;
	@FXML public TextField vintageTimeTextField;
	@FXML public Slider vintageAgeSlider;
	@FXML public TextField vintageAgeTextField;
	@FXML public Slider vintageFreqSlider;
	@FXML public TextField vintageFreqTextField;
	@FXML public Slider vintageLevelSlider;
	@FXML public TextField vintageLevelTextField;
	@FXML public Slider multiTimeSlider;
	@FXML public TextField multiTimeTextField;
	@FXML public Slider multiFeedbackSlider;
	@FXML public TextField multiFeedbackTextField;
	@FXML public ListView<Integer> multiTapPatternListView;
	@FXML public Slider multiLevelSlider;
	@FXML public TextField multiLevelTextField;
	@FXML public Slider reverseTimeSlider;
	@FXML public TextField reverseTimeTextField;
	@FXML public Slider reverseFeedbackSlider;
	@FXML public TextField reverseFeedbackTextField;
	@FXML public Slider reverseFreqSlider;
	@FXML public TextField reverseFreqTextField;
	@FXML public Slider reverseLevelSlider;
	@FXML public TextField reverseLevelTextField;
	@FXML public ToggleButton reverbToggleButton;
	@FXML public TabPane reverbTabPane;
	@FXML public Slider roomDecaySlider;
	@FXML public TextField roomDecayTextField;
	@FXML public Slider roomPreDelaySlider;
	@FXML public TextField roomPreDelayTextField;
	@FXML public Slider roomToneSlider;
	@FXML public TextField roomToneTextField;
	@FXML public Slider roomLevelSlider;
	@FXML public TextField roomLevelTextField;
	@FXML public Slider hallDecaySlider;
	@FXML public TextField hallDecayTextField;
	@FXML public Slider hallPreDelaySlider;
	@FXML public TextField hallPreDelayTextField;
	@FXML public Slider hallToneSlider;
	@FXML public TextField hallToneTextField;
	@FXML public Slider hallLevelSlider;
	@FXML public TextField hallLevelTextField;
	@FXML public Slider springDecaySlider;
	@FXML public TextField springDecayTextField;
	@FXML public Slider springPreDelaySlider;
	@FXML public TextField springPreDelayTextField;
	@FXML public Slider springToneSlider;
	@FXML public TextField springToneTextField;
	@FXML public Slider springLevelSlider;
	@FXML public TextField springLevelTextField;
	@FXML public Slider stadiumDecaySlider;
	@FXML public TextField stadiumDecayTextField;
	@FXML public Slider stadiumPreDelaySlider;
	@FXML public TextField stadiumPreDelayTextField;
	@FXML public Slider stadiumToneSlider;
	@FXML public TextField stadiumToneTextField;
	@FXML public Slider stadiumLevelSlider;
	@FXML public TextField stadiumLevelTextField;
	@FXML public TextField presetNumberTextField;
	@FXML public TextField presetNameTextField;
	@FXML public TextField presetSearchTextField;
	@FXML public ListView<String> presetListView;
	@FXML public ToggleButton autoFlattenEQToggleButton;
	@FXML public MenuItem aboutMenuItem;
	@FXML public MenuItem openTunerMenuItem;

	public boolean ignorePresetChange = false;
	private HostServices hostServices;

	public void setHostServices(HostServices hostServices) {
		this.hostServices = hostServices;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ampToggleButton.setOnAction(event -> CODEInterfaceApplication.INTERFACE.togglePreamp(ampToggleButton.isSelected()));

		ampListView.setItems(FXCollections.observableArrayList(
				"Marshall JTM45",
				"Marshall DSL",
				"Clean American",
				"Marshall JVM410H",
				"Acoustic",
				"Marshall Bluesbreaker",
				"Marshall Plexi",
				"Crunch American",
				"Marshall JCM800",
				"50's British",
				"Marshall JVM",
				"Marshall DSL",
				"OD American",
				"Marshall Silver Jubilee",
				"Natural"
		));
		ampListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CODEInterfaceApplication.INTERFACE.setPreampType(ampListView.getItems().indexOf(newValue)));

		setupSliderAndTextField(gainSlider, gainTextField, CODEInterfaceApplication.INTERFACE::setGain, false);
		setupSliderAndTextField(volumeSlider, volumeTextField, CODEInterfaceApplication.INTERFACE::setVolume, false);
		setupSliderAndTextField(gateSlider, gateTextField, CODEInterfaceApplication.INTERFACE::setGate, false);
		setupSliderAndTextField(bassSlider, bassTextField, CODEInterfaceApplication.INTERFACE::setBass, false);
		setupSliderAndTextField(middleSlider, middleTextField, CODEInterfaceApplication.INTERFACE::setMiddle, false);
		setupSliderAndTextField(trebleSlider, trebleTextField, CODEInterfaceApplication.INTERFACE::setTreble, false);

		powerToggleButton.setOnAction(event -> CODEInterfaceApplication.INTERFACE.togglePowerAmp(powerToggleButton.isSelected()));

		powerListView.setItems(FXCollections.observableArrayList(
				"Classic Marshall 100w",
				"Vintage Marshall 30w",
				"British Class A",
				"American Class A/B"
		));
		powerListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CODEInterfaceApplication.INTERFACE.setPowerAmpType(powerListView.getItems().indexOf(newValue)));

		setupSliderAndTextField(presenceSlider, presenceTextField, CODEInterfaceApplication.INTERFACE::setPresence, false);
		setupSliderAndTextField(resonanceSlider, resonanceTextField, CODEInterfaceApplication.INTERFACE::setResonance, false);

		cabToggleButton.setOnAction(event -> CODEInterfaceApplication.INTERFACE.toggleCab(cabToggleButton.isSelected()));

		cabListView.setItems(FXCollections.observableArrayList(
				"1960",
				"1960V",
				"1960X",
				"1960HW",
				"1936",
				"1936V",
				"1912",
				"1974CX"
		));
		cabListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CODEInterfaceApplication.INTERFACE.setCabType(cabListView.getItems().indexOf(newValue)));

		preFXToggleButton.setOnAction(event -> CODEInterfaceApplication.INTERFACE.togglePreFXPedal(preFXToggleButton.isSelected()));

		preFXTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CODEInterfaceApplication.INTERFACE.setPreFXPedalType(preFXTabPane.getSelectionModel().getSelectedIndex()));

		setupSliderAndTextField(compressorToneSlider, compressorToneTextField, CODEInterfaceApplication.INTERFACE::setPedalParameter1, false);
		setupSliderAndTextField(compressorRatioSlider, compressorRatioTextField, CODEInterfaceApplication.INTERFACE::setPedalParameter2, false);
		setupSliderAndTextField(compressorCompSlider, compressorCompTextField, CODEInterfaceApplication.INTERFACE::setPedalParameter3, false);
		setupSliderAndTextField(compressorLevelSlider, compressorLevelTextField, CODEInterfaceApplication.INTERFACE::setPedalParameter4, false);

		distortionModeListView.setItems(FXCollections.observableArrayList(
				"GUV",
				"ODR",
				"DIST"
		));
		distortionModeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CODEInterfaceApplication.INTERFACE.setPedalParameter1(distortionModeListView.getItems().indexOf(newValue), preFXTabPane.getSelectionModel().getSelectedIndex()));

		setupSliderAndTextField(distortionDriveSlider, distortionDriveTextField, CODEInterfaceApplication.INTERFACE::setPedalParameter2, false);
		setupSliderAndTextField(distortionToneSlider, distortionToneTextField, CODEInterfaceApplication.INTERFACE::setPedalParameter3, false);
		setupSliderAndTextField(distortionLevelSlider, distortionLevelTextField, CODEInterfaceApplication.INTERFACE::setPedalParameter4, false);

		autoWahModeListView.setItems(FXCollections.observableArrayList(
				"ENV",
				"LFO"
		));
		autoWahModeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CODEInterfaceApplication.INTERFACE.setPedalParameter1(autoWahModeListView.getItems().indexOf(newValue), preFXTabPane.getSelectionModel().getSelectedIndex()));

		setupSliderAndTextField(autoWahFreqSlider, autoWahFreqTextField, CODEInterfaceApplication.INTERFACE::setPedalParameter2, false);
		setupSliderAndTextField(autoWahSensitivitySlider, autoWahSensitivityTextField, CODEInterfaceApplication.INTERFACE::setPedalParameter3, false);
		setupSliderAndTextField(autoWahResSlider, autoWahResTextField, CODEInterfaceApplication.INTERFACE::setPedalParameter4, false);

		setupSliderAndTextField(pitchShifterSemitoneSlider, pitchShifterSemitoneTextField, CODEInterfaceApplication.INTERFACE::setPedalParameter1, true);
		setupSliderAndTextField(pitchShifterFineSlider, pitchShifterFineTextField, CODEInterfaceApplication.INTERFACE::setPedalParameter2, true);
		setupSliderAndTextField(pitchShifterRegenSlider, pitchShifterRegenTextField, CODEInterfaceApplication.INTERFACE::setPedalParameter3, false);
		setupSliderAndTextField(pitchShifterMixSlider, pitchShifterMixTextField, CODEInterfaceApplication.INTERFACE::setPedalParameter4, false);

		modulationToggleButton.setOnAction(event -> CODEInterfaceApplication.INTERFACE.toggleModulation(modulationToggleButton.isSelected()));

		modulationTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CODEInterfaceApplication.INTERFACE.setModulationType(modulationTabPane.getSelectionModel().getSelectedIndex()));

		chorusModeListView.setItems(FXCollections.observableArrayList(
				"CLS",
				"VIB"
		));
		chorusModeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CODEInterfaceApplication.INTERFACE.setModulationParameter1(chorusModeListView.getItems().indexOf(newValue)));

		setupSliderAndTextField(chorusSpeedSlider, chorusSpeedTextField, CODEInterfaceApplication.INTERFACE::setModulationParameter2, false);
		setupSliderAndTextField(chorusDepthSlider, chorusDepthTextField, CODEInterfaceApplication.INTERFACE::setModulationParameter3, false);
		setupSliderAndTextField(chorusToneSlider, chorusToneTextField, CODEInterfaceApplication.INTERFACE::setModulationParameter4, false);

		flangerModeListView.setItems(FXCollections.observableArrayList(
				"JET",
				"MET"
		));
		flangerModeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CODEInterfaceApplication.INTERFACE.setModulationParameter1(flangerModeListView.getItems().indexOf(newValue)));

		setupSliderAndTextField(flangerSpeedSlider, flangerSpeedTextField, CODEInterfaceApplication.INTERFACE::setModulationParameter2, false);
		setupSliderAndTextField(flangerDepthSlider, flangerDepthTextField, CODEInterfaceApplication.INTERFACE::setModulationParameter3, false);
		setupSliderAndTextField(flangerRegenSlider, flangerRegenTextField, CODEInterfaceApplication.INTERFACE::setModulationParameter4, false);

		phaserModeListView.setItems(FXCollections.observableArrayList(
				"CLS",
				"VBE"
		));
		phaserModeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CODEInterfaceApplication.INTERFACE.setModulationParameter1(phaserModeListView.getItems().indexOf(newValue)));

		setupSliderAndTextField(phaserSpeedSlider, phaserSpeedTextField, CODEInterfaceApplication.INTERFACE::setModulationParameter2, false);
		setupSliderAndTextField(phaserDepthSlider, phaserDepthTextField, CODEInterfaceApplication.INTERFACE::setModulationParameter3, false);
		setupSliderAndTextField(phaserRegenSlider, phaserRegenTextField, CODEInterfaceApplication.INTERFACE::setModulationParameter4, false);

		tremoloModeListView.setItems(FXCollections.observableArrayList(
				"VLV",
				"SQR"
		));
		tremoloModeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CODEInterfaceApplication.INTERFACE.setModulationParameter1(tremoloModeListView.getItems().indexOf(newValue)));

		setupSliderAndTextField(tremoloSpeedSlider, tremoloSpeedTextField, CODEInterfaceApplication.INTERFACE::setModulationParameter2, false);
		setupSliderAndTextField(tremoloDepthSlider, tremoloDepthTextField, CODEInterfaceApplication.INTERFACE::setModulationParameter3, false);
		setupSliderAndTextField(tremoloSkewSlider, tremoloSkewTextField, CODEInterfaceApplication.INTERFACE::setModulationParameter4, true);

		delayToggleButton.setOnAction(event -> CODEInterfaceApplication.INTERFACE.toggleDelay(delayToggleButton.isSelected()));

		delayTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CODEInterfaceApplication.INTERFACE.setDelayType(delayTabPane.getSelectionModel().getSelectedIndex()));

		setupSliderAndTextField(studioTimeSlider, studioTimeTextField, CODEInterfaceApplication.INTERFACE::setDelayParameter1, true);
		setupSliderAndTextField(studioFeedbackSlider, studioFeedbackTextField, CODEInterfaceApplication.INTERFACE::setDelayParameter2, false);
		setupSliderAndTextField(studioFreqSlider, studioFreqTextField, CODEInterfaceApplication.INTERFACE::setDelayParameter3, false);
		setupSliderAndTextField(studioLevelSlider, studioLevelTextField, CODEInterfaceApplication.INTERFACE::setDelayParameter4, false);

		setupSliderAndTextField(vintageTimeSlider, vintageTimeTextField, CODEInterfaceApplication.INTERFACE::setDelayParameter1, true);
		setupSliderAndTextField(vintageAgeSlider, vintageAgeTextField, CODEInterfaceApplication.INTERFACE::setDelayParameter2, false);
		setupSliderAndTextField(vintageFreqSlider, vintageFreqTextField, CODEInterfaceApplication.INTERFACE::setDelayParameter3, false);
		setupSliderAndTextField(vintageLevelSlider, vintageLevelTextField, CODEInterfaceApplication.INTERFACE::setDelayParameter4, false);

		setupSliderAndTextField(multiTimeSlider, multiTimeTextField, CODEInterfaceApplication.INTERFACE::setDelayParameter1, true);
		setupSliderAndTextField(multiFeedbackSlider, multiFeedbackTextField, CODEInterfaceApplication.INTERFACE::setDelayParameter2, false);
		multiTapPatternListView.setItems(FXCollections.observableArrayList(1, 2, 3, 4));
		multiTapPatternListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CODEInterfaceApplication.INTERFACE.setDelayParameter3(newValue, delayTabPane.getSelectionModel().getSelectedIndex()));
		setupSliderAndTextField(multiLevelSlider, multiLevelTextField, CODEInterfaceApplication.INTERFACE::setDelayParameter4, false);

		setupSliderAndTextField(reverseTimeSlider, reverseTimeTextField, CODEInterfaceApplication.INTERFACE::setDelayParameter1, true);
		setupSliderAndTextField(reverseFeedbackSlider, reverseFeedbackTextField, CODEInterfaceApplication.INTERFACE::setDelayParameter2, false);
		setupSliderAndTextField(reverseFreqSlider, reverseFreqTextField, CODEInterfaceApplication.INTERFACE::setDelayParameter3, false);
		setupSliderAndTextField(reverseLevelSlider, reverseLevelTextField, CODEInterfaceApplication.INTERFACE::setDelayParameter4, false);

		reverbToggleButton.setOnAction(event -> CODEInterfaceApplication.INTERFACE.toggleReverb(reverbToggleButton.isSelected()));

		reverbTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> CODEInterfaceApplication.INTERFACE.setReverbType(reverbTabPane.getSelectionModel().getSelectedIndex()));

		setupSliderAndTextField(roomDecaySlider, roomDecayTextField, CODEInterfaceApplication.INTERFACE::setReverbParameter1, false);
		setupSliderAndTextField(roomPreDelaySlider, roomPreDelayTextField, CODEInterfaceApplication.INTERFACE::setReverbParameter2, false);
		setupSliderAndTextField(roomToneSlider, roomToneTextField, CODEInterfaceApplication.INTERFACE::setReverbParameter3, false);
		setupSliderAndTextField(roomLevelSlider, roomLevelTextField, CODEInterfaceApplication.INTERFACE::setReverbParameter4, false);

		setupSliderAndTextField(hallDecaySlider, hallDecayTextField, CODEInterfaceApplication.INTERFACE::setReverbParameter1, false);
		setupSliderAndTextField(hallPreDelaySlider, hallPreDelayTextField, CODEInterfaceApplication.INTERFACE::setReverbParameter2, false);
		setupSliderAndTextField(hallToneSlider, hallToneTextField, CODEInterfaceApplication.INTERFACE::setReverbParameter3, false);
		setupSliderAndTextField(hallLevelSlider, hallLevelTextField, CODEInterfaceApplication.INTERFACE::setReverbParameter4, false);

		setupSliderAndTextField(springDecaySlider, springDecayTextField, CODEInterfaceApplication.INTERFACE::setReverbParameter1, false);
		setupSliderAndTextField(springPreDelaySlider, springPreDelayTextField, CODEInterfaceApplication.INTERFACE::setReverbParameter2, false);
		setupSliderAndTextField(springToneSlider, springToneTextField, CODEInterfaceApplication.INTERFACE::setReverbParameter3, false);
		setupSliderAndTextField(springLevelSlider, springLevelTextField, CODEInterfaceApplication.INTERFACE::setReverbParameter4, false);

		setupSliderAndTextField(stadiumDecaySlider, stadiumDecayTextField, CODEInterfaceApplication.INTERFACE::setReverbParameter1, false);
		setupSliderAndTextField(stadiumPreDelaySlider, stadiumPreDelayTextField, CODEInterfaceApplication.INTERFACE::setReverbParameter2, false);
		setupSliderAndTextField(stadiumToneSlider, stadiumToneTextField, CODEInterfaceApplication.INTERFACE::setReverbParameter3, false);
		setupSliderAndTextField(stadiumLevelSlider, stadiumLevelTextField, CODEInterfaceApplication.INTERFACE::setReverbParameter4, false);

		presetNumberTextField.textProperty().setValue(String.valueOf(CODEInterfaceApplication.DEFAULT_CONFIG.presetNumber));
		presetNameTextField.textProperty().setValue(CODEInterfaceApplication.DEFAULT_CONFIG.presetName);

		presetSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
			ignorePresetChange = true;
			if (newValue.isEmpty()) {
				presetListView.setItems(FXCollections.observableArrayList(CODEInterfaceApplication.PRESETS.stream().map(config -> config.presetName).toList()));
			} else {
				presetListView.setItems(FXCollections.observableArrayList(CODEInterfaceApplication.PRESETS.stream().filter(config -> config.presetName.toLowerCase().contains(newValue.toLowerCase())).map(config -> config.presetName).toList()));
			}
		});

		ObservableList<String> presets = FXCollections.observableArrayList();
		for (AmpConfig config : CODEInterfaceApplication.PRESETS) {
			presets.add(config.presetName);
		}
		presetListView.setItems(presets);
		presetListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (ignorePresetChange) {
				ignorePresetChange = false;
				return;
			}

			CODEInterfaceApplication.PRESETS.stream().filter(config -> config.presetName.equals(newValue)).findFirst().ifPresent(config -> {
				try {
					CODEInterfaceApplication.INTERFACE.sendProgramChange(config.presetNumber);
				} catch (InvalidMidiDataException e) {
					throw new RuntimeException(e);
				}

				AmpConfig.setInterfaceValues(this, config);
			});
		});

		autoFlattenEQToggleButton.setOnAction(event -> {
			// if enabled, flatten the EQ
			if (autoFlattenEQToggleButton.isSelected()) {
				CODEInterfaceApplication.INTERFACE.setBass(5);
				CODEInterfaceApplication.INTERFACE.setMiddle(5);
				CODEInterfaceApplication.INTERFACE.setTreble(5);

				bassSlider.setValue(5);
				middleSlider.setValue(5);
				trebleSlider.setValue(5);
			}
		});

		aboutMenuItem.setOnAction(event -> {
			FXMLLoader fxmlLoader = new FXMLLoader(AboutDialogController.class.getResource("about-view.fxml"));
			try {
				Scene scene = new Scene(fxmlLoader.load(), 600, 400);
				Stage stage = new Stage();
				stage.setTitle("About Marshall CODE Interface");
				stage.setScene(scene);
				stage.show();

				AboutDialogController aboutDialogController = fxmlLoader.getController();
				aboutDialogController.setHostServices(hostServices);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		openTunerMenuItem.setOnAction(event -> {
			CODEInterfaceApplication.INTERFACE.toggleTuner(true);
			TuningDialogController.openDialog(CODEInterfaceApplication.INTERFACE::setTuningDialogController);
		});
	}

	/**
	 * Set a slider and text field to be linked together. This also enforces float values to only have one decimal place.
	 * @param slider the slider
	 * @param textField the text field
	 * @param consumer the consumer which will accept the new value
	 * @param clampToInt whether to clamp the value to an integer
	 */
	private void setupSliderAndTextField(Slider slider, TextField textField, Consumer<Float> consumer, boolean clampToInt) {
		slider.valueProperty().addListener((observable, oldValue, newValue) -> {
			float value = clampToInt ? Math.round(newValue.floatValue()) : (float) Math.round(newValue.floatValue() * 10) / 10;
			if (clampToInt) {
				textField.setText(String.valueOf((int) value));
			} else {
				textField.setText(String.valueOf(value));
			}

			consumer.accept(value);
		});

		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				float value = Float.parseFloat(newValue);
				slider.setValue(clampToInt ? Math.round(value) : value);
			} catch (NumberFormatException e) {
				textField.setText(String.valueOf(slider.getValue()));
			}
		});
	}

	/**
	 * Set a slider and text field to be linked together. This also enforces float values to only have one decimal place.
	 * @param slider the slider
	 * @param textField the text field
	 * @param consumer the consumer which will accept the new value
	 * @param clampToInt whether to clamp the value to an integer
	 */
	private void setupSliderAndTextField(Slider slider, TextField textField, BiConsumer<Float, Integer> consumer, boolean clampToInt) {
		slider.valueProperty().addListener((observable, oldValue, newValue) -> {
			float value = clampToInt ? Math.round(newValue.floatValue()) : (float) Math.round(newValue.floatValue() * 10) / 10;
			if (clampToInt) {
				textField.setText(String.valueOf((int) value));
			} else {
				textField.setText(String.valueOf(value));
			}

			int selectedPreFX = preFXTabPane.getSelectionModel().getSelectedIndex();
			consumer.accept(value, selectedPreFX);
		});

		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				float value = Float.parseFloat(newValue);
				slider.setValue(clampToInt ? Math.round(value) : value);
			} catch (NumberFormatException e) {
				textField.setText(String.valueOf(slider.getValue()));
			}
		});
	}
}