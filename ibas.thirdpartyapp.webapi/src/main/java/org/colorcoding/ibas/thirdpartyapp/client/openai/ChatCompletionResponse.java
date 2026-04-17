package org.colorcoding.ibas.thirdpartyapp.client.openai;

import java.util.List;

public class ChatCompletionResponse {

	private String id;
	private String object;
	private long created;
	private String model;
	private List<Choice> choices;
	private Usage usage;
	private String systemFingerprint;
	private String serviceTier;
	private Error error;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public List<Choice> getChoices() {
		return choices;
	}

	public void setChoices(List<Choice> choices) {
		this.choices = choices;
	}

	public Usage getUsage() {
		return usage;
	}

	public void setUsage(Usage usage) {
		this.usage = usage;
	}

	public String getSystemFingerprint() {
		return systemFingerprint;
	}

	public void setSystemFingerprint(String systemFingerprint) {
		this.systemFingerprint = systemFingerprint;
	}

	public String getServiceTier() {
		return serviceTier;
	}

	public void setServiceTier(String serviceTier) {
		this.serviceTier = serviceTier;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public static class Error {
		private String message;
		private String type;
		private String param;
		private String code;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getParam() {
			return param;
		}

		public void setParam(String param) {
			this.param = param;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
	}

	public static class Choice {
		private int index;
		private ChatMessage message;
		private ChatMessage delta;
		private String finishReason;
		private Logprobs logprobs;

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public ChatMessage getMessage() {
			return message;
		}

		public void setMessage(ChatMessage message) {
			this.message = message;
		}

		public ChatMessage getDelta() {
			return delta;
		}

		public void setDelta(ChatMessage delta) {
			this.delta = delta;
		}

		public String getFinishReason() {
			return finishReason;
		}

		public void setFinishReason(String finishReason) {
			this.finishReason = finishReason;
		}

		public Logprobs getLogprobs() {
			return logprobs;
		}

		public void setLogprobs(Logprobs logprobs) {
			this.logprobs = logprobs;
		}
	}

	public static class Logprobs {
		private List<TokenLogprob> content;
		private List<TokenLogprob> refusal;

		public List<TokenLogprob> getContent() {
			return content;
		}

		public void setContent(List<TokenLogprob> content) {
			this.content = content;
		}

		public List<TokenLogprob> getRefusal() {
			return refusal;
		}

		public void setRefusal(List<TokenLogprob> refusal) {
			this.refusal = refusal;
		}

		public static class TokenLogprob {
			private String token;
			private double logprob;
			private List<Integer> bytes;
			private List<TopLogprob> topLogprobs;

			public String getToken() {
				return token;
			}

			public void setToken(String token) {
				this.token = token;
			}

			public double getLogprob() {
				return logprob;
			}

			public void setLogprob(double logprob) {
				this.logprob = logprob;
			}

			public List<Integer> getBytes() {
				return bytes;
			}

			public void setBytes(List<Integer> bytes) {
				this.bytes = bytes;
			}

			public List<TopLogprob> getTopLogprobs() {
				return topLogprobs;
			}

			public void setTopLogprobs(List<TopLogprob> topLogprobs) {
				this.topLogprobs = topLogprobs;
			}
		}

		public static class TopLogprob {
			private String token;
			private double logprob;
			private List<Integer> bytes;

			public String getToken() {
				return token;
			}

			public void setToken(String token) {
				this.token = token;
			}

			public double getLogprob() {
				return logprob;
			}

			public void setLogprob(double logprob) {
				this.logprob = logprob;
			}

			public List<Integer> getBytes() {
				return bytes;
			}

			public void setBytes(List<Integer> bytes) {
				this.bytes = bytes;
			}
		}
	}

	public static class Usage {
		private int promptTokens;
		private int completionTokens;
		private int totalTokens;
		private CompletionTokensDetails completionTokensDetails;
		private PromptTokensDetails promptTokensDetails;

		public int getPromptTokens() {
			return promptTokens;
		}

		public void setPromptTokens(int promptTokens) {
			this.promptTokens = promptTokens;
		}

		public int getCompletionTokens() {
			return completionTokens;
		}

		public void setCompletionTokens(int completionTokens) {
			this.completionTokens = completionTokens;
		}

		public int getTotalTokens() {
			return totalTokens;
		}

		public void setTotalTokens(int totalTokens) {
			this.totalTokens = totalTokens;
		}

		public CompletionTokensDetails getCompletionTokensDetails() {
			return completionTokensDetails;
		}

		public void setCompletionTokensDetails(CompletionTokensDetails completionTokensDetails) {
			this.completionTokensDetails = completionTokensDetails;
		}

		public PromptTokensDetails getPromptTokensDetails() {
			return promptTokensDetails;
		}

		public void setPromptTokensDetails(PromptTokensDetails promptTokensDetails) {
			this.promptTokensDetails = promptTokensDetails;
		}
	}

	public static class CompletionTokensDetails {
		private int reasoningTokens;
		private int audioTokens;
		private int acceptedPredictionTokens;
		private int rejectedPredictionTokens;

		public int getReasoningTokens() {
			return reasoningTokens;
		}

		public void setReasoningTokens(int reasoningTokens) {
			this.reasoningTokens = reasoningTokens;
		}

		public int getAudioTokens() {
			return audioTokens;
		}

		public void setAudioTokens(int audioTokens) {
			this.audioTokens = audioTokens;
		}

		public int getAcceptedPredictionTokens() {
			return acceptedPredictionTokens;
		}

		public void setAcceptedPredictionTokens(int acceptedPredictionTokens) {
			this.acceptedPredictionTokens = acceptedPredictionTokens;
		}

		public int getRejectedPredictionTokens() {
			return rejectedPredictionTokens;
		}

		public void setRejectedPredictionTokens(int rejectedPredictionTokens) {
			this.rejectedPredictionTokens = rejectedPredictionTokens;
		}
	}

	public static class PromptTokensDetails {
		private int cachedTokens;
		private int audioTokens;

		public int getCachedTokens() {
			return cachedTokens;
		}

		public void setCachedTokens(int cachedTokens) {
			this.cachedTokens = cachedTokens;
		}

		public int getAudioTokens() {
			return audioTokens;
		}

		public void setAudioTokens(int audioTokens) {
			this.audioTokens = audioTokens;
		}
	}
}
