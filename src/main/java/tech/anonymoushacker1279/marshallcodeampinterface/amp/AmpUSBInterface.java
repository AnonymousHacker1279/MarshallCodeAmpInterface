package tech.anonymoushacker1279.marshallcodeampinterface.amp;

import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;
import tech.anonymoushacker1279.marshallcodeampinterface.midi.AmpMIDIInterface;
import tech.anonymoushacker1279.marshallcodeampinterface.midi.IOMIDIDevice;

import javax.sound.midi.*;
import java.util.ArrayList;

public class AmpUSBInterface extends AmpMIDIInterface {

	private final IOMIDIDevice device;
	protected static final ArrayList<byte[]> INCOMING_SYSEX_QUEUE = new ArrayList<>();

	public AmpUSBInterface(IOMIDIDevice device) {
		this.device = device;
		device.setReceiver(new MessageReceiver());
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void sendControlChange(int control, int value) throws InvalidMidiDataException {
		device.send(new ShortMessage(ShortMessage.CONTROL_CHANGE, control, value), -1);
	}

	@Override
	public void sendProgramChange(int program) throws InvalidMidiDataException {
		device.send(new ShortMessage(ShortMessage.PROGRAM_CHANGE, program, 0), -1);
	}

	@Override
	public void sendSysexMessage(byte[] message) throws InvalidMidiDataException {
		device.send(new SysexMessage(message, message.length), -1);
	}

	@Override
	public void close() {
		device.close();
	}

	@Override
	public byte[] receiveSysexMessage(int expectedLength) {
		waitForSysexMessage();

		byte[] message = INCOMING_SYSEX_QUEUE.getFirst();
		while (message.length != expectedLength) {
			INCOMING_SYSEX_QUEUE.removeFirst();

			if (INCOMING_SYSEX_QUEUE.isEmpty()) {
				waitForSysexMessage();
			}

			message = INCOMING_SYSEX_QUEUE.getFirst();
		}

		INCOMING_SYSEX_QUEUE.removeFirst();
		return message;
	}

	private void waitForSysexMessage() {
		while (INCOMING_SYSEX_QUEUE.isEmpty()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private class MessageReceiver implements Receiver {

		@Override
		public void send(MidiMessage message, long timeStamp) {
			if (message instanceof SysexMessage sysexMessage) {
				CODEInterfaceApplication.LOGGER.debug("Received sysex message: {}", sysexMessage.getMessage());
				INCOMING_SYSEX_QUEUE.add(sysexMessage.getMessage());
			}

			if (message instanceof ShortMessage shortMessage) {
				if (shortMessage.getCommand() == ShortMessage.CONTROL_CHANGE) {
					CODEInterfaceApplication.LOGGER.debug("Received control change: {} {}", shortMessage.getData1(), shortMessage.getData2());
					handleControlChange(shortMessage.getData1(), shortMessage.getData2());
				}
				if (shortMessage.getCommand() == ShortMessage.PROGRAM_CHANGE) {
					CODEInterfaceApplication.LOGGER.debug("Received program change: {}", shortMessage.getData1());
					handlePresetChange(shortMessage.getData1());
				}

				if (shortMessage.getCommand() == ShortMessage.POLY_PRESSURE && getTuningDialogController() != null) {
					CODEInterfaceApplication.LOGGER.debug("Received tuning data: {} {}", shortMessage.getData1(), shortMessage.getData2());
					handleTuningDataChange(shortMessage.getData1(), shortMessage.getData2());
				}
			}
		}

		@Override
		public void close() {
		}
	}
}