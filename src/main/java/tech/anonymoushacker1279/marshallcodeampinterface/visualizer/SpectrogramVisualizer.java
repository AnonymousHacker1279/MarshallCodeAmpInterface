package tech.anonymoushacker1279.marshallcodeampinterface.visualizer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SpectrogramVisualizer extends AudioVisualizer {

	private final int[][] spectrogramData;
	private int currentColumn;

	public SpectrogramVisualizer(int width, int height) {
		super(width, height);
		this.spectrogramData = new int[width][height];
		this.currentColumn = 0;
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
	public void drawVisualizer() {
		PixelWriter pixelWriter = visualizerImage.getPixelWriter();
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				int intensity = spectrogramData[(int) ((currentColumn + x) % getWidth())][y];
				Color color = getColorForIntensity(intensity);
				pixelWriter.setColor(x, (int) (getHeight() - y - 1), color);
			}
		}

		GraphicsContext gc = getGraphicsContext2D();
		gc.drawImage(visualizerImage, 0, 0);
		drawFrequencyLabels(gc);
	}

	private Color getColorForIntensity(int intensity) {
		double normalized = intensity / 255.0;
		if (normalized < 0.33) {
			return Color.color(0, 0, Math.min(1.0, normalized * 3)); // Black to purple
		} else if (normalized < 0.66) {
			return Color.color(Math.min(1.0, (normalized - 0.33) * 3), 0, 1); // Purple to orange
		} else {
			return Color.color(1, Math.min(1.0, (normalized - 0.66) * 3), Math.max(0.0, 1 - (normalized - 0.66) * 3)); // Orange to white
		}
	}

	private void drawFrequencyLabels(GraphicsContext gc) {
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