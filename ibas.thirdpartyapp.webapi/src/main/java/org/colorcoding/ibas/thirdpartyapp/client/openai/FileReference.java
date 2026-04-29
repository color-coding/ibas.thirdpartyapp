package org.colorcoding.ibas.thirdpartyapp.client.openai;

/**
 * OpenAI API 文件引用对象 用于 Assistants API 的文件引用
 */
public class FileReference {

	private String fileId;
	private String fileName;
	private String mimeType;
	private byte[] data;

	public FileReference() {
	}

	public FileReference(String fileId) {
		this.fileId = fileId;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public final String getMimeType() {
		return mimeType;
	}

	public final void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}