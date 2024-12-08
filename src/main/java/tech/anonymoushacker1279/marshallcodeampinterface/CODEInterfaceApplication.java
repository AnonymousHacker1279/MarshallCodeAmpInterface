package tech.anonymoushacker1279.marshallcodeampinterface;

import atlantafx.base.theme.CupertinoDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tech.anonymoushacker1279.marshallcodeampinterface.amp.AmpConfig;
import tech.anonymoushacker1279.marshallcodeampinterface.midi.*;

import javax.sound.midi.*;
import java.io.IOException;
import java.util.ArrayList;

public class CODEInterfaceApplication extends Application {

	public static CODEInterfaceController CONTROLLER;
	public static AmpMIDIInterface INTERFACE;
	public static AmpConfig DEFAULT_CONFIG;
	public static ArrayList<AmpConfig> PRESETS = new ArrayList<>(100);

	@Override
	public void start(Stage stage) throws IOException {
		initializeDevices();

		DEFAULT_CONFIG = AmpConfig.create(INTERFACE);
		for (int i = 0; i < 100; i++) {
			PRESETS.add(AmpConfig.create(INTERFACE, i));
		}

		Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());

		FXMLLoader fxmlLoader = new FXMLLoader(CODEInterfaceApplication.class.getResource("main-view.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 1280, 1100);
		stage.setTitle("Marshall CODE Interface");
		stage.setScene(scene);
		stage.show();
		CONTROLLER = fxmlLoader.getController();
		CONTROLLER.setHostServices(getHostServices());

		AmpConfig.setInterfaceValues(CONTROLLER, DEFAULT_CONFIG);
	}

	private static void initializeDevices() {
		try {
			IOMIDIDevice device = IOMIDIDevice.createWrapper("CODE");
			INTERFACE = new AmpUSBInterface(device);
		} catch (MidiUnavailableException e) {
			throw new RuntimeException("No CODE device found");
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