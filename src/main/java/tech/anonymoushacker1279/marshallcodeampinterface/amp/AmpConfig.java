package tech.anonymoushacker1279.marshallcodeampinterface.amp;

import com.google.gson.JsonObject;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;
import tech.anonymoushacker1279.marshallcodeampinterface.controller.CODEInterfaceController;
import tech.anonymoushacker1279.marshallcodeampinterface.midi.AmpMIDIInterface;

import java.util.ArrayList;
import java.util.List;

public class AmpConfig {

	public String presetName;
	public int presetNumber;
	public boolean ampEnabled;
	public int ampType;
	public int gain;
	public int volume;
	public int gate;
	public int bass;
	public int middle;
	public int treble;
	public boolean powerEnabled;
	public int powerType;
	public int presence;
	public int resonance;
	public boolean cabEnabled;
	public int cabType;
	public boolean preFXEnabled;
	public int preFXType;
	public int preFXParameter1;
	public int preFXParameter2;
	public int preFXParameter3;
	public int preFXParameter4;
	public boolean modulationEnabled;
	public int modulationType;
	public int modulationParameter1;
	public int modulationParameter2;
	public int modulationParameter3;
	public int modulationParameter4;
	public boolean delayEnabled;
	public int delayType;
	public int delayParameter1;
	public int delayParameter2;
	public int delayParameter3;
	public int delayParameter4;
	public boolean reverbEnabled;
	public int reverbType;
	public int reverbParameter1;
	public int reverbParameter2;
	public int reverbParameter3;
	public int reverbParameter4;

	private static int DELAY_TIME_MSB = -1;

	private AmpConfig() {
	}

	/**
	 * Create an empty AmpConfig instance. This should only be used for initialization.
	 *
	 * @return an empty AmpConfig instance
	 */
	public static AmpConfig empty() {
		return new AmpConfig();
	}

	/**
	 * Create a new AmpConfig instance based on the current amp settings.
	 *
	 * @param ampMIDIInterface the AmpMIDIInterface instance
	 * @return a new AmpConfig instance
	 */
	public static AmpConfig create(AmpMIDIInterface ampMIDIInterface) {
		return create(ampMIDIInterface.getAmpConfig());

	}

	/**
	 * Create a new AmpConfig instance based on the provided preset number.
	 *
	 * @param ampMIDIInterface the AmpMIDIInterface instance
	 * @param presetNumber     the preset number
	 * @return a new AmpConfig instance
	 */
	public static AmpConfig create(AmpMIDIInterface ampMIDIInterface, int presetNumber) {
		return create(ampMIDIInterface.getAmpConfig(presetNumber));
	}

	/**
	 * Create a new AmpConfig instance based on the provided sysex data. Use {@link AmpMIDIInterface#getAmpConfig()} to
	 * get the current amp configuration.
	 *
	 * @param sysexData a byte array of sysex data
	 * @return a new AmpConfig instance
	 */
	private static AmpConfig create(byte[] sysexData) {
		CODEInterfaceApplication.LOGGER.debug("Creating a new configuration instance from sysex data");

		AmpConfig config = new AmpConfig();

		config.presetName = new String(sysexData, 10, 19).trim();
		config.presetNumber = sysexData[9];

		// Preamp settings
		config.gain = sysexData[29];
		config.bass = sysexData[30];
		config.middle = sysexData[31];
		config.treble = sysexData[32];
		config.volume = sysexData[33];

		// FX pedal settings
		config.preFXEnabled = sysexData[34] == 1;
		config.preFXType = sysexData[35];
		config.preFXParameter1 = sysexData[36];
		config.preFXParameter2 = sysexData[37];
		config.preFXParameter3 = sysexData[38];
		config.preFXParameter4 = sysexData[39];

		// Amp settings
		config.ampEnabled = sysexData[40] == 1;
		config.ampType = sysexData[41];
		config.gate = sysexData[42];

		// Modulation settings
		config.modulationEnabled = sysexData[43] == 1;
		config.modulationType = sysexData[44];
		config.modulationParameter1 = sysexData[45];
		config.modulationParameter2 = sysexData[46];
		config.modulationParameter3 = sysexData[47];
		config.modulationParameter4 = sysexData[48];

		// Delay settings
		config.delayEnabled = sysexData[49] == 1;
		config.delayType = sysexData[50];
		config.delayParameter1 = (sysexData[51] * 128) + sysexData[52]; // 2 bytes, MSB & LSB
		config.delayParameter2 = sysexData[53];
		config.delayParameter3 = sysexData[54];
		config.delayParameter4 = sysexData[55];

		// Reverb settings
		config.reverbEnabled = sysexData[56] == 1;
		config.reverbType = sysexData[57];
		config.reverbParameter1 = sysexData[58];
		config.reverbParameter2 = sysexData[59];
		config.reverbParameter3 = sysexData[60];
		config.reverbParameter4 = sysexData[61];

		// Power amp settings
		config.powerEnabled = sysexData[62] == 1;
		config.powerType = sysexData[63];

		// Cab settings
		config.cabEnabled = sysexData[64] == 1;
		config.cabType = sysexData[65];

		// More power amp settings
		config.resonance = sysexData[66];
		config.presence = sysexData[67];

		return config;
	}

