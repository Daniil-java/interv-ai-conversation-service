package com.kuklin.ai_conversation_service.integrations;

import com.kuklin.ai_conversation_service.configurations.FeignClientConfig;
import com.kuklin.ai_conversation_service.sharedlibrary.BalanceSubtractRequest;
import com.kuklin.ai_conversation_service.sharedlibrary.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        value = "user-service-feign-client",
        url = "${integrations.user-service.url}",
        configuration = FeignClientConfig.class
)
public interface UserServiceFeignClient {
    @RequestMapping(
            method = RequestMethod.PATCH,
            value = "/api/v1/users/{userId}/balance",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    UserDto subtractBalance(
            @PathVariable("userId") Long userId,
            @RequestBody BalanceSubtractRequest subtractTokens
    );

    @GetMapping("/api/v1/users/{userId}")
    UserDto getUserById(@PathVariable Long userId);

}
