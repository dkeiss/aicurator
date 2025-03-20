package de.dkeiss.aicurator.config;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;

import java.util.Map;

import static org.springframework.ai.anthropic.api.AnthropicApi.ChatModel.CLAUDE_3_5_SONNET;

@Configuration
public class ChatClientConfig {

    @Bean
    @Profile({"default", "claude3"})
    public ChatClient claude3() {
        AnthropicChatOptions chatOptions = AnthropicChatOptions.builder()
                .model(CLAUDE_3_5_SONNET)
                .maxTokens(4000)
                .temperature(0.4)
                .build();
        AnthropicChatModel chatModel = AnthropicChatModel.builder()
                .anthropicApi(new AnthropicApi(System.getenv("ANTHROPIC_KEY")))
                .defaultOptions(chatOptions)
                .build();
        return ChatClient.builder(chatModel).build();
    }

    @Bean
    @Profile({"chatgpt"})
    public ChatClient gpt4() {
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_4_O_MINI)
                .maxTokens(4000)
                .temperature(0.4)
                .responseFormat(ResponseFormat.builder()
                        .jsonSchema("""
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
                                """)
                        .build())
                .streamUsage(false)
                .httpHeaders(Map.of(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"))
                .build();
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .apiKey(System.getenv("CHATGPT_KEY"))
                        .build())
                .defaultOptions(chatOptions)
                .build();
        return ChatClient.builder(chatModel).build();
    }

    /*
     * Use LM Studio to mimic the chat model locally.
     */
    @Bean
    @Profile({"local"})
    public ChatClient local() {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl("http://localhost:1234")
                        .apiKey("1234")
                        .build())
                .build();
        return ChatClient.builder(chatModel).build();
    }

}
