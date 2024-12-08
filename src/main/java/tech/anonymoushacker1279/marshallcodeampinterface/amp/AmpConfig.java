package tech.anonymoushacker1279.marshallcodeampinterface.amp;

import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceController;
import tech.anonymoushacker1279.marshallcodeampinterface.midi.AmpMIDIInterface;

import java.util.Arrays;

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

	private AmpConfig() {}

	/**
	 * Create a new AmpConfig instance based on the current amp settings.
	 * @param ampMIDIInterface the AmpMIDIInterface instance
	 * @return a new AmpConfig instance
	 */
	public static AmpConfig create(AmpMIDIInterface ampMIDIInterface) {
		return create(ampMIDIInterface.getAmpConfig());

	}

	/**
	 * Create a new AmpConfig instance based on the provided preset number.
	 * @param ampMIDIInterface the AmpMIDIInterface instance
	 * @param presetNumber the preset number
	 * @return a new AmpConfig instance
	 */
	public static AmpConfig create(AmpMIDIInterface ampMIDIInterface, int presetNumber) {
		return create(ampMIDIInterface.getAmpConfig(presetNumber));
	}

	/**
	 * Create a new AmpConfig instance based on the provided sysex data.
	 * Use {@link AmpMIDIInterface#getAmpConfig()} to get the current amp configuration.
	 * @param sysexData a byte array of sysex data
	 * @return a new AmpConfig instance
	 */
	private static AmpConfig create(byte[] sysexData) {
		// Remove first and last status bytes
		sysexData = Arrays.copyOfRange(sysexData, 1, sysexData.length - 1);
		AmpConfig config = new AmpConfig();

		// Bytes 9-27 are the preset name
		config.presetName = new String(sysexData, 9, 18);
		config.presetNumber = sysexData[8];
		config.ampEnabled = sysexData[39] == 1;
		config.ampType = sysexData[40];
		config.gain = sysexData[28];
		config.volume = sysexData[32];
		config.gate = sysexData[41];
		config.bass = sysexData[29];
		config.middle = sysexData[30];
		config.treble = sysexData[31];
		config.powerEnabled = sysexData[61] == 1;
		config.powerType = sysexData[62];
		config.presence = sysexData[66];
		config.resonance = sysexData[65];
		config.cabEnabled = sysexData[63] == 1;
		config.cabType = sysexData[64];
		config.preFXEnabled = sysexData[33] == 1;
		config.preFXType = sysexData[34];
		config.preFXParameter1 = sysexData[35];
		config.preFXParameter2 = sysexData[36];
		config.preFXParameter3 = sysexData[37];
		config.preFXParameter4 = sysexData[38];
		config.modulationEnabled = sysexData[42] == 1;
		config.modulationType = sysexData[43];
		config.modulationParameter1 = sysexData[44];
		config.modulationParameter2 = sysexData[45];
		config.modulationParameter3 = sysexData[46];
		config.modulationParameter4 = sysexData[47];
		config.delayEnabled = sysexData[48] == 1;
		config.delayType = sysexData[49];
		config.delayParameter1 = (sysexData[50] * 128) + sysexData[51]; // 2 bytes, MSB & LSB
		config.delayParameter2 = sysexData[52];
		config.delayParameter3 = sysexData[53];
		config.delayParameter4 = sysexData[54];
		config.reverbEnabled = sysexData[55] == 1;
		config.reverbType = sysexData[56];
		config.reverbParameter1 = sysexData[57];
		config.reverbParameter2 = sysexData[58];
		config.reverbParameter3 = sysexData[59];
		config.reverbParameter4 = sysexData[60];

		return config;
	}

	public static void setInterfaceValues(CODEInterfaceController controller, AmpConfig config) {
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
				controller.tremoloSkewSlider.setValue(config.modulationParameter4  - 50);
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
}