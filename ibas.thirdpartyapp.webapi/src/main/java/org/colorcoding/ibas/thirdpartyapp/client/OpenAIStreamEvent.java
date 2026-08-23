package org.colorcoding.ibas.thirdpartyapp.client;

/**
 * 一个 SSE 事件。事件内容保持为原始字符串，由上层根据 OpenAI 或业务扩展协议解析。
 */
public class OpenAIStreamEvent {
	private final String event;
	private final String data;

	public OpenAIStreamEvent(String event, String data) {
		this.event = event;
		this.data = data;
	}

	public String getEvent() {
		return event;
	}

	public String getData() {
		return data;
	}
}
