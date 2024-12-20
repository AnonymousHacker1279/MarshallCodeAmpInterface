package tech.anonymoushacker1279.marshallcodeampinterface;

import atlantafx.base.theme.CupertinoDark;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import tech.anonymoushacker1279.marshallcodeampinterface.amp.AmpBLEInterface;
import tech.anonymoushacker1279.marshallcodeampinterface.amp.AmpConfig;
import tech.anonymoushacker1279.marshallcodeampinterface.amp.AmpUSBInterface;
import tech.anonymoushacker1279.marshallcodeampinterface.controller.BTScanningInterfaceController;
import tech.anonymoushacker1279.marshallcodeampinterface.controller.CODEInterfaceController;
import tech.anonymoushacker1279.marshallcodeampinterface.midi.AmpMIDIInterface;
import tech.anonymoushacker1279.marshallcodeampinterface.midi.IOMIDIDevice;

import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CODEInterfaceApplication extends Application {

	public static CODEInterfaceController CONTROLLER;
	public static AmpMIDIInterface INTERFACE;
	public static AmpConfig DEFAULT_CONFIG = AmpConfig.empty();
	public static ArrayList<AmpConfig> PRESETS = new ArrayList<>(100);

	@Override
	public void start(Stage stage) throws IOException {
		initializeDevices();

		Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());

		FXMLLoader fxmlLoader = new FXMLLoader(CODEInterfaceApplication.class.getResource("main-view.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 1280, 1100);
		stage.setTitle("Marshall CODE Interface");
		stage.setScene(scene);
		stage.show();
		CONTROLLER = fxmlLoader.getController();
		CONTROLLER.setHostServices(getHostServices());

		if (INTERFACE instanceof AmpBLEInterface) {
			InputStream stream = CODEInterfaceApplication.class.getResourceAsStream("bluetooth.png");

			if (stream == null) {
				throw new RuntimeException("Failed to load BLE image");
			}

			CONTROLLER.connectionMethodImageView.setImage(new Image(stream));
		}

		new Thread(this::loadPresets).start();
	}

	private void loadPresets() {
		while (!INTERFACE.isReady()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println("Interrupted while waiting for interface to be ready");
			}
		}

		DEFAULT_CONFIG = AmpConfig.create(INTERFACE);
		AmpConfig.setInterfaceValues(CONTROLLER, DEFAULT_CONFIG);

		INTERFACE.setAmpHardwareInformation();

		for (int i = 0; i < 100; i++) {
			AmpConfig preset = AmpConfig.create(INTERFACE, i);
			PRESETS.add(preset);
			Platform.runLater(() -> CONTROLLER.presetListView.getItems().add(preset.presetName));

			double progress = (double) i / 100;
			Platform.runLater(() -> CONTROLLER.presetLoadingIndicator.setProgress(progress));
		}

		Platform.runLater(() -> CONTROLLER.presetLoadingIndicator.setVisible(false));
	}

	private static void initializeDevices() {
		try {
			System.out.println("Attempting USB connection...");
			IOMIDIDevice device = IOMIDIDevice.createWrapper("CODE");
			INTERFACE = new AmpUSBInterface(device);
		} catch (MidiUnavailableException | RuntimeException e) {
			System.out.println("USB connection failed, attempting BLE connection...");

			try {
				BTScanningInterfaceController.openDialog();
				INTERFACE = new AmpBLEInterface();
			} catch (RuntimeException e2) {
				System.out.println("BLE connection failed, exiting...");
				e2.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		INTERFACE.close();
	}
}