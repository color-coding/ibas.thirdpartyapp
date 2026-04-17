package org.colorcoding.ibas.thirdpartyapp.client.openai;

public class ToolCall {

	public static final String TYPE_FUNCTION = "function";

	private String id;
	private String type;
	private FunctionCall function;

	public ToolCall() {
		this.type = TYPE_FUNCTION;
	}

	public ToolCall(String id, FunctionCall function) {
		this.id = id;
		this.type = TYPE_FUNCTION;
		this.function = function;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public FunctionCall getFunction() {
		return function;
	}

	public void setFunction(FunctionCall function) {
		this.function = function;
	}
}
