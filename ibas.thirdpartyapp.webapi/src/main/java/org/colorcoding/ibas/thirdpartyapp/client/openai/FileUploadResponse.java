package org.colorcoding.ibas.thirdpartyapp.client.openai;

/**
 * OpenAI API 文件上传响应
 */
public class FileUploadResponse {

	private String id;
	private String object;
	private Long bytes;
	private Long createdAt;
	private Long expiresAt;
	private String fileName;
	private String purpose;
	private String status;
	private String statusDetails;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public Long getBytes() {
		return bytes;
	}

	public void setBytes(Long bytes) {
		this.bytes = bytes;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public Long getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Long expiresAt) {
		this.expiresAt = expiresAt;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusDetails() {
		return statusDetails;
	}

	public void setStatusDetails(String statusDetails) {
		this.statusDetails = statusDetails;
	}
}
