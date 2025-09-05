package com.kuklin.ai_conversation_service.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageStatus {
    NEW, PROCESSING, DONE, ERROR;
}
