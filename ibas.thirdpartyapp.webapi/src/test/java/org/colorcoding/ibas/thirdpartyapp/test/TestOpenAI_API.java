package org.colorcoding.ibas.thirdpartyapp.test;

import java.util.Properties;

import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.thirdpartyapp.MyConfiguration;
import org.colorcoding.ibas.thirdpartyapp.client.ApplicationClient;
import org.colorcoding.ibas.thirdpartyapp.client.ApplicationClientManager;
import org.colorcoding.ibas.thirdpartyapp.client.OpenAI_API;
import org.colorcoding.ibas.thirdpartyapp.client.openai.ChatCompletionResponse;
import org.colorcoding.ibas.thirdpartyapp.client.openai.ChatMessage;
import org.colorcoding.ibas.thirdpartyapp.client.openai.FileUploadRequest;

import junit.framework.TestCase;

public class TestOpenAI_API extends TestCase {

	public void testSendMessage() throws Exception {
		ApplicationClient client = ApplicationClientManager.newInstance().create("OPENAI_API_ALBL");
		Properties properties = new Properties();
		ChatMessage message = ChatMessage.createUserMessage();
		message.setContent("who are you?");
		properties.put(Strings.concat(OpenAI_API.PARAM_NAME_MESSAGE, String.valueOf(message.hashCode())), message);

		IOperationResult<ChatCompletionResponse> operationResult = client
				.execute(OpenAI_API.EXECUT_NAME_CHAT_COMPLETIONS, properties);
		if (operationResult.getError() != null) {
			throw operationResult.getError();
		}
		for (ChatCompletionResponse item : operationResult.getResultObjects()) {
			System.out.println(item.getId());
		}
	}

	public void testUploadFile() throws Exception {
		ApplicationClient client = ApplicationClientManager.newInstance().create("OPENAI_API_ALBL");
		Properties properties = new Properties();
		FileUploadRequest request = new FileUploadRequest();
		request.setPurpose(FileUploadRequest.PURPOSE_VALUE_ASSISTANTS);
		request.setFileName("app.xml");
		request.setData(Files.readAllBytes(Files.valueOf(MyConfiguration.getWorkFolder(), "..", "..", "app.xml")));
		properties.put(Strings.concat(OpenAI_API.PARAM_NAME_FILE, String.valueOf(request.hashCode())), request);

		IOperationResult<ChatCompletionResponse> operationResult = client.execute(OpenAI_API.EXECUT_NAME_FILES_UPLOAD,
				properties);
		if (operationResult.getError() != null) {
			throw operationResult.getError();
		}
		for (ChatCompletionResponse item : operationResult.getResultObjects()) {
			System.out.println(item.getId());
		}

	}

}
