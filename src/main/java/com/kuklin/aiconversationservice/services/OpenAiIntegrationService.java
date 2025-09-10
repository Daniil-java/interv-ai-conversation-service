package com.kuklin.aiconversationservice.services;

import com.kuklin.aiconversationservice.entities.ChatMessage;
import com.kuklin.aiconversationservice.integrations.OpenAiFeignClient;
import com.kuklin.aiconversationservice.models.AiResponse;
import com.kuklin.aiconversationservice.models.SpeechRequest;
import com.kuklin.aiconversationservice.models.TranscriptionResponse;
import com.kuklin.aiconversationservice.models.enums.ProviderVariant;
import com.kuklin.aiconversationservice.models.openai.OpenAiChatCompletionRequest;
import com.kuklin.aiconversationservice.models.openai.OpenAiChatCompletionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Slf4j
public class OpenAiIntegrationService {
    private final OpenAiFeignClient openAiFeignClient;
    private final String aiKey;

    public OpenAiIntegrationService(@Value("${GENERATION_TOKEN}") String aiKey,
                                    OpenAiFeignClient openAiFeignClient) {
        this.aiKey = aiKey;
        this.openAiFeignClient = openAiFeignClient;
    }

    public AiResponse fetchResponse(ChatMessage userMessage, List<ChatMessage> chatMessageList) {
        OpenAiChatCompletionRequest request;
        if (chatMessageList == null) {
            request = OpenAiChatCompletionRequest.makeDefaultRequest(userMessage.getContent());
        } else {
            request = OpenAiChatCompletionRequest.makeRequest(
                    chatMessageList, userMessage.getModel(), userMessage.getTemperature());
        }

        OpenAiChatCompletionResponse response =
                openAiFeignClient.generate("Bearer " + aiKey, request);

        return response.toAiResponse(userMessage.getModel());
    }

    public String fetchAudioResponse(byte[] content) {
        Resource resource = new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return "audio.ogg";
            }
        };

        TranscriptionResponse response = openAiFeignClient.transcribeAudio(
                "Bearer " + aiKey,
                resource,
                "whisper-1"
//                "gpt-4o-transcribe"
        );

        return response.getText();
    }

    public byte[] makeSpeech(String text) {
        SpeechRequest speechRequest = new SpeechRequest();
        speechRequest.setInput(text);
        speechRequest.setModel("gpt-4o-mini-tts");
        speechRequest.setVoice("alloy");
        return openAiFeignClient.makeSpeech(
                "Bearer " + aiKey,
                speechRequest);
    }

    public ProviderVariant getProviderName() {
        return ProviderVariant.OPENAI;
    }

}
