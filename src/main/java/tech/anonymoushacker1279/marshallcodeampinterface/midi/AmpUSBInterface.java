package tech.anonymoushacker1279.marshallcodeampinterface.midi;

import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;
import tech.anonymoushacker1279.marshallcodeampinterface.TuningDialogController;
import tech.anonymoushacker1279.marshallcodeampinterface.amp.AmpConfig;

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
	public byte[] receiveSysexMessage() {
		while (INCOMING_SYSEX_QUEUE.isEmpty()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		byte[] message = INCOMING_SYSEX_QUEUE.getFirst();
		INCOMING_SYSEX_QUEUE.removeFirst();
		return message;
	}

	private class MessageReceiver implements Receiver {

		@Override
		public void send(MidiMessage message, long timeStamp) {
			if (message instanceof SysexMessage sysexMessage) {
				INCOMING_SYSEX_QUEUE.add(sysexMessage.getMessage());
			}

			if (message instanceof ShortMessage shortMessage) {
				if (shortMessage.getCommand() == ShortMessage.CONTROL_CHANGE) {
					if (shortMessage.getData1() == 52) {
						if (shortMessage.getData2() == 1) {
							TuningDialogController.openDialog(AmpUSBInterface.this::setTuningDialogController);
						} else {
							if (getTuningDialogController() != null) {
								TuningDialogController.closeDialog();
								setTuningDialogController(null);
							}
						}
					}

					AmpConfig.updateInterface(CODEInterfaceApplication.CONTROLLER, CODEInterfaceApplication.DEFAULT_CONFIG, shortMessage.getData1(), shortMessage.getData2());
				}
				if (shortMessage.getCommand() == ShortMessage.PROGRAM_CHANGE) {
					AmpConfig.setInterfaceValues(CODEInterfaceApplication.CONTROLLER, CODEInterfaceApplication.PRESETS.get(shortMessage.getData1()));
					CODEInterfaceApplication.CONTROLLER.presetSearchTextField.clear();
				}

				if (shortMessage.getCommand() == ShortMessage.POLY_PRESSURE && getTuningDialogController() != null) {
					getTuningDialogController().updateTuner(shortMessage.getData1(), shortMessage.getData2());
				}
			}
		}

		@Override
		public void close() {
		}
	}
}