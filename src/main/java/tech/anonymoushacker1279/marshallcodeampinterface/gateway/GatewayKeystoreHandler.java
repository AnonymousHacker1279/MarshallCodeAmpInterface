package tech.anonymoushacker1279.marshallcodeampinterface.gateway;

import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GatewayKeystoreHandler {

	private static final String KEYSTORE_PASSWORD = getSystemSpecificKeystorePassword();

	private final String keystorePath;

	/**
	 * Create a new Gateway keystore handler, creating the keystore file if it doesn't exist.
	 */
	public GatewayKeystoreHandler() {
		keystorePath = CODEInterfaceApplication.getRootDataPath() + "/gateway_keystore.jks";

		// Check if the file exists, if not create it
		try {
			FileInputStream fis = new FileInputStream(keystorePath);
			fis.close();
		} catch (Exception e) {
			try {
				KeyStore keyStore = KeyStore.getInstance("JCEKS");
				keyStore.load(null, KEYSTORE_PASSWORD.toCharArray());
				try (FileOutputStream fos = new FileOutputStream(keystorePath)) {
					keyStore.store(fos, KEYSTORE_PASSWORD.toCharArray());
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	/**
	 * Store the user's credentials in the keystore
	 *
	 * @param username the username
	 * @param password the password
	 * @throws Exception if an error occurs
	 */
	public void storeCredentials(String username, String password) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("JCEKS");
		keyStore.load(null, KEYSTORE_PASSWORD.toCharArray());

		SecretKey usernameKey = new SecretKeySpec(username.getBytes(), "AES");
		SecretKey passwordKey = new SecretKeySpec(password.getBytes(), "AES");

		KeyStore.SecretKeyEntry usernameEntry = new KeyStore.SecretKeyEntry(usernameKey);
		KeyStore.SecretKeyEntry passwordEntry = new KeyStore.SecretKeyEntry(passwordKey);

		KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(KEYSTORE_PASSWORD.toCharArray());

		keyStore.setEntry("username", usernameEntry, protectionParam);
		keyStore.setEntry("password", passwordEntry, protectionParam);

		try (FileOutputStream fos = new FileOutputStream(keystorePath)) {
			keyStore.store(fos, KEYSTORE_PASSWORD.toCharArray());
		}
	}

	/**
	 * Load the user's credentials from the keystore
	 *
	 * @return the username and password
	 * @throws Exception if an error occurs
	 */
	public String[] loadCredentials() throws Exception {
		KeyStore keyStore = KeyStore.getInstance("JCEKS");
		try (FileInputStream fis = new FileInputStream(keystorePath)) {
			keyStore.load(fis, KEYSTORE_PASSWORD.toCharArray());
		} catch (IOException e) {
			CODEInterfaceApplication.LOGGER.error("Failed to load credentials from keystore, the keystore password may have changed");
			CODEInterfaceApplication.LOGGER.info("Removing inaccessible keystore file");

			// Delete the keystore file
			CODEInterfaceApplication.KEYSTORE_HANDLER.deleteKeystore();

			return null;
		}

		KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(KEYSTORE_PASSWORD.toCharArray());

		KeyStore.SecretKeyEntry usernameEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry("username", protectionParam);
		KeyStore.SecretKeyEntry passwordEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry("password", protectionParam);

		SecretKey usernameKey = usernameEntry.getSecretKey();
		SecretKey passwordKey = passwordEntry.getSecretKey();

		String username = new String(usernameKey.getEncoded());
		String password = new String(passwordKey.getEncoded());

		return new String[]{username, password};
	}

	/**
	 * Delete the keystore file.
	 */
	public void deleteKeystore() {
		File file = new File(keystorePath);
		if (file.delete()) {
			CODEInterfaceApplication.LOGGER.debug("Keystore file deleted successfully");
		} else {
			CODEInterfaceApplication.LOGGER.error("Failed to delete keystore file");
		}
	}

	/**
	 * Keystore passwords are system specific and based on the device name, hashed with SHA-256.
	 * <p>
	 * This isn't designed to be foolproof, but it keeps credentials from existing in plaintext on disk.
	 *
	 * @return the system specific keystore password
	 */
	private static String getSystemSpecificKeystorePassword() {
		// Get the system name
		try {
			String systemName = InetAddress.getLocalHost().getHostName();

			// Hash the system name with SHA-256
			MessageDigest digest;
			try {
				digest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}

			byte[] hash = digest.digest(systemName.getBytes(StandardCharsets.UTF_8));

			// Convert the hash to a string
			StringBuilder hexString = new StringBuilder(2 * hash.length);
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
}