	/**
	 * Create a new AmpConfig instance based on the provided JSON object. Used for loading presets from files.
	 *
	 * @param jsonObject the JSON object
	 * @return a new AmpConfig instance
	 */
	public static AmpConfig create(JsonObject jsonObject) {
		CODEInterfaceApplication.LOGGER.debug("Creating a new configuration instance from a JSON object");

		AmpConfig config = new AmpConfig();
		config.presetName = "Local File Preset";
		config.presetNumber = 0;
		config.ampEnabled = jsonObject.get("ampEnabled").getAsBoolean();
		config.ampType = jsonObject.get("ampType").getAsInt();
		config.gain = jsonObject.get("gain").getAsInt();
		config.volume = jsonObject.get("volume").getAsInt();
		config.gate = jsonObject.get("gate").getAsInt();
		config.bass = jsonObject.get("bass").getAsInt();
		config.middle = jsonObject.get("middle").getAsInt();
		config.treble = jsonObject.get("treble").getAsInt();
		config.powerEnabled = jsonObject.get("powerEnabled").getAsBoolean();
		config.powerType = jsonObject.get("powerType").getAsInt();
		config.presence = jsonObject.get("presence").getAsInt();
		config.resonance = jsonObject.get("resonance").getAsInt();
		config.cabEnabled = jsonObject.get("cabEnabled").getAsBoolean();
		config.cabType = jsonObject.get("cabType").getAsInt();
		config.preFXEnabled = jsonObject.get("preFXEnabled").getAsBoolean();
		config.preFXType = jsonObject.get("preFXType").getAsInt();
		config.preFXParameter1 = jsonObject.get("preFXParameter1").getAsInt();
		config.preFXParameter2 = jsonObject.get("preFXParameter2").getAsInt();
		config.preFXParameter3 = jsonObject.get("preFXParameter3").getAsInt();
		config.preFXParameter4 = jsonObject.get("preFXParameter4").getAsInt();
		config.modulationEnabled = jsonObject.get("modulationEnabled").getAsBoolean();
		config.modulationType = jsonObject.get("modulationType").getAsInt();
		config.modulationParameter1 = jsonObject.get("modulationParameter1").getAsInt();
		config.modulationParameter2 = jsonObject.get("modulationParameter2").getAsInt();
		config.modulationParameter3 = jsonObject.get("modulationParameter3").getAsInt();
		config.modulationParameter4 = jsonObject.get("modulationParameter4").getAsInt();
		config.delayEnabled = jsonObject.get("delayEnabled").getAsBoolean();
		config.delayType = jsonObject.get("delayType").getAsInt();
		config.delayParameter1 = jsonObject.get("delayParameter1").getAsInt();
		config.delayParameter2 = jsonObject.get("delayParameter2").getAsInt();
		config.delayParameter3 = jsonObject.get("delayParameter3").getAsInt();
		config.delayParameter4 = jsonObject.get("delayParameter4").getAsInt();
		config.reverbEnabled = jsonObject.get("reverbEnabled").getAsBoolean();
		config.reverbType = jsonObject.get("reverbType").getAsInt();
		config.reverbParameter1 = jsonObject.get("reverbParameter1").getAsInt();
		config.reverbParameter2 = jsonObject.get("reverbParameter2").getAsInt();
		config.reverbParameter3 = jsonObject.get("reverbParameter3").getAsInt();
		config.reverbParameter4 = jsonObject.get("reverbParameter4").getAsInt();

		return config;
	}

