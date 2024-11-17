package de.dkeiss.aicurator.config;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;

import java.util.Map;

import static org.springframework.ai.anthropic.api.AnthropicApi.ChatModel.CLAUDE_3_5_SONNET;
import static org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest.ResponseFormat.Type.JSON_SCHEMA;

@Configuration
public class ChatClientConfig {

    @Bean
    @Profile({"default", "claude3"})
    public ChatClient claude3() {
        AnthropicChatOptions anthropicChatOptions = AnthropicChatOptions.builder()
                .withModel(CLAUDE_3_5_SONNET)
                .withMaxTokens(4000)
                .withTemperature(0.4)
                .build();
        ChatModel chatModel = new AnthropicChatModel(new AnthropicApi(System.getenv("ANTHROPIC_KEY")), anthropicChatOptions);
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        return builder.build();
    }

    @Bean
    @Profile({"chatgpt"})
    public ChatClient gpt4() {
        String responseFormat = """
                    {
                   "type": "object",
                   "properties": {
                     "pageSourceCode": {
                       "type": "string"
                     },
                     "explanation": {
                       "type": "string"
                     },
                     "locator": {
                       "type": "string"
                     }
                   },
                   "required": ["pageSourceCode", "explanation", "locator"],
                   "additionalProperties": false
                 }
                """;

        OpenAiChatOptions build = OpenAiChatOptions.builder()
                .withModel(OpenAiApi.ChatModel.GPT_4_O_MINI)
                .withMaxTokens(4000)
                .withTemperature(0.4)
                .withResponseFormat(new OpenAiApi.ChatCompletionRequest.ResponseFormat(JSON_SCHEMA, responseFormat))
                .withStreamUsage(false)
                .withHttpHeaders(Map.of(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"))
                .build();
        ChatModel chatModel = new OpenAiChatModel(new OpenAiApi(System.getenv("CHATGPT_KEY")), build);
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        return builder.build();
    }

    /*
     * Use LM Studio to mimic the chat model locally.
     */
    @Bean
    @Profile({"local"})
    public ChatClient local() {
        OpenAiChatOptions build = OpenAiChatOptions.builder()
                .build();
        ChatModel chatModel = new OpenAiChatModel(new OpenAiApi("http://localhost:1234", "1234"), build);
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        return builder.build();
    }

}
