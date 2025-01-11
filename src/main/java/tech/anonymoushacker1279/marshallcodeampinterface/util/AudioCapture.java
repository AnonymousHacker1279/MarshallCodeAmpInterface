package tech.anonymoushacker1279.marshallcodeampinterface.util;

import tech.anonymoushacker1279.marshallcodeampinterface.CODEInterfaceApplication;
import tech.anonymoushacker1279.marshallcodeampinterface.amp.AmpUSBInterface;

import javax.sound.sampled.*;

public class AudioCapture {

	private TargetDataLine targetLine;

	/**
	 * Start capturing audio from the CODE device. This will only work over a USB connection, as the amp will not
	 * register an input device over Bluetooth.
	 *
	 * @throws LineUnavailableException if the line is unavailable
	 */
	public void startCapture() throws LineUnavailableException {
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (Mixer.Info mixerInfo : mixerInfos) {
			if (CODEInterfaceApplication.INTERFACE instanceof AmpUSBInterface) {
				if (mixerInfo.getName().contains("Digital Audio Interface (CODE)")) {
					Mixer mixer = AudioSystem.getMixer(mixerInfo);
					openTargetLine(mixer);

					if (targetLine != null) {
						return;
					}
				}
			} else {
				CODEInterfaceApplication.LOGGER.warn("Visualizers are not supported when using a Bluetooth connection");
			}
		}

		throw new LineUnavailableException("No supported audio format found for CODE device");
	}

	/**
	 * Open the target line for capturing audio. The inputs are a bit hardcoded, but this seems to be the highest
	 * resolution audio that is exposed by the device.
	 *
	 * @param mixer the mixer
	 * @throws LineUnavailableException if the line is unavailable
	 */
	private void openTargetLine(Mixer mixer) throws LineUnavailableException {
		Line.Info[] targetLineInfos = mixer.getTargetLineInfo();
		for (Line.Info targetLineInfo : targetLineInfos) {
			if (targetLineInfo instanceof DataLine.Info dataLineInfo) {
				AudioFormat[] formats = dataLineInfo.getFormats();
				for (AudioFormat format : formats) {
					if (format.getSampleSizeInBits() == 16) {
						AudioFormat specifiedFormat = new AudioFormat(
								format.getEncoding(),
								48000,
								format.getSampleSizeInBits(),
								format.getChannels(),
								format.getFrameSize(),
								2500,
								format.isBigEndian()
						);

						targetLine = (TargetDataLine) mixer.getLine(dataLineInfo);
						targetLine.open(specifiedFormat);
						targetLine.start();

						return;
					}
				}
			}
		}
	}

	/**
	 * Read a chunk of audio data from the target line. This is called continuously by visualizers to update the
	 * display.
	 * <p>
	 * Each chunk is 4096 bytes long.
	 *
	 * @return the audio data
	 */
	public byte[] readAudioData() {
		byte[] buffer = new byte[4096];
		int bytesRead = targetLine.read(buffer, 0, buffer.length);
		if (bytesRead > 0) {
			return buffer;
		}

		return null;
	}

	/**
	 * Stop capturing audio.
	 */
	public void stopCapture() {
		targetLine.stop();
		targetLine.close();
	}
}