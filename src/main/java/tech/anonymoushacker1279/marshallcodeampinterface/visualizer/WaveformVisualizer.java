package tech.anonymoushacker1279.marshallcodeampinterface.visualizer;

public class WaveformVisualizer extends AudioVisualizer {

	private double[] audioData;
	private static final int BLUE_ARGB = (255 << 24) | 255;

	public WaveformVisualizer(int width, int height) {
		super(width, height);
	}

	@Override
	public void updateVisualizer(double[] audioData) {
		this.audioData = audioData;
	}

	@Override
	protected void drawVisualizer(int width, int height) {
		if (audioData == null) {
			return;
		}

		double centerY = height / 2.0;
		double xScale = width / (double) audioData.length;
		double yScale = 4;

		// Draw the waveform
		for (int i = 0; i < audioData.length; i++) {
			double x = i * xScale;
			double y = centerY - (audioData[i] * centerY * yScale);
			int pixelX = (int) x;
			int pixelY = (int) y;
			if (pixelX >= 0 && pixelX < width && pixelY >= 0 && pixelY < height) {
				writeToBuffer(pixelX, pixelY, BLUE_ARGB);
			}
		}
	}
}