package com.kuklin.aiconversationservice.controllers;

import com.kuklin.aiconversationservice.services.ChatMessageService;
import com.kuklin.sharedlibrary.MessageRequestDto;
import com.kuklin.sharedlibrary.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    //ServiceMessage - не затрагивает пользовательский баланс
    @PostMapping("/service")
    public String sendServiceMessage(
            @RequestBody @Validated MessageRequestDto messageRequestDto) {
        return chatMessageService.sendServiceMessage(messageRequestDto);
    }

    @PostMapping
    public MessageResponseDto sendUserMessage(@RequestBody @Validated MessageRequestDto messageRequestDto) {
        return chatMessageService.processUserMessageOrGetNull(messageRequestDto);
    }


}
