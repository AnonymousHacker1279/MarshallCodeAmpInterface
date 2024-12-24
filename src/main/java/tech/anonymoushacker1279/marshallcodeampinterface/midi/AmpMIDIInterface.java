package tech.anonymoushacker1279.marshallcodeampinterface.midi;


import org.jetbrains.annotations.Nullable;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;
import tech.anonymoushacker1279.marshallcodeampinterface.amp.AmpConfig;
import tech.anonymoushacker1279.marshallcodeampinterface.amp.AmpModel;
import tech.anonymoushacker1279.marshallcodeampinterface.controller.TuningDialogController;

import javax.sound.midi.InvalidMidiDataException;

public abstract class AmpMIDIInterface {

	@Nullable
	private TuningDialogController tuningDialogController;

	public AmpMIDIInterface() {
	}

	/**
	 * Send a control change message to the connected device
	 *
	 * @param control the control number
	 * @param value   the control value
	 */
	public abstract void sendControlChange(int control, int value) throws InvalidMidiDataException;

	/**
	 * Send a program change message to the connected device
	 *
	 * @param program the program number
	 */
	public abstract void sendProgramChange(int program) throws InvalidMidiDataException;

	/**
	 * Send a sysex message to the connected device
	 *
	 * @param message the sysex message
	 */
	public abstract void sendSysexMessage(byte[] message) throws InvalidMidiDataException;

	/**
	 * Receive a sysex message from the connected device
	 */
	public abstract byte[] receiveSysexMessage();

	/**
	 * Validate an incoming sysex message to check for errors
	 *
	 * @param message the sysex message
	 * @return true if the message is valid, false otherwise
	 */
	public boolean validateSysexMessage(byte[] message) {
		// Look for the sysex start and end bytes
		if (message[0] != (byte) 0xF0 || message[message.length - 1] != (byte) 0xF7) {
			System.out.println("Start/stop bytes missing!");
			return false;
		}

		byte statusByte = message[7];
		if (statusByte == 0x72 || statusByte == 0x73) {
			if (message.length < 75) {
				System.out.println("Message length too short!");
			}
			return message.length >= 75;
		}

		return true;
	}

	/**
	 * Close the connection to the device
	 */
	public abstract void close();

	/**
	 * Check if the device is ready to receive messages
	 *
	 * @return true if the device is ready, false otherwise
	 */
	public abstract boolean isReady();

	/**
	 * Set the tuning dialog controller
	 *
	 * @param tuningDialogController the tuning dialog controller
	 */
	public void setTuningDialogController(@Nullable TuningDialogController tuningDialogController) {
		this.tuningDialogController = tuningDialogController;
	}

	@Nullable
	public TuningDialogController getTuningDialogController() {
		return tuningDialogController;
	}

	/**
	 * Handle an incoming control change message
	 *
	 * @param control the control number
	 * @param value   the control value
	 */
	protected void handleControlChange(int control, int value) {
		if (control == 52) {
			if (value == 1) {
				TuningDialogController.openDialog(this::setTuningDialogController);
			} else {
				if (getTuningDialogController() != null) {
					TuningDialogController.closeDialog();
					setTuningDialogController(null);
				}
			}
		}

		AmpConfig.updateInterface(CODEInterfaceApplication.CONTROLLER, CODEInterfaceApplication.DEFAULT_CONFIG, control, value);
	}

	/**
	 * Handle an incoming program change message
	 *
	 * @param preset the preset number
	 */
	protected void handlePresetChange(int preset) {
		AmpConfig.setInterfaceValues(CODEInterfaceApplication.CONTROLLER, CODEInterfaceApplication.PRESETS.get(preset));
		CODEInterfaceApplication.CONTROLLER.presetSearchTextField.clear();
	}

	/**
	 * Handle an incoming tuning data change message
	 *
	 * @param note     the note
	 * @param accuracy the accuracy
	 */
	protected void handleTuningDataChange(int note, int accuracy) {
		if (getTuningDialogController() != null) {
			getTuningDialogController().updateTuner(note, accuracy);
		}
	}

