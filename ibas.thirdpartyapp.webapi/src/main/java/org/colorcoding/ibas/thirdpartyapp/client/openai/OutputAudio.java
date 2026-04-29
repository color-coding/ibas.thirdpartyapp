package org.colorcoding.ibas.thirdpartyapp.client.openai;

public class OutputAudio {

	public static final String FORMAT_MP3 = "mp3";
	public static final String FORMAT_WAV = "wav";
	public static final String FORMAT_FLAC = "flac";
	public static final String FORMAT_OGG = "ogg";
	public static final String FORMAT_AAC = "aac";
	public static final String FORMAT_PCM = "pcm";

	private String id;
	private byte[] data;
	private String format;
	private long expiresAt;
	private String transcript;

	public OutputAudio() {
	}

	public OutputAudio(byte[] data, String format) {
		this.data = data;
		this.format = format;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public long getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(long expiresAt) {
		this.expiresAt = expiresAt;
	}

	public String getTranscript() {
		return transcript;
	}

	public void setTranscript(String transcript) {
		this.transcript = transcript;
	}
}