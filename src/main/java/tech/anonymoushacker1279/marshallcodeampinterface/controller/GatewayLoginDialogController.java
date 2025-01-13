package tech.anonymoushacker1279.marshallcodeampinterface.controller;

import atlantafx.base.controls.PasswordTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class GatewayLoginDialogController implements Initializable {

	@FXML
	private TextField usernameTextField;
	@FXML
	private PasswordTextField passwordTextField;
	@FXML
	private Button loginButton;
	@FXML
	private Text loginFailedText;

	private static final FXMLLoader fxmlLoader = new FXMLLoader(CODEInterfaceApplication.class.getResource("views/login-to-gateway.fxml"));
	private static Scene scene;
	private static Stage stage;

	public static void openDialog() {
		Platform.runLater(() -> {
			try {
				if (scene == null || stage == null) {
					CODEInterfaceApplication.LOGGER.debug("Loading Gateway Login scene");
					scene = new Scene(fxmlLoader.load());
					stage = new Stage();
					stage.setTitle("Log In to Marshall Gateway");
					stage.setScene(scene);
					stage.setResizable(false);
					stage.getIcons().add(new Image(Objects.requireNonNull(CODEInterfaceApplication.class.getResourceAsStream("images/code50.png"))));
				}

				stage.show();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loginButton.setOnAction(event -> {
			String username = usernameTextField.getText();
			String password = passwordTextField.getPassword();

			boolean success = CODEInterfaceApplication.GATEWAY_API_HANDLER.login(username, password);

			if (success) {
				stage.close();
			} else {
				loginFailedText.setVisible(true);
			}
		});
	}
}