package tech.anonymoushacker1279.marshallcodeampinterface.visualizer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;

public abstract class AudioVisualizer extends Canvas {

	protected final WritableImage visualizerImage;

	public AudioVisualizer(int width, int height) {
		super(width, height);
		this.visualizerImage = new WritableImage(width, height);

		// Periodically update and draw the visualizer
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(25), e -> drawVisualizer()));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}

	public abstract void updateVisualizer(double[] audioData);

	public abstract void drawVisualizer();
}