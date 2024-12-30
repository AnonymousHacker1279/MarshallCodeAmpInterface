package tech.anonymoushacker1279.marshallcodeampinterface;

import atlantafx.base.theme.CupertinoDark;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.anonymoushacker1279.marshallcodeampinterface.amp.AmpBLEInterface;
import tech.anonymoushacker1279.marshallcodeampinterface.amp.AmpConfig;
import tech.anonymoushacker1279.marshallcodeampinterface.amp.AmpUSBInterface;
import tech.anonymoushacker1279.marshallcodeampinterface.controller.BTScanningInterfaceController;
import tech.anonymoushacker1279.marshallcodeampinterface.controller.CODEInterfaceController;
import tech.anonymoushacker1279.marshallcodeampinterface.midi.AmpMIDIInterface;
import tech.anonymoushacker1279.marshallcodeampinterface.midi.IOMIDIDevice;

import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

public class CODEInterfaceApplication extends Application {

	public static CODEInterfaceController CONTROLLER;
	public static AmpMIDIInterface INTERFACE;
	public static AmpConfig DEFAULT_CONFIG = AmpConfig.empty();
	public static ArrayList<AmpConfig> PRESETS = new ArrayList<>(100);
	public static boolean isClosing = false;

	public static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void start(Stage stage) throws IOException {
		LOGGER.info("Starting Marshall CODE Amp Interface...");
		initializeDevices();

		Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());

		LOGGER.debug("Loading main scene");
		FXMLLoader fxmlLoader = new FXMLLoader(CODEInterfaceApplication.class.getResource("main-view.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 1260, 1070);
		stage.setTitle("Marshall CODE Interface");
		stage.setMaxWidth(1260);
		stage.setMaxHeight(830);
		stage.getIcons().add(new Image(Objects.requireNonNull(CODEInterfaceApplication.class.getResourceAsStream("code50.png"))));
		stage.setScene(scene);
		stage.show();
		CONTROLLER = fxmlLoader.getController();
		CONTROLLER.setHostServices(getHostServices());

		if (INTERFACE instanceof AmpBLEInterface) {
			CONTROLLER.connectionMethodImageView.setImage(new Image(Objects.requireNonNull(CODEInterfaceApplication.class.getResourceAsStream("bluetooth.png"))));
		}

		new Thread(this::threadedSetup, "Async Initialization Handler").start();
	}

	private void threadedSetup() {
		while (!INTERFACE.isReady()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LOGGER.error(e);
			}
		}

		INTERFACE.setAmpHardwareInformation();

		LOGGER.info("Loading presets...");
		DEFAULT_CONFIG = AmpConfig.create(INTERFACE);
		AmpConfig.setInterfaceValues(CONTROLLER, DEFAULT_CONFIG);

		for (int i = 0; i < 100; i++) {
			AmpConfig preset = AmpConfig.create(INTERFACE, i);
			PRESETS.add(preset);
			Platform.runLater(() -> CONTROLLER.presetListView.getItems().add(preset.presetName));

			double progress = (double) i / 100;
			Platform.runLater(() -> CONTROLLER.presetLoadingIndicator.setProgress(progress));
		}

		Platform.runLater(() -> CONTROLLER.presetLoadingIndicator.setVisible(false));
		LOGGER.info("Preset loading complete");
	}

	private static void initializeDevices() {
		LOGGER.info("Beginning device initialization");
		try {
			LOGGER.debug("Attempting USB connection...");
			IOMIDIDevice device = IOMIDIDevice.createWrapper("CODE");
			INTERFACE = new AmpUSBInterface(device);
		} catch (MidiUnavailableException | RuntimeException e) {
			LOGGER.debug("USB connection failed, attempting BLE connection...");

			try {
				LOGGER.debug("Attempting BLE connection...");
				BTScanningInterfaceController.openDialog();
				INTERFACE = new AmpBLEInterface();
			} catch (RuntimeException e2) {
				LOGGER.fatal("Failed to connect to device via USB or BLE, exiting...");
				LOGGER.fatal(e2);
				System.exit(1);
			}
		}

		LOGGER.info("Device initialization complete, successfully connected via {}", INTERFACE instanceof AmpUSBInterface ? "USB" : "BLE");
	}

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		isClosing = true;
		INTERFACE.close();
		LOGGER.info("Exiting Marshall CODE Amp Interface...");
	}

	/**
	 * Get the root path for application data to be stored. While running in the IDE, this is the root of the project.
	 * Otherwise, it should be the user's application data directory.
	 *
	 * @return the root path for application data
	 */
	public static Path getRootDataPath() {
		// Check for RUNNING_IN_IDE environment variable
		String runningInIDE = System.getenv("RUNNING_IN_IDE");
		if (runningInIDE != null && runningInIDE.equals("true")) {
			return Path.of(System.getProperty("user.dir"), "runtimeData");
		}

		Path path = Path.of(System.getProperty("appdata"), "CODE Amp Interface");
		// Create the directory if it doesn't exist
		if (!path.toFile().exists()) {
			boolean created = path.toFile().mkdir();
			if (!created) {
				throw new RuntimeException("Failed to create application data directory");
			}
		}

		return path;
	}
}