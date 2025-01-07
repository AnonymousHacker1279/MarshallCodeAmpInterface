package tech.anonymoushacker1279.marshallcodeampinterface.visualizer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import tech.anonymoushacker1279.marshallcodeampinterface.util.AudioCapture;

import java.nio.IntBuffer;
import java.util.Arrays;

public abstract class AudioVisualizer extends Canvas {

	private final WritableImage visualizerImage;
	private final PixelBuffer<IntBuffer> pixelBuffer;
	private final IntBuffer intBuffer;
	private final int width;
	private final int height;
	private final int[] blankImage;

	/**
	 * Create a new audio visualizer with the specified width and height. These values should ideally match the values
	 * of the parent container.
	 *
	 * @param width  the width
	 * @param height the height
	 */
	public AudioVisualizer(int width, int height) {
		super(width, height);
		this.intBuffer = IntBuffer.allocate(width * height);
		this.pixelBuffer = new PixelBuffer<>(width, height, intBuffer, PixelFormat.getIntArgbPreInstance());
		this.visualizerImage = new WritableImage(pixelBuffer);
		// Separate width/height fields are stored because the parent methods getWidth() and getHeight() are slow
		this.width = width;
		this.height = height;

		// Create an array of black pixels to be used when clearing buffers
		this.blankImage = new int[width * height];
		Arrays.fill(blankImage, 0xFF000000); // ARGB for black

		// Periodically update and draw the visualizer
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(25), e -> {
			if (isVisible()) {
				handleVisualizerDrawUpdate();
			}
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}

	/**
	 * Clear the buffer by filling it with black pixels.
	 */
	protected void clearBuffer() {
		intBuffer.clear();
		intBuffer.put(blankImage);
		intBuffer.flip();
	}

	/**
	 * Push changes from the int buffer to the pixel buffer.
	 */
	protected void updatePixelBuffer() {
		pixelBuffer.updateBuffer(b -> null);
	}

	/**
	 * Write the given ARGB value to the buffer at the specified coordinates.
	 *
	 * @param x    the x coordinate
	 * @param y    the y coordinate
	 * @param argb the ARGB value
	 */
	protected void writeToBuffer(int x, int y, int argb) {
		intBuffer.put(y * width + x, argb);
	}

	/**
	 * Convert a {@link Color} to an ARGB integer value.
	 *
	 * @param color the color
	 * @return the ARGB value
	 */
	protected int colorToARGB(Color color) {
		return (255 << 24) | ((int) (color.getRed() * 255) << 16) | ((int) (color.getGreen() * 255) << 8) | (int) (color.getBlue() * 255);
	}

	/**
	 * Update the visualizer with the given audio data. Called continuously during audio processing in chunks of 4096
	 * samples.
	 * <p>
	 * See {@link AudioCapture#readAudioData()} for more information on how audio data is processed.
	 *
	 * @param audioData the audio data
	 */
	public abstract void updateVisualizer(double[] audioData);

	/**
	 * Handle visualizer drawing. This method is called periodically by the timeline.
	 * <p>
	 * This clears the buffer, draws the visualizer, updates the pixel buffer, and uploads the image to the canvas.
	 */
	protected void handleVisualizerDrawUpdate() {
		clearBuffer();
		drawVisualizer(width, height);
		updatePixelBuffer();
		getGraphicsContext2D().drawImage(visualizerImage, 0, 0);
		drawOverlays(getGraphicsContext2D());
	}

	/**
	 * Perform the actual drawing logic. Must be implemented by subclasses.
	 */
	protected abstract void drawVisualizer(int width, int height);

	/**
	 * Draw any overlays on top of the visualizer. Subclasses can override this method to draw additional content.
	 */
	protected void drawOverlays(GraphicsContext gc) {
	}
}