package com.kuklin.aiconversationservice.services;

import com.kuklin.aiconversationservice.entities.Model;
import com.kuklin.aiconversationservice.repositories.ModelRepository;
import com.kuklin.sharedlibrary.ChatModel;
import com.kuklin.sharedlibrary.exceptions.ErrorResponseException;
import com.kuklin.sharedlibrary.exceptions.ErrorStatus;
import com.kuklin.sharedlibrary.exceptions.ServiceOrigin;
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
                .orElseThrow(() -> new ErrorResponseException(
                        ErrorStatus.PROVIDER_NOT_FOUND,
                        ServiceOrigin.AI_CONVERSATION_SERVICE)
                );
    }
}
