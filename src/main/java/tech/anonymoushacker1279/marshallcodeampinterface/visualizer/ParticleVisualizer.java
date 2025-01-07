package tech.anonymoushacker1279.marshallcodeampinterface.visualizer;

import javafx.scene.paint.Color;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParticleVisualizer extends AudioVisualizer {

	private final List<Particle> particles;
	private final Random random;

	public ParticleVisualizer(int width, int height) {
		super(width, height);
		particles = new CopyOnWriteArrayList<>();
		random = new Random();
	}

	@Override
	public void updateVisualizer(double[] audioData) {
		for (double sample : audioData) {
			double intensity = Math.abs(sample);
			// Normalize intensity regardless of volume
			intensity = Math.min(1.0, Math.max(0.0, intensity * 10));
			if (random.nextDouble() < (intensity / 2)) {
				double x = getWidth() / 2;
				double y = getHeight() / 2;
				double velocityX = (random.nextDouble() - 0.5) * (intensity * 100);
				double velocityY = (random.nextDouble() - 0.5) * (intensity * 100);
				double life = random.nextDouble() * 0.9;
				double brightness = Math.min(1.0, Math.max(0.0, intensity * 4)); // Adjust brightness based on intensity
				Color color = Color.hsb(random.nextDouble() * 360, 1.0, brightness);
				particles.add(new Particle(x, y, velocityX, velocityY, life, color));
			}
		}

		// Update existing particles
		particles.removeIf(particle -> !particle.isAlive());
		for (Particle particle : particles) {
			particle.update();
		}
	}

	@Override
	protected void drawVisualizer(int width, int height) {
		for (Particle particle : particles) {
			int x = (int) particle.getX();
			int y = (int) particle.getY();
			if (x >= 0 && x < width && y >= 0 && y < height) {
				writeToBuffer(x, y, colorToARGB(particle.getColor()));
			}
		}
	}

	static class Particle {
		private double x, y;
		private final double velocityX;
		private final double velocityY;
		private double life;
		private final Color color;

		public Particle(double x, double y, double velocityX, double velocityY, double life, Color color) {
			this.x = x;
			this.y = y;
			this.velocityX = velocityX;
			this.velocityY = velocityY;
			this.life = life;
			this.color = color;
		}

		public void update() {
			x += velocityX;
			y += velocityY;
			life -= 0.016;
		}

		public boolean isAlive() {
			return life > 0;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public Color getColor() {
			return color;
		}
	}
}