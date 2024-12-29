package tech.anonymoushacker1279.marshallcodeampinterface.amp;

import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;
import tech.anonymoushacker1279.marshallcodeampinterface.controller.BTScanningInterfaceController;
import tech.anonymoushacker1279.marshallcodeampinterface.midi.AmpMIDIInterface;
import tech.anonymoushacker1279.marshallcodeampinterface.util.BTConfigurationData;
import tech.anonymoushacker1279.orionble.OrionBLE;
import tech.anonymoushacker1279.orionble.devices.BLEDevice;
import tech.anonymoushacker1279.orionble.devices.DeviceFilter;
import tech.anonymoushacker1279.orionble.gatt.GATTCharacteristic;
import tech.anonymoushacker1279.orionble.gatt.GATTNotification;
import tech.anonymoushacker1279.orionble.gatt.GATTService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class AmpBLEInterface extends AmpMIDIInterface {

	private OrionBLE orion;
	private boolean isConnected = false;
	private static BLEDevice device;
	private static final GATTService service = new GATTService("14839ac4-7d7e-415c-9a42-167340cf2339", true);
	private static final GATTCharacteristic rwCharacteristic = new GATTCharacteristic(
			"ba04c4b2-892b-43be-b69c-5d13f2195392",
			"",
			List.of(GATTCharacteristic.GATTProperties.READ, GATTCharacteristic.GATTProperties.WRITE_WITHOUT_RESPONSE, GATTCharacteristic.GATTProperties.WRITE)
	);
	private static final GATTCharacteristic notifyCharacteristic = new GATTCharacteristic(
			"0734594a-a8e7-4b1a-a6b1-cd5243059a57",
			"",
			List.of(GATTCharacteristic.GATTProperties.NOTIFY)
	);

	private int[] lastSysexMessage;

	public AmpBLEInterface() {
		new Thread(this::initializeConnection, "BLE Connection Initializer").start();
	}

	private void initializeConnection() {
		CODEInterfaceApplication.LOGGER.debug("Initializing OrionBLE");
		orion = new OrionBLE("http://localhost", 5249);
		orion.waitForConnection(5);

		BTConfigurationData config = BTConfigurationData.load();

		if (config == null) {
			DeviceFilter filter = new DeviceFilter.Builder().namePrefix("CODE").build();
			try {
				CODEInterfaceApplication.LOGGER.debug("Discovering CODE devices...");
				device = orion.discoverDevices(filter).getFirst();
				new BTConfigurationData(device.name(), device.address(), device.isPaired()).save();
			} catch (NoSuchElementException e) {
				throw new RuntimeException("No CODE device found");
			}
		} else {
			device = new BLEDevice(config.name(), config.address(), config.isPaired());
		}

		CODEInterfaceApplication.LOGGER.debug("Registering BLE notify event and starting notification listener");
		orion.registerNotifyEvent(device, service, notifyCharacteristic);
		orion.startNotificationListener(device, service, notifyCharacteristic, this::handleIncomingMessage, 500);

		BTScanningInterfaceController.closeDialog();
		isConnected = true;
	}

	@Override
	public boolean isReady() {
		return isConnected;
	}

	@Override
	public void sendControlChange(int control, int value) {
		if (!isReady()) {
			return;
		}

		orion.writeCharacteristic(device, service, rwCharacteristic, new int[]{176, control, value});
	}

	@Override
	public void sendProgramChange(int program) {
		if (!isReady()) {
			return;
		}

		orion.writeCharacteristic(device, service, rwCharacteristic, new int[]{192, program});
	}

	@Override
	public void sendSysexMessage(byte[] message) {
		// Convert byte[] to int[]
		int[] intMessage = new int[message.length];
		for (int i = 0; i < message.length; i++) {
			intMessage[i] = message[i] & 0xFF;
		}
		orion.pauseNotificationListener(device, service, notifyCharacteristic);
		orion.writeCharacteristic(device, service, rwCharacteristic, intMessage);
		lastSysexMessage = intMessage;
	}

	@Override
	public byte[] receiveSysexMessage() {
		List<GATTNotification> notifications;
		do {
			notifications = orion.getNotifications(device, service, notifyCharacteristic);

			if (!notifications.isEmpty()) {
				String value = notifications.getFirst().value();

				// Convert to byte array
				List<Integer> message = Stream.of(value.split(" ")).map(Integer::parseInt).toList();
				byte[] byteArray = new byte[message.size()];
				for (int i = 0; i < message.size(); i++) {
					byteArray[i] = message.get(i).byteValue();
				}

				boolean isValidResponse = validateSysexMessage(byteArray);
				if (!isValidResponse) {
					// Attempt to resend the last message
					CODEInterfaceApplication.LOGGER.warn("Invalid response received, resending last message");
					orion.writeCharacteristic(device, service, rwCharacteristic, lastSysexMessage);
					notifications.clear();
					continue;
				}

				orion.resumeNotificationListener(device, service, notifyCharacteristic);
				lastSysexMessage = null;
				return byteArray;
			}

			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		} while (notifications.isEmpty());

		return new byte[0];
	}

	@Override
	public void close() {
		CODEInterfaceApplication.LOGGER.debug("Unregistering BLE notify event and stopping notification listener");
		orion.stopNotificationListener(device, service, notifyCharacteristic);
		orion.unregisterNotifyEvent(device, service, notifyCharacteristic);
	}

	private void handleIncomingMessage(GATTNotification notification) {
		String message = notification.value();
		List<Integer> messageList = Stream.of(message.split(" ")).map(Integer::parseInt).toList();

		switch (messageList.getFirst()) {
			case 176 -> {
				// Control change
				int control = messageList.get(1);
				int value = messageList.get(2);
				CODEInterfaceApplication.LOGGER.debug("Received control change: {} {}", control, value);
				handleControlChange(control, value);
			}
			case 192 -> {
				// Program change
				int program = messageList.get(1);
				CODEInterfaceApplication.LOGGER.debug("Received program change: {}", program);
				handlePresetChange(program);
			}
			case 160 -> {
				// Polyphonic key pressure (tuning data)
				int key = messageList.get(1);
				int pressure = messageList.get(2);
				CODEInterfaceApplication.LOGGER.debug("Received tuning data: {} {}", key, pressure);
				handleTuningDataChange(key, pressure);
			}
			case 240 -> {
				// System exclusive message, these should be ignored as they are explicitly handled when expected
			}
			default -> CODEInterfaceApplication.LOGGER.warn("Received unknown message type: {}", message);
		}
	}
}