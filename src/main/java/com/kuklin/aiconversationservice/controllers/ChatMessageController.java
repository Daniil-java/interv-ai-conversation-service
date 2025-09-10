package com.kuklin.aiconversationservice.controllers;

import com.kuklin.aiconversationservice.models.MessageRequestDto;
import com.kuklin.aiconversationservice.models.MessageResponseDto;
import com.kuklin.aiconversationservice.services.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    //ServiceMessage - не затрагивает пользовательский баланс
    @PostMapping("/messages/service")
    public String sendServiceMessage(
            @RequestBody @Validated MessageRequestDto messageRequestDto) {
        return chatMessageService.sendServiceMessage(messageRequestDto);
    }

    @PostMapping("/messages/")
    public MessageResponseDto sendUserMessage(@RequestBody @Validated MessageRequestDto messageRequestDto) {
        return chatMessageService.processUserMessageOrGetNull(messageRequestDto);
    }


}
