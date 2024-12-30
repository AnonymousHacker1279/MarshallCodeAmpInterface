package tech.anonymoushacker1279.marshallcodeampinterface.util;

import javax.sound.sampled.*;

public class AudioCapture {

	private TargetDataLine targetLine;

	public void startCapture() throws LineUnavailableException {
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (Mixer.Info mixerInfo : mixerInfos) {
			Mixer mixer = AudioSystem.getMixer(mixerInfo);
			if (mixerInfo.getName().contains("Digital Audio Interface (CODE)")) {
				Line.Info[] targetLineInfos = mixer.getTargetLineInfo();
				for (Line.Info targetLineInfo : targetLineInfos) {
					if (targetLineInfo instanceof DataLine.Info dataLineInfo) {
						AudioFormat[] formats = dataLineInfo.getFormats();
						for (AudioFormat format : formats) {
							if (format.getChannels() == 2 && format.getSampleSizeInBits() == 16) {
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
		}

		throw new LineUnavailableException("No supported audio format found for CODE device");
	}

	public byte[] readAudioData() {
		byte[] buffer = new byte[4096];
		int bytesRead = targetLine.read(buffer, 0, buffer.length);
		if (bytesRead > 0) {
			return buffer;
		}
		return null;
	}

	public void stopCapture() {
		targetLine.stop();
		targetLine.close();
	}
}