import asyncio

import mido
import rtmidi
from PySide6.QtCore import QTimer

from ui.main_window_ui import Ui_MainWindow


def midi_to_note(midi_number: int) -> str:
	"""Convert a MIDI note number to a note name."""
	notes = ["C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"]
	octave = int(midi_number / 12) - 1
	note_index = midi_number % 12
	return notes[note_index] + str(octave)


class AmpMIDIInterface:

	def __init__(self, main, ui: Ui_MainWindow):
		self.connected = False
		self.main = main
		self.ui = ui
		self.ignore_updates = False
		try:
			self.port = mido.open_ioport('CODE 0')
			self.connected = True

			self.timer = QTimer()
			self.timer.timeout.connect(self.__handle_incoming_messages)
			self.timer.start(100)
		except OSError:
			pass

	def __handle_incoming_messages(self) -> None:
		"""Handle incoming messages from the amp."""
		try:
			for msg in self.port.iter_pending():
				if self.ignore_updates:
					return

				if msg.is_cc(52):
					if msg.value == 1:
						self.main.open_tuner_dialog(False)
					else:
						self.main.close_tuner_dialog(False)

					return

				if msg.is_cc() or msg.type == "program_change":
					asyncio.run(self.main.setup_from_config())

				if msg.type == "polytouch":
					note = midi_to_note(msg.note)
					accuracy = msg.value
					self.main.tunerDialog.draw_tuner(note, accuracy)

			self.ignore_updates = False
		except KeyboardInterrupt:
			pass

	def __send_control_change(self, control_id: int, value: int) -> None:
		"""Send a control_change message to the connected amp."""
		if not self.connected:
			try:
				self.port = mido.open_ioport('CODE 0')
				self.connected = True

				self.ui.connectionStatusLabel.setText('Status: CONNECTED')
				self.ui.connectionStatusLabel.setStyleSheet('color: green')

				asyncio.run(self.main.async_setup())
			except OSError:
				return
			return

		self.ignore_updates = True

		# Check both inputs to ensure they are within the valid range
		if control_id < 0 or control_id > 127:
			raise ValueError(f'Control ID must be between 0 and 127. Got {control_id} with value {value}.')
		if value < 0 or value > 127:
			if value == -1:
				return
			raise ValueError(f'Value must be between 0 and 127. Got {value} for control ID {control_id}.')

		try:
			self.port.send(mido.Message('control_change', control=control_id, value=value))
		except rtmidi.SystemError:
			self.connected = False
			self.ui.connectionStatusLabel.setText('Status: DISCONNECTED')
			self.ui.connectionStatusLabel.setStyleSheet('color: red')

	def send_program_change(self, program: int):
		"""Send a program change message to the connected amp."""
		if not self.connected:
			return

		self.port.send(mido.Message('program_change', program=program))
		asyncio.run(self.main.setup_from_config())

	def get_amp_configuration(self, preset: int = -1) -> list:
		"""
		Get the specified amp preset configuration.

		:param preset: The preset to get the configuration for. Omitting this will return the current configuration.
		"""
		if not self.connected:
			return []

		if preset == -1:
			self.port.send(mido.Message('sysex', data=[0x00, 0x21, 0x15, 0x7F, 0x7F, 0x7F, 0x73, 0x01, 0x00]))
		else:
			self.port.send(mido.Message('sysex', data=[0x00, 0x21, 0x15, 0x7F, 0x7F, 0x7F, 0x72, 0x01, preset]))

		msg = self.port.receive()
		while msg.type != "sysex":
			msg = self.port.receive()
		return msg.data

	def set_gain(self, value: int) -> None:
		"""Set the gain of the amp."""
		self.ui.gainDisplay.display(value / 10.0)
		self.main.amp_config.GAIN = value
		self.__send_control_change(70, value)

	def set_volume(self, value: int) -> None:
		"""Set the volume of the amp."""
		self.ui.volumeDisplay.display(value / 10.0)
		self.main.amp_config.VOLUME = value
		self.__send_control_change(74, value)

	def set_gate_threshold(self, value: int) -> None:
		"""Set the gate threshold of the amp."""
		self.ui.gateDisplay.display(value / 10.0)
		self.main.amp_config.GATE_THRESHOLD = value
		self.__send_control_change(83, value)

	def set_bass(self, value: int) -> None:
		"""Set the bass of the amp."""
		self.ui.bassDisplay.display(value / 10.0)
		self.main.amp_config.BASS = value
		self.__send_control_change(71, value)

	def set_middle(self, value: int) -> None:
		"""Set the middle of the amp."""
		self.ui.middleDisplay.display(value / 10.0)
		self.main.amp_config.MIDDLE = value
		self.__send_control_change(72, value)

	def set_treble(self, value: int) -> None:
		"""Set the treble of the amp."""
		self.ui.trebleDisplay.display(value / 10.0)
		self.main.amp_config.TREBLE = value
		self.__send_control_change(73, value)

	def set_presence(self, value: int) -> None:
		"""Set the presence of the amp."""
		self.ui.presenceDisplay.display(value / 10.0)
		self.main.amp_config.PRESENCE = value
		self.__send_control_change(118, value)

	def set_resonance(self, value: int) -> None:
		"""Set the resonance of the amp."""
		self.ui.resonanceDisplay.display(value / 10.0)
		self.main.amp_config.RESONANCE = value
		self.__send_control_change(119, value)

	def toggle_preamp(self, state: bool) -> None:
		"""Toggle the preamp of the amp."""
		self.main.amp_config.AMP_STATE = state
		self.__send_control_change(81, 1 if state else 0)

	def set_preamp_type(self, value: int) -> None:
		"""Set the type of the amp."""
		self.main.amp_config.AMP_TYPE = value
		self.__send_control_change(82, value)

	def toggle_power_amp(self, state: bool) -> None:
		"""Toggle the power amp of the amp."""
		self.main.amp_config.POWER_AMP_STATE = state
		self.__send_control_change(114, 1 if state else 0)

	def set_power_amp_type(self, value: int) -> None:
		"""Set the type of the power amp."""
		self.main.amp_config.POWER_AMP_TYPE = value
		self.__send_control_change(115, value)

	def toggle_cab(self, state: bool) -> None:
		"""Toggle the cabinet of the amp."""
		self.main.amp_config.CABINET_STATE = state
		self.__send_control_change(116, 1 if state else 0)

	def set_cab_type(self, value: int) -> None:
		"""Set the type of the cabinet."""
		self.main.amp_config.CABINET_TYPE = value
		self.__send_control_change(117, value)

	def toggle_pedal(self, state: bool) -> None:
		"""Toggle the pedal of the amp."""
		self.main.amp_config.PEDAL_STATE = state
		self.__send_control_change(75, 1 if state else 0)

	def set_pedal_type(self, value: int) -> None:
		"""Set the type of the pedal."""
		self.main.amp_config.PEDAL_TYPE = value
		self.__send_control_change(76, value)

	def set_pedal_p1(self, value: int) -> None:
		"""Set the first parameter of the pedal."""
		match self.main.amp_config.PEDAL_TYPE:
			case 0:
				self.ui.compressorToneDisplay.display(value / 10.0)
			case 3:
				self.ui.pitchShifterSemitoneDisplay.display(value)
		self.main.amp_config.PEDAL_P1 = value
		self.__send_control_change(77, value)

	def set_pedal_p2(self, value: int) -> None:
		"""Set the second parameter of the pedal."""
		match self.main.amp_config.PEDAL_TYPE:
			case 0:
				self.ui.compressorRatioDisplay.display(value / 10.0)
			case 1:
				self.ui.distortionDriveDisplay.display(value / 10.0)
			case 2:
				self.ui.autoWahFreqDisplay.display(value / 10.0)
			case 3:
				self.ui.pitchShifterFineDisplay.display(value / 10.0)
		self.main.amp_config.PEDAL_P2 = value
		self.__send_control_change(78, value)

	def set_pedal_p3(self, value: int) -> None:
		"""Set the third parameter of the pedal."""
		match self.main.amp_config.PEDAL_TYPE:
			case 0:
				self.ui.compressorCompressionDisplay.display(value / 10.0)
			case 1:
				self.ui.distortionToneDisplay.display(value / 10.0)
			case 2:
				self.ui.autoWahSensitivityDisplay.display(value / 10.0)
			case 3:
				self.ui.pitchShifterRegenDisplay.display(value / 10.0)
		self.main.amp_config.PEDAL_P3 = value
		self.__send_control_change(79, value)

	def set_pedal_p4(self, value: int) -> None:
		"""Set the fourth parameter of the pedal."""
		match self.main.amp_config.PEDAL_TYPE:
			case 0:
				self.ui.compressorLevelDisplay.display(value / 10.0)
			case 1:
				self.ui.distortionLevelDisplay.display(value / 10.0)
			case 2:
				self.ui.autoWahResDisplay.display(value / 10.0)
			case 3:
				self.ui.pitchShifterMixDisplay.display(value / 10.0)
		self.main.amp_config.PEDAL_P4 = value
		self.__send_control_change(80, value)

	def toggle_modulation(self, state: bool) -> None:
		"""Toggle the modulation of the amp."""
		self.main.amp_config.MODULATION_STATE = state
		self.__send_control_change(85, 1 if state else 0)

	def set_modulation_type(self, value: int) -> None:
		"""Set the type of the modulation."""
		self.main.amp_config.MODULATION_TYPE = value
		self.__send_control_change(86, value)

	def set_modulation_p1(self, value: int) -> None:
		"""Set the first parameter of the modulation."""
		self.main.amp_config.MODULATION_P1 = value
		self.__send_control_change(90, value)

	def set_modulation_p2(self, value: int) -> None:
		"""Set the second parameter of the modulation."""
		match self.main.amp_config.MODULATION_TYPE:
			case 0:
				self.ui.chorusSpeedDisplay.display(value / 10.0)
			case 1:
				self.ui.flangerSpeedDisplay.display(value / 10.0)
			case 2:
				self.ui.phaserSpeedDisplay.display(value / 10.0)
			case 3:
				self.ui.tremoloSpeedDisplay.display(value / 10.0)
		self.main.amp_config.MODULATION_P2 = value
		self.__send_control_change(87, value)

	def set_modulation_p3(self, value: int) -> None:
		"""Set the third parameter of the modulation."""
		match self.main.amp_config.MODULATION_TYPE:
			case 0:
				self.ui.chorusDepthDisplay.display(value / 10.0)
			case 1:
				self.ui.flangerDepthDisplay.display(value / 10.0)
			case 2:
				self.ui.phaserDepthDisplay.display(value / 10.0)
			case 3:
				self.ui.tremoloDepthDisplay.display(value / 10.0)
		self.main.amp_config.MODULATION_P3 = value
		self.__send_control_change(89, value)

	def set_modulation_p4(self, value: int) -> None:
		"""Set the fourth parameter of the modulation."""
		match self.main.amp_config.MODULATION_TYPE:
			case 0:
				self.ui.chorusToneDisplay.display(value / 10.0)
			case 1:
				self.ui.flangerRegenDisplay.display(value / 10.0)
			case 2:
				self.ui.phaserRegenDisplay.display(value / 10.0)
			case 3:
				self.ui.tremoloSkewDisplay.display(value)
				value += 50
		self.main.amp_config.MODULATION_P4 = value
		self.__send_control_change(102, value)

	def toggle_delay(self, state: bool) -> None:
		"""Toggle the delay of the amp."""
		self.main.amp_config.DELAY_STATE = state
		self.__send_control_change(103, 1 if state else 0)

	def set_delay_type(self, value: int) -> None:
		"""Set the type of the delay."""
		self.main.amp_config.DELAY_TYPE = value
		self.__send_control_change(104, value)

	def set_delay_p1(self, value: int) -> None:
		"""Set the first parameter of the delay."""
		match self.main.amp_config.DELAY_TYPE:
			case 0:
				self.ui.studioTimeDisplay.display(value)
			case 1:
				self.ui.vintageTimeDisplay.display(value)
			case 2:
				self.ui.multiTimeDisplay.display(value)
			case 3:
				self.ui.reverseTimeDisplay.display(value)
		self.main.amp_config.DELAY_P1 = value

		# Delay uses MSB/LSB on 31 & 63
		msb = value // 128    # Integer division to get the MSB
		lsb = value % 128     # Modulo operation to get the LSB
		self.__send_control_change(31, msb)
		self.__send_control_change(63, lsb)

	def set_delay_p2(self, value: int) -> None:
		"""Set the second parameter of the delay."""
		match self.main.amp_config.DELAY_TYPE:
			case 0:
				self.ui.studioFeedbackDisplay.display(value / 10.0)
			case 1:
				self.ui.vintageAgeDisplay.display(value / 10.0)
			case 2:
				self.ui.multiFeedbackDisplay.display(value / 10.0)
			case 3:
				self.ui.reverseFeedbackDisplay.display(value / 10.0)
		self.main.amp_config.DELAY_P2 = value
		self.__send_control_change(105, value)

	def set_delay_p3(self, value: int) -> None:
		"""Set the third parameter of the delay."""
		match self.main.amp_config.DELAY_TYPE:
			case 0:
				self.ui.studioFreqDisplay.display(value / 10.0)
			case 1:
				self.ui.vintageFreqDisplay.display(value / 10.0)
			case 3:
				self.ui.reverseFreqDisplay.display(value / 10.0)
		self.main.amp_config.DELAY_P3 = value
		self.__send_control_change(106, value)

	def set_delay_p4(self, value: int) -> None:
		"""Set the fourth parameter of the delay."""
		match self.main.amp_config.DELAY_TYPE:
			case 0:
				self.ui.studioLevelDisplay.display(value / 10.0)
			case 1:
				self.ui.vintageLevelDisplay.display(value / 10.0)
			case 2:
				self.ui.multiLevelDisplay.display(value / 10.0)
			case 3:
				self.ui.reverseLevelDisplay.display(value / 10.0)
		self.main.amp_config.DELAY_P4 = value
		self.__send_control_change(107, value)

	def toggle_reverb(self, state: bool) -> None:
		"""Toggle the reverb of the amp."""
		self.main.amp_config.REVERB_STATE = state
		self.__send_control_change(108, 1 if state else 0)

	def set_reverb_type(self, value: int) -> None:
		"""Set the type of the reverb."""
		self.main.amp_config.REVERB_TYPE = value
		self.__send_control_change(109, value)

	def set_reverb_p1(self, value: int) -> None:
		"""Set the first parameter of the reverb."""
		match self.main.amp_config.REVERB_TYPE:
			case 0:
				self.ui.roomDecayDisplay.display(value / 10.0)
			case 1:
				self.ui.hallDecayDisplay.display(value / 10.0)
			case 2:
				self.ui.springDecayDisplay.display(value / 10.0)
			case 3:
				self.ui.stadiumDecayDisplay.display(value / 10.0)
		self.main.amp_config.REVERB_P1 = value
		self.__send_control_change(110, value)
		
	def set_reverb_p2(self, value: int) -> None:
		"""Set the second parameter of the reverb."""
		match self.main.amp_config.REVERB_TYPE:
			case 0:
				self.ui.roomPreDelayDisplay.display(value / 10.0)
			case 1:
				self.ui.hallPreDelayDisplay.display(value / 10.0)
			case 2:
				self.ui.springPreDelayDisplay.display(value / 10.0)
			case 3:
				self.ui.stadiumPreDelayDisplay.display(value / 10.0)
		self.main.amp_config.REVERB_P2 = value
		self.__send_control_change(111, value)

	def set_reverb_p3(self, value: int) -> None:
		"""Set the third parameter of the reverb."""
		match self.main.amp_config.REVERB_TYPE:
			case 0:
				self.ui.roomToneDisplay.display(value / 10.0)
			case 1:
				self.ui.hallToneDisplay.display(value / 10.0)
			case 2:
				self.ui.springToneDisplay.display(value / 10.0)
			case 3:
				self.ui.stadiumToneDisplay.display(value / 10.0)
		self.main.amp_config.REVERB_P3 = value
		self.__send_control_change(112, value)

	def set_reverb_p4(self, value: int) -> None:
		"""Set the fourth parameter of the reverb."""
		match self.main.amp_config.REVERB_TYPE:
			case 0:
				self.ui.roomLevelDisplay.display(value / 10.0)
			case 1:
				self.ui.hallLevelDisplay.display(value / 10.0)
			case 2:
				self.ui.springLevelDisplay.display(value / 10.0)
			case 3:
				self.ui.stadiumLevelDisplay.display(value / 10.0)
		self.main.amp_config.REVERB_P4 = value
		self.__send_control_change(113, value)

	def set_tuner_state(self, state: bool) -> None:
		"""Set the state of the tuner."""
		self.__send_control_change(52, 1 if state else 0)
