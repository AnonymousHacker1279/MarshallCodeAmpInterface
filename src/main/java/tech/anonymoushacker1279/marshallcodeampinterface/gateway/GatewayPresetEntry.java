package tech.anonymoushacker1279.marshallcodeampinterface.gateway;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;
import tech.anonymoushacker1279.marshallcodeampinterface.amp.AmpConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record GatewayPresetEntry(String presetName, String trackName, String artistName, int downloadCount,
                                 String coverImageURL, Supplier<?> downloadAction) {

	/**
	 * Create a list of GatewayPresetEntry objects from a JSON array, used during preset search
	 *
	 * @param jsonArray   the JSON array
	 * @param client      the HttpClient
	 * @param accessToken the user's MyMarshall access token
	 * @return a list of GatewayPresetEntry objects
	 */
	public static List<GatewayPresetEntry> createFromJSON(JsonArray jsonArray, HttpClient client, String accessToken) {
		ArrayList<GatewayPresetEntry> entries = new ArrayList<>(jsonArray.size());

		jsonArray.asList().forEach((element) -> {
			JsonObject searchObject = element.getAsJsonObject();
			JsonObject presetObject = getPreset(client, accessToken, searchObject.get("serverPresetId").getAsString());

			String presetName = presetObject.get("name").getAsString();
			String trackName = !presetObject.get("trackName").isJsonNull() ? presetObject.get("trackName").getAsString() : "";
			String artistName = !presetObject.get("artistName").isJsonNull() ? presetObject.get("artistName").getAsString() : "";
			int downloadCount = presetObject.get("downloads").getAsInt();
			String coverImageURL = presetObject.get("coverImageUrl").getAsString();
			String controlData = presetObject.get("controlData").getAsJsonArray().get(0).getAsJsonObject().get("controlData").getAsString();

			entries.add(new GatewayPresetEntry(presetName, trackName, artistName, downloadCount, coverImageURL, handleDownload(controlData, presetName)));
		});

		return entries;
	}

	/**
	 * Get the details of a specific preset
	 *
	 * @param client      the HttpClient
	 * @param accessToken the user's MyMarshall access token
	 * @param presetId    the ID of the preset
	 * @return the preset details
	 */
	private static JsonObject getPreset(HttpClient client, String accessToken, String presetId) {
		String presetDetailsURL = "https://my.marshall.com/api/gateway/preset/details";

		JsonObject body = new JsonObject();
		JsonArray presetIds = new JsonArray();
		presetIds.add(presetId);
		body.add("serverPresetIds", presetIds);

		HttpRequest request = HttpRequest.newBuilder()
				.POST(HttpRequest.BodyPublishers.ofString(body.toString()))
				.uri(URI.create(presetDetailsURL))
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.header("Authorization", "Bearer " + accessToken)
				.header("api-version", "2")
				.header("AppId", "marshall.gateway.v2.client")
				.header("DeviceOS", "Android")
				.header("DeviceOSVersion", "13")
				.header("User-Agent", "Marshall Gateway/2.1.7")
				.build();

		try {
			String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
			Gson gson = new Gson();
			JsonArray array = gson.fromJson(response, JsonArray.class);
			return array.get(0).getAsJsonObject();
		} catch (Exception e) {
			CODEInterfaceApplication.LOGGER.error("Failed to get preset details", e);
			return new JsonObject();
		}
	}

	/**
	 * Handle the download action for a preset
	 *
	 * @param controlData the control data for the preset
	 * @param presetName  the name of the preset
	 * @return a Supplier to handle the download action
	 */
	private static Supplier<?> handleDownload(String controlData, String presetName) {
		return () -> {
			try {
				JsonObject jsonObject = controlDataToJson(controlData);
				AmpConfig config = AmpConfig.create(jsonObject);
				AmpConfig.setInterfaceValues(CODEInterfaceApplication.CONTROLLER, config);
				CODEInterfaceApplication.CONTROLLER.presetNameTextField.setText(presetName);
				CODEInterfaceApplication.LOGGER.info("Downloaded preset: {}", presetName);
				return true;
			} catch (Exception e) {
				CODEInterfaceApplication.LOGGER.error("Failed to download preset", e);
				return false;
			}
		};
	}

	/**
	 * Convert preset control data to a JSON object, which can be loaded as an {@link AmpConfig} object. Why Marshall
	 * decided to store the control data as a string in the first place is beyond me.
	 *
	 * @param controlData the control data
	 * @return the control data as a JSON object
	 */
	private static JsonObject controlDataToJson(String controlData) {
		JsonObject jsonObject = new JsonObject();

		String regex = "(CODE1:PEDAL,|PRE_AMP,|MODULATION,|DELAY,|REVERB,|POWER_AMP,|CABINET,)";
		controlData = controlData.replaceAll(regex, "");
		controlData = controlData.replace("|", ",");

		String[] controlDataArray = controlData.split(",");

		jsonObject.addProperty("preFXEnabled", Integer.parseInt(controlDataArray[0]) == 1);
		jsonObject.addProperty("preFXType", Integer.parseInt(controlDataArray[1], 16));
		jsonObject.addProperty("preFXParameter1", (int) (Float.parseFloat(controlDataArray[2]) * 10));
		jsonObject.addProperty("preFXParameter2", (int) (Float.parseFloat(controlDataArray[3]) * 10));
		jsonObject.addProperty("preFXParameter3", (int) (Float.parseFloat(controlDataArray[4]) * 10));
		jsonObject.addProperty("preFXParameter4", (int) (Float.parseFloat(controlDataArray[5]) * 10));

		jsonObject.addProperty("ampEnabled", Integer.parseInt(controlDataArray[6]) == 1);
		jsonObject.addProperty("ampType", Integer.parseInt(controlDataArray[7], 16));
		jsonObject.addProperty("gate", (int) (Float.parseFloat(controlDataArray[8]) * 10));
		jsonObject.addProperty("gain", (int) (Float.parseFloat(controlDataArray[9]) * 10));
		jsonObject.addProperty("volume", (int) (Float.parseFloat(controlDataArray[10]) * 10));
		jsonObject.addProperty("bass", (int) (Float.parseFloat(controlDataArray[11]) * 10));
		jsonObject.addProperty("middle", (int) (Float.parseFloat(controlDataArray[12]) * 10));
		jsonObject.addProperty("treble", (int) (Float.parseFloat(controlDataArray[13]) * 10));

		jsonObject.addProperty("modulationEnabled", Integer.parseInt(controlDataArray[14]) == 1);
		jsonObject.addProperty("modulationType", Integer.parseInt(controlDataArray[15], 16));
		jsonObject.addProperty("modulationParameter1", (int) (Float.parseFloat(controlDataArray[16]) * 10));
		jsonObject.addProperty("modulationParameter2", (int) (Float.parseFloat(controlDataArray[17]) * 10));
		jsonObject.addProperty("modulationParameter3", (int) (Float.parseFloat(controlDataArray[18]) * 10));
		jsonObject.addProperty("modulationParameter4", (int) (Float.parseFloat(controlDataArray[19]) * 10));

		jsonObject.addProperty("delayEnabled", Integer.parseInt(controlDataArray[20]) == 1);
		jsonObject.addProperty("delayType", Integer.parseInt(controlDataArray[21], 16));
		jsonObject.addProperty("delayParameter1", (int) (Float.parseFloat(controlDataArray[22]) * 10));
		jsonObject.addProperty("delayParameter2", (int) (Float.parseFloat(controlDataArray[23]) * 10));
		jsonObject.addProperty("delayParameter3", (int) (Float.parseFloat(controlDataArray[24]) * 10));
		jsonObject.addProperty("delayParameter4", (int) (Float.parseFloat(controlDataArray[25]) * 10));

		jsonObject.addProperty("reverbEnabled", Integer.parseInt(controlDataArray[26]) == 1);
		jsonObject.addProperty("reverbType", Integer.parseInt(controlDataArray[27], 16));
		jsonObject.addProperty("reverbParameter1", (int) (Float.parseFloat(controlDataArray[28]) * 10));
		jsonObject.addProperty("reverbParameter2", (int) (Float.parseFloat(controlDataArray[29]) * 10));
		jsonObject.addProperty("reverbParameter3", (int) (Float.parseFloat(controlDataArray[30]) * 10));
		jsonObject.addProperty("reverbParameter4", (int) (Float.parseFloat(controlDataArray[31]) * 10));

		jsonObject.addProperty("powerEnabled", Integer.parseInt(controlDataArray[32]) == 1);
		jsonObject.addProperty("powerType", Integer.parseInt(controlDataArray[33], 16));
		jsonObject.addProperty("presence", (int) (Float.parseFloat(controlDataArray[34]) * 10));
		jsonObject.addProperty("resonance", (int) (Float.parseFloat(controlDataArray[35]) * 10));

		jsonObject.addProperty("cabEnabled", Integer.parseInt(controlDataArray[36]) == 1);
		jsonObject.addProperty("cabType", Integer.parseInt(controlDataArray[37], 16));

		return jsonObject;
	}
}