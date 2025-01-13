package tech.anonymoushacker1279.marshallcodeampinterface.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.semver4j.Semver;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class UpdateAvailableController implements Initializable {

	@FXML
	private Button downloadButton;
	@FXML
	private Text currentVersionText;
	@FXML
	private Text latestVersionText;
	@FXML
	private TextArea changelogTextArea;

	private HostServices hostServices;
	private static final FXMLLoader fxmlLoader = new FXMLLoader(CODEInterfaceApplication.class.getResource("views/update-available.fxml"));
	private static Scene scene;
	private static Stage stage;

	public void setHostServices(HostServices hostServices) {
		this.hostServices = hostServices;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		downloadButton.setOnAction(event -> hostServices.showDocument("https://github.com/AnonymousHacker1279/MarshallCodeAmpInterface/releases/latest"));
	}

	public static void openDialog(HostServices services, String latestVersion, String changelog, String downloadUrl) {
		Platform.runLater(() -> {
			try {
				if (scene == null || stage == null) {
					CODEInterfaceApplication.LOGGER.debug("Loading Update Available scene");
					scene = new Scene(fxmlLoader.load());
					stage = new Stage();
					stage.setTitle("Update Available");
					stage.setScene(scene);
					stage.setResizable(false);
					stage.getIcons().add(new Image(Objects.requireNonNull(CODEInterfaceApplication.class.getResourceAsStream("images/code50.png"))));
				}

				stage.show();

				UpdateAvailableController controller = fxmlLoader.getController();
				controller.currentVersionText.setText("Current Version: " + CODEInterfaceApplication.APP_VERSION);
				controller.latestVersionText.setText("Latest Version: " + latestVersion);
				controller.changelogTextArea.setText(changelog);
				controller.downloadButton.setOnAction(event -> services.showDocument(downloadUrl));
				controller.setHostServices(services);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public static void checkForUpdates(HostServices services) {
		try (HttpClient client = HttpClient.newHttpClient()) {
			HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.uri(URI.create("https://api.github.com/repos/AnonymousHacker1279/MarshallCodeAmpInterface/releases/latest"))
					.build();

			String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
			// Load as JSON object
			Gson gson = new GsonBuilder().create();
			Map<String, ?> releaseInfo = gson.fromJson(response, TypeToken.getParameterized(Map.class, String.class, Object.class).getType());

			try {
				String latestVersion = releaseInfo.get("tag_name").toString();
				String changelog = releaseInfo.get("body").toString();
				String downloadUrl = releaseInfo.get("html_url").toString();

				Semver latestSemver = new Semver(latestVersion);
				if (latestSemver.isGreaterThan(CODEInterfaceApplication.APP_VERSION)) {
					CODEInterfaceApplication.LOGGER.info("Update available: {}", latestVersion);
					openDialog(services, latestVersion, changelog, downloadUrl);
				} else {
					CODEInterfaceApplication.LOGGER.info("No updates available");
				}
			} catch (NullPointerException e) {
				CODEInterfaceApplication.LOGGER.error("Failed to check for updates due to missing response information");
			}
		} catch (IOException | InterruptedException e) {
			CODEInterfaceApplication.LOGGER.error("Failed to check for updates", e);
		}
	}
}