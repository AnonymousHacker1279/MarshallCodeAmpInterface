package tech.anonymoushacker1279.marshallcodeampinterface.amp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public record AmpModel(String ampName, int familyId, int modelId, int deviceId, String ampImage) {

	private static List<AmpModel> ALL_MODELS;

	public static void load() {
		Gson gson = new Gson();
		InputStream inputStream = CODEInterfaceApplication.class.getResourceAsStream("amp_models.json");

		if (inputStream != null) {
			CODEInterfaceApplication.LOGGER.debug("Loading amp model information");

			try (Reader reader = new InputStreamReader(inputStream)) {
				ALL_MODELS = gson.fromJson(reader, TypeToken.getParameterized(List.class, AmpModel.class).getType());
			} catch (IOException e) {
				CODEInterfaceApplication.LOGGER.error("Failed to load amp models", e);
			}
		} else {
			throw new RuntimeException("Failed to load amp models");
		}
	}

	public static AmpModel getModel(int familyId, int modelId, int deviceId) {
		for (AmpModel model : ALL_MODELS) {
			if (model.familyId() == familyId && model.modelId() == modelId && model.deviceId() == deviceId) {
				return model;
			}
		}

		throw new IllegalArgumentException("No model found with family ID " + familyId + ", model ID " + modelId + ", and device ID " + deviceId);
	}
}