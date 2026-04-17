package org.colorcoding.ibas.thirdpartyapp.client.openai;

public class Tool {

	public static final String TYPE_FUNCTION = "function";

	private String type;
	private ToolFunction function;

	public Tool() {
		this.type = TYPE_FUNCTION;
	}

	public Tool(ToolFunction function) {
		this.type = TYPE_FUNCTION;
		this.function = function;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ToolFunction getFunction() {
		return function;
	}

	public void setFunction(ToolFunction function) {
		this.function = function;
	}
}
