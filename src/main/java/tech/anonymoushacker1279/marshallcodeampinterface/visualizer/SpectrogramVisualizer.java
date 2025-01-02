package tech.anonymoushacker1279.marshallcodeampinterface.visualizer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SpectrogramVisualizer extends AudioVisualizer {

	private final int[][] spectrogramData;
	private int currentColumn;
	private final int[] colorLookupTable;

	public SpectrogramVisualizer(int width, int height) {
		super(width, height);
		this.spectrogramData = new int[width][height];
		this.currentColumn = 0;
		this.colorLookupTable = new int[256];
		initializeColorLookupTable();
	}

	/**
	 * Initialize the color lookup table for the spectrogram. Massively improves performance by precomputing colors
	 * instead of calculating them on the fly.
	 */
	private void initializeColorLookupTable() {
		for (int i = 0; i < 256; i++) {
			double normalized = i / 255.0;
			if (normalized < 0.33) {
				Color color = Color.color(0, 0, Math.min(1.0, normalized * 3)); // Black to purple
				colorLookupTable[i] = colorToARGB(color);
			} else if (normalized < 0.66) {
				Color color = Color.color(Math.min(1.0, (normalized - 0.33) * 3), 0, 1);
				colorLookupTable[i] = colorToARGB(color); // Purple to orange
			} else {
				Color color = Color.color(1, Math.min(1.0, (normalized - 0.66) * 3), Math.max(0.0, 1 - (normalized - 0.66) * 3));
				colorLookupTable[i] = colorToARGB(color); // Orange to white
			}
		}
	}

	@Override
	public void updateVisualizer(double[] frequencyData) {
		// Normalize frequency data to fit within the height of the canvas
		for (int i = 0; i < frequencyData.length && i < getHeight(); i++) {
			int intensity = (int) ((frequencyData[i] + 12) / 24 * 255);
			intensity = Math.max(0, Math.min(255, intensity)); // Clamp intensity to 0-255
			spectrogramData[currentColumn][i] = intensity;
		}

		// Move to the next column
		currentColumn = (int) ((currentColumn + 1) % getWidth());
	}

	@Override
	protected void drawVisualizer(int width, int height) {
		// Draw spectrogram data to the buffer
		for (int x = 0; x < width; x++) {
			int column = (currentColumn + x) % width;
			for (int y = 0; y < height; y++) {
				int intensity = spectrogramData[column][y];
				writeToBuffer(x, height - 1 - y, colorLookupTable[intensity]);
			}
		}
	}

	@Override
	protected void drawOverlays(GraphicsContext gc) {
		gc.setFont(new Font(10));
		for (int i = 0; i < getHeight(); i += 50) {
			int frequency = (int) (i * (48000.0 / 2) / getHeight());
			// Create a background rectangle to make the text more readable
			gc.setFill(Color.BLACK);
			gc.fillRect(0, getHeight() - i - 11, 50, 10);

			gc.setFill(Color.WHITE);
			gc.fillText(frequency + " Hz", 5, getHeight() - i - 1);
		}
	}
}