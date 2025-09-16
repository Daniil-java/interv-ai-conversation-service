package com.kuklin.aiconversationservice.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProviderVariant {
    OPENAI(ChatRole.ASSISTANT), GEMINI(ChatRole.MODEL);

    private ChatRole assistant;
}
