package org.colorcoding.ibas.thirdpartyapp.client.openai;

import java.util.List;
import java.util.Map;

import java.util.ArrayList;

public class ChatCompletionRequest {

	private String model;
	private List<ChatMessage> messages;
	// 多模态输出
	private List<String> modalities;
	private AudioConfig audio;
	// 输出控制
	private Double temperature;
	private Double topP;
	private Integer n;
	private Integer maxTokens;
	private Integer maxCompletionTokens;
	private Object stop;
	private Boolean stream;
	// 随机性/惩罚
	private Double frequencyPenalty;
	private Double presencePenalty;
	private Integer seed;
	// 工具调用
	private List<Tool> tools;
	private Object toolChoice;
	private Boolean parallelToolCalls;
	// 用户标识
	private String user;
	// 日志概率
	private Boolean logprobs;
	private Integer topLogprobs;
	// 响应格式
	private ResponseFormat responseFormat;
	// logit偏差
	private Map<String, Integer> logitBias;

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public List<ChatMessage> getMessages() {
		if (messages == null) {
			messages = new ArrayList<>();
		}
		return messages;
	}

	public void setMessages(List<ChatMessage> messages) {
		this.messages = messages;
	}

	public List<String> getModalities() {
		if (modalities == null) {
			modalities = new ArrayList<>();
		}
		return modalities;
	}

	public void setModalities(List<String> modalities) {
		this.modalities = modalities;
	}

	public AudioConfig getAudio() {
		return audio;
	}

	public void setAudio(AudioConfig audio) {
		this.audio = audio;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Double getTopP() {
		return topP;
	}

	public void setTopP(Double topP) {
		this.topP = topP;
	}

	public Integer getN() {
		return n;
	}

	public void setN(Integer n) {
		this.n = n;
	}

	public Integer getMaxTokens() {
		return maxTokens;
	}

	public void setMaxTokens(Integer maxTokens) {
		this.maxTokens = maxTokens;
	}

	public Integer getMaxCompletionTokens() {
		return maxCompletionTokens;
	}

	public void setMaxCompletionTokens(Integer maxCompletionTokens) {
		this.maxCompletionTokens = maxCompletionTokens;
	}

	public Object getStop() {
		return stop;
	}

	public void setStop(Object stop) {
		this.stop = stop;
	}

	public Boolean getStream() {
		return stream;
	}

	public void setStream(Boolean stream) {
		this.stream = stream;
	}

	public Double getFrequencyPenalty() {
		return frequencyPenalty;
	}

	public void setFrequencyPenalty(Double frequencyPenalty) {
		this.frequencyPenalty = frequencyPenalty;
	}

	public Double getPresencePenalty() {
		return presencePenalty;
	}

	public void setPresencePenalty(Double presencePenalty) {
		this.presencePenalty = presencePenalty;
	}

	public Integer getSeed() {
		return seed;
	}

	public void setSeed(Integer seed) {
		this.seed = seed;
	}

	public List<Tool> getTools() {
		if (tools == null) {
			tools = new ArrayList<>();
		}
		return tools;
	}

	public void setTools(List<Tool> tools) {
		this.tools = tools;
	}

	public Object getToolChoice() {
		return toolChoice;
	}

	public void setToolChoice(Object toolChoice) {
		this.toolChoice = toolChoice;
	}

	public Boolean getParallelToolCalls() {
		return parallelToolCalls;
	}

	public void setParallelToolCalls(Boolean parallelToolCalls) {
		this.parallelToolCalls = parallelToolCalls;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Boolean getLogprobs() {
		return logprobs;
	}

	public void setLogprobs(Boolean logprobs) {
		this.logprobs = logprobs;
	}

	public Integer getTopLogprobs() {
		return topLogprobs;
	}

	public void setTopLogprobs(Integer topLogprobs) {
		this.topLogprobs = topLogprobs;
	}

	public ResponseFormat getResponseFormat() {
		return responseFormat;
	}

	public void setResponseFormat(ResponseFormat responseFormat) {
		this.responseFormat = responseFormat;
	}

	public Map<String, Integer> getLogitBias() {
		return logitBias;
	}

	public void setLogitBias(Map<String, Integer> logitBias) {
		this.logitBias = logitBias;
	}
}
