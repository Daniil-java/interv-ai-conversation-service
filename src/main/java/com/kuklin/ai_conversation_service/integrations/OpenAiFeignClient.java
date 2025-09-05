package com.kuklin.ai_conversation_service.integrations;

import com.kuklin.ai_conversation_service.configurations.FeignClientConfig;
import com.kuklin.ai_conversation_service.models.SpeechRequest;
import com.kuklin.ai_conversation_service.models.TranscriptionResponse;
import com.kuklin.ai_conversation_service.models.openai.OpenAiChatCompletionRequest;
import com.kuklin.ai_conversation_service.models.openai.OpenAiChatCompletionResponse;
import org.springframework.core.io.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;

@FeignClient(
        value = "open-ai-feign-client",
        url = "${integrations.openai-api.url}",
        configuration = FeignClientConfig.class
)
public interface OpenAiFeignClient {
    @PostMapping("chat/completions")
    OpenAiChatCompletionResponse generate(@RequestHeader("Authorization") String key,
                                          @RequestBody OpenAiChatCompletionRequest request);

    @PostMapping(value = "audio/transcriptions",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    TranscriptionResponse transcribeAudio(
            @RequestHeader("Authorization") String key,
            @RequestPart("file") Resource file,
            @RequestPart("model") String model
    );

    @PostMapping("audio/speech")
    byte[] makeSpeech(
            @RequestHeader("Authorization") String key,
            @RequestBody SpeechRequest speechRequest
    );

}
