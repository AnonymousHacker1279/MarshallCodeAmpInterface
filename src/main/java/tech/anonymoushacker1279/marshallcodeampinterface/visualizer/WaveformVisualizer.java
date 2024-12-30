package tech.anonymoushacker1279.marshallcodeampinterface.visualizer;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class WaveformVisualizer extends AudioVisualizer {

	private double[] audioData;

	public WaveformVisualizer(int width, int height) {
		super(width, height);
	}

	@Override
	public void updateVisualizer(double[] audioData) {
		if (audioData == null || audioData.length == 0) {
			return;
		}
		this.audioData = new double[audioData.length];
		System.arraycopy(audioData, 0, this.audioData, 0, audioData.length);
		drawVisualizer();
	}

	@Override
	public void drawVisualizer() {
		if (audioData == null) {
			return;
		}

		int width = (int) getWidth();
		int height = (int) getHeight();
		PixelWriter pixelWriter = visualizerImage.getPixelWriter();

		double centerY = height / 2.0;
		double xScale = width / (double) audioData.length;
		double yScale = 4;

		// Clear the image
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				pixelWriter.setColor(x, y, Color.BLACK);
			}
		}

		// Draw the waveform without vertical mirroring
		for (int i = 0; i < audioData.length; i++) {
			double x = i * xScale;
			double y = centerY - (audioData[i] * centerY * yScale);
			int pixelX = (int) x;
			int pixelY = (int) y;
			if (pixelX >= 0 && pixelX < width && pixelY >= 0 && pixelY < height) {
				pixelWriter.setColor(pixelX, pixelY, Color.BLUE);
			}
		}

		getGraphicsContext2D().drawImage(visualizerImage, 0, 0);
	}
}