package com.kuklin.aiconversationservice.services;

import com.kuklin.aiconversationservice.entities.ChatMessage;
import com.kuklin.aiconversationservice.entities.Conversation;
import com.kuklin.aiconversationservice.entities.Model;
import com.kuklin.aiconversationservice.integrations.UserServiceFeignClient;
import com.kuklin.aiconversationservice.models.AiResponse;
import com.kuklin.aiconversationservice.models.MessageRequestDto;
import com.kuklin.aiconversationservice.models.MessageResponseDto;
import com.kuklin.aiconversationservice.models.enums.MessageStatus;
import com.kuklin.aiconversationservice.repositories.ChatMessageRepository;
import com.kuklin.aiconversationservice.sharedlibrary.BalanceSubtractRequest;
import com.kuklin.aiconversationservice.sharedlibrary.UserDto;
import com.kuklin.aiconversationservice.sharedlibrary.exceptions.ErrorResponseException;
import com.kuklin.aiconversationservice.sharedlibrary.exceptions.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ConversationService conversationService;
    private final ModelService modelService;
    private final OpenAiIntegrationService openAiIntegrationService;
    private final UserServiceFeignClient userServiceFeignClient;
    private static final String SERVICE_CONV = "SERVICE MESSAGE";

    public String sendServiceMessage(MessageRequestDto messageRequestDto) throws ErrorResponseException {
        Conversation conversation = conversationService.
                getNewConversation(messageRequestDto.getUserId(), SERVICE_CONV);
        Model model = modelService.findModelOrThrowError(messageRequestDto.getModel());

        ChatMessage userMessage = ChatMessage
                .newUserMessage(messageRequestDto, conversation, model)
                .setServiceMessage(true)
                .setConversation(conversation);
        chatMessageRepository.save(userMessage);

        try {
            return openAiIntegrationService.fetchResponse(userMessage, null).getContent();
        } catch (Exception e) {
            log.error("AI Connection error!");
            throw new ErrorResponseException(ErrorStatus.AI_CONNECTION_ERROR);
        }
    }

    public MessageResponseDto processUserMessageOrGetNull(MessageRequestDto messageRequestDto) throws ErrorResponseException {
        //Конвертация дто в сущность
        ChatMessage userMessage = makeUserMessage(messageRequestDto);

        if (userMessage == null) throw new NullPointerException();

        //Получение контекста беседы
        List<ChatMessage> chatMessageList =
                chatMessageRepository.findAllByConversation_Id(messageRequestDto.getConversationId());

        UserDto userDto = userServiceFeignClient.getUserById(messageRequestDto.getUserId());
        if (userDto.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ErrorResponseException(ErrorStatus.USER_INSUFFICIENT_FUNDS);
        }

        try {
            //Получение провайдера по параметру приходящего запроса и исполнение запроса
            AiResponse response = openAiIntegrationService.fetchResponse(
                    userMessage, chatMessageList);

            //Конвертация ответа в сущность
            ChatMessage assistantMessage = ChatMessage.newAssistantMessage(
                    response,
                    userMessage
            );

            userMessage.setStatus(MessageStatus.DONE);
            //Вычитание токенов с баланса пользователя, если сообщение не является служебным
            BalanceSubtractRequest request = new BalanceSubtractRequest()
                    .setAmount(assistantMessage.getInputToken().add(assistantMessage.getOutputToken()));

            userServiceFeignClient.subtractBalance(userDto.getId(), request);

            chatMessageRepository.save(userMessage);

            assistantMessage.setStatus(MessageStatus.DONE);
            ChatMessage chatMessage = chatMessageRepository.save(assistantMessage);

            return new MessageResponseDto()
                    .setContent(chatMessage.getContent())
                    .setOutputToken(chatMessage.getOutputToken())
                    .setInputToken(chatMessage.getInputToken());
        } catch (Exception e) {
            log.error("AI connection error: ", e.getMessage());
            setStatusAndErrorDetails(userMessage, MessageStatus.ERROR, e.getMessage());
            throw new ErrorResponseException(ErrorStatus.AI_CONNECTION_ERROR);
        }
    }

    private ChatMessage makeUserMessage(MessageRequestDto messageRequestDto) {
        Conversation conversation = conversationService
                .getByIdOrGetNull(messageRequestDto.getConversationId());

        Model model = modelService.findModelOrThrowError(messageRequestDto.getModel());

        if (conversation == null) return null;

        if (conversation.getName() == null) {
            conversation = conversationService.setNameForConversation(conversation, messageRequestDto.getContent());
        }

        ChatMessage userMessage = ChatMessage.newUserMessage(messageRequestDto, conversation, model);

        return chatMessageRepository.save(userMessage).setConversation(conversation);
    }

    private void setStatusAndErrorDetails(ChatMessage userMessage, MessageStatus messageStatus, String errorStatus) {
        userMessage.setStatus(messageStatus);
        userMessage.setErrorDetails(errorStatus);
        chatMessageRepository.save(userMessage);
    }
}
