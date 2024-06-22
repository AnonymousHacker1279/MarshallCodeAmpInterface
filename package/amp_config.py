class AmpConfig:

	PRESET_NAME: str = ""
	PRESET_NUMBER: int = 0
	GAIN: int = 0
	BASS: int = 0
	MIDDLE: int = 0
	TREBLE: int = 0
	VOLUME: int = 0
	PEDAL_STATE: int = 0
	PEDAL_TYPE: int = 0
	PEDAL_P1: int = 0
	PEDAL_P2: int = 0
	PEDAL_P3: int = 0
	PEDAL_P4: int = 0
	AMP_STATE: bool = False
	AMP_TYPE: int = 0
	GATE_THRESHOLD: int = 0
	MODULATION_STATE: bool = False
	MODULATION_TYPE: int = 0
	MODULATION_P1: int = 0
	MODULATION_P2: int = 0
	MODULATION_P3: int = 0
	MODULATION_P4: int = 0
	DELAY_STATE: bool = False
	DELAY_TYPE: int = 0
	DELAY_P1: int = 0   # Time in MS, requires MSB/LSB to be set
	DELAY_P2: int = 0
	DELAY_P3: int = 0
	DELAY_P4: int = 0
	REVERB_STATE: bool = False
	REVERB_TYPE: int = 0
	REVERB_P1: int = 0
	REVERB_P2: int = 0
	REVERB_P3: int = 0
	REVERB_P4: int = 0
	POWER_AMP_STATE: bool = False
	POWER_AMP_TYPE: int = 0
	CABINET_STATE: bool = False
	CABINET_TYPE: int = 0
	RESONANCE: int = 0
	PRESENCE: int = 0

	def __init__(self):
		pass

	def load_from_sysex(self, data: list) -> None:
		"""Load the configuration from a SysEx message."""
		self.PRESET_NAME = ''.join([chr(byte) for byte in data[9:27]]).strip()
		self.PRESET_NUMBER = data[8]
		self.GAIN = data[28]
		self.BASS = data[29]
		self.MIDDLE = data[30]
		self.TREBLE = data[31]
		self.VOLUME = data[32]
		self.PEDAL_STATE = data[33]
		self.PEDAL_TYPE = data[34]
		self.PEDAL_P1 = data[35]
		self.PEDAL_P2 = data[36]
		self.PEDAL_P3 = data[37]
		self.PEDAL_P4 = data[38]
		self.AMP_STATE = data[39] == 1
		self.AMP_TYPE = data[40]
		self.GATE_THRESHOLD = data[41]
		self.MODULATION_STATE = data[42] == 1
		self.MODULATION_TYPE = data[43]
		self.MODULATION_P1 = data[44]
		self.MODULATION_P2 = data[45]
		self.MODULATION_P3 = data[46]
		self.MODULATION_P4 = data[47]
		self.DELAY_STATE = data[48] == 1
		self.DELAY_TYPE = data[49]
		self.DELAY_P1 = (data[50] * 128) + data[51]
		self.DELAY_P2 = data[52]
		self.DELAY_P3 = data[53]
		self.DELAY_P4 = data[54]
		self.REVERB_STATE = data[55] == 1
		self.REVERB_TYPE = data[56]
		self.REVERB_P1 = data[57]
		self.REVERB_P2 = data[58]
		self.REVERB_P3 = data[59]
		self.REVERB_P4 = data[60]
		self.POWER_AMP_STATE = data[61] == 1
		self.POWER_AMP_TYPE = data[62]
		self.CABINET_STATE = data[63] == 1
		self.CABINET_TYPE = data[64]
		self.RESONANCE = data[65]
		self.PRESENCE = data[66]
