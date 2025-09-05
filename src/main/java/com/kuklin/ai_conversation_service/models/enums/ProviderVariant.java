package com.kuklin.ai_conversation_service.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProviderVariant {
    OPENAI(ChatRole.ASSISTANT), GEMINI(ChatRole.MODEL);

    private ChatRole assistant;
}
