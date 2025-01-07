package tech.anonymoushacker1279.marshallcodeampinterface.midi;

import javax.sound.midi.*;

public class IOMIDIDevice {

	private final MidiDevice inputDevice;
	private final MidiDevice outputDevice;
	private final Transmitter transmitter;
	private final Receiver receiver;

	private IOMIDIDevice(MidiDevice inputDevice, MidiDevice outputDevice) throws MidiUnavailableException {
		this.inputDevice = inputDevice;
		this.outputDevice = outputDevice;

		// Open devices
		this.inputDevice.open();
		this.outputDevice.open();

		// Get transmitter and receiver
		this.transmitter = this.inputDevice.getTransmitter();
		this.receiver = this.outputDevice.getReceiver();
	}

	public void send(MidiMessage message, long timeStamp) {
		if (receiver != null) {
			receiver.send(message, timeStamp);
		}
	}

	public void setReceiver(Receiver customReceiver) {
		this.transmitter.setReceiver(customReceiver);
	}

	public void close() {
		if (inputDevice.isOpen()) {
			inputDevice.close();
		}
		if (outputDevice.isOpen()) {
			outputDevice.close();
		}
	}

	/**
	 * Create a wrapper for a MIDI device with the given name. The wrapper will have a transmitter and receiver.
	 *
	 * @param deviceName The name of the MIDI device to wrap
	 * @return The wrapped MIDI device
	 */
	public static IOMIDIDevice createWrapper(String deviceName) throws MidiUnavailableException {
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		MidiDevice inputDevice = null;
		MidiDevice outputDevice = null;

		for (MidiDevice.Info info : infos) {
			MidiDevice device = MidiSystem.getMidiDevice(info);
			if (device.getMaxTransmitters() != 0 && info.getName().equals(deviceName)) {
				inputDevice = device;
			}
			if (device.getMaxReceivers() != 0 && info.getName().equals(deviceName)) {
				outputDevice = device;
			}
		}

		if (inputDevice != null && outputDevice != null) {
			return new IOMIDIDevice(inputDevice, outputDevice);
		} else {
			throw new IllegalArgumentException("Could not find matching input and output devices for name: " + deviceName);
		}
	}
}