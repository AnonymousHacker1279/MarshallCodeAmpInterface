package tech.anonymoushacker1279.marshallcodeampinterface.util;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class AudioProcessor {

	private final FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);

	/**
	 * Normalize the data, apply the FFT, and return the magnitudes of the complex data (in dB).
	 *
	 * @param audioData the audio data
	 * @return the processed audio data
	 */
	public double[] processAudioData(byte[] audioData) {
		double[] audioSamples = getRawAudioData(audioData);
		Complex[] complexData = fft.transform(audioSamples, TransformType.FORWARD);
		double[] magnitudes = new double[complexData.length / 2];
		for (int i = 0; i < magnitudes.length; i++) {
			magnitudes[i] = 20 * Math.log10(complexData[i].abs());
		}

		return magnitudes;
	}

	/**
	 * Convert raw audio data to an array of doubles. No additional processing is done.
	 *
	 * @param audioData the raw audio data
	 * @return the audio samples
	 */
	public double[] getRawAudioData(byte[] audioData) {
		double[] audioSamples = new double[audioData.length / 2];
		for (int i = 0; i < audioSamples.length; i++) {
			audioSamples[i] = ((audioData[2 * i + 1] << 8) | (audioData[2 * i] & 0xff)) / 32768.0;
		}
		return audioSamples;
	}
}