module tech.anonymoushacker1279.marshallcodeampinterface {
	requires javafx.fxml;
	requires atlantafx.base;
	requires org.jetbrains.annotations;
	requires OrionBLE;
	requires java.net.http;
	requires org.apache.logging.log4j.core;
	requires java.desktop;

	opens tech.anonymoushacker1279.marshallcodeampinterface to javafx.fxml;
	exports tech.anonymoushacker1279.marshallcodeampinterface;
	exports tech.anonymoushacker1279.marshallcodeampinterface.midi;
	exports tech.anonymoushacker1279.marshallcodeampinterface.amp;
	exports tech.anonymoushacker1279.marshallcodeampinterface.controller;
	opens tech.anonymoushacker1279.marshallcodeampinterface.controller to javafx.fxml;
	exports tech.anonymoushacker1279.marshallcodeampinterface.util;
	opens tech.anonymoushacker1279.marshallcodeampinterface.util to javafx.fxml;
}