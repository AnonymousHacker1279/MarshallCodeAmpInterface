package tech.anonymoushacker1279.marshallcodeampinterface.visualizer;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class VUVisualizer extends AudioVisualizer {

	private static final double MAX_AMPLITUDE = 1.0;
	private final double[] loudnessHistory;
	private final boolean[] clippingHistory;
	private int historyIndex;

	public VUVisualizer(int width, int height) {
		super(width, height);
		loudnessHistory = new double[width];
		clippingHistory = new boolean[width];
		historyIndex = 0;
	}

	@Override
	public void updateVisualizer(double[] audioData) {
		if (audioData == null || audioData.length == 0) {
			return;
		}

		// Calculate the RMS (Root Mean Square) loudness
		double rms = 0;
		for (double sample : audioData) {
			rms += sample * sample;
		}
		rms = Math.sqrt(rms / audioData.length);

		// Convert RMS to decibels
		double db = 20 * Math.log10(rms / MAX_AMPLITUDE);

		// Store the loudness in the history
		loudnessHistory[historyIndex] = db;

		// Check for clipping and store the state
		clippingHistory[historyIndex] = checkForClipping(audioData);

		historyIndex = (historyIndex + 1) % loudnessHistory.length;
	}

	private boolean checkForClipping(double[] audioData) {
		for (double sample : audioData) {
			if (Math.abs(sample) >= MAX_AMPLITUDE) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void drawVisualizer() {
		int width = (int) getWidth();
		int height = (int) getHeight();
		PixelWriter pixelWriter = visualizerImage.getPixelWriter();

		// Clear the image
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				pixelWriter.setColor(x, y, Color.BLACK);
			}
		}

		// Draw the VU meter and histogram
		double yScale = 1;
		for (int i = 0; i < loudnessHistory.length; i++) {
			double loudness = loudnessHistory[(historyIndex + i) % loudnessHistory.length];
			int barHeight = (int) ((loudness + 60) / 60 * height * yScale); // Normalize dB to fit the height

			// Highlight clipping points
			Color color = clippingHistory[(historyIndex + i) % loudnessHistory.length] ? Color.RED : Color.GREEN;

			for (int y = height - 1; y >= height - barHeight; y--) {
				if (y >= 0 && y < height) {
					pixelWriter.setColor(i, y, color);
				}
			}
		}

		getGraphicsContext2D().drawImage(visualizerImage, 0, 0);
	}
}