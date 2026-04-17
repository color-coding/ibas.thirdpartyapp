package org.colorcoding.ibas.thirdpartyapp.client.openai;

public class ImageUrl {

	public static final String DETAIL_LOW = "low";
	public static final String DETAIL_HIGH = "high";
	public static final String DETAIL_AUTO = "auto";

	private String url;
	private String detail;

	public ImageUrl() {
	}

	public ImageUrl(String url) {
		this.url = url;
	}

	public ImageUrl(String url, String detail) {
		this.url = url;
		this.detail = detail;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
}
