package org.colorcoding.ibas.thirdpartyapp.client.openai;

import org.colorcoding.ibas.bobas.common.Strings;

/**
 * OpenAI API 消息内容部分 支持文本、图片、音频、文件等多种内容类型
 */
public class ContentPart {

	// Content type constants
	public static final String TYPE_TEXT = "text";
	public static final String TYPE_IMAGE_URL = "image_url";
	public static final String TYPE_INPUT_AUDIO = "input_audio";
	public static final String TYPE_FILE = "file";
	/** 第三方兼容扩展，并非 OpenAI 标准内容类型。 */
	public static final String TYPE_THINKING = "thinking";
	public static final String TYPE_REFUSAL = "refusal";

	private String type;
	private String text;
	private ImageUrl imageUrl;
	private InputAudio inputAudio;
	private FileReference file;
	private String thinking;
	private String refusal;

	public ContentPart() {
	}

	// ========== Text ==========

	public static ContentPart createTextPart(String text) {
		ContentPart part = new ContentPart();
		part.setType(TYPE_TEXT);
		part.setText(text);
		return part;
	}

	public ContentPart(String text) {
		this.type = TYPE_TEXT;
		this.text = text;
	}

	// ========== Image URL ==========

	public static ContentPart createImageUrlPart(String url) {
		ContentPart part = new ContentPart();
		part.setType(TYPE_IMAGE_URL);
		part.setImageUrl(new ImageUrl(url));
		return part;
	}

	public static ContentPart createImageUrlPart(String url, String detail) {
		ContentPart part = new ContentPart();
		part.setType(TYPE_IMAGE_URL);
		part.setImageUrl(new ImageUrl(url, detail));
		return part;
	}

	public static ContentPart createImageUrlPart(ImageUrl imageUrl) {
		ContentPart part = new ContentPart();
		part.setType(TYPE_IMAGE_URL);
		part.setImageUrl(imageUrl);
		return part;
	}

	public ContentPart(ImageUrl imageUrl) {
		this.type = TYPE_IMAGE_URL;
		this.imageUrl = imageUrl;
	}

	// ========== Input Audio ==========

	public static ContentPart createAudioPart(byte[] data, String format) {
		ContentPart part = new ContentPart();
		part.setType(TYPE_INPUT_AUDIO);
		part.setInputAudio(new InputAudio(data, format));
		return part;
	}

	public static ContentPart createAudioPart(InputAudio inputAudio) {
		ContentPart part = new ContentPart();
		part.setType(TYPE_INPUT_AUDIO);
		part.setInputAudio(inputAudio);
		return part;
	}

	// ========== File ==========

	public static ContentPart createFilePart(String fileId) {
		ContentPart part = new ContentPart();
		part.setType(TYPE_FILE);
		part.setFile(new FileReference(fileId));
		return part;
	}

	public static ContentPart createFilePart(FileReference file) {
		ContentPart part = new ContentPart();
		part.setType(TYPE_FILE);
		part.setFile(file);
		return part;
	}

	public static ContentPart createFilePart(byte[] data, String fileName) {
		ContentPart part = new ContentPart();
		part.setType(TYPE_FILE);
		FileReference ref = new FileReference();
		ref.setData(data);
		ref.setFileName(fileName);
		part.setFile(ref);
		return part;
	}


	// ========== 第三方 Thinking 扩展 ==========

	public static ContentPart createThinkingPart(String thinking) {
		ContentPart part = new ContentPart();
		part.setType(TYPE_THINKING);
		part.setThinking(thinking);
		return part;
	}

	// ========== Refusal ==========

	public static ContentPart createRefusalPart(String refusal) {
		ContentPart part = new ContentPart();
		part.setType(TYPE_REFUSAL);
		part.setRefusal(refusal);
		return part;
	}

	// ========== Getters & Setters ==========

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ImageUrl getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(ImageUrl imageUrl) {
		this.imageUrl = imageUrl;
	}

	public InputAudio getInputAudio() {
		return inputAudio;
	}

	public void setInputAudio(InputAudio inputAudio) {
		this.inputAudio = inputAudio;
	}

	public FileReference getFile() {
		return file;
	}

	public void setFile(FileReference file) {
		this.file = file;
	}


	public String getThinking() {
		return thinking;
	}

	public void setThinking(String thinking) {
		this.thinking = thinking;
	}

	public String getRefusal() {
		return refusal;
	}

	public void setRefusal(String refusal) {
		this.refusal = refusal;
	}

	// ========== Type check methods ==========

	public boolean isText() {
		return TYPE_TEXT.equals(this.type);
	}

	public boolean isImageUrl() {
		return TYPE_IMAGE_URL.equals(this.type);
	}

	public boolean isInputAudio() {
		return TYPE_INPUT_AUDIO.equals(this.type);
	}

	public boolean isFile() {
		return TYPE_FILE.equals(this.type);
	}


	public boolean isThinking() {
		return TYPE_THINKING.equals(this.type);
	}

	public boolean isRefusal() {
		return TYPE_REFUSAL.equals(this.type);
	}

	@Override
	public String toString() {
		if (this.isText()) {
			return Strings.format("{%s: %s}", this.type, Strings.substring(this.text, 30));
		}
		if (this.isImageUrl() && this.imageUrl != null) {
			return Strings.format("{%s: %s}", this.type, this.imageUrl.hashCode());
		}
		if (this.isInputAudio() && this.inputAudio != null) {
			return Strings.format("{%s: %s}", this.type, this.inputAudio.hashCode());
		}
		if (this.isFile() && this.file != null) {
			return Strings.format("{%s: %s}", this.type, this.file.hashCode());
		}
		if (this.isThinking()) {
			return Strings.format("{%s: %s}", this.type, Strings.substring(this.thinking, 30));
		}
		if (this.isRefusal()) {
			return Strings.format("{%s: %s}", this.type, Strings.substring(this.refusal, 30));
		}
		return Strings.format("{%s: %s}", this.type, this.hashCode());
	}
}
