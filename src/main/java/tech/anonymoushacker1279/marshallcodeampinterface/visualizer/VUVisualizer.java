package tech.anonymoushacker1279.marshallcodeampinterface.visualizer;

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

	/**
	 * Check if any of the samples in the audio data exceed the maximum amplitude.
	 *
	 * @param audioData the audio data
	 * @return true if clipping is detected, false otherwise
	 */
	private boolean checkForClipping(double[] audioData) {
		for (double sample : audioData) {
			if (Math.abs(sample) >= MAX_AMPLITUDE) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void drawVisualizer(int width, int height) {
		for (int i = 0; i < loudnessHistory.length; i++) {
			double loudness = loudnessHistory[(historyIndex + i) % loudnessHistory.length];
			int barHeight = (int) ((loudness + 60) / 60 * height);
			Color color = clippingHistory[(historyIndex + i) % loudnessHistory.length] ? Color.RED : Color.GREEN;

			for (int y = height - 1; y >= height - barHeight; y--) {
				if (y >= 0 && y < height) {
					writeToBuffer(i, y, colorToARGB(color));
				}
			}
		}
	}
}