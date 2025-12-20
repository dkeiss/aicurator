package de.dkeiss.aicurator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static io.cucumber.core.options.RuntimeOptions.defaultOptions;
import static org.springframework.ai.anthropic.api.AnthropicApi.ChatModel.CLAUDE_3_7_SONNET;
import static org.springframework.ai.mistralai.api.MistralAiApi.ChatModel.*;
import static org.springframework.ai.openai.api.OpenAiApi.ChatModel.GPT_5;
import static org.springframework.ai.openai.api.ResponseFormat.Type.JSON_SCHEMA;
import static org.springframework.http.HttpHeaders.ACCEPT_ENCODING;

@Configuration
public class ChatClientConfig {

    @Bean
    @Profile({"default", "claude"})
    public ChatClient claude() {
        AnthropicChatOptions chatOptions = AnthropicChatOptions.builder()
                .model(CLAUDE_3_7_SONNET)
                .maxTokens(4000)
                .temperature(0.4)
                .build();
        AnthropicChatModel chatModel = AnthropicChatModel.builder()
                .anthropicApi(AnthropicApi.builder()
                        .apiKey(System.getenv("ANTHROPIC_KEY"))
                        .build())
                .defaultOptions(chatOptions)
                .build();
        return ChatClient.builder(chatModel).build();
    }

    @Bean
    @Profile({"chatgpt"})
    public ChatClient chatgpt() {
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model(GPT_5)
                .maxCompletionTokens(4000)
                .responseFormat(ResponseFormat.builder()
                        .type(JSON_SCHEMA)
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
                .httpHeaders(Map.of(ACCEPT_ENCODING, "gzip, deflate"))
                .build();
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .apiKey(System.getenv("CHATGPT_KEY"))
                        .build())
                .defaultOptions(chatOptions)
                .build();
        return ChatClient.builder(chatModel).build();
    }

    @Bean
    @Profile({"mistral"})
    public ChatClient mistral() {
        MistralAiChatOptions chatOptions = MistralAiChatOptions.builder()
                .model(CODESTRAL)
                .maxTokens(4000)
                .temperature(0.4)
                .build();

        MistralAiChatModel chatModel = MistralAiChatModel.builder()
                .mistralAiApi(MistralAiApi.builder()
                        .apiKey(System.getenv("MISTRAL_KEY"))
                        .restClientBuilder(RestClient.builder()
                                .requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory())))
                        .build())
                .defaultOptions(chatOptions)
                .build();

        return ChatClient.builder(chatModel).build();
    }

    /*
     * Use LM Studio to load chat model locally.
     */
    @Bean
    @Profile({"local-mistral"})
    public ChatClient localMistral() {
        MistralAiChatOptions chatOptions = MistralAiChatOptions.builder()
                .model("mistralai/codestral-22b-v0.1")
                .temperature(0.4)
                .build();

        MistralAiChatModel chatModel = MistralAiChatModel.builder()
                .mistralAiApi(MistralAiApi.builder()
                        .baseUrl("http://localhost:1234")
                        .apiKey("1234")
                        .restClientBuilder(RestClient.builder()
                                .requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory())))
                        .build())
                .defaultOptions(chatOptions)
                .build();

        return ChatClient.builder(chatModel).build();
    }

}
