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

	def to_json(self) -> dict:
		"""Create a JSON representation of the configuration."""
		return {
			"preset_name": self.PRESET_NAME,
			"preset_number": self.PRESET_NUMBER,
			"gain": self.GAIN,
			"bass": self.BASS,
			"middle": self.MIDDLE,
			"treble": self.TREBLE,
			"volume": self.VOLUME,
			"pedal_state": self.PEDAL_STATE,
			"pedal_type": self.PEDAL_TYPE,
			"pedal_p1": self.PEDAL_P1,
			"pedal_p2": self.PEDAL_P2,
			"pedal_p3": self.PEDAL_P3,
			"pedal_p4": self.PEDAL_P4,
			"amp_state": self.AMP_STATE,
			"amp_type": self.AMP_TYPE,
			"gate_threshold": self.GATE_THRESHOLD,
			"modulation_state": self.MODULATION_STATE,
			"modulation_type": self.MODULATION_TYPE,
			"modulation_p1": self.MODULATION_P1,
			"modulation_p2": self.MODULATION_P2,
			"modulation_p3": self.MODULATION_P3,
			"modulation_p4": self.MODULATION_P4,
			"delay_state": self.DELAY_STATE,
			"delay_type": self.DELAY_TYPE,
			"delay_p1": self.DELAY_P1,
			"delay_p2": self.DELAY_P2,
			"delay_p3": self.DELAY_P3,
			"delay_p4": self.DELAY_P4,
			"reverb_state": self.REVERB_STATE,
			"reverb_type": self.REVERB_TYPE,
			"reverb_p1": self.REVERB_P1,
			"reverb_p2": self.REVERB_P2,
			"reverb_p3": self.REVERB_P3,
			"reverb_p4": self.REVERB_P4,
			"power_amp_state": self.POWER_AMP_STATE,
			"power_amp_type": self.POWER_AMP_TYPE,
			"cabinet_state": self.CABINET_STATE,
			"cabinet_type": self.CABINET_TYPE,
			"resonance": self.RESONANCE,
			"presence": self.PRESENCE
		}

	def load_from_json(self, config: dict) -> None:
		"""Load the configuration from a JSON object."""
		self.PRESET_NAME = config["preset_name"]
		self.PRESET_NUMBER = config["preset_number"]
		self.GAIN = config["gain"]
		self.BASS = config["bass"]
		self.MIDDLE = config["middle"]
		self.TREBLE = config["treble"]
		self.VOLUME = config["volume"]
		self.PEDAL_STATE = config["pedal_state"]
		self.PEDAL_TYPE = config["pedal_type"]
		self.PEDAL_P1 = config["pedal_p1"]
		self.PEDAL_P2 = config["pedal_p2"]
		self.PEDAL_P3 = config["pedal_p3"]
		self.PEDAL_P4 = config["pedal_p4"]
		self.AMP_STATE = config["amp_state"]
		self.AMP_TYPE = config["amp_type"]
		self.GATE_THRESHOLD = config["gate_threshold"]
		self.MODULATION_STATE = config["modulation_state"]
		self.MODULATION_TYPE = config["modulation_type"]
		self.MODULATION_P1 = config["modulation_p1"]
		self.MODULATION_P2 = config["modulation_p2"]
		self.MODULATION_P3 = config["modulation_p3"]
		self.MODULATION_P4 = config["modulation_p4"]
		self.DELAY_STATE = config["delay_state"]
		self.DELAY_TYPE = config["delay_type"]
		self.DELAY_P1 = config["delay_p1"]
		self.DELAY_P2 = config["delay_p2"]
		self.DELAY_P3 = config["delay_p3"]
		self.DELAY_P4 = config["delay_p4"]
		self.REVERB_STATE = config["reverb_state"]
		self.REVERB_TYPE = config["reverb_type"]
		self.REVERB_P1 = config["reverb_p1"]
		self.REVERB_P2 = config["reverb_p2"]
		self.REVERB_P3 = config["reverb_p3"]
		self.REVERB_P4 = config["reverb_p4"]
		self.POWER_AMP_STATE = config["power_amp_state"]
		self.POWER_AMP_TYPE = config["power_amp_type"]
		self.CABINET_STATE = config["cabinet_state"]
		self.CABINET_TYPE = config["cabinet_type"]
		self.RESONANCE = config["resonance"]
		self.PRESENCE = config["presence"]