	/**
	 * Toggle the state of the preamp
	 *
	 * @param state the state of the preamp
	 */
	public void togglePreamp(boolean state) {
		try {
			sendControlChange(81, state ? 1 : 0);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the preamp type. Must be an integer between 0 and 14.
	 *
	 * @param type the preamp type
	 */
	public void setPreampType(int type) {
		try {
			sendControlChange(82, type);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the gain of the preamp. Must be a float between 0 and 10.
	 *
	 * @param gain the gain of the preamp
	 */
	public void setGain(float gain) {
		try {
			sendControlChange(70, (int) (gain * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the volume of the preamp. Must be a float between 0 and 10.
	 *
	 * @param volume the volume of the preamp
	 */
	public void setVolume(float volume) {
		try {
			sendControlChange(74, (int) (volume * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the gate threshold of the preamp. Must be a float between 0 and 10.
	 *
	 * @param gate the gate threshold of the preamp
	 */
	public void setGate(float gate) {
		try {
			sendControlChange(83, (int) (gate * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the bass of the preamp. Must be a float between 0 and 10.
	 *
	 * @param bass the bass of the preamp
	 */
	public void setBass(float bass) {
		try {
			sendControlChange(71, (int) (bass * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the middle of the preamp. Must be a float between 0 and 10.
	 *
	 * @param middle the middle of the preamp
	 */
	public void setMiddle(float middle) {
		try {
			sendControlChange(72, (int) (middle * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the treble of the preamp. Must be a float between 0 and 10.
	 *
	 * @param treble the treble of the preamp
	 */
	public void setTreble(float treble) {
		try {
			sendControlChange(73, (int) (treble * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Toggle the state of the power amp
	 *
	 * @param state the state of the power amp
	 */
	public void togglePowerAmp(boolean state) {
		try {
			sendControlChange(114, state ? 1 : 0);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the power amp type. Must be an integer between 0 and 3.
	 *
	 * @param type the power amp type
	 */
	public void setPowerAmpType(int type) {
		try {
			sendControlChange(115, type);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the presence of the power amp. Must be a float between 0 and 10.
	 *
	 * @param presence the presence of the power amp
	 */
	public void setPresence(float presence) {
		try {
			sendControlChange(118, (int) (presence * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the resonance of the power amp. Must be a float between 0 and 10.
	 *
	 * @param resonance the resonance of the power amp
	 */
	public void setResonance(float resonance) {
		try {
			sendControlChange(119, (int) (resonance * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Toggle the state of the cab
	 *
	 * @param state the state of the cab
	 */
	public void toggleCab(boolean state) {
		try {
			sendControlChange(116, state ? 1 : 0);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the cab type. Must be an integer between 0 and 7.
	 *
	 * @param type the cab type
	 */
	public void setCabType(int type) {
		try {
			sendControlChange(117, type);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Toggle the state of the preFX pedal
	 *
	 * @param state the state of the preFX pedal
	 */
	public void togglePreFXPedal(boolean state) {
		try {
			sendControlChange(75, state ? 1 : 0);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the preFX pedal type. Must be an integer between 0 and 3. The pedal types are as follows:
	 * <ol>
	 * <li>Compressor</li>
	 * <li>Distortion</li>
	 * <li>Auto Wah</li>
	 * <li>Pitch Shifter</li>
	 * </ol>
	 *
	 * @param type the preFX pedal type
	 */
	public void setPreFXPedalType(int type) {
		try {
			sendControlChange(76, type);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set preFX pedal parameter 1. The accepted value is dependent on the pedal type:
	 * <ol>
	 *     <li>Compressor (Tone): 0-10</li>
	 *     <li>Distortion (Mode): 0-2</li>
	 *     <li>Auto Wah (Mode): 0-1</li>
	 *     <li>
	 *         Pitch Shifter (Semitone): 0-24
	 *         <p>
	 *             Note: 12 is considered the default value of zero. Lower values are negative, higher values are positive.
	 *         </p>
	 *     </li>
	 * </ol>
	 *
	 * @param parameter1 the preFX pedal parameter 1
	 * @param pedalType  the type of pedal
	 */
	public void setPedalParameter1(float parameter1, int pedalType) {
		try {
			switch (pedalType) {
				case 0 -> sendControlChange(77, (int) (parameter1 * 10));
				case 1, 2 -> sendControlChange(77, (int) parameter1);
				case 3 -> sendControlChange(77, (int) (parameter1 + 12));
			}
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set preFX pedal parameter 2. The accepted value is dependent on the pedal type:
	 * <ol>
	 *     <li>Compressor (Ratio): 0-10</li>
	 *     <li>Distortion (Drive): 0-10</li>
	 *     <li>Auto Wah (Freq): 0-10</li>
	 *     <li>Pitch Shifter (Fine): -50-50</li>
	 * </ol>
	 *
	 * @param parameter2 the preFX pedal parameter 2
	 * @param pedalType  the type of pedal
	 */
	public void setPedalParameter2(float parameter2, int pedalType) {
		try {
			switch (pedalType) {
				case 0, 1, 2 -> sendControlChange(78, (int) (parameter2 * 10));
				case 3 -> sendControlChange(78, (int) (parameter2 + 50));
			}
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set preFX pedal parameter 3. The accepted value is dependent on the pedal type:
	 * <ol>
	 *     <li>Compressor (Comp)</li>
	 *     <li>Distortion (Tone)</li>
	 *     <li>Auto Wah (Sensitivity)</li>
	 *     <li>Pitch Shifter (Regen)</li>
	 * </ol>
	 *
	 * @param parameter3 the preFX pedal parameter 3
	 */
	public void setPedalParameter3(float parameter3) {
		try {
			sendControlChange(79, (int) (parameter3 * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set preFX pedal parameter 4. The accepted value is not dependent on the pedal type, and will always be between 0
	 * and 10.
	 * <ol>
	 *     <li>Compressor (Level)</li>
	 *     <li>Distortion (Level)</li>
	 *     <li>Auto Wah (Res)</li>
	 *     <li>Pitch Shifter (Mix)</li>
	 * </ol>
	 *
	 * @param parameter4 the preFX pedal parameter 4
	 */
	public void setPedalParameter4(float parameter4) {
		try {
			sendControlChange(80, (int) (parameter4 * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Toggle the state of modulation
	 *
	 * @param state the state of modulation
	 */
	public void toggleModulation(boolean state) {
		try {
			sendControlChange(85, state ? 1 : 0);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the modulation type. Must be an integer between 0 and 3. The modulation types are as follows:
	 * <ol>
	 * <li>Chorus</li>
	 * <li>Flanger</li>
	 * <li>Phaser</li>
	 * <li>Tremolo</li>
	 * </ol>
	 *
	 * @param type the modulation type
	 */
	public void setModulationType(int type) {
		try {
			sendControlChange(86, type);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set modulation parameter 1. The accepted value is not dependent on the modulation type, and will always be
	 * between 0 and 1. This always sets the mode of the selected modulation type.
	 *
	 * @param parameter1 the modulation parameter 1
	 */
	public void setModulationParameter1(float parameter1) {
		try {
			sendControlChange(90, (int) parameter1);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set modulation parameter 2. The accepted value is not dependent on the modulation type, and will always be
	 * between 0 and 10. This always sets the speed of the selected modulation type.
	 *
	 * @param parameter2 the modulation parameter 2
	 */
	public void setModulationParameter2(float parameter2) {
		try {
			sendControlChange(87, (int) (parameter2 * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set modulation parameter 3. The accepted value is not dependent on the modulation type, and will always be
	 * between 0 and 10. This always sets the depth of the selected modulation type.
	 *
	 * @param parameter3 the modulation parameter 3
	 */
	public void setModulationParameter3(float parameter3) {
		try {
			sendControlChange(89, (int) (parameter3 * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set modulation parameter 4. The accepted value is dependent on the modulation type:
	 * <ol>
	 *     <li>Chorus (Tone): 0-10</li>
	 *     <li>Flanger (Regen): 0-10</li>
	 *     <li>Phaser (Regen): 0-10</li>
	 *     <li>Tremolo (Skew): -50-50</li>
	 *
	 * @param parameter4 the modulation parameter 4
	 */
	public void setModulationParameter4(float parameter4, int modulationType) {
		try {
			switch (modulationType) {
				case 0, 1, 2 -> sendControlChange(102, (int) (parameter4 * 10));
				case 3 -> sendControlChange(102, (int) (parameter4 + 50));
			}
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Toggle the state of delay
	 *
	 * @param state the state of delay
	 */
	public void toggleDelay(boolean state) {
		try {
			sendControlChange(103, state ? 1 : 0);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the delay type. Must be an integer between 0 and 3. The delay types are as follows:
	 * <ol>
	 * <li>Studio</li>
	 * <li>Vintage</li>
	 * <li>Multi</li>
	 * <li>Reverse</li>
	 * </ol>
	 *
	 * @param type the delay type
	 */
	public void setDelayType(int type) {
		try {
			sendControlChange(104, type);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set delay parameter 1. The accepted value is not dependent on the delay type, and will always be between 0 and
	 * 4000. This always sets the time of the selected delay type.
	 *
	 * @param parameter1 the delay parameter 1
	 */
	public void setDelayParameter1(float parameter1) {
		try {
			// Delay uses MSB/LSB on 31 & 63
			sendControlChange(31, (int) (parameter1 / 128));
			sendControlChange(63, (int) (parameter1 % 128));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set delay parameter 2. The accepted value is not dependent on the delay type, and will always be between 0 and
	 * 10. This always sets the feedback/age of the selected delay type.
	 *
	 * @param parameter2 the delay parameter 2
	 */
	public void setDelayParameter2(float parameter2) {
		try {
			sendControlChange(105, (int) (parameter2 * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set delay parameter 3. The accepted value is dependent on the delay type:
	 * <ol>
	 *     <li>Studio (Freq): 0-10</li>
	 *     <li>Vintage (Freq): 0-10</li>
	 *     <li>Multi (Tap Pattern): 0-3</li>
	 *     <li>Reverse (Freq): 0-10</li>
	 * </ol>
	 *
	 * @param parameter3 the delay parameter 3
	 */
	public void setDelayParameter3(float parameter3, int delayType) {
		try {
			switch (delayType) {
				case 0, 1, 3 -> sendControlChange(106, (int) (parameter3 * 10));
				case 2 -> sendControlChange(106, (int) parameter3);
			}
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set delay parameter 4. The accepted value is not dependent on the delay type, and will always be between 0 and
	 * 10. This always sets the level of the selected delay type.
	 *
	 * @param parameter4 the delay parameter 4
	 */
	public void setDelayParameter4(float parameter4) {
		try {
			sendControlChange(107, (int) (parameter4 * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Toggle the state of reverb
	 *
	 * @param state the state of reverb
	 */
	public void toggleReverb(boolean state) {
		try {
			sendControlChange(108, state ? 1 : 0);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the reverb type. Must be an integer between 0 and 3. The reverb types are as follows:
	 * <ol>
	 *     <li>Room</li>
	 *     <li>Hall</li>
	 *     <li>Spring</li>
	 *     <li>Stadium</li>
	 * </ol>
	 *
	 * @param type the reverb type
	 */
	public void setReverbType(int type) {
		try {
			sendControlChange(109, type);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set reverb parameter 1. The accepted value is not dependent on the reverb type, and will always be between 0 and
	 * 10. This always sets the decay of the selected reverb type.
	 *
	 * @param parameter1 the reverb parameter 1
	 */
	public void setReverbParameter1(float parameter1) {
		try {
			sendControlChange(110, (int) (parameter1 * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set reverb parameter 2. The accepted value is not dependent on the reverb type, and will always be between 0 and
	 * 10. This always sets the pre-delay of the selected reverb type.
	 *
	 * @param parameter2 the reverb parameter 2
	 */
	public void setReverbParameter2(float parameter2) {
		try {
			sendControlChange(111, (int) (parameter2 * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set reverb parameter 3. The accepted value is not dependent on the reverb type, and will always be between 0 and
	 * 10. This always sets the tone of the selected reverb type.
	 *
	 * @param parameter3 the reverb parameter 3
	 */
	public void setReverbParameter3(float parameter3) {
		try {
			sendControlChange(112, (int) (parameter3 * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set reverb parameter 4. The accepted value is not dependent on the reverb type, and will always be between 0 and
	 * 10. This always sets the level of the selected reverb type.
	 *
	 * @param parameter4 the reverb parameter 4
	 */
	public void setReverbParameter4(float parameter4) {
		try {
			sendControlChange(113, (int) (parameter4 * 10));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Toggle the state of the built-in amp tuner
	 *
	 * @param state the state of the tuner
	 */
	public void toggleTuner(boolean state) {
		try {
			sendControlChange(52, state ? 1 : 0);
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the current amp configuration as a byte array
	 */
	public byte[] getAmpConfig() {
		try {
			sendSysexMessage(new byte[]{(byte) 0xF0, 0x00, 0x21, 0x15, 0x7F, 0x7F, 0x7F, 0x73, 0x01, 0x00, (byte) 0xF7});
			return receiveSysexMessage();
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the amp configuration for a specific preset as a byte array
	 */
	public byte[] getAmpConfig(int preset) {
		try {
			sendSysexMessage(new byte[]{(byte) 0xF0, 0x00, 0x21, 0x15, 0x7F, 0x7F, 0x7F, 0x72, 0x01, (byte) preset, (byte) 0xF7});
			return receiveSysexMessage();
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the amp hardware information in the interface.
	 */
	public void setAmpHardwareInformation() {
		try {
			AmpModel.load();
			sendSysexMessage(new byte[]{(byte) 0xF0, 0x00, 0x21, 0x15, 0x7F, 0x7F, 0x7F, 0x10, (byte) 0xF7});
			byte[] message = receiveSysexMessage();
			CODEInterfaceApplication.CONTROLLER.serialNumberTextField.setText(new String(message, 9, 18));

			int familyId = message[4] & 0xFF;
			int modelId = message[5] & 0xFF;
			int deviceId = message[6] & 0xFF;
			AmpModel model = AmpModel.getModel(familyId, modelId, deviceId);
			CODEInterfaceApplication.CONTROLLER.modelTextField.setText(model.ampName());

			int majorHardwareVersion = message[19] & 0xFF;
			int minorHardwareVersion = message[20] & 0xFF;
			CODEInterfaceApplication.CONTROLLER.revisionTextField.setText("v" + majorHardwareVersion + "." + minorHardwareVersion);

			int majorBootloaderVersion = message[21] & 0xFF;
			int minorBootloaderVersion = message[22] & 0xFF;
			CODEInterfaceApplication.CONTROLLER.bootloaderTextField.setText("v" + majorBootloaderVersion + "." + minorBootloaderVersion);

			int majorMcuVersion = message[27] & 0xFF;
			int minorMcuVersion = message[28] & 0xFF;
			CODEInterfaceApplication.CONTROLLER.mcuTextField.setText("v" + majorMcuVersion + "." + minorMcuVersion);

			int majorDspVersion = message[32] & 0xFF;
			int minorDspVersion = message[33] & 0xFF;
			CODEInterfaceApplication.CONTROLLER.dspTextField.setText("v" + majorDspVersion + "." + minorDspVersion);

			sendSysexMessage(new byte[]{(byte) 0xF0, 0x00, 0x21, 0x15, 0x7F, 0x7F, 0x7F, 0x62, 0x01, 0x04, (byte) 0xF7});
			message = receiveSysexMessage();
			CODEInterfaceApplication.CONTROLLER.bluetoothTextField.setText(new String(message, 11, 3));
		} catch (InvalidMidiDataException e) {
			throw new RuntimeException(e);
		}
	}
}