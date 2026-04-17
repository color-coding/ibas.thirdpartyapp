package org.colorcoding.ibas.thirdpartyapp.client.openai;

/**
 * OpenAI API 文件引用对象
 * 用于 Assistants API 的文件引用
 */
public class FileReference {

	private String fileId;
	private String filename;
	private String data;

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

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}