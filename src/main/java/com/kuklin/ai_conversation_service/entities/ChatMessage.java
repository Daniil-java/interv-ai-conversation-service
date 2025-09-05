package com.kuklin.ai_conversation_service.entities;

import com.kuklin.ai_conversation_service.models.AiResponse;
import com.kuklin.ai_conversation_service.models.MessageRequestDto;
import com.kuklin.ai_conversation_service.models.enums.ChatRole;
import com.kuklin.ai_conversation_service.models.enums.MessageStatus;
import com.kuklin.ai_conversation_service.models.enums.MessageType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChatRole role;
    private String content;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;

    private Float temperature;
    private MessageType messageType;
    private String errorDetails;
    private BigDecimal inputToken;
    private BigDecimal outputToken;
    private BigDecimal nativeTokensSum;
    private BigDecimal generalTokensSum;
    private MessageStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;
    private boolean isServiceMessage;
    private OffsetDateTime created;

    public static ChatMessage newUserMessage(
            MessageRequestDto messageRequestDto, Conversation conversation, Model model) {
        return new ChatMessage()
                .setContent(messageRequestDto.getContent())
                .setMessageType(MessageType.TEXT)
                .setRole(ChatRole.USER)
                .setStatus(MessageStatus.NEW)
                .setModel(model)
                .setTemperature(messageRequestDto.getTemperature())
                .setConversation(conversation)
                .setServiceMessage(false);
    }

    public static ChatMessage newAssistantMessage(AiResponse response,
                                                  ChatMessage userMessage) {
        BigDecimal nativeTokensSum = response.getCompletionTokens().add(response.getPromptTokens());
        BigDecimal generalTokensSum =
                response.getPromptTokens().multiply(userMessage.getModel().getInputMultiplier())
                        .add(response.getCompletionTokens().multiply(userMessage.getModel().getOutputMultiplier()));

        return new ChatMessage()
                .setContent(response.getContent())
                .setMessageType(MessageType.TEXT)
                .setRole(ChatRole.ASSISTANT)
                .setInputToken(response.getPromptTokens())
                .setOutputToken(response.getCompletionTokens())
                .setConversation(userMessage.getConversation())
                .setNativeTokensSum(nativeTokensSum)
                .setGeneralTokensSum(generalTokensSum)
                .setTemperature(0f)
                .setModel(userMessage.getModel())
                .setServiceMessage(false)
                ;
    }
}