	public static void setInterfaceValues(CODEInterfaceController controller, AmpConfig config) {
		CODEInterfaceApplication.LOGGER.debug("Setting interface values from a configuration object");

		CODEInterfaceApplication.DEFAULT_CONFIG = config;
		controller.ignorePresetChange = true;

		controller.presetNumberTextField.setText(String.valueOf(config.presetNumber));
		controller.presetNameTextField.setText(config.presetName);
		controller.presetListView.getSelectionModel().select(config.presetNumber);
		controller.ampToggleButton.setSelected(config.ampEnabled);
		controller.ampListView.getSelectionModel().select(config.ampType);
		controller.gainSlider.setValue(config.gain / 10f);
		controller.volumeSlider.setValue(config.volume / 10f);
		controller.gateSlider.setValue(config.gate / 10f);
		controller.bassSlider.setValue(config.bass / 10f);
		controller.middleSlider.setValue(config.middle / 10f);
		controller.trebleSlider.setValue(config.treble / 10f);
		controller.powerToggleButton.setSelected(config.powerEnabled);
		controller.powerListView.getSelectionModel().select(config.powerType);
		controller.presenceSlider.setValue(config.presence / 10f);
		controller.resonanceSlider.setValue(config.resonance / 10f);
		controller.cabToggleButton.setSelected(config.cabEnabled);
		controller.cabListView.getSelectionModel().select(config.cabType);

		controller.preFXToggleButton.setSelected(config.preFXEnabled);
		controller.preFXTabPane.getSelectionModel().select(config.preFXType);
		switch (config.preFXType) {
			case 0 -> {
				controller.compressorToneSlider.setValue(config.preFXParameter1 / 10f);
				controller.compressorRatioSlider.setValue(config.preFXParameter2 / 10f);
				controller.compressorCompSlider.setValue(config.preFXParameter3 / 10f);
				controller.compressorLevelSlider.setValue(config.preFXParameter4 / 10f);
			}
			case 1 -> {
				controller.distortionModeListView.getSelectionModel().select(config.preFXParameter1);
				controller.distortionDriveSlider.setValue(config.preFXParameter2 / 10f);
				controller.distortionToneSlider.setValue(config.preFXParameter3 / 10f);
				controller.distortionLevelSlider.setValue(config.preFXParameter4 / 10f);
			}
			case 2 -> {
				controller.autoWahModeListView.getSelectionModel().select(config.preFXParameter1);
				controller.autoWahFreqSlider.setValue(config.preFXParameter2 / 10f);
				controller.autoWahSensitivitySlider.setValue(config.preFXParameter3 / 10f);
				controller.autoWahResSlider.setValue(config.preFXParameter4 / 10f);
			}
			case 3 -> {
				controller.pitchShifterSemitoneSlider.setValue(config.preFXParameter1 - 12);
				controller.pitchShifterFineSlider.setValue(config.preFXParameter2 / 10f);
				controller.pitchShifterRegenSlider.setValue(config.preFXParameter3 / 10f);
				controller.pitchShifterMixSlider.setValue(config.preFXParameter4 / 10f);
			}
		}

		controller.modulationToggleButton.setSelected(config.modulationEnabled);
		controller.modulationTabPane.getSelectionModel().select(config.modulationType);
		switch (config.modulationType) {
			case 0 -> {
				controller.chorusModeListView.getSelectionModel().select(config.modulationParameter1);
				controller.chorusSpeedSlider.setValue(config.modulationParameter2 / 10f);
				controller.chorusDepthSlider.setValue(config.modulationParameter3 / 10f);
				controller.chorusDepthSlider.setValue(config.modulationParameter4 / 10f);
			}
			case 1 -> {
				controller.flangerModeListView.getSelectionModel().select(config.modulationParameter1);
				controller.flangerSpeedSlider.setValue(config.modulationParameter2 / 10f);
				controller.flangerDepthSlider.setValue(config.modulationParameter3 / 10f);
				controller.flangerRegenSlider.setValue(config.modulationParameter4 / 10f);
			}
			case 2 -> {
				controller.phaserModeListView.getSelectionModel().select(config.modulationParameter1);
				controller.phaserSpeedSlider.setValue(config.modulationParameter2 / 10f);
				controller.phaserDepthSlider.setValue(config.modulationParameter3 / 10f);
				controller.phaserRegenSlider.setValue(config.modulationParameter4 / 10f);
			}
			case 3 -> {
				controller.tremoloModeListView.getSelectionModel().select(config.modulationParameter1);
				controller.tremoloSpeedSlider.setValue(config.modulationParameter2 / 10f);
				controller.tremoloDepthSlider.setValue(config.modulationParameter3 / 10f);
				controller.tremoloSkewSlider.setValue(config.modulationParameter4 - 50);
			}
		}

		controller.delayToggleButton.setSelected(config.delayEnabled);
		controller.delayTabPane.getSelectionModel().select(config.delayType);
		switch (config.delayType) {
			case 0 -> {
				controller.studioTimeSlider.setValue(config.delayParameter1);
				controller.studioFeedbackSlider.setValue(config.delayParameter2 / 10f);
				controller.studioFreqSlider.setValue(config.delayParameter3 / 10f);
				controller.studioLevelSlider.setValue(config.delayParameter4 / 10f);
			}
			case 1 -> {
				controller.vintageTimeSlider.setValue(config.delayParameter1);
				controller.vintageAgeSlider.setValue(config.delayParameter2 / 10f);
				controller.vintageFreqSlider.setValue(config.delayParameter3 / 10f);
				controller.vintageLevelSlider.setValue(config.delayParameter4 / 10f);
			}
			case 2 -> {
				controller.multiTimeSlider.setValue(config.delayParameter1);
				controller.multiFeedbackSlider.setValue(config.delayParameter2 / 10f);
				controller.multiTapPatternListView.getSelectionModel().select(config.delayParameter3);
				controller.multiLevelSlider.setValue(config.delayParameter4 / 10f);
			}
			case 3 -> {
				controller.reverseTimeSlider.setValue(config.delayParameter1);
				controller.reverseFeedbackSlider.setValue(config.delayParameter2 / 10f);
				controller.reverseFreqSlider.setValue(config.delayParameter3 / 10f);
				controller.reverseLevelSlider.setValue(config.delayParameter4 / 10f);
			}
		}

		controller.reverbToggleButton.setSelected(config.reverbEnabled);
		controller.reverbTabPane.getSelectionModel().select(config.reverbType);
		switch (config.reverbType) {
			case 0 -> {
				controller.roomDecaySlider.setValue(config.reverbParameter1 / 10f);
				controller.roomPreDelaySlider.setValue(config.reverbParameter2 / 10f);
				controller.roomToneSlider.setValue(config.reverbParameter3 / 10f);
				controller.roomLevelSlider.setValue(config.reverbParameter4 / 10f);
			}
			case 1 -> {
				controller.hallDecaySlider.setValue(config.reverbParameter1 / 10f);
				controller.hallPreDelaySlider.setValue(config.reverbParameter2 / 10f);
				controller.hallToneSlider.setValue(config.reverbParameter3 / 10f);
				controller.hallLevelSlider.setValue(config.reverbParameter4 / 10f);
			}
			case 2 -> {
				controller.springDecaySlider.setValue(config.reverbParameter1 / 10f);
				controller.springPreDelaySlider.setValue(config.reverbParameter2 / 10f);
				controller.springToneSlider.setValue(config.reverbParameter3 / 10f);
				controller.springLevelSlider.setValue(config.reverbParameter4 / 10f);
			}
			case 3 -> {
				controller.stadiumDecaySlider.setValue(config.reverbParameter1 / 10f);
				controller.stadiumPreDelaySlider.setValue(config.reverbParameter2 / 10f);
				controller.stadiumToneSlider.setValue(config.reverbParameter3 / 10f);
				controller.stadiumLevelSlider.setValue(config.reverbParameter4 / 10f);
			}
		}

		if (controller.autoFlattenEQToggleButton.isSelected()) {
			CODEInterfaceApplication.INTERFACE.setBass(5);
			CODEInterfaceApplication.INTERFACE.setMiddle(5);
			CODEInterfaceApplication.INTERFACE.setTreble(5);

			controller.bassSlider.setValue(5);
			controller.middleSlider.setValue(5);
			controller.trebleSlider.setValue(5);
		}

		controller.ignorePresetChange = false;
	}

