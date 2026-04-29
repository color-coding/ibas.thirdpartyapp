package org.colorcoding.ibas.thirdpartyapp.client.openai;

/**
 * OpenAI API 文件上传请求
 */
public class FileUploadRequest {

	public static final String PURPOSE_VALUE_ASSISTANTS = "assistants";// 助手 / 对话
	public static final String PURPOSE_VALUE_BATCH = "batch";// 批处理
	public static final String PURPOSE_VALUE_FINE_TUNE = "fine-tune";// ：微调
	public static final String PURPOSE_VALUE_USER_DATA = "user_data";// 通用文件（最新推荐）

	private String purpose;
	private String fileName;
	private byte[] data;

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}