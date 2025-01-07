package tech.anonymoushacker1279.marshallcodeampinterface.util;

import com.google.gson.Gson;
import org.jetbrains.annotations.Nullable;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public record BTConfigurationData(String name, String address, boolean isPaired) {

	/**
	 * Save a BLE device configuration to disk, to improve application startup performance. This allows discovery to be
	 * skipped if the device is known and still exists.
	 */
	public void save() {
		Gson gson = new Gson();
		String json = gson.toJson(this);

		try {
			CODEInterfaceApplication.LOGGER.info("Saving Bluetooth configuration data");
			gson.newJsonWriter(new FileWriter(CODEInterfaceApplication.getRootDataPath() + "/btconfig.json")).jsonValue(json).close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Load a BLE device configuration from disk.
	 *
	 * @return the configuration data, or null if it could not be loaded
	 */
	@Nullable
	public static BTConfigurationData load() {
		Gson gson = new Gson();
		try {
			CODEInterfaceApplication.LOGGER.info("Loading Bluetooth configuration data");
			Reader reader = Files.newBufferedReader(Paths.get(CODEInterfaceApplication.getRootDataPath() + "/btconfig.json"));
			return gson.fromJson(reader, BTConfigurationData.class);
		} catch (IOException e) {
			CODEInterfaceApplication.LOGGER.info("Failed to find a configuration file, skipping");
			return null;
		}
	}
}