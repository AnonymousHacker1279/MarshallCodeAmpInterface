package tech.anonymoushacker1279.marshallcodeampinterface.gateway;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GatewayAPIHandler {

	private final HttpClient httpClient = HttpClient.newHttpClient();
	@Nullable
	private String accessToken;

	private boolean isLoggedIn = false;

	/**
	 * Attempt to log into the Marshall Gateway API using the stored credentials
	 *
	 * @return true if the login was successful
	 */
	public boolean tryKeystoreLogin() {
		// Check if the user has already logged in
		String[] credentials;

		try {
			credentials = CODEInterfaceApplication.KEYSTORE_HANDLER.loadCredentials();
		} catch (NullPointerException e) {
			credentials = null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (credentials != null) {
			return login(credentials[0], credentials[1]);
		}

		return false;
	}

	/**
	 * Log into the Marshall Gateway API, storing the access token if successful
	 *
	 * @param username the user's username
	 * @param password the user's password
	 * @return true if the login was successful
	 */
	public boolean login(String username, String password) {
		// Get an access token for this account
		String tokenURL = "https://my.marshall.com/token";

		// Define JSON request body
		String requestBodyString = "grant_type=" + URLEncoder.encode("password", StandardCharsets.UTF_8)
				+ "&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)
				+ "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);

		// Make the request
		HttpRequest request = HttpRequest.newBuilder()
				.POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
				.uri(URI.create(tokenURL))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("Accept", "application/json")
				.build();

		// Send the request
		try {
			CODEInterfaceApplication.LOGGER.debug("Logging into Marshall Gateway API");

			String response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
			// Convert the response to a JSON object
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

			if (jsonObject.get("error") != null) {
				String error = jsonObject.get("error").getAsString();
				String errorDescription = jsonObject.get("error_description").getAsString();

				if (error.equals("invalid_grant")) {
					CODEInterfaceApplication.LOGGER.warn("Login failed due to an invalid username or password");
				} else {
					CODEInterfaceApplication.LOGGER.error("Failed to log into Marshall Gateway API: {}, {}", error, errorDescription);
				}

				return false;
			}

			accessToken = jsonObject.get("access_token").getAsString();
			CODEInterfaceApplication.KEYSTORE_HANDLER.storeCredentials(username, password);
			isLoggedIn = true;

			CODEInterfaceApplication.LOGGER.info("Successfully logged into Marshall Gateway API");
		} catch (Exception e) {
			CODEInterfaceApplication.LOGGER.error("Failed to log into Marshall Gateway API", e);
		}

		return isLoggedIn;
	}

	/**
	 * Search for presets on the Marshall Gateway API
	 *
	 * @param searchTerm the search term
	 * @return a list of GatewayPresetEntry objects
	 */
	public List<GatewayPresetEntry> searchPresets(String searchTerm) {
		String presetSearchURL = "https://my.marshall.com/api/gateway/preset/search?searchText=" + URLEncoder.encode(searchTerm, StandardCharsets.UTF_8) + "&ampType=0";

		// Some headers are spoofed based on what the official Marshall Gateway app sends
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.uri(URI.create(presetSearchURL))
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
			String response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

			if (jsonObject.get("error") != null) {
				String error = jsonObject.get("error").getAsString();
				String errorDescription = jsonObject.get("error_description").getAsString();

				CODEInterfaceApplication.LOGGER.error("Failed to search for presets: {}, {}", error, errorDescription);
				return List.of();
			}

			return GatewayPresetEntry.createFromJSON(jsonObject.get("results").getAsJsonArray(), httpClient, accessToken);
		} catch (Exception e) {
			CODEInterfaceApplication.LOGGER.error("Failed to search for presets", e);
			return List.of();
		}
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}
}