package com.kuklin.aiconversationservice.models.openai;

import com.kuklin.aiconversationservice.entities.ChatMessage;
import com.kuklin.aiconversationservice.entities.Model;
import com.kuklin.aiconversationservice.models.enums.ChatModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class OpenAiChatCompletionRequest {
    private String model;
    private List<Message> messages;
    private float temperature;

    public static final float TEMPERATURE_DEFAULT = 0.1f;

    public static OpenAiChatCompletionRequest makeRequest(
            List<ChatMessage> chatMessageList, Model model, Float temperature) {

        if (temperature == null) temperature = OpenAiChatCompletionRequest.TEMPERATURE_DEFAULT;
        return new OpenAiChatCompletionRequest()
                        .setTemperature(temperature)
                        .setModel(model.getModelName())
                        .setMessages(Message.convertFromChatMessages(chatMessageList));
    }

    public static OpenAiChatCompletionRequest makeDefaultRequest(
            String content) {

        return new OpenAiChatCompletionRequest()
                .setTemperature(TEMPERATURE_DEFAULT)
                .setModel(ChatModel.GPT4O.getModel())
                .setMessages(Message.getFirstMessage(content));
    }

}
