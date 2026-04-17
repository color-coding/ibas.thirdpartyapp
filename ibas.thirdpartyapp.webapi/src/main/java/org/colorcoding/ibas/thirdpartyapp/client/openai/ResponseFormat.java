package org.colorcoding.ibas.thirdpartyapp.client.openai;

public class ResponseFormat {

	public static final String TYPE_TEXT = "text";
	public static final String TYPE_JSON_OBJECT = "json_object";
	public static final String TYPE_JSON_SCHEMA = "json_schema";

	private String type;
	private Object jsonSchema;

	public ResponseFormat() {
	}

	public ResponseFormat(String type) {
		this.type = type;
	}

	public ResponseFormat(String type, Object jsonSchema) {
		this.type = type;
		this.jsonSchema = jsonSchema;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getJsonSchema() {
		return jsonSchema;
	}

	public void setJsonSchema(Object jsonSchema) {
		this.jsonSchema = jsonSchema;
	}
}