	public static void updateInterface(CODEInterfaceController controller, AmpConfig config, int controlID, int value) {
		switch (controlID) {
			case 81 -> {
				config.ampEnabled = value == 1;
				controller.ampToggleButton.setSelected(value == 1);
			}
			case 82 -> {
				config.ampType = value;
				controller.ampListView.getSelectionModel().select(value);
			}
			case 70 -> {
				config.gain = value;
				controller.gainSlider.setValue(value / 10f);
			}
			case 74 -> {
				config.volume = value;
				controller.volumeSlider.setValue(value / 10f);
			}
			case 83 -> {
				config.gate = value;
				controller.gateSlider.setValue(value / 10f);
			}
			case 71 -> {
				config.bass = value;
				controller.bassSlider.setValue(value / 10f);
			}
			case 72 -> {
				config.middle = value;
				controller.middleSlider.setValue(value / 10f);
			}
			case 73 -> {
				config.treble = value;
				controller.trebleSlider.setValue(value / 10f);
			}
			case 114 -> {
				config.powerEnabled = value == 1;
				controller.powerToggleButton.setSelected(value == 1);
			}
			case 115 -> {
				config.powerType = value;
				controller.powerListView.getSelectionModel().select(value);
			}
			case 118 -> {
				config.presence = value;
				controller.presenceSlider.setValue(value / 10f);
			}
			case 119 -> {
				config.resonance = value;
				controller.resonanceSlider.setValue(value / 10f);
			}
			case 116 -> {
				config.cabEnabled = value == 1;
				controller.cabToggleButton.setSelected(value == 1);
			}
			case 117 -> {
				config.cabType = value;
				controller.cabListView.getSelectionModel().select(value);
			}
			case 75 -> {
				config.preFXEnabled = value == 1;
				controller.preFXToggleButton.setSelected(value == 1);
			}
			case 76 -> {
				config.preFXType = value;
				controller.preFXTabPane.getSelectionModel().select(value);
			}
			case 77 -> {
				config.preFXParameter1 = value;
				switch (config.preFXType) {
					case 0 -> controller.compressorToneSlider.setValue(value / 10f);
					case 1 -> controller.distortionModeListView.getSelectionModel().select(value);
					case 2 -> controller.autoWahModeListView.getSelectionModel().select(value);
					case 3 -> controller.pitchShifterSemitoneSlider.setValue(value - 12);
				}
			}
			case 78 -> {
				config.preFXParameter2 = value;
				switch (config.preFXType) {
					case 0 -> controller.compressorRatioSlider.setValue(value / 10f);
					case 1 -> controller.distortionDriveSlider.setValue(value / 10f);
					case 2 -> controller.autoWahFreqSlider.setValue(value / 10f);
					case 3 -> controller.pitchShifterFineSlider.setValue(value / 10f);
				}
			}
			case 79 -> {
				config.preFXParameter3 = value;
				switch (config.preFXType) {
					case 0 -> controller.compressorCompSlider.setValue(value / 10f);
					case 1 -> controller.distortionToneSlider.setValue(value / 10f);
					case 2 -> controller.autoWahSensitivitySlider.setValue(value / 10f);
					case 3 -> controller.pitchShifterRegenSlider.setValue(value / 10f);
				}
			}
			case 80 -> {
				config.preFXParameter4 = value;
				switch (config.preFXType) {
					case 0 -> controller.compressorLevelSlider.setValue(value / 10f);
					case 1 -> controller.distortionLevelSlider.setValue(value / 10f);
					case 2 -> controller.autoWahResSlider.setValue(value / 10f);
					case 3 -> controller.pitchShifterMixSlider.setValue(value / 10f);
				}
			}
			case 85 -> {
				config.modulationEnabled = value == 1;
				controller.modulationToggleButton.setSelected(value == 1);
			}
			case 86 -> {
				config.modulationType = value;
				controller.modulationTabPane.getSelectionModel().select(value);
			}
			case 87 -> {
				config.modulationParameter1 = value;
				switch (config.modulationType) {
					case 0 -> controller.chorusModeListView.getSelectionModel().select(value);
					case 1 -> controller.flangerModeListView.getSelectionModel().select(value);
					case 2 -> controller.phaserModeListView.getSelectionModel().select(value);
					case 3 -> controller.tremoloModeListView.getSelectionModel().select(value);
				}
			}
			case 89 -> {
				config.modulationParameter2 = value;
				switch (config.modulationType) {
					case 0 -> controller.chorusSpeedSlider.setValue(value / 10f);
					case 1 -> controller.flangerSpeedSlider.setValue(value / 10f);
					case 2 -> controller.phaserSpeedSlider.setValue(value / 10f);
					case 3 -> controller.tremoloSpeedSlider.setValue(value / 10f);
				}
			}
			case 90 -> {
				config.modulationParameter3 = value;
				switch (config.modulationType) {
					case 0 -> controller.chorusDepthSlider.setValue(value / 10f);
					case 1 -> controller.flangerDepthSlider.setValue(value / 10f);
					case 2 -> controller.phaserDepthSlider.setValue(value / 10f);
					case 3 -> controller.tremoloDepthSlider.setValue(value / 10f);
				}
			}
			case 102 -> {
				config.modulationParameter4 = value;
				switch (config.modulationType) {
					case 0 -> controller.chorusToneSlider.setValue(value / 10f);
					case 1 -> controller.flangerRegenSlider.setValue(value / 10f);
					case 2 -> controller.phaserRegenSlider.setValue(value / 10f);
					case 3 -> controller.tremoloSkewSlider.setValue(value - 50);
				}
			}
			case 103 -> {
				config.delayEnabled = value == 1;
				controller.delayToggleButton.setSelected(value == 1);
			}
			case 104 -> {
				config.delayType = value;
				controller.delayTabPane.getSelectionModel().select(value);
			}
			case 31 -> DELAY_TIME_MSB = value;
			case 63 -> {
				int delayTime = (DELAY_TIME_MSB * 128) + value;
				config.delayParameter1 = delayTime;
				switch (config.delayType) {
					case 0 -> controller.studioTimeSlider.setValue(delayTime);
					case 1 -> controller.vintageTimeSlider.setValue(delayTime);
					case 2 -> controller.multiTimeSlider.setValue(delayTime);
					case 3 -> controller.reverseTimeSlider.setValue(delayTime);
				}
			}
			case 105 -> {
				config.delayParameter2 = value;
				switch (config.delayType) {
					case 0 -> controller.studioFeedbackSlider.setValue(value / 10f);
					case 1 -> controller.vintageAgeSlider.setValue(value / 10f);
					case 2 -> controller.multiFeedbackSlider.setValue(value / 10f);
					case 3 -> controller.reverseFeedbackSlider.setValue(value / 10f);
				}
			}
			case 106 -> {
				config.delayParameter3 = value;
				switch (config.delayType) {
					case 0 -> controller.studioFreqSlider.setValue(value / 10f);
					case 1 -> controller.vintageFreqSlider.setValue(value / 10f);
					case 2 -> controller.multiTapPatternListView.getSelectionModel().select(value);
					case 3 -> controller.reverseFreqSlider.setValue(value / 10f);
				}
			}
			case 107 -> {
				config.delayParameter4 = value;
				switch (config.delayType) {
					case 0 -> controller.studioLevelSlider.setValue(value / 10f);
					case 1 -> controller.vintageLevelSlider.setValue(value / 10f);
					case 2 -> controller.multiLevelSlider.setValue(value / 10f);
					case 3 -> controller.reverseLevelSlider.setValue(value / 10f);
				}
			}
			case 108 -> {
				config.reverbEnabled = value == 1;
				controller.reverbToggleButton.setSelected(value == 1);
			}
			case 109 -> {
				config.reverbType = value;
				controller.reverbTabPane.getSelectionModel().select(value);
			}
			case 110 -> {
				config.reverbParameter1 = value;
				switch (config.reverbType) {
					case 0 -> controller.roomDecaySlider.setValue(value / 10f);
					case 1 -> controller.hallDecaySlider.setValue(value / 10f);
					case 2 -> controller.springDecaySlider.setValue(value / 10f);
					case 3 -> controller.stadiumDecaySlider.setValue(value / 10f);
				}
			}
			case 111 -> {
				config.reverbParameter2 = value;
				switch (config.reverbType) {
					case 0 -> controller.roomPreDelaySlider.setValue(value / 10f);
					case 1 -> controller.hallPreDelaySlider.setValue(value / 10f);
					case 2 -> controller.springPreDelaySlider.setValue(value / 10f);
					case 3 -> controller.stadiumPreDelaySlider.setValue(value / 10f);
				}
			}
			case 112 -> {
				config.reverbParameter3 = value;
				switch (config.reverbType) {
					case 0 -> controller.roomToneSlider.setValue(value / 10f);
					case 1 -> controller.hallToneSlider.setValue(value / 10f);
					case 2 -> controller.springToneSlider.setValue(value / 10f);
					case 3 -> controller.stadiumToneSlider.setValue(value / 10f);
				}
			}
			case 113 -> {
				config.reverbParameter4 = value;
				switch (config.reverbType) {
					case 0 -> controller.roomLevelSlider.setValue(value / 10f);
					case 1 -> controller.hallLevelSlider.setValue(value / 10f);
					case 2 -> controller.springLevelSlider.setValue(value / 10f);
					case 3 -> controller.stadiumLevelSlider.setValue(value / 10f);
				}
			}
		}
	}

