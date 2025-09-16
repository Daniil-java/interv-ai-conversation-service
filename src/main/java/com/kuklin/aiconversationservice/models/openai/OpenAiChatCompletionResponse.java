package com.kuklin.aiconversationservice.models.openai;

import com.kuklin.aiconversationservice.models.BaseResponse;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
@Data
@Accessors(chain = true)
public class OpenAiChatCompletionResponse extends BaseResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    private String systemFingerprint;

    @Override
    public String getContent() {
        return choices.get(0).getMessage().getContent();
    }

    @Override
    protected int getPromptTokenCount() {
        return usage.getPromptTokens();
    }

    @Override
    protected int getCompletionTokenCount() {
        return usage.getCompletionTokens();
    }

}
