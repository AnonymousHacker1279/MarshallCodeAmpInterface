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
import org.semver4j.Semver;
import tech.anonymoushacker1279.marshallcodeampinterface.amp.AmpBLEInterface;
import tech.anonymoushacker1279.marshallcodeampinterface.amp.AmpConfig;
import tech.anonymoushacker1279.marshallcodeampinterface.amp.AmpUSBInterface;
import tech.anonymoushacker1279.marshallcodeampinterface.controller.BTScanningInterfaceController;
import tech.anonymoushacker1279.marshallcodeampinterface.controller.CODEInterfaceController;
import tech.anonymoushacker1279.marshallcodeampinterface.controller.ErrorDialogController;
import tech.anonymoushacker1279.marshallcodeampinterface.controller.UpdateAvailableController;
import tech.anonymoushacker1279.marshallcodeampinterface.midi.AmpMIDIInterface;
import tech.anonymoushacker1279.marshallcodeampinterface.midi.IOMIDIDevice;

import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

public class CODEInterfaceApplication extends Application {

	public static CODEInterfaceController CONTROLLER;
	public static AmpMIDIInterface INTERFACE;
	public static AmpConfig DEFAULT_CONFIG = AmpConfig.empty();
	public static ArrayList<AmpConfig> PRESETS = new ArrayList<>(100);
	public static boolean isClosing = false;

	public static final Logger LOGGER = LogManager.getLogger();

	public static Semver APP_VERSION = Semver.ZERO;
	public static String COMMIT = "Unknown";

	public static Stage MAIN_STAGE;

	@Override
	public void start(Stage stage) throws IOException {
		LOGGER.info("Starting Marshall CODE Amp Interface...");
		MAIN_STAGE = stage;
		Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());

		initializeDevices();

		LOGGER.debug("Loading main scene");
		FXMLLoader fxmlLoader = new FXMLLoader(CODEInterfaceApplication.class.getResource("views/main.fxml"));
		Scene scene = new Scene(fxmlLoader.load());
		stage.setTitle("Marshall CODE Interface");
		stage.getIcons().add(new Image(Objects.requireNonNull(CODEInterfaceApplication.class.getResourceAsStream("images/code50.png"))));
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();

		CONTROLLER = fxmlLoader.getController();
		CONTROLLER.setHostServices(getHostServices());

		if (INTERFACE instanceof AmpBLEInterface) {
			CONTROLLER.connectionMethodImageView.setImage(new Image(Objects.requireNonNull(CODEInterfaceApplication.class.getResourceAsStream("images/bluetooth.png"))));
		}

		new Thread(this::threadedSetup, "Async Initialization Handler").start();

		UpdateAvailableController.checkForUpdates(getHostServices());
	}

	private void threadedSetup() {
		while (!INTERFACE.isReady()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LOGGER.error(e);
			}
		}

		LOGGER.info("Device initialization complete, successfully connected via {}", INTERFACE instanceof AmpUSBInterface ? "USB" : "BLE");

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
			BTScanningInterfaceController.openDialog();
			INTERFACE = new AmpBLEInterface();
		}
	}

	public static void main(String[] args) {
		try {
			InputStream url = CODEInterfaceApplication.class.getResourceAsStream("app.properties");
			Properties properties = new Properties();
			properties.load(url);
			APP_VERSION = new Semver(properties.getProperty("version"));
			COMMIT = properties.getProperty("commit");
		} catch (IOException | NullPointerException e) {
			LOGGER.error("Failed to load version information from app.properties file", e);
		}

		launch();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		isClosing = true;
		INTERFACE.close();

		if (CONTROLLER.getAudioCapture() != null) {
			CONTROLLER.getAudioCapture().stopCapture();
		}

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

		Path path = Path.of(System.getenv("appdata"), "CODE Amp Interface");
		// Create the directory if it doesn't exist
		if (!path.toFile().exists()) {
			boolean created = path.toFile().mkdir();
			if (!created) {
				RuntimeException e = new RuntimeException("Failed to create application data directory");
				ErrorDialogController.openDialog("Unable to create a data directory for the application.", e.getMessage());
				LOGGER.fatal(e);
				System.exit(1);
			}
		}

		return path;
	}
}