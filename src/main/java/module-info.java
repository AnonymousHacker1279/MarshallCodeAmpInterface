module tech.anonymoushacker1279.marshallcodeampinterface {
	requires javafx.fxml;
	requires atlantafx.base;
	requires org.jetbrains.annotations;
	requires java.desktop;


	opens tech.anonymoushacker1279.marshallcodeampinterface to javafx.fxml;
	exports tech.anonymoushacker1279.marshallcodeampinterface;
	exports tech.anonymoushacker1279.marshallcodeampinterface.midi;
	exports tech.anonymoushacker1279.marshallcodeampinterface.amp;
}