package org.colorcoding.ibas.thirdpartyapp.client.openai;

import java.util.ArrayList;
import java.util.List;

import org.colorcoding.ibas.bobas.common.Strings;

public class ChatMessage {

	public static final String ROLE_VALUE_SYSTEM = "system";

	public static final String ROLE_VALUE_DEVELOPER = "developer";

	public static final String ROLE_VALUE_USER = "user";

	public static final String ROLE_VALUE_ASSISTANT = "assistant";

	public static final String ROLE_VALUE_TOOL = "tool";

	public static ChatMessage createUserMessage() {
		return createUserMessage((String) null);
	}

	public static ChatMessage createUserMessage(String content) {
		return new ChatMessage(ROLE_VALUE_USER, content);
	}

	public static ChatMessage createSystemMessage() {
		return createSystemMessage(null);
	}

	public static ChatMessage createSystemMessage(String content) {
		return new ChatMessage(ROLE_VALUE_SYSTEM, content);
	}

	public static ChatMessage createDeveloperMessage() {
		return createDeveloperMessage(null);
	}

	public static ChatMessage createDeveloperMessage(String content) {
		return new ChatMessage(ROLE_VALUE_DEVELOPER, content);
	}

	public ChatMessage() {

	}

	public ChatMessage(String role, String content) {
		this.setRole(role);
		this.setContent(content);
	}

	public ChatMessage(String role, Object content) {
		this.setRole(role);
		this.setContent(content);
	}

	private String role;
	private Object content;
	private String name;
	private List<ToolCall> toolCalls;
	private String toolCallId;
	private OutputAudio outputAudio;
	private String refusal;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	@SuppressWarnings("unchecked")
	public <T> T getContentAs(Class<T> type) {
		if (this.content != null) {
			if (type.isInstance(this.content)) {
				return (T) this.content;
			}
		}
		if (List.class == type) {
			return (T) new ArrayList<>(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<ContentPart> getContentAsParts() {
		return (List<ContentPart>) this.getContentAs(List.class);
	}

	public String getContentAsString() {
		return this.getContentAs(String.class);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ToolCall> getToolCalls() {
		if (toolCalls == null) {
			toolCalls = new ArrayList<>();
		}
		return toolCalls;
	}

	public void setToolCalls(List<ToolCall> toolCalls) {
		this.toolCalls = toolCalls;
	}

	public String getToolCallId() {
		return toolCallId;
	}

	public void setToolCallId(String toolCallId) {
		this.toolCallId = toolCallId;
	}

	public String getRefusal() {
		return refusal;
	}

	public void setRefusal(String refusal) {
		this.refusal = refusal;
	}

	public OutputAudio getOutputAudio() {
		return outputAudio;
	}

	public void setOutputAudio(OutputAudio outputAudio) {
		this.outputAudio = outputAudio;
	}

	public ChatMessage append(String text) {
		if (this.content instanceof String) {
			this.content = (String) this.content + (text == null ? Strings.VALUE_EMPTY : text);
		} else if (this.content == null) {
			this.content = (text == null ? Strings.VALUE_EMPTY : text);
		}
		return this;
	}

	public ChatMessage append(Object object) {
		return append(Strings.valueOf(object));
	}

	public ChatMessage appendLine(String text) {
		return append(text == null ? "\n" : text + "\n");
	}

	public ChatMessage appendLine(Object object) {
		return appendLine(Strings.valueOf(object));
	}
}
