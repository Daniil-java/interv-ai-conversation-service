package com.kuklin.aiconversationservice.services;

import com.kuklin.aiconversationservice.entities.ChatMessage;
import com.kuklin.aiconversationservice.entities.Conversation;
import com.kuklin.aiconversationservice.entities.Model;
import com.kuklin.aiconversationservice.integrations.UserServiceFeignClient;
import com.kuklin.aiconversationservice.models.AiResponse;
import com.kuklin.aiconversationservice.models.SpeechRequest;
import com.kuklin.aiconversationservice.models.TranscriptionResponse;
import com.kuklin.aiconversationservice.models.enums.MessageStatus;
import com.kuklin.aiconversationservice.repositories.ChatMessageRepository;
import com.kuklin.sharedlibrary.BalanceSubtractRequest;
import com.kuklin.sharedlibrary.MessageRequestDto;
import com.kuklin.sharedlibrary.MessageResponseDto;
import com.kuklin.sharedlibrary.UserDto;
import com.kuklin.sharedlibrary.exceptions.ErrorResponseException;
import com.kuklin.sharedlibrary.exceptions.ErrorStatus;
import com.kuklin.sharedlibrary.exceptions.ServiceOrigin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
            return openAiIntegrationService.fetchResponse(userMessage).getContent();
        } catch (Exception e) {
            log.error("AI Connection error!");
            throw new ErrorResponseException(ErrorStatus.AI_CONNECTION_ERROR, ServiceOrigin.AI_CONVERSATION_SERVICE);
        }
    }

    @Transactional
    public MessageResponseDto processUserMessageOrGetNull(MessageRequestDto messageRequestDto) throws ErrorResponseException {
        //Проверка, что баланс больше нуля
        validateBalanceOrThrow(messageRequestDto.getUserId());

        //Получение беседы и отправка ошибки, в случае не существования беседы
        Conversation conversation = getConversationOrThrow(messageRequestDto);

        //Получение контекста беседы
        List<ChatMessage> chatMessageList =
                chatMessageRepository.findAllByConversation_Id(messageRequestDto.getConversationId());

        //Получение сущности модели из БД
        Model model = modelService.findModelOrThrowError(messageRequestDto.getModel());

        ChatMessage userMessage = ChatMessage.newUserMessage(messageRequestDto, conversation, model);

        //Запрос в ИИ
        AiResponse response = fetchResponseOrThrow(chatMessageList, model, userMessage.getTemperature());

        //Конвертация ответа в сущность
        ChatMessage assistantMessage = ChatMessage
                .newAssistantMessage(response, userMessage);

        //Вычитание токенов с баланса пользователя, если сообщение не является служебным
        subtractBalance(assistantMessage, messageRequestDto.getUserId());

        chatMessageRepository.save(userMessage.setStatus(MessageStatus.DONE));
        ChatMessage chatMessage = chatMessageRepository.save(assistantMessage.setStatus(MessageStatus.DONE));

        return new MessageResponseDto()
                .setContent(chatMessage.getContent())
                .setOutputToken(chatMessage.getOutputToken())
                .setInputToken(chatMessage.getInputToken());

    }

    private void subtractBalance(ChatMessage assistantMessage, Long userId) {
        BalanceSubtractRequest request = new BalanceSubtractRequest()
                .setAmount(assistantMessage.getInputToken().add(assistantMessage.getOutputToken()));
        userServiceFeignClient.subtractBalance(userId, request);
    }

    private Conversation getConversationOrThrow(MessageRequestDto messageRequestDto) {
        Conversation conversation = conversationService
                .getByIdOrGetNull(messageRequestDto.getConversationId());
        if (conversation == null) throw new ErrorResponseException(
                ErrorStatus.CONVERSATION_NOT_FOUND, ServiceOrigin.AI_CONVERSATION_SERVICE);
        //Назначение имени для беседы
        if (conversation.getName() == null) {
            conversation = conversationService.setNameForConversation(conversation, messageRequestDto.getContent());
        }
        return conversation;
    }

    //Проверка баланса пользователя
    private void validateBalanceOrThrow(Long userId) {
        UserDto userDto = userServiceFeignClient.getUserById(userId);
        if (userDto.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ErrorResponseException(ErrorStatus.USER_INSUFFICIENT_FUNDS,
                    ServiceOrigin.AI_CONVERSATION_SERVICE);
        }
    }

    private AiResponse fetchResponseOrThrow(List<ChatMessage> chatMessageList, Model model, Float temp) {
        try {
            //Получение провайдера по параметру приходящего запроса и исполнение запроса
            return openAiIntegrationService
                    .fetchResponse(chatMessageList, model, temp);
        } catch (Exception e) {
            log.error("AI connection error: ", e.getMessage());
            throw new ErrorResponseException(ErrorStatus.AI_CONNECTION_ERROR,
                    ServiceOrigin.AI_CONVERSATION_SERVICE);
        }
    }
}
