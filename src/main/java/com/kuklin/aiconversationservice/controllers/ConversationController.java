package com.kuklin.aiconversationservice.controllers;

import com.kuklin.aiconversationservice.models.ConversationDto;
import com.kuklin.aiconversationservice.services.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;

    @PostMapping
    public ConversationDto postNewConversationDto(@RequestBody ConversationDto conversationDto) {
        return conversationService.getNewConversationDto(conversationDto);
    }

    @GetMapping("/{id}")
    public ConversationDto getConversationDtoByIdOrGetNull(@PathVariable Long id) {
        return conversationService.getConversationDtoByIdOrGetNull(id);
    }
}
