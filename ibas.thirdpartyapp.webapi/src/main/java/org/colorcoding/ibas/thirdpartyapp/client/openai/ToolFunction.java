package org.colorcoding.ibas.thirdpartyapp.client.openai;

public class ToolFunction {

	private String name;
	private String description;
	private Object parameters;

	public ToolFunction() {
	}

	public ToolFunction(String name, String description, Object parameters) {
		this.name = name;
		this.description = description;
		this.parameters = parameters;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Object getParameters() {
		return parameters;
	}

	public void setParameters(Object parameters) {
		this.parameters = parameters;
	}
}
