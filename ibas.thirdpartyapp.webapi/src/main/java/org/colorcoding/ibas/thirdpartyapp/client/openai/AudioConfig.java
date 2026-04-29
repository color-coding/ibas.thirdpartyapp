package org.colorcoding.ibas.thirdpartyapp.client.openai;

public class AudioConfig {

	public static final String VOICE_ALLOY = "alloy";
	public static final String VOICE_ASH = "ash";
	public static final String VOICE_BALLAD = "ballad";
	public static final String VOICE_CORAL = "coral";
	public static final String VOICE_ECHO = "echo";
	public static final String VOICE_SAGE = "sage";
	public static final String VOICE_SHIMMER = "shimmer";
	public static final String VOICE_VERSE = "verse";

	public static final String FORMAT_WAV = "wav";
	public static final String FORMAT_MP3 = "mp3";
	public static final String FORMAT_FLAC = "flac";
	public static final String FORMAT_OPUS = "opus";
	public static final String FORMAT_PCM16 = "pcm16";

	private String voice;
	private String format;

	public AudioConfig() {
	}

	public AudioConfig(String voice, String format) {
		this.voice = voice;
		this.format = format;
	}

	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}