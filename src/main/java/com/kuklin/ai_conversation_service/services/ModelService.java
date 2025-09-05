package com.kuklin.ai_conversation_service.services;

import com.kuklin.ai_conversation_service.entities.Model;
import com.kuklin.ai_conversation_service.models.enums.ChatModel;
import com.kuklin.ai_conversation_service.repositories.ModelRepository;
import com.kuklin.ai_conversation_service.sharedlibrary.exceptions.ErrorResponseException;
import com.kuklin.ai_conversation_service.sharedlibrary.exceptions.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelService {
    private final ModelRepository modelRepository;

    public Model findModelOrThrowError(ChatModel chatModel) {
        return modelRepository.findByModelName(chatModel.getModel())
                .orElseThrow(() -> new ErrorResponseException(ErrorStatus.PROVIDER_NOT_FOUND));
    }
}
