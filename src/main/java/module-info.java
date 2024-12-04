module tech.anonymoushacker1279.marshallcodeampinterfacev2 {
	requires javafx.fxml;
	requires atlantafx.base;
	requires java.desktop;


	opens tech.anonymoushacker1279.marshallcodeampinterfacev2 to javafx.fxml;
	exports tech.anonymoushacker1279.marshallcodeampinterfacev2;
	exports tech.anonymoushacker1279.marshallcodeampinterfacev2.midi;
	exports tech.anonymoushacker1279.marshallcodeampinterfacev2.amp;
}