	public static AmpConfig getCurrentConfig(CODEInterfaceController controller) {
		AmpConfig config = new AmpConfig();
		config.presetName = controller.presetNameTextField.getText();
		config.presetNumber = Integer.parseInt(controller.presetNumberTextField.getText());
		config.ampEnabled = controller.ampToggleButton.isSelected();
		config.ampType = controller.ampListView.getSelectionModel().getSelectedIndex();
		config.gain = (int) (controller.gainSlider.getValue() * 10);
		config.volume = (int) (controller.volumeSlider.getValue() * 10);
		config.gate = (int) (controller.gateSlider.getValue() * 10);
		config.bass = (int) (controller.bassSlider.getValue() * 10);
		config.middle = (int) (controller.middleSlider.getValue() * 10);
		config.treble = (int) (controller.trebleSlider.getValue() * 10);
		config.powerEnabled = controller.powerToggleButton.isSelected();
		config.powerType = controller.powerListView.getSelectionModel().getSelectedIndex();
		config.presence = (int) (controller.presenceSlider.getValue() * 10);
		config.resonance = (int) (controller.resonanceSlider.getValue() * 10);
		config.cabEnabled = controller.cabToggleButton.isSelected();
		config.cabType = controller.cabListView.getSelectionModel().getSelectedIndex();
		config.preFXEnabled = controller.preFXToggleButton.isSelected();
		config.preFXType = controller.preFXTabPane.getSelectionModel().getSelectedIndex();
		switch (config.preFXType) {
			case 0 -> {
				config.preFXParameter1 = (int) (controller.compressorToneSlider.getValue() * 10);
				config.preFXParameter2 = (int) (controller.compressorRatioSlider.getValue() * 10);
				config.preFXParameter3 = (int) (controller.compressorCompSlider.getValue() * 10);
				config.preFXParameter4 = (int) (controller.compressorLevelSlider.getValue() * 10);
			}
			case 1 -> {
				config.preFXParameter1 = controller.distortionModeListView.getSelectionModel().getSelectedIndex();
				config.preFXParameter2 = (int) (controller.distortionDriveSlider.getValue() * 10);
				config.preFXParameter3 = (int) (controller.distortionToneSlider.getValue() * 10);
				config.preFXParameter4 = (int) (controller.distortionLevelSlider.getValue() * 10);
			}
			case 2 -> {
				config.preFXParameter1 = controller.autoWahModeListView.getSelectionModel().getSelectedIndex();
				config.preFXParameter2 = (int) (controller.autoWahFreqSlider.getValue() * 10);
				config.preFXParameter3 = (int) (controller.autoWahSensitivitySlider.getValue() * 10);
				config.preFXParameter4 = (int) (controller.autoWahResSlider.getValue() * 10);
			}
			case 3 -> {
				config.preFXParameter1 = (int) (controller.pitchShifterSemitoneSlider.getValue() + 12);
				config.preFXParameter2 = (int) (controller.pitchShifterFineSlider.getValue() * 10);
				config.preFXParameter3 = (int) (controller.pitchShifterRegenSlider.getValue() * 10);
				config.preFXParameter4 = (int) (controller.pitchShifterMixSlider.getValue() * 10);
			}
		}
		config.modulationEnabled = controller.modulationToggleButton.isSelected();
		config.modulationType = controller.modulationTabPane.getSelectionModel().getSelectedIndex();
		switch (config.modulationType) {
			case 0 -> {
				config.modulationParameter1 = controller.chorusModeListView.getSelectionModel().getSelectedIndex();
				config.modulationParameter2 = (int) (controller.chorusSpeedSlider.getValue() * 10);
				config.modulationParameter3 = (int) (controller.chorusDepthSlider.getValue() * 10);
				config.modulationParameter4 = (int) (controller.chorusToneSlider.getValue() * 10);
			}
			case 1 -> {
				config.modulationParameter1 = controller.flangerModeListView.getSelectionModel().getSelectedIndex();
				config.modulationParameter2 = (int) (controller.flangerSpeedSlider.getValue() * 10);
				config.modulationParameter3 = (int) (controller.flangerDepthSlider.getValue() * 10);
				config.modulationParameter4 = (int) (controller.flangerRegenSlider.getValue() * 10);
			}
			case 2 -> {
				config.modulationParameter1 = controller.phaserModeListView.getSelectionModel().getSelectedIndex();
				config.modulationParameter2 = (int) (controller.phaserSpeedSlider.getValue() * 10);
				config.modulationParameter3 = (int) (controller.phaserDepthSlider.getValue() * 10);
				config.modulationParameter4 = (int) (controller.phaserRegenSlider.getValue() * 10);
			}
			case 3 -> {
				config.modulationParameter1 = controller.tremoloModeListView.getSelectionModel().getSelectedIndex();
				config.modulationParameter2 = (int) (controller.tremoloSpeedSlider.getValue() * 10);
				config.modulationParameter3 = (int) (controller.tremoloDepthSlider.getValue() * 10);
				config.modulationParameter4 = (int) (controller.tremoloSkewSlider.getValue() + 50);
			}
		}
		config.delayEnabled = controller.delayToggleButton.isSelected();
		config.delayType = controller.delayTabPane.getSelectionModel().getSelectedIndex();
		switch (config.delayType) {
			case 0 -> {
				config.delayParameter1 = (int) controller.studioTimeSlider.getValue();
				config.delayParameter2 = (int) (controller.studioFeedbackSlider.getValue() * 10);
				config.delayParameter3 = (int) (controller.studioFreqSlider.getValue() * 10);
				config.delayParameter4 = (int) (controller.studioLevelSlider.getValue() * 10);
			}
			case 1 -> {
				config.delayParameter1 = (int) controller.vintageTimeSlider.getValue();
				config.delayParameter2 = (int) (controller.vintageAgeSlider.getValue() * 10);
				config.delayParameter3 = (int) (controller.vintageFreqSlider.getValue() * 10);
				config.delayParameter4 = (int) (controller.vintageLevelSlider.getValue() * 10);
			}
			case 2 -> {
				config.delayParameter1 = (int) controller.multiTimeSlider.getValue();
				config.delayParameter2 = (int) (controller.multiFeedbackSlider.getValue() * 10);
				config.delayParameter3 = controller.multiTapPatternListView.getSelectionModel().getSelectedIndex();
				config.delayParameter4 = (int) (controller.multiLevelSlider.getValue() * 10);
			}
			case 3 -> {
				config.delayParameter1 = (int) controller.reverseTimeSlider.getValue();
				config.delayParameter2 = (int) (controller.reverseFeedbackSlider.getValue() * 10);
				config.delayParameter3 = (int) (controller.reverseFreqSlider.getValue() * 10);
				config.delayParameter4 = (int) (controller.reverseLevelSlider.getValue() * 10);
			}
		}
		config.reverbEnabled = controller.reverbToggleButton.isSelected();
		config.reverbType = controller.reverbTabPane.getSelectionModel().getSelectedIndex();
		switch (config.reverbType) {
			case 0 -> {
				config.reverbParameter1 = (int) (controller.roomDecaySlider.getValue() * 10);
				config.reverbParameter2 = (int) (controller.roomPreDelaySlider.getValue() * 10);
				config.reverbParameter3 = (int) (controller.roomToneSlider.getValue() * 10);
				config.reverbParameter4 = (int) (controller.roomLevelSlider.getValue() * 10);
			}
			case 1 -> {
				config.reverbParameter1 = (int) (controller.hallDecaySlider.getValue() * 10);
				config.reverbParameter2 = (int) (controller.hallPreDelaySlider.getValue() * 10);
				config.reverbParameter3 = (int) (controller.hallToneSlider.getValue() * 10);
				config.reverbParameter4 = (int) (controller.hallLevelSlider.getValue() * 10);
			}
			case 2 -> {
				config.reverbParameter1 = (int) (controller.springDecaySlider.getValue() * 10);
				config.reverbParameter2 = (int) (controller.springPreDelaySlider.getValue() * 10);
				config.reverbParameter3 = (int) (controller.springToneSlider.getValue() * 10);
				config.reverbParameter4 = (int) (controller.springLevelSlider.getValue() * 10);
			}
			case 3 -> {
				config.reverbParameter1 = (int) (controller.stadiumDecaySlider.getValue() * 10);
				config.reverbParameter2 = (int) (controller.stadiumPreDelaySlider.getValue() * 10);
				config.reverbParameter3 = (int) (controller.stadiumToneSlider.getValue() * 10);
				config.reverbParameter4 = (int) (controller.stadiumLevelSlider.getValue() * 10);
			}
		}

		return config;
	}

	public static JsonObject createJsonFromConfig(AmpConfig config) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("appVersion", CODEInterfaceApplication.APP_VERSION.toString());
		jsonObject.addProperty("ampEnabled", config.ampEnabled);
		jsonObject.addProperty("ampType", config.ampType);
		jsonObject.addProperty("gain", config.gain);
		jsonObject.addProperty("volume", config.volume);
		jsonObject.addProperty("gate", config.gate);
		jsonObject.addProperty("bass", config.bass);
		jsonObject.addProperty("middle", config.middle);
		jsonObject.addProperty("treble", config.treble);
		jsonObject.addProperty("powerEnabled", config.powerEnabled);
		jsonObject.addProperty("powerType", config.powerType);
		jsonObject.addProperty("presence", config.presence);
		jsonObject.addProperty("resonance", config.resonance);
		jsonObject.addProperty("cabEnabled", config.cabEnabled);
		jsonObject.addProperty("cabType", config.cabType);
		jsonObject.addProperty("preFXEnabled", config.preFXEnabled);
		jsonObject.addProperty("preFXType", config.preFXType);
		jsonObject.addProperty("preFXParameter1", config.preFXParameter1);
		jsonObject.addProperty("preFXParameter2", config.preFXParameter2);
		jsonObject.addProperty("preFXParameter3", config.preFXParameter3);
		jsonObject.addProperty("preFXParameter4", config.preFXParameter4);
		jsonObject.addProperty("modulationEnabled", config.modulationEnabled);
		jsonObject.addProperty("modulationType", config.modulationType);
		jsonObject.addProperty("modulationParameter1", config.modulationParameter1);
		jsonObject.addProperty("modulationParameter2", config.modulationParameter2);
		jsonObject.addProperty("modulationParameter3", config.modulationParameter3);
		jsonObject.addProperty("modulationParameter4", config.modulationParameter4);
		jsonObject.addProperty("delayEnabled", config.delayEnabled);
		jsonObject.addProperty("delayType", config.delayType);
		jsonObject.addProperty("delayParameter1", config.delayParameter1);
		jsonObject.addProperty("delayParameter2", config.delayParameter2);
		jsonObject.addProperty("delayParameter3", config.delayParameter3);
		jsonObject.addProperty("delayParameter4", config.delayParameter4);
		jsonObject.addProperty("reverbEnabled", config.reverbEnabled);
		jsonObject.addProperty("reverbType", config.reverbType);
		jsonObject.addProperty("reverbParameter1", config.reverbParameter1);
		jsonObject.addProperty("reverbParameter2", config.reverbParameter2);
		jsonObject.addProperty("reverbParameter3", config.reverbParameter3);
		jsonObject.addProperty("reverbParameter4", config.reverbParameter4);

		return jsonObject;
	}

	/**
	 * Create a sysex message from an AmpConfig object.
	 *
	 * @param config The AmpConfig object to create a sysex message from
	 * @return A byte array containing the sysex message
	 */
	public static byte[] createSysexFromConfig(AmpConfig config, int preset) {
		ArrayList<Integer> message = new ArrayList<>(List.of(0xF0, 0x00, 0x21, 0x15, 0x7F, 0x7F, 0x7F, 0x72, 0x02, preset));

		// First 18 bytes are the preset name
		byte[] nameBytes = config.presetName.getBytes();
		for (int i = 0; i < 18; i++) {
			// If the name is less than 18 characters, pad with spaces
			if (i < nameBytes.length) {
				message.add(nameBytes[i] & 0xFF);
			} else {
				message.add(0x20);
			}
		}

		// Byte 28 is a fixed value
		message.add(0x00);

		// Next 5 bytes are preamp settings
		message.add(config.gain);
		message.add(config.bass);
		message.add(config.middle);
		message.add(config.treble);
		message.add(config.volume);

		// Next 6 bytes are FX pedal settings
		message.add(config.preFXEnabled ? 1 : 0);
		message.add(config.preFXType);
		message.add(config.preFXParameter1);
		message.add(config.preFXParameter2);
		message.add(config.preFXParameter3);
		message.add(config.preFXParameter4);

		// Next 3 bytes are amp settings
		message.add(config.ampEnabled ? 1 : 0);
		message.add(config.ampType);
		message.add(config.gate);

		// Next 6 bytes are modulation settings
		message.add(config.modulationEnabled ? 1 : 0);
		message.add(config.modulationType);
		message.add(config.modulationParameter1);
		message.add(config.modulationParameter2);
		message.add(config.modulationParameter3);
		message.add(config.modulationParameter4);

		// Next 7 bytes are delay settings
		message.add(config.delayEnabled ? 1 : 0);
		message.add(config.delayType);
		message.add(config.delayParameter1 / 128); // MSB
		message.add(config.delayParameter1 % 128); // LSB
		message.add(config.delayParameter2);
		message.add(config.delayParameter3);
		message.add(config.delayParameter4);

		// Next 6 bytes are reverb settings
		message.add(config.reverbEnabled ? 1 : 0);
		message.add(config.reverbType);
		message.add(config.reverbParameter1);
		message.add(config.reverbParameter2);
		message.add(config.reverbParameter3);
		message.add(config.reverbParameter4);

		// Next 2 bytes are power amp settings
		message.add(config.powerEnabled ? 1 : 0);
		message.add(config.powerType);

		// Next 2 bytes are cab settings
		message.add(config.cabEnabled ? 1 : 0);
		message.add(config.cabType);

		// Next 2 bytes are more power amp settings (resonance and presence)
		message.add(config.resonance);
		message.add(config.presence);

		// Next 6 bytes are fixed values (3, 4, 1, 2, 3, 4)
		message.addAll(List.of(3, 4, 1, 2, 3, 4));

		// Finally, the sysex end byte
		message.add(0xF7);

		byte[] messageBytes = new byte[message.size()];
		for (int i = 0; i < message.size(); i++) {
			messageBytes[i] = message.get(i).byteValue();
		}

		return messageBytes;
	}
}