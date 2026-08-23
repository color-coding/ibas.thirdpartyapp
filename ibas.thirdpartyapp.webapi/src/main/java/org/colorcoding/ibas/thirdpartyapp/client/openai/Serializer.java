package org.colorcoding.ibas.thirdpartyapp.client.openai;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.colorcoding.ibas.bobas.common.Bytes;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.serialization.SerializationException;
import org.colorcoding.ibas.bobas.serialization.jersey.SerializerJson;

public class Serializer extends SerializerJson {

	// ==================== 序列化（请求） ====================

	public void serialize(ChatCompletionRequest request, OutputStream output) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		b.add("model", request.getModel());
		if (request.getMessages() != null && !request.getMessages().isEmpty()) {
			JsonArrayBuilder arr = Json.createArrayBuilder();
			for (ChatMessage msg : request.getMessages()) {
				JsonObjectBuilder mb = Json.createObjectBuilder();
				if (msg.getRole() != null) {
					mb.add("role", msg.getRole());
				}
				if (msg.getContent() != null) {
					Object content = msg.getContent();
					if (content instanceof String) {
						mb.add("content", (String) content);
					} else if (content instanceof List && !((List<?>) content).isEmpty()) {
						JsonArrayBuilder ca = Json.createArrayBuilder();
						for (Object item : (List<?>) content) {
							if (item instanceof ContentPart) {
								ContentPart part = (ContentPart) item;
								JsonObjectBuilder pb = Json.createObjectBuilder();
								if (part.getType() != null) {
									pb.add("type", part.getType());
								}
								if (part.getText() != null) {
									pb.add("text", part.getText());
								}
								if (part.getImageUrl() != null) {
									JsonObjectBuilder ib = Json.createObjectBuilder();
									if (part.getImageUrl().getUrl() != null) {
										ib.add("url", part.getImageUrl().getUrl());
									}
									if (part.getImageUrl().getDetail() != null) {
										ib.add("detail", part.getImageUrl().getDetail());
									}
									pb.add("image_url", ib.build());
								}
								if (part.getInputAudio() != null) {
									JsonObjectBuilder ab = Json.createObjectBuilder();
									if (part.getInputAudio().getData() != null) {
										ab.add("data", Bytes.toBase64String(part.getInputAudio().getData()));
									}
									if (part.getInputAudio().getFormat() != null) {
										ab.add("format", part.getInputAudio().getFormat());
									}
									pb.add("input_audio", ab.build());
								}
								if (part.getFile() != null) {
									JsonObjectBuilder fb = Json.createObjectBuilder();
									if (part.getFile().getFileId() != null) {
										fb.add("file_id", part.getFile().getFileId());
									} else {
										if (part.getFile().getFileName() != null) {
											fb.add("filename", part.getFile().getFileName());
										}
										if (part.getFile().getData() != null) {
											fb.add("file_data", Bytes.toBase64String(part.getFile().getData()));
										}
									}
									pb.add("file", fb.build());
								}
								if (part.getThinking() != null) {
									pb.add("thinking", part.getThinking());
								}
								if (part.getRefusal() != null) {
									pb.add("refusal", part.getRefusal());
								}
								ca.add(pb.build());
							}
						}
						mb.add("content", ca.build());
					}
				}
				if (msg.getName() != null) {
					mb.add("name", msg.getName());
				}
				if (msg.getToolCalls() != null && !msg.getToolCalls().isEmpty()) {
					JsonArrayBuilder ta = Json.createArrayBuilder();
					for (ToolCall tc : msg.getToolCalls()) {
						JsonObjectBuilder tcb = Json.createObjectBuilder();
						if (tc.getId() != null) {
							tcb.add("id", tc.getId());
						}
						if (tc.getType() != null) {
							tcb.add("type", tc.getType());
						}
						if (tc.getFunction() != null) {
							JsonObjectBuilder fb = Json.createObjectBuilder();
							if (tc.getFunction().getName() != null) {
								fb.add("name", tc.getFunction().getName());
							}
							if (tc.getFunction().getArguments() != null) {
								fb.add("arguments", tc.getFunction().getArguments());
							}
							tcb.add("function", fb.build());
						}
						ta.add(tcb.build());
					}
					mb.add("tool_calls", ta.build());
				}
				if (msg.getToolCallId() != null) {
					mb.add("tool_call_id", msg.getToolCallId());
				}
				if (msg.getRefusal() != null) {
					mb.add("refusal", msg.getRefusal());
				}
				if (msg.getReasoningContent() != null) {
					mb.add("reasoning_content", msg.getReasoningContent());
				}
				if (msg.getOutputAudio() != null && msg.getOutputAudio().getId() != null) {
					mb.add("audio", Json.createObjectBuilder().add("id", msg.getOutputAudio().getId()).build());
				}
				arr.add(mb.build());
			}
			b.add("messages", arr.build());
		}
			if (request.getModalities() != null && !request.getModalities().isEmpty()) {
				JsonArrayBuilder ma = Json.createArrayBuilder();
				for (String modality : request.getModalities()) {
					ma.add(modality);
				}
				b.add("modalities", ma.build());
			}
			if (request.getAudio() != null) {
				JsonObjectBuilder ab = Json.createObjectBuilder();
				if (request.getAudio().getVoice() != null) {
					ab.add("voice", request.getAudio().getVoice());
				}
				if (request.getAudio().getFormat() != null) {
					ab.add("format", request.getAudio().getFormat());
				}
				b.add("audio", ab.build());
			}
		if (request.getTemperature() != null) {
			b.add("temperature", request.getTemperature());
		}
		if (request.getTopP() != null) {
			b.add("top_p", request.getTopP());
		}
		if (request.getN() != null) {
			b.add("n", request.getN());
		}
		if (request.getMaxTokens() != null) {
			b.add("max_tokens", request.getMaxTokens());
		}
		if (request.getMaxCompletionTokens() != null) {
			b.add("max_completion_tokens", request.getMaxCompletionTokens());
		}
		if (request.getStop() != null) {
			Object stop = request.getStop();
			if (stop instanceof String) {
				b.add("stop", (String) stop);
			} else if (stop instanceof List && !((List<?>) stop).isEmpty()) {
				JsonArrayBuilder sa = Json.createArrayBuilder();
				for (Object item : (List<?>) stop) {
					sa.add(Strings.valueOf(item));
				}
				b.add("stop", sa.build());
			}
		}
		if (request.getStream() != null) {
			b.add("stream", request.getStream());
		}
		if (request.getFrequencyPenalty() != null) {
			b.add("frequency_penalty", request.getFrequencyPenalty());
		}
		if (request.getPresencePenalty() != null) {
			b.add("presence_penalty", request.getPresencePenalty());
		}
		if (request.getSeed() != null) {
			b.add("seed", request.getSeed());
		}
		if (request.getTools() != null && !request.getTools().isEmpty()) {
			JsonArrayBuilder arr = Json.createArrayBuilder();
			for (Tool tool : request.getTools()) {
				JsonObjectBuilder tb = Json.createObjectBuilder();
				if (tool.getType() != null) {
					tb.add("type", tool.getType());
				}
				if (tool.getFunction() != null) {
					JsonObjectBuilder fb = Json.createObjectBuilder();
					if (tool.getFunction().getName() != null) {
						fb.add("name", tool.getFunction().getName());
					}
					if (tool.getFunction().getDescription() != null) {
						fb.add("description", tool.getFunction().getDescription());
					}
					if (tool.getFunction().getParameters() != null) {
						this.addJsonValue(fb, "parameters", tool.getFunction().getParameters());
					}
					if (tool.getFunction().getStrict() != null) {
						fb.add("strict", tool.getFunction().getStrict());
					}
					tb.add("function", fb.build());
				}
				arr.add(tb.build());
			}
			b.add("tools", arr.build());
		}
		if (request.getToolChoice() != null) {
			Object tc = request.getToolChoice();
			if (tc instanceof String) {
				b.add("tool_choice", (String) tc);
			} else if (tc instanceof Tool) {
				Tool tool = (Tool) tc;
				JsonObjectBuilder tb = Json.createObjectBuilder();
				if (tool.getType() != null) {
					tb.add("type", tool.getType());
				}
				if (tool.getFunction() != null) {
					JsonObjectBuilder fb = Json.createObjectBuilder();
					if (tool.getFunction().getName() != null) {
						fb.add("name", tool.getFunction().getName());
					}
					if (tool.getFunction().getDescription() != null) {
						fb.add("description", tool.getFunction().getDescription());
					}
					if (tool.getFunction().getParameters() != null) {
						this.addJsonValue(fb, "parameters", tool.getFunction().getParameters());
					}
					if (tool.getFunction().getStrict() != null) {
						fb.add("strict", tool.getFunction().getStrict());
					}
					tb.add("function", fb.build());
				}
				b.add("tool_choice", tb.build());
			}
		}
		if (request.getParallelToolCalls() != null) {
			b.add("parallel_tool_calls", request.getParallelToolCalls());
		}
		if (request.getUser() != null) {
			b.add("user", request.getUser());
		}
		if (request.getSafetyIdentifier() != null) {
			b.add("safety_identifier", request.getSafetyIdentifier());
		}
		if (request.getPromptCacheKey() != null) {
			b.add("prompt_cache_key", request.getPromptCacheKey());
		}
		if (request.getVerbosity() != null) {
			b.add("verbosity", request.getVerbosity());
		}
		if (request.getLogprobs() != null) {
			b.add("logprobs", request.getLogprobs());
		}
		if (request.getTopLogprobs() != null) {
			b.add("top_logprobs", request.getTopLogprobs());
		}
		if (request.getResponseFormat() != null) {
			ResponseFormat format = request.getResponseFormat();
			JsonObjectBuilder fb = Json.createObjectBuilder();
			if (format.getType() != null) {
				fb.add("type", format.getType());
			}
			if (format.getJsonSchema() != null) {
				this.addJsonValue(fb, "json_schema", format.getJsonSchema());
			}
			b.add("response_format", fb.build());
		}
		if (request.getLogitBias() != null) {
			JsonObjectBuilder lb = Json.createObjectBuilder();
			for (Entry<String, Integer> entry : request.getLogitBias().entrySet()) {
				lb.add(entry.getKey(), entry.getValue());
			}
			b.add("logit_bias", lb.build());
		}
		if (request.getReasoningEffort() != null) {
			b.add("reasoning_effort", request.getReasoningEffort());
		}
		if (request.getThinking() != null) {
			this.addJsonValue(b, "thinking", request.getThinking());
		}
		this.writeJsonObject(b.build(), output);
	}

	public void serialize(ChatMessage msg, OutputStream output) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		if (msg.getRole() != null) {
			b.add("role", msg.getRole());
		}
		if (msg.getContent() != null) {
			Object content = msg.getContent();
			if (content instanceof String) {
				b.add("content", (String) content);
			} else if (content instanceof List && !((List<?>) content).isEmpty()) {
				JsonArrayBuilder arr = Json.createArrayBuilder();
				for (Object item : (List<?>) content) {
					if (item instanceof ContentPart) {
						ContentPart part = (ContentPart) item;
						JsonObjectBuilder pb = Json.createObjectBuilder();
						if (part.getType() != null) {
							pb.add("type", part.getType());
						}
						if (part.getText() != null) {
							pb.add("text", part.getText());
						}
						if (part.getImageUrl() != null) {
							JsonObjectBuilder ib = Json.createObjectBuilder();
							if (part.getImageUrl().getUrl() != null) {
								ib.add("url", part.getImageUrl().getUrl());
							}
							if (part.getImageUrl().getDetail() != null) {
								ib.add("detail", part.getImageUrl().getDetail());
							}
							pb.add("image_url", ib.build());
						}
						if (part.getInputAudio() != null) {
							JsonObjectBuilder ab = Json.createObjectBuilder();
							if (part.getInputAudio().getData() != null) {
								ab.add("data", Bytes.toBase64String(part.getInputAudio().getData()));
							}
							if (part.getInputAudio().getFormat() != null) {
								ab.add("format", part.getInputAudio().getFormat());
							}
							pb.add("input_audio", ab.build());
						}
						if (part.getFile() != null) {
							JsonObjectBuilder fb = Json.createObjectBuilder();
							if (part.getFile().getFileId() != null) {
								fb.add("file_id", part.getFile().getFileId());
							} else {
								if (part.getFile().getFileName() != null) {
									fb.add("filename", part.getFile().getFileName());
								}
								if (part.getFile().getData() != null) {
									fb.add("file_data", Bytes.toBase64String(part.getFile().getData()));
								}
							}
							pb.add("file", fb.build());
						}
						if (part.getThinking() != null) {
							pb.add("thinking", part.getThinking());
						}
						if (part.getRefusal() != null) {
							pb.add("refusal", part.getRefusal());
						}
						arr.add(pb.build());
					}
				}
				b.add("content", arr.build());
			}
		}
		if (msg.getName() != null) {
			b.add("name", msg.getName());
		}
		if (msg.getToolCalls() != null && !msg.getToolCalls().isEmpty()) {
			JsonArrayBuilder arr = Json.createArrayBuilder();
			for (ToolCall tc : msg.getToolCalls()) {
				JsonObjectBuilder tcb = Json.createObjectBuilder();
				if (tc.getId() != null) {
					tcb.add("id", tc.getId());
				}
				if (tc.getType() != null) {
					tcb.add("type", tc.getType());
				}
				if (tc.getFunction() != null) {
					JsonObjectBuilder fb = Json.createObjectBuilder();
					if (tc.getFunction().getName() != null) {
						fb.add("name", tc.getFunction().getName());
					}
					if (tc.getFunction().getArguments() != null) {
						fb.add("arguments", tc.getFunction().getArguments());
					}
					tcb.add("function", fb.build());
				}
				arr.add(tcb.build());
			}
			b.add("tool_calls", arr.build());
		}
		if (msg.getToolCallId() != null) {
			b.add("tool_call_id", msg.getToolCallId());
		}
		if (msg.getRefusal() != null) {
			b.add("refusal", msg.getRefusal());
		}
		if (msg.getReasoningContent() != null) {
			b.add("reasoning_content", msg.getReasoningContent());
		}
		if (msg.getOutputAudio() != null && msg.getOutputAudio().getId() != null) {
			b.add("audio", Json.createObjectBuilder().add("id", msg.getOutputAudio().getId()).build());
		}
		this.writeJsonObject(b.build(), output);
	}

	public void serialize(ContentPart part, OutputStream output) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		if (part.getType() != null) {
			b.add("type", part.getType());
		}
		if (part.getText() != null) {
			b.add("text", part.getText());
		}
		if (part.getImageUrl() != null) {
			JsonObjectBuilder ib = Json.createObjectBuilder();
			if (part.getImageUrl().getUrl() != null) {
				ib.add("url", part.getImageUrl().getUrl());
			}
			if (part.getImageUrl().getDetail() != null) {
				ib.add("detail", part.getImageUrl().getDetail());
			}
			b.add("image_url", ib.build());
		}
		if (part.getInputAudio() != null) {
			JsonObjectBuilder ab = Json.createObjectBuilder();
			if (part.getInputAudio().getData() != null) {
				ab.add("data", Bytes.toBase64String(part.getInputAudio().getData()));
			}
			if (part.getInputAudio().getFormat() != null) {
				ab.add("format", part.getInputAudio().getFormat());
			}
			b.add("input_audio", ab.build());
		}
		if (part.getFile() != null) {
			JsonObjectBuilder fb = Json.createObjectBuilder();
			if (part.getFile().getFileId() != null) {
				fb.add("file_id", part.getFile().getFileId());
			} else {
				if (part.getFile().getFileName() != null) {
					fb.add("filename", part.getFile().getFileName());
				}
				if (part.getFile().getData() != null) {
					fb.add("file_data", Bytes.toBase64String(part.getFile().getData()));
				}
			}
			b.add("file", fb.build());
		}
		if (part.getThinking() != null) {
			b.add("thinking", part.getThinking());
		}
		if (part.getRefusal() != null) {
			b.add("refusal", part.getRefusal());
		}
		this.writeJsonObject(b.build(), output);
	}

	public void serialize(ImageUrl imageUrl, OutputStream output) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		if (imageUrl.getUrl() != null) {
			b.add("url", imageUrl.getUrl());
		}
		if (imageUrl.getDetail() != null) {
			b.add("detail", imageUrl.getDetail());
		}
		this.writeJsonObject(b.build(), output);
	}

	public void serialize(InputAudio inputAudio, OutputStream output) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		if (inputAudio.getData() != null) {
			b.add("data", Bytes.toBase64String(inputAudio.getData()));
		}
		if (inputAudio.getFormat() != null) {
			b.add("format", inputAudio.getFormat());
		}
		this.writeJsonObject(b.build(), output);
	}

	public void serialize(FileReference file, OutputStream output) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		if (file.getFileId() != null) {
			// 引用方式
			b.add("file_id", file.getFileId());
		} else {
			// base64方式
			if (file.getFileName() != null) {
				b.add("filename", file.getFileName());
			}
			if (file.getData() != null) {
				b.add("file_data", Bytes.toBase64String(file.getData()));
			}
		}
		this.writeJsonObject(b.build(), output);
	}

	public void serialize(ToolCall toolCall, OutputStream output) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		if (toolCall.getId() != null) {
			b.add("id", toolCall.getId());
		}
		if (toolCall.getType() != null) {
			b.add("type", toolCall.getType());
		}
		if (toolCall.getFunction() != null) {
			JsonObjectBuilder fb = Json.createObjectBuilder();
			if (toolCall.getFunction().getName() != null) {
				fb.add("name", toolCall.getFunction().getName());
			}
			if (toolCall.getFunction().getArguments() != null) {
				fb.add("arguments", toolCall.getFunction().getArguments());
			}
			b.add("function", fb.build());
		}
		this.writeJsonObject(b.build(), output);
	}

	public void serialize(FunctionCall func, OutputStream output) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		if (func.getName() != null) {
			b.add("name", func.getName());
		}
		if (func.getArguments() != null) {
			b.add("arguments", func.getArguments());
		}
		this.writeJsonObject(b.build(), output);
	}

	public void serialize(Tool tool, OutputStream output) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		if (tool.getType() != null) {
			b.add("type", tool.getType());
		}
		if (tool.getFunction() != null) {
			JsonObjectBuilder fb = Json.createObjectBuilder();
			if (tool.getFunction().getName() != null) {
				fb.add("name", tool.getFunction().getName());
			}
			if (tool.getFunction().getDescription() != null) {
				fb.add("description", tool.getFunction().getDescription());
			}
			if (tool.getFunction().getParameters() != null) {
				this.addJsonValue(fb, "parameters", tool.getFunction().getParameters());
			}
			if (tool.getFunction().getStrict() != null) {
				fb.add("strict", tool.getFunction().getStrict());
			}
			b.add("function", fb.build());
		}
		this.writeJsonObject(b.build(), output);
	}

	public void serialize(ToolFunction func, OutputStream output) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		if (func.getName() != null) {
			b.add("name", func.getName());
		}
		if (func.getDescription() != null) {
			b.add("description", func.getDescription());
		}
		if (func.getParameters() != null) {
			this.addJsonValue(b, "parameters", func.getParameters());
		}
		if (func.getStrict() != null) {
			b.add("strict", func.getStrict());
		}
		this.writeJsonObject(b.build(), output);
	}

	public void serialize(ResponseFormat format, OutputStream output) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		if (format.getType() != null) {
			b.add("type", format.getType());
		}
		if (format.getJsonSchema() != null) {
			this.addJsonValue(b, "json_schema", format.getJsonSchema());
		}
		this.writeJsonObject(b.build(), output);
	}

	private JsonObject toJsonObject(Map<?, ?> map) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		for (Entry<?, ?> entry : map.entrySet()) {
			this.addJsonValue(b, String.valueOf(entry.getKey()), entry.getValue());
		}
		return b.build();
	}

	private void addJsonValue(JsonObjectBuilder builder, String name, Object value) {
		if (value == null) {
			builder.addNull(name);
		} else if (value instanceof JsonValue) {
			builder.add(name, (JsonValue) value);
		} else if (value instanceof String || value instanceof Character) {
			builder.add(name, value.toString());
		} else if (value instanceof Boolean) {
			builder.add(name, (Boolean) value);
		} else if (value instanceof BigDecimal) {
			builder.add(name, (BigDecimal) value);
		} else if (value instanceof BigInteger) {
			builder.add(name, (BigInteger) value);
		} else if (value instanceof Byte || value instanceof Short || value instanceof Integer
				|| value instanceof Long) {
			builder.add(name, ((Number) value).longValue());
		} else if (value instanceof Number) {
			builder.add(name, ((Number) value).doubleValue());
		} else if (value instanceof Map) {
			builder.add(name, this.toJsonObject((Map<?, ?>) value));
		} else if (value instanceof Collection || value.getClass().isArray()) {
			builder.add(name, this.toJsonArray(value));
		} else {
			throw new SerializationException("Unsupported JSON value type: " + value.getClass().getName());
		}
	}

	private JsonArray toJsonArray(Object values) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		if (values.getClass().isArray()) {
			for (int i = 0; i < Array.getLength(values); i++) {
				this.addJsonValue(builder, Array.get(values, i));
			}
		} else {
			for (Object value : (Collection<?>) values) {
				this.addJsonValue(builder, value);
			}
		}
		return builder.build();
	}

	private void addJsonValue(JsonArrayBuilder builder, Object value) {
		if (value == null) {
			builder.addNull();
		} else if (value instanceof JsonValue) {
			builder.add((JsonValue) value);
		} else if (value instanceof String || value instanceof Character) {
			builder.add(value.toString());
		} else if (value instanceof Boolean) {
			builder.add((Boolean) value);
		} else if (value instanceof BigDecimal) {
			builder.add((BigDecimal) value);
		} else if (value instanceof BigInteger) {
			builder.add((BigInteger) value);
		} else if (value instanceof Byte || value instanceof Short || value instanceof Integer
				|| value instanceof Long) {
			builder.add(((Number) value).longValue());
		} else if (value instanceof Number) {
			builder.add(((Number) value).doubleValue());
		} else if (value instanceof Map) {
			builder.add(this.toJsonObject((Map<?, ?>) value));
		} else if (value instanceof Collection || value.getClass().isArray()) {
			builder.add(this.toJsonArray(value));
		} else {
			throw new SerializationException("Unsupported JSON value type: " + value.getClass().getName());
		}
	}

	private void writeJsonObject(JsonObject jsonObject, OutputStream output) {
		try {
			output.write(jsonObject.toString().getBytes("utf-8"));
		} catch (IOException e) {
			throw new SerializationException(e);
		}
	}
	// ==================== 反序列化（响应） ====================

	public ChatCompletionResponse deserialize(JsonObject json, ChatCompletionResponse response) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "id") && jsonValue instanceof JsonString) {
				response.setId(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "object") && jsonValue instanceof JsonString) {
				response.setObject(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "created") && jsonValue instanceof JsonNumber) {
				response.setCreated(((JsonNumber) jsonValue).longValue());
			} else if (Strings.equals(item.getKey(), "model") && jsonValue instanceof JsonString) {
				response.setModel(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "choices") && jsonValue instanceof JsonArray) {
				response.setChoices(new ArrayList<>());
				JsonArray values = ((JsonArray) jsonValue);
				for (int i = 0; i < values.size(); i++) {
					response.getChoices()
							.add(this.deserialize(values.getJsonObject(i), new ChatCompletionResponse.Choice()));
				}
			} else if (Strings.equals(item.getKey(), "usage") && jsonValue instanceof JsonObject) {
				response.setUsage(this.deserialize((JsonObject) jsonValue, new ChatCompletionResponse.Usage()));
			} else if (Strings.equals(item.getKey(), "system_fingerprint") && jsonValue instanceof JsonString) {
				response.setSystemFingerprint(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "service_tier") && jsonValue instanceof JsonString) {
				response.setServiceTier(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "error") && jsonValue instanceof JsonObject) {
				response.setError(this.deserialize((JsonObject) jsonValue, new ChatCompletionResponse.Error()));
			}
		}
		return response;
	}

	public ChatCompletionResponse.Error deserialize(JsonObject json, ChatCompletionResponse.Error error) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "message") && jsonValue instanceof JsonString) {
				error.setMessage(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "type") && jsonValue instanceof JsonString) {
				error.setType(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "param") && jsonValue instanceof JsonString) {
				error.setParam(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "code") && jsonValue instanceof JsonString) {
				error.setCode(((JsonString) jsonValue).getString());
			}
		}
		return error;
	}

	public ChatCompletionResponse.Choice deserialize(JsonObject json, ChatCompletionResponse.Choice choice) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "index") && jsonValue instanceof JsonNumber) {
				choice.setIndex(((JsonNumber) jsonValue).intValue());
			} else if (Strings.equals(item.getKey(), "finish_reason") && jsonValue instanceof JsonString) {
				choice.setFinishReason(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "message") && jsonValue instanceof JsonObject) {
				choice.setMessage(this.deserialize((JsonObject) jsonValue, new ChatMessage()));
			} else if (Strings.equals(item.getKey(), "delta") && jsonValue instanceof JsonObject) {
				choice.setDelta(this.deserialize((JsonObject) jsonValue, new ChatMessage()));
			} else if (Strings.equals(item.getKey(), "logprobs") && jsonValue instanceof JsonObject) {
				choice.setLogprobs(this.deserialize((JsonObject) jsonValue, new ChatCompletionResponse.Logprobs()));
			}
		}
		return choice;
	}

	public ChatCompletionResponse.Logprobs deserialize(JsonObject json, ChatCompletionResponse.Logprobs logprobs) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "content") && jsonValue instanceof JsonArray) {
				logprobs.setContent(new ArrayList<>());
				JsonArray values = (JsonArray) jsonValue;
				for (int i = 0; i < values.size(); i++) {
					logprobs.getContent().add(this.deserialize(values.getJsonObject(i),
							new ChatCompletionResponse.Logprobs.TokenLogprob()));
				}
			} else if (Strings.equals(item.getKey(), "refusal") && jsonValue instanceof JsonArray) {
				logprobs.setRefusal(new ArrayList<>());
				JsonArray values = (JsonArray) jsonValue;
				for (int i = 0; i < values.size(); i++) {
					logprobs.getRefusal().add(this.deserialize(values.getJsonObject(i),
							new ChatCompletionResponse.Logprobs.TokenLogprob()));
				}
			}
		}
		return logprobs;
	}

	public ChatCompletionResponse.Logprobs.TokenLogprob deserialize(JsonObject json,
			ChatCompletionResponse.Logprobs.TokenLogprob tokenLogprob) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "token") && jsonValue instanceof JsonString) {
				tokenLogprob.setToken(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "logprob") && jsonValue instanceof JsonNumber) {
				tokenLogprob.setLogprob(((JsonNumber) jsonValue).doubleValue());
			} else if (Strings.equals(item.getKey(), "bytes") && jsonValue instanceof JsonArray) {
				tokenLogprob.setBytes(new ArrayList<>());
				JsonArray values = (JsonArray) jsonValue;
				for (int i = 0; i < values.size(); i++) {
					tokenLogprob.getBytes().add(values.getInt(i));
				}
			} else if (Strings.equals(item.getKey(), "top_logprobs") && jsonValue instanceof JsonArray) {
				tokenLogprob.setTopLogprobs(new ArrayList<>());
				JsonArray values = (JsonArray) jsonValue;
				for (int i = 0; i < values.size(); i++) {
					tokenLogprob.getTopLogprobs().add(this.deserialize(values.getJsonObject(i),
							new ChatCompletionResponse.Logprobs.TopLogprob()));
				}
			}
		}
		return tokenLogprob;
	}

	public ChatCompletionResponse.Logprobs.TopLogprob deserialize(JsonObject json,
			ChatCompletionResponse.Logprobs.TopLogprob topLogprob) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "token") && jsonValue instanceof JsonString) {
				topLogprob.setToken(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "logprob") && jsonValue instanceof JsonNumber) {
				topLogprob.setLogprob(((JsonNumber) jsonValue).doubleValue());
			} else if (Strings.equals(item.getKey(), "bytes") && jsonValue instanceof JsonArray) {
				topLogprob.setBytes(new ArrayList<>());
				JsonArray values = (JsonArray) jsonValue;
				for (int i = 0; i < values.size(); i++) {
					topLogprob.getBytes().add(values.getInt(i));
				}
			}
		}
		return topLogprob;
	}

	public ChatCompletionResponse.Usage deserialize(JsonObject json, ChatCompletionResponse.Usage usage) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "prompt_tokens") && jsonValue instanceof JsonNumber) {
				usage.setPromptTokens(((JsonNumber) jsonValue).intValue());
			} else if (Strings.equals(item.getKey(), "completion_tokens") && jsonValue instanceof JsonNumber) {
				usage.setCompletionTokens(((JsonNumber) jsonValue).intValue());
			} else if (Strings.equals(item.getKey(), "total_tokens") && jsonValue instanceof JsonNumber) {
				usage.setTotalTokens(((JsonNumber) jsonValue).intValue());
			} else if (Strings.equals(item.getKey(), "completion_tokens_details") && jsonValue instanceof JsonObject) {
				usage.setCompletionTokensDetails(
						this.deserialize((JsonObject) jsonValue, new ChatCompletionResponse.CompletionTokensDetails()));
			} else if (Strings.equals(item.getKey(), "prompt_tokens_details") && jsonValue instanceof JsonObject) {
				usage.setPromptTokensDetails(
						this.deserialize((JsonObject) jsonValue, new ChatCompletionResponse.PromptTokensDetails()));
			}
		}
		return usage;
	}

	public ChatCompletionResponse.CompletionTokensDetails deserialize(JsonObject json,
			ChatCompletionResponse.CompletionTokensDetails details) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "reasoning_tokens") && jsonValue instanceof JsonNumber) {
				details.setReasoningTokens(((JsonNumber) jsonValue).intValue());
			} else if (Strings.equals(item.getKey(), "audio_tokens") && jsonValue instanceof JsonNumber) {
				details.setAudioTokens(((JsonNumber) jsonValue).intValue());
			} else if (Strings.equals(item.getKey(), "accepted_prediction_tokens") && jsonValue instanceof JsonNumber) {
				details.setAcceptedPredictionTokens(((JsonNumber) jsonValue).intValue());
			} else if (Strings.equals(item.getKey(), "rejected_prediction_tokens") && jsonValue instanceof JsonNumber) {
				details.setRejectedPredictionTokens(((JsonNumber) jsonValue).intValue());
			}
		}
		return details;
	}

	public ChatCompletionResponse.PromptTokensDetails deserialize(JsonObject json,
			ChatCompletionResponse.PromptTokensDetails details) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "cached_tokens") && jsonValue instanceof JsonNumber) {
				details.setCachedTokens(((JsonNumber) jsonValue).intValue());
			} else if (Strings.equals(item.getKey(), "audio_tokens") && jsonValue instanceof JsonNumber) {
				details.setAudioTokens(((JsonNumber) jsonValue).intValue());
			}
		}
		return details;
	}

	public ChatMessage deserialize(JsonObject json, ChatMessage message) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "role") && jsonValue instanceof JsonString) {
				message.setRole(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "content")) {
				if (jsonValue instanceof JsonString) {
					message.setContent(((JsonString) jsonValue).getString());
				} else if (jsonValue instanceof JsonArray) {
					ArrayList<ContentPart> contentParts = new ArrayList<>();
					JsonArray values = (JsonArray) jsonValue;
					for (int i = 0; i < values.size(); i++) {
						contentParts.add(this.deserialize(values.getJsonObject(i), new ContentPart()));
					}
					message.setContent(contentParts);
				} else if (jsonValue == null || jsonValue == JsonValue.NULL) {
					message.setContent(null);
				}
			} else if (Strings.equals(item.getKey(), "name") && jsonValue instanceof JsonString) {
				message.setName(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "tool_calls") && jsonValue instanceof JsonArray) {
				message.setToolCalls(new ArrayList<>());
				JsonArray values = (JsonArray) jsonValue;
				for (int i = 0; i < values.size(); i++) {
					message.getToolCalls().add(this.deserialize(values.getJsonObject(i), new ToolCall()));
				}
			} else if (Strings.equals(item.getKey(), "tool_call_id") && jsonValue instanceof JsonString) {
				message.setToolCallId(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "refusal") && jsonValue instanceof JsonString) {
				message.setRefusal(((JsonString) jsonValue).getString());
			} else if ((Strings.equals(item.getKey(), "audio") || Strings.equals(item.getKey(), "output_audio"))
					&& jsonValue instanceof JsonObject) {
				message.setOutputAudio(this.deserialize((JsonObject) jsonValue, new OutputAudio()));
			} else if ((Strings.equals(item.getKey(), "reasoning_content")
					|| Strings.equals(item.getKey(), "reasoning"))
					&& jsonValue instanceof JsonString) {
				message.setReasoningContent(((JsonString) jsonValue).getString());
			}
		}
		return message;
	}

	public ToolCall deserialize(JsonObject json, ToolCall toolCall) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "id") && jsonValue instanceof JsonString) {
				toolCall.setId(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "type") && jsonValue instanceof JsonString) {
				toolCall.setType(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "function") && jsonValue instanceof JsonObject) {
				toolCall.setFunction(this.deserialize((JsonObject) jsonValue, new FunctionCall()));
			}
		}
		return toolCall;
	}

	public FunctionCall deserialize(JsonObject json, FunctionCall functionCall) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "name") && jsonValue instanceof JsonString) {
				functionCall.setName(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "arguments") && jsonValue instanceof JsonString) {
				functionCall.setArguments(((JsonString) jsonValue).getString());
			}
		}
		return functionCall;
	}

	public ContentPart deserialize(JsonObject json, ContentPart contentPart) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "type") && jsonValue instanceof JsonString) {
				contentPart.setType(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "text") && jsonValue instanceof JsonString) {
				contentPart.setText(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "image_url") && jsonValue instanceof JsonObject) {
				contentPart.setImageUrl(this.deserialize((JsonObject) jsonValue, new ImageUrl()));
			} else if (Strings.equals(item.getKey(), "input_audio") && jsonValue instanceof JsonObject) {
				contentPart.setInputAudio(this.deserialize((JsonObject) jsonValue, new InputAudio()));
			} else if (Strings.equals(item.getKey(), "file") && jsonValue instanceof JsonObject) {
				contentPart.setFile(this.deserialize((JsonObject) jsonValue, new FileReference()));
				} else if (Strings.equals(item.getKey(), "thinking") && jsonValue instanceof JsonString) {
					contentPart.setThinking(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "refusal") && jsonValue instanceof JsonString) {
				contentPart.setRefusal(((JsonString) jsonValue).getString());
			}
		}
		return contentPart;
	}

	public ImageUrl deserialize(JsonObject json, ImageUrl imageUrl) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "url") && jsonValue instanceof JsonString) {
				imageUrl.setUrl(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "detail") && jsonValue instanceof JsonString) {
				imageUrl.setDetail(((JsonString) jsonValue).getString());
			}
		}
		return imageUrl;
	}

	public InputAudio deserialize(JsonObject json, InputAudio inputAudio) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "data") && jsonValue instanceof JsonString) {
				inputAudio.setData(Bytes.fromBase64String(((JsonString) jsonValue).getString()));
			} else if (Strings.equals(item.getKey(), "format") && jsonValue instanceof JsonString) {
				inputAudio.setFormat(((JsonString) jsonValue).getString());
			}
		}
		return inputAudio;
	}

		public OutputAudio deserialize(JsonObject json, OutputAudio outputAudio) {
			JsonValue jsonValue = null;
			for (Entry<String, JsonValue> item : json.entrySet()) {
				jsonValue = item.getValue();
				if (Strings.equals(item.getKey(), "id") && jsonValue instanceof JsonString) {
					outputAudio.setId(((JsonString) jsonValue).getString());
				} else if (Strings.equals(item.getKey(), "data") && jsonValue instanceof JsonString) {
					outputAudio.setData(Bytes.fromBase64String(((JsonString) jsonValue).getString()));
				} else if (Strings.equals(item.getKey(), "format") && jsonValue instanceof JsonString) {
					outputAudio.setFormat(((JsonString) jsonValue).getString());
				} else if (Strings.equals(item.getKey(), "expires_at") && jsonValue instanceof JsonNumber) {
					outputAudio.setExpiresAt(((JsonNumber) jsonValue).longValue());
				} else if (Strings.equals(item.getKey(), "transcript") && jsonValue instanceof JsonString) {
					outputAudio.setTranscript(((JsonString) jsonValue).getString());
				}
			}
			return outputAudio;
		}

	public FileReference deserialize(JsonObject json, FileReference file) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "file_id") && jsonValue instanceof JsonString) {
				file.setFileId(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "filename") && jsonValue instanceof JsonString) {
				file.setFileName(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "media_type") && jsonValue instanceof JsonString) {
				file.setMimeType(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "file_data") && jsonValue instanceof JsonString) {
				file.setData(Bytes.fromBase64String(((JsonString) jsonValue).getString()));
			}
		}
		return file;
	}

	// ==================== Files API ====================

	public void serialize(FileUploadRequest request, OutputStream output) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		if (request.getPurpose() != null) {
			b.add("purpose", request.getPurpose());
		}
		if (request.getFileName() != null) {
			b.add("filename", request.getFileName());
		}
		this.writeJsonObject(b.build(), output);
	}

	public void serialize(FileUploadResponse response, OutputStream output) {
		JsonObjectBuilder b = Json.createObjectBuilder();
		if (response.getId() != null) {
			b.add("id", response.getId());
		}
		if (response.getObject() != null) {
			b.add("object", response.getObject());
		}
		if (response.getBytes() != null) {
			b.add("bytes", response.getBytes());
		}
		if (response.getCreatedAt() != null) {
			b.add("created_at", response.getCreatedAt());
		}
		if (response.getExpiresAt() != null) {
			b.add("expires_at", response.getExpiresAt());
		}
		if (response.getFileName() != null) {
			b.add("filename", response.getFileName());
		}
		if (response.getPurpose() != null) {
			b.add("purpose", response.getPurpose());
		}
		if (response.getStatus() != null) {
			b.add("status", response.getStatus());
		}
		if (response.getStatusDetails() != null) {
			b.add("status_details", response.getStatusDetails());
		}
		this.writeJsonObject(b.build(), output);
	}

	public FileUploadResponse deserialize(JsonObject json, FileUploadResponse response) {
		JsonValue jsonValue = null;
		for (Entry<String, JsonValue> item : json.entrySet()) {
			jsonValue = item.getValue();
			if (Strings.equals(item.getKey(), "id") && jsonValue instanceof JsonString) {
				response.setId(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "object") && jsonValue instanceof JsonString) {
				response.setObject(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "bytes") && jsonValue instanceof JsonNumber) {
				response.setBytes(((JsonNumber) jsonValue).longValue());
			} else if (Strings.equals(item.getKey(), "created_at") && jsonValue instanceof JsonNumber) {
				response.setCreatedAt(((JsonNumber) jsonValue).longValue());
			} else if (Strings.equals(item.getKey(), "expires_at") && jsonValue instanceof JsonNumber) {
				response.setExpiresAt(((JsonNumber) jsonValue).longValue());
			} else if (Strings.equals(item.getKey(), "filename") && jsonValue instanceof JsonString) {
				response.setFileName(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "purpose") && jsonValue instanceof JsonString) {
				response.setPurpose(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "status") && jsonValue instanceof JsonString) {
				response.setStatus(((JsonString) jsonValue).getString());
			} else if (Strings.equals(item.getKey(), "status_details") && jsonValue instanceof JsonString) {
				response.setStatusDetails(((JsonString) jsonValue).getString());
			}
		}
		return response;
	}

}
