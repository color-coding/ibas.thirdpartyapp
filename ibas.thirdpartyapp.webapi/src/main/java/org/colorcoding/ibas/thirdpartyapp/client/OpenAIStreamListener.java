package org.colorcoding.ibas.thirdpartyapp.client;

/**
 * OpenAI 兼容 SSE 事件监听器。
 */
public interface OpenAIStreamListener {
	/** 收到一个 SSE 事件。 */
	void onEvent(OpenAIStreamEvent event) throws Exception;

	/** 流正常结束时调用。 */
	default void onCompleted() throws Exception {
	}

	/** 流读取失败时调用。 */
	default void onError(Exception error) {
	}
}
