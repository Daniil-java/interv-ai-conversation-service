package com.kuklin.ai_conversation_service.models;

import lombok.Data;

@Data
public class SpeechRequest {
    String input;
    String model;
    String voice;
}
