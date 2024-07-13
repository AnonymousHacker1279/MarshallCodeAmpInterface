import asyncio
import json
from typing import List

from PySide6.QtWidgets import QMainWindow, QFileDialog

from package.about_dialog import AboutDialog
from package.amp_config import AmpConfig
from package.amp_midi_interface import AmpMIDIInterface
from package.tuner_dialog import TunerDialog
from package.ui.main_window_ui import Ui_MainWindow


class AmpInterfaceWindow(QMainWindow):

	def __init__(self):
		super(AmpInterfaceWindow, self).__init__()
		self.ui = Ui_MainWindow()
		self.ui.setupUi(self)

		self.aboutDialog = AboutDialog()
		self.ui.actionAbout.triggered.connect(self.open_about_dialog)

		self.tunerDialog = TunerDialog(self)
		self.ui.actionTuner.triggered.connect(lambda _: self.open_tuner_dialog(True))

		self.ui.actionRefresh_Amp_Settings.triggered.connect(lambda _: self.setup_from_config())
		self.ui.actionLoad_from_File.triggered.connect(lambda _: self.open_preset_file())
		self.ui.actionSave_to_File.triggered.connect(lambda _: self.save_preset_file())

		self.interface = AmpMIDIInterface(self, self.ui)
		if self.interface.connected:
			self.ui.connectionStatusLabel.setText('Status: CONNECTED')
			self.ui.connectionStatusLabel.setStyleSheet('color: green')

		self.amp_config = AmpConfig()
		self.presets: List[AmpConfig] = []

		self.attach_signals()

		self.setup_from_config()
		self.setup_presets()

	def open_about_dialog(self) -> None:
		self.aboutDialog.show()

	def open_tuner_dialog(self, update_tuner: bool = False) -> None:
		self.tunerDialog.show()
		if update_tuner:
			self.interface.set_tuner_state(update_tuner)

	def close_tuner_dialog(self, update_tuner: bool = False) -> None:
		self.tunerDialog.close()
		if update_tuner:
			self.interface.set_tuner_state(update_tuner)

	def closeEvent(self, event) -> None:
		"""Close the MIDI port when the window is closed."""
		if self.interface.connected:
			self.interface.port.close()

		event.accept()

	def attach_signals(self) -> None:
		"""Attach signals to their respective update functions."""
		# Dials
		self.ui.gainDial.valueChanged.connect(self.interface.set_gain)
		self.ui.volumeDial.valueChanged.connect(self.interface.set_volume)
		self.ui.gateDial.valueChanged.connect(self.interface.set_gate_threshold)
		self.ui.bassDial.valueChanged.connect(self.interface.set_bass)
		self.ui.middleDial.valueChanged.connect(self.interface.set_middle)
		self.ui.trebleDial.valueChanged.connect(self.interface.set_treble)
		self.ui.presenceDial.valueChanged.connect(self.interface.set_presence)
		self.ui.resonanceDial.valueChanged.connect(self.interface.set_resonance)
		self.ui.compressorToneDial.valueChanged.connect(self.interface.set_pedal_p1)
		self.ui.compressorRatioDial.valueChanged.connect(self.interface.set_pedal_p2)
		self.ui.compressorCompressionDial.valueChanged.connect(self.interface.set_pedal_p3)
		self.ui.compressorLevelDial.valueChanged.connect(self.interface.set_pedal_p4)
		self.ui.distortionDriveDial.valueChanged.connect(self.interface.set_pedal_p2)
		self.ui.distortionToneDial.valueChanged.connect(self.interface.set_pedal_p3)
		self.ui.distortionLevelDial.valueChanged.connect(self.interface.set_pedal_p4)
		self.ui.autoWahFreqDial.valueChanged.connect(self.interface.set_pedal_p2)
		self.ui.autoWahSensitivityDial.valueChanged.connect(self.interface.set_pedal_p3)
		self.ui.autoWahResDial.valueChanged.connect(self.interface.set_pedal_p4)
		self.ui.pitchShifterFineDial.valueChanged.connect(self.interface.set_pedal_p2)
		self.ui.pitchShifterRegenDial.valueChanged.connect(self.interface.set_pedal_p3)
		self.ui.pitchShifterMixDial.valueChanged.connect(self.interface.set_pedal_p4)
		self.ui.chorusSpeedDial.valueChanged.connect(self.interface.set_modulation_p2)
		self.ui.chorusDepthDial.valueChanged.connect(self.interface.set_modulation_p3)
		self.ui.chorusToneDial.valueChanged.connect(self.interface.set_modulation_p4)
		self.ui.flangerSpeedDial.valueChanged.connect(self.interface.set_modulation_p2)
		self.ui.flangerDepthDial.valueChanged.connect(self.interface.set_modulation_p3)
		self.ui.flangerRegenDial.valueChanged.connect(self.interface.set_modulation_p4)
		self.ui.phaserSpeedDial.valueChanged.connect(self.interface.set_modulation_p2)
		self.ui.phaserDepthDial.valueChanged.connect(self.interface.set_modulation_p3)
		self.ui.phaserRegenDial.valueChanged.connect(self.interface.set_modulation_p4)
		self.ui.tremoloSpeedDial.valueChanged.connect(self.interface.set_modulation_p2)
		self.ui.tremoloDepthDial.valueChanged.connect(self.interface.set_modulation_p3)
		self.ui.tremoloSkewDial.valueChanged.connect(self.interface.set_modulation_p4)
		self.ui.studioTimeDial.valueChanged.connect(self.interface.set_delay_p1)
		self.ui.studioFeedbackDial.valueChanged.connect(self.interface.set_delay_p2)
		self.ui.studioFreqDial.valueChanged.connect(self.interface.set_delay_p3)
		self.ui.studioLevelDial.valueChanged.connect(self.interface.set_delay_p4)
		self.ui.vintageTimeDial.valueChanged.connect(self.interface.set_delay_p1)
		self.ui.vintageAgeDial.valueChanged.connect(self.interface.set_delay_p2)
		self.ui.vintageFreqDial.valueChanged.connect(self.interface.set_delay_p3)
		self.ui.vintageLevelDial.valueChanged.connect(self.interface.set_delay_p4)
		self.ui.multiTimeDial.valueChanged.connect(self.interface.set_delay_p1)
		self.ui.multiFeedbackDial.valueChanged.connect(self.interface.set_delay_p2)
		self.ui.multiLevelDial.valueChanged.connect(self.interface.set_delay_p4)
		self.ui.reverseTimeDial.valueChanged.connect(self.interface.set_delay_p1)
		self.ui.reverseFeedbackDial.valueChanged.connect(self.interface.set_delay_p2)
		self.ui.reverseFreqDial.valueChanged.connect(self.interface.set_delay_p3)
		self.ui.reverseLevelDial.valueChanged.connect(self.interface.set_delay_p4)
		self.ui.roomDecayDial.valueChanged.connect(self.interface.set_reverb_p1)
		self.ui.roomPreDelayDial.valueChanged.connect(self.interface.set_reverb_p2)
		self.ui.roomToneDial.valueChanged.connect(self.interface.set_reverb_p3)
		self.ui.roomLevelDial.valueChanged.connect(self.interface.set_reverb_p4)
		self.ui.hallDecayDial.valueChanged.connect(self.interface.set_reverb_p1)
		self.ui.hallPreDelayDial.valueChanged.connect(self.interface.set_reverb_p2)
		self.ui.hallToneDial.valueChanged.connect(self.interface.set_reverb_p3)
		self.ui.hallLevelDial.valueChanged.connect(self.interface.set_reverb_p4)
		self.ui.springDecayDial.valueChanged.connect(self.interface.set_reverb_p1)
		self.ui.springPreDelayDial.valueChanged.connect(self.interface.set_reverb_p2)
		self.ui.springToneDial.valueChanged.connect(self.interface.set_reverb_p3)
		self.ui.springLevelDial.valueChanged.connect(self.interface.set_reverb_p4)
		self.ui.stadiumDecayDial.valueChanged.connect(self.interface.set_reverb_p1)
		self.ui.stadiumPreDelayDial.valueChanged.connect(self.interface.set_reverb_p2)
		self.ui.stadiumToneDial.valueChanged.connect(self.interface.set_reverb_p3)
		self.ui.stadiumLevelDial.valueChanged.connect(self.interface.set_reverb_p4)

		# Buttons
		self.ui.ampToggleButton.clicked.connect(self.interface.toggle_preamp)
		self.ui.powerToggleButton.clicked.connect(self.interface.toggle_power_amp)
		self.ui.cabToggleButton.clicked.connect(self.interface.toggle_cab)
		self.ui.preFXToggleButton.clicked.connect(self.interface.toggle_pedal)
		self.ui.modulationToggleButton.clicked.connect(self.interface.toggle_modulation)
		self.ui.delayToggleButton.clicked.connect(self.interface.toggle_delay)
		self.ui.reverbToggleButton.clicked.connect(self.interface.toggle_reverb)
		self.ui.flattenEQButton.clicked.connect(self.flatten_eq)

		# Lists and tabs
		self.ui.ampList.currentRowChanged.connect(self.interface.set_preamp_type)
		self.ui.powerList.currentRowChanged.connect(self.interface.set_power_amp_type)
		self.ui.cabList.currentRowChanged.connect(self.interface.set_cab_type)
		self.ui.preFXTab.currentChanged.connect(self.interface.set_pedal_type)
		self.ui.distortionModeList.currentRowChanged.connect(self.interface.set_pedal_p1)
		self.ui.autoWahModeList.currentRowChanged.connect(self.interface.set_pedal_p1)
		self.ui.modulationTab.currentChanged.connect(self.interface.set_modulation_type)
		self.ui.chorusModeList.currentRowChanged.connect(self.interface.set_modulation_p1)
		self.ui.flangerModeList.currentRowChanged.connect(self.interface.set_modulation_p1)
		self.ui.phaserModeList.currentRowChanged.connect(self.interface.set_modulation_p1)
		self.ui.tremoloModeList.currentRowChanged.connect(self.interface.set_modulation_p1)
		self.ui.delayTab.currentChanged.connect(self.interface.set_delay_type)
		self.ui.multiTapPatternList.currentRowChanged.connect(self.interface.set_delay_p3)
		self.ui.reverbTab.currentChanged.connect(self.interface.set_reverb_type)

		# Preset list
		self.ui.presetList.currentRowChanged.connect(lambda current_index: self.interface.send_program_change(current_index))

	def setup_presets(self) -> None:
		if not self.interface.connected:
			return

		for i in range(0, 100):
			preset_data = self.interface.get_amp_configuration(i)
			if len(preset_data) != 0:
				preset_config = AmpConfig()
				preset_config.load_from_sysex(preset_data)
				self.presets.append(preset_config)
			else:
				print(f'Preset {i} not found')

		for i in range(0, len(self.presets)):
			self.ui.presetList.addItem(self.presets[i].PRESET_NAME)

	def setup_from_config(self, load_from_amp: bool = True) -> None:
		if load_from_amp:
			config = self.interface.get_amp_configuration()
			if len(config) != 0:
				self.amp_config.load_from_sysex(config)

		# Preset information
		self.ui.presetNumberDisplay.display(self.amp_config.PRESET_NUMBER)
		self.ui.presetNameLabel.setText(self.amp_config.PRESET_NAME)

		# Amp settings
		self.ui.ampToggleButton.setChecked(self.amp_config.AMP_STATE)
		self.ui.ampList.setCurrentRow(self.amp_config.AMP_TYPE)
		# Pre-Amp settings
		self.ui.gainDial.setValue(self.amp_config.GAIN)
		self.ui.gainDisplay.display(self.ui.gainDial.value() / 10.0)
		self.ui.volumeDial.setValue(self.amp_config.VOLUME)
		self.ui.volumeDisplay.display(self.ui.volumeDial.value() / 10.0)
		self.ui.gateDial.setValue(self.amp_config.GATE_THRESHOLD)
		self.ui.gateDisplay.display(self.ui.gateDial.value() / 10.0)
		# EQ settings
		self.ui.bassDial.setValue(self.amp_config.BASS)
		self.ui.bassDisplay.display(self.ui.bassDial.value() / 10.0)
		self.ui.middleDial.setValue(self.amp_config.MIDDLE)
		self.ui.middleDisplay.display(self.ui.middleDial.value() / 10.0)
		self.ui.trebleDial.setValue(self.amp_config.TREBLE)
		self.ui.trebleDisplay.display(self.ui.trebleDial.value() / 10.0)
		# Power settings
		self.ui.powerToggleButton.setChecked(self.amp_config.POWER_AMP_STATE)
		self.ui.powerList.setCurrentRow(self.amp_config.POWER_AMP_TYPE)
		self.ui.presenceDial.setValue(self.amp_config.PRESENCE)
		self.ui.presenceDisplay.display(self.ui.presenceDial.value() / 10.0)
		self.ui.resonanceDial.setValue(self.amp_config.RESONANCE)
		self.ui.resonanceDisplay.display(self.ui.resonanceDial.value() / 10.0)
		# Cab settings
		self.ui.cabToggleButton.setChecked(self.amp_config.CABINET_STATE)
		self.ui.cabList.setCurrentRow(self.amp_config.CABINET_TYPE)
		# Pre-FX (Pedal) settings
		self.ui.preFXToggleButton.setChecked(self.amp_config.PEDAL_STATE)
		self.ui.preFXTab.setCurrentIndex(self.amp_config.PEDAL_TYPE)
		match self.amp_config.PEDAL_TYPE:
			case 0:
				self.ui.compressorToneDial.setValue(self.amp_config.PEDAL_P1)
				self.ui.compressorToneDisplay.display(self.ui.compressorToneDial.value() / 10.0)
				self.ui.compressorRatioDial.setValue(self.amp_config.PEDAL_P2)
				self.ui.compressorRatioDisplay.display(self.ui.compressorRatioDial.value() / 10.0)
				self.ui.compressorCompressionDial.setValue(self.amp_config.PEDAL_P3)
				self.ui.compressorCompressionDisplay.display(self.ui.compressorCompressionDial.value() / 10.0)
				self.ui.compressorLevelDial.setValue(self.amp_config.PEDAL_P4)
				self.ui.compressorLevelDisplay.display(self.ui.compressorLevelDial.value() / 10.0)
			case 1:
				self.ui.distortionModeList.setCurrentRow(self.amp_config.PEDAL_P1)
				self.ui.distortionDriveDial.setValue(self.amp_config.PEDAL_P2)
				self.ui.distortionDriveDisplay.display(self.ui.distortionDriveDial.value() / 10.0)
				self.ui.distortionToneDial.setValue(self.amp_config.PEDAL_P3)
				self.ui.distortionToneDisplay.display(self.ui.distortionToneDial.value() / 10.0)
				self.ui.distortionLevelDial.setValue(self.amp_config.PEDAL_P4)
				self.ui.distortionLevelDisplay.display(self.ui.distortionLevelDial.value() / 10.0)
			case 2:
				self.ui.autoWahModeList.setCurrentRow(self.amp_config.PEDAL_P1)
				self.ui.autoWahFreqDial.setValue(self.amp_config.PEDAL_P2)
				self.ui.autoWahFreqDisplay.display(self.ui.autoWahFreqDial.value() / 10.0)
				self.ui.autoWahSensitivityDial.setValue(self.amp_config.PEDAL_P3)
				self.ui.autoWahSensitivityDisplay.display(self.ui.autoWahSensitivityDial.value() / 10.0)
				self.ui.autoWahResDial.setValue(self.amp_config.PEDAL_P4)
				self.ui.autoWahResDisplay.display(self.ui.autoWahResDial.value() / 10.0)
			case 3:
				self.ui.pitchShifterSemitoneDial.setValue(self.amp_config.PEDAL_P1)
				self.ui.pitchShifterSemitoneDisplay.display(self.ui.pitchShifterSemitoneDial.value() / 10.0)
				self.ui.pitchShifterFineDial.setValue(self.amp_config.PEDAL_P2)
				self.ui.pitchShifterFineDisplay.display(self.ui.pitchShifterFineDial.value() / 10.0)
				self.ui.pitchShifterRegenDial.setValue(self.amp_config.PEDAL_P3)
				self.ui.pitchShifterRegenDisplay.display(self.ui.pitchShifterRegenDial.value() / 10.0)
				self.ui.pitchShifterMixDial.setValue(self.amp_config.PEDAL_P4)
				self.ui.pitchShifterMixDisplay.display(self.ui.pitchShifterMixDial.value() / 10.0)
		# Modulation settings
		self.ui.modulationToggleButton.setChecked(self.amp_config.MODULATION_STATE)
		self.ui.modulationTab.setCurrentIndex(self.amp_config.MODULATION_TYPE)
		match self.amp_config.MODULATION_TYPE:
			case 0:
				self.ui.chorusModeList.setCurrentRow(self.amp_config.MODULATION_P1)
				self.ui.chorusSpeedDial.setValue(self.amp_config.MODULATION_P2)
				self.ui.chorusSpeedDisplay.display(self.ui.chorusSpeedDial.value() / 10.0)
				self.ui.chorusDepthDial.setValue(self.amp_config.MODULATION_P3)
				self.ui.chorusDepthDisplay.display(self.ui.chorusDepthDial.value() / 10.0)
				self.ui.chorusToneDial.setValue(self.amp_config.MODULATION_P4)
				self.ui.chorusToneDisplay.display(self.ui.chorusToneDial.value() / 10.0)
			case 1:
				self.ui.flangerModeList.setCurrentRow(self.amp_config.MODULATION_P1)
				self.ui.flangerSpeedDial.setValue(self.amp_config.MODULATION_P2)
				self.ui.flangerSpeedDisplay.display(self.ui.flangerSpeedDial.value() / 10.0)
				self.ui.flangerDepthDial.setValue(self.amp_config.MODULATION_P3)
				self.ui.flangerDepthDisplay.display(self.ui.flangerDepthDial.value() / 10.0)
				self.ui.flangerRegenDial.setValue(self.amp_config.MODULATION_P4)
				self.ui.flangerRegenDisplay.display(self.ui.flangerRegenDial.value() / 10.0)
			case 2:
				self.ui.phaserModeList.setCurrentRow(self.amp_config.MODULATION_P1)
				self.ui.phaserSpeedDial.setValue(self.amp_config.MODULATION_P2)
				self.ui.phaserSpeedDisplay.display(self.ui.phaserSpeedDial.value() / 10.0)
				self.ui.phaserDepthDial.setValue(self.amp_config.MODULATION_P3)
				self.ui.phaserDepthDisplay.display(self.ui.phaserDepthDial.value() / 10.0)
				self.ui.phaserRegenDial.setValue(self.amp_config.MODULATION_P4)
				self.ui.phaserRegenDisplay.display(self.ui.phaserRegenDial.value() / 10.0)
			case 3:
				self.ui.tremoloModeList.setCurrentRow(self.amp_config.MODULATION_P1)
				self.ui.tremoloSpeedDial.setValue(self.amp_config.MODULATION_P2)
				self.ui.tremoloSpeedDisplay.display(self.ui.tremoloSpeedDial.value() / 10.0)
				self.ui.tremoloDepthDial.setValue(self.amp_config.MODULATION_P3)
				self.ui.tremoloDepthDisplay.display(self.ui.tremoloDepthDial.value() / 10.0)
				self.ui.tremoloSkewDial.setValue(self.amp_config.MODULATION_P4)
				self.ui.tremoloSkewDisplay.display(self.ui.tremoloSkewDial.value() / 10.0)
		# Delay settings
		self.ui.delayToggleButton.setChecked(self.amp_config.DELAY_STATE)
		self.ui.delayTab.setCurrentIndex(self.amp_config.DELAY_TYPE)
		match self.amp_config.DELAY_TYPE:
			case 0:
				self.ui.studioTimeDial.setValue(self.amp_config.DELAY_P1)
				self.ui.studioTimeDisplay.display(self.ui.studioTimeDial.value())
				self.ui.studioFeedbackDial.setValue(self.amp_config.DELAY_P2)
				self.ui.studioFeedbackDisplay.display(self.ui.studioFeedbackDial.value() / 10.0)
				self.ui.studioFreqDial.setValue(self.amp_config.DELAY_P3)
				self.ui.studioFreqDisplay.display(self.ui.studioFreqDial.value() / 10.0)
				self.ui.studioLevelDial.setValue(self.amp_config.DELAY_P4)
				self.ui.studioLevelDisplay.display(self.ui.studioLevelDial.value() / 10.0)
			case 1:
				self.ui.vintageTimeDial.setValue(self.amp_config.DELAY_P1)
				self.ui.vintageTimeDisplay.display(self.ui.vintageTimeDial.value())
				self.ui.vintageAgeDial.setValue(self.amp_config.DELAY_P2)
				self.ui.vintageAgeDisplay.display(self.ui.vintageAgeDial.value() / 10.0)
				self.ui.vintageFreqDial.setValue(self.amp_config.DELAY_P3)
				self.ui.vintageFreqDisplay.display(self.ui.vintageFreqDial.value() / 10.0)
				self.ui.vintageLevelDial.setValue(self.amp_config.DELAY_P4)
				self.ui.vintageLevelDisplay.display(self.ui.vintageLevelDial.value() / 10.0)
			case 2:
				self.ui.multiTimeDial.setValue(self.amp_config.DELAY_P1)
				self.ui.multiTimeDisplay.display(self.ui.multiTimeDial.value())
				self.ui.multiFeedbackDial.setValue(self.amp_config.DELAY_P2)
				self.ui.multiFeedbackDisplay.display(self.ui.multiFeedbackDial.value() / 10.0)
				self.ui.multiTapPatternList.setCurrentRow(self.amp_config.DELAY_P3)
				self.ui.multiLevelDial.setValue(self.amp_config.DELAY_P4)
				self.ui.multiLevelDisplay.display(self.ui.multiLevelDial.value() / 10.0)
			case 3:
				self.ui.reverseTimeDial.setValue(self.amp_config.DELAY_P1)
				self.ui.reverseTimeDisplay.display(self.ui.reverseTimeDial.value())
				self.ui.reverseFeedbackDial.setValue(self.amp_config.DELAY_P2)
				self.ui.reverseFeedbackDisplay.display(self.ui.reverseFeedbackDial.value() / 10.0)
				self.ui.reverseFreqDial.setValue(self.amp_config.DELAY_P3)
				self.ui.reverseFreqDisplay.display(self.ui.reverseFreqDial.value() / 10.0)
				self.ui.reverseLevelDial.setValue(self.amp_config.DELAY_P4)
				self.ui.reverseLevelDisplay.display(self.ui.reverseLevelDial.value() / 10.0)
		# Reverb settings
		self.ui.reverbToggleButton.setChecked(self.amp_config.REVERB_STATE)
		self.ui.reverbTab.setCurrentIndex(self.amp_config.REVERB_TYPE)
		match self.amp_config.REVERB_TYPE:
			case 0:
				self.ui.roomDecayDial.setValue(self.amp_config.REVERB_P1)
				self.ui.roomDecayDisplay.display(self.ui.roomDecayDial.value() / 10.0)
				self.ui.roomPreDelayDial.setValue(self.amp_config.REVERB_P2)
				self.ui.roomPreDelayDisplay.display(self.ui.roomPreDelayDial.value() / 10.0)
				self.ui.roomToneDial.setValue(self.amp_config.REVERB_P3)
				self.ui.roomToneDisplay.display(self.ui.roomToneDial.value() / 10.0)
				self.ui.roomLevelDial.setValue(self.amp_config.REVERB_P4)
				self.ui.roomLevelDisplay.display(self.ui.roomLevelDial.value() / 10.0)
			case 1:
				self.ui.hallDecayDial.setValue(self.amp_config.REVERB_P1)
				self.ui.hallDecayDisplay.display(self.ui.hallDecayDial.value() / 10.0)
				self.ui.hallPreDelayDial.setValue(self.amp_config.REVERB_P2)
				self.ui.hallPreDelayDisplay.display(self.ui.hallPreDelayDial.value() / 10.0)
				self.ui.hallToneDial.setValue(self.amp_config.REVERB_P3)
				self.ui.hallToneDisplay.display(self.ui.hallToneDial.value() / 10.0)
				self.ui.hallLevelDial.setValue(self.amp_config.REVERB_P4)
				self.ui.hallLevelDisplay.display(self.ui.hallLevelDial.value() / 10.0)
			case 2:
				self.ui.springDecayDial.setValue(self.amp_config.REVERB_P1)
				self.ui.springDecayDisplay.display(self.ui.springDecayDial.value() / 10.0)
				self.ui.springPreDelayDial.setValue(self.amp_config.REVERB_P2)
				self.ui.springPreDelayDisplay.display(self.ui.springPreDelayDial.value() / 10.0)
				self.ui.springToneDial.setValue(self.amp_config.REVERB_P3)
				self.ui.springToneDisplay.display(self.ui.springToneDial.value() / 10.0)
				self.ui.springLevelDial.setValue(self.amp_config.REVERB_P4)
				self.ui.springLevelDisplay.display(self.ui.springLevelDial.value() / 10.0)
			case 3:
				self.ui.stadiumDecayDial.setValue(self.amp_config.REVERB_P1)
				self.ui.stadiumDecayDisplay.display(self.ui.stadiumDecayDial.value() / 10.0)
				self.ui.stadiumPreDelayDial.setValue(self.amp_config.REVERB_P2)
				self.ui.stadiumPreDelayDisplay.display(self.ui.stadiumPreDelayDial.value() / 10.0)
				self.ui.stadiumToneDial.setValue(self.amp_config.REVERB_P3)
				self.ui.stadiumToneDisplay.display(self.ui.stadiumToneDial.value() / 10.0)
				self.ui.stadiumLevelDial.setValue(self.amp_config.REVERB_P4)
				self.ui.stadiumLevelDisplay.display(self.ui.stadiumLevelDial.value() / 10.0)

	def open_preset_file(self):
		"""Open a preset file and load the configuration."""
		file_name = QFileDialog.getOpenFileName(self, 'Select a preset file', '', 'JSON Files (*.json)')[0]
		if file_name:
			with open(file_name, 'r') as file:
				config = json.load(file)
				self.amp_config.load_from_json(config)
				self.setup_from_config(False)

	def save_preset_file(self):
		"""Save the configuration to a preset file."""
		file_name = QFileDialog.getSaveFileName(self, 'Save the current configuration', '', 'JSON Files (*.json)')[0]
		if file_name:
			with open(file_name, 'w') as file:
				config = self.amp_config.to_json()
				json.dump(config, file, indent=4)

	def flatten_eq(self):
		"""Flatten all EQ settings"""
		self.interface.set_bass(50)
		self.interface.set_middle(50)
		self.interface.set_treble(50)
