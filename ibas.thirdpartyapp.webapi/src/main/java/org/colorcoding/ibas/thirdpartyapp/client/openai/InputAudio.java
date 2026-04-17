package org.colorcoding.ibas.thirdpartyapp.client.openai;

/**
 * OpenAI API 音频输入对象
 * 用于 GPT-4o Audio 模型的音频输入
 */
public class InputAudio {

	public static final String FORMAT_MP3 = "mp3";
	public static final String FORMAT_WAV = "wav";
	public static final String FORMAT_FLAC = "flac";
	public static final String FORMAT_OGG = "ogg";
	public static final String FORMAT_AAC = "aac";
	public static final String FORMAT_PCM = "pcm";

	private String data;
	private String format;

	public InputAudio() {
	}

	public InputAudio(String data, String format) {
		this.data = data;
		this.format = format;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}