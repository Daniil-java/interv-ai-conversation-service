package com.kuklin.aiconversationservice.services;

import com.kuklin.aiconversationservice.entities.Model;
import com.kuklin.aiconversationservice.models.enums.ChatModel;
import com.kuklin.aiconversationservice.repositories.ModelRepository;
import com.kuklin.aiconversationservice.sharedlibrary.exceptions.ErrorResponseException;
import com.kuklin.aiconversationservice.sharedlibrary.exceptions.ErrorStatus;
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
