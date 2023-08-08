package com.fastcampus.sns.controller;

import com.fastcampus.sns.controller.request.UserJoinRequest;
import com.fastcampus.sns.controller.request.UserLoginRequest;
import com.fastcampus.sns.controller.request.UserLoginResponse;
import com.fastcampus.sns.controller.response.AlarmResponse;
import com.fastcampus.sns.controller.response.Response;
import com.fastcampus.sns.controller.response.UserJoinResponse;
import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.model.User;
import com.fastcampus.sns.service.AlarmService;
import com.fastcampus.sns.service.UserService;
import com.fastcampus.sns.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final AlarmService alarmService;


    // todo : implement
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {

        User user = userService.join(request.getName(), request.getPassword());
        return Response.success(UserJoinResponse.fromUser(user));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    @GetMapping("/alarm")
    public Response<Page<AlarmResponse>> alarm(Pageable pageable, Authentication authentication) {
        return Response.success(userService.alarmList(authentication.getName(), pageable).map(AlarmResponse::fromAlarm));
    }


    //js - eventSource 는 헤더지원 X --> param로 전달
    @GetMapping("/alarm/subscribe")
    public SseEmitter subscribe(Authentication authentication) {
        log.info("테스트중..");
        User user = Optional.ofNullable(ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class)).orElseThrow(() -> new SnsApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "Casting to User class failed.."));

        return alarmService.connectAlarm(user.getId());
    }
}
