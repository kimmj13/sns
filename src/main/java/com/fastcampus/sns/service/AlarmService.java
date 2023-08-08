package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AlarmService {

    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final static String ALARM_NAME = "alarm";
    private final EmitterRepository emitterRepository;

    public void send(Integer alarmId, Integer userId) {

        emitterRepository.get(userId).ifPresentOrElse(sseEmitter -> {
            try {
                log.info("send");
                sseEmitter.send(SseEmitter.event()
                        .id(alarmId.toString())
                        .name(ALARM_NAME)
                        .data("new alarm"));
            } catch (IOException e) {
                emitterRepository.delete(userId);
                throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
            }
        }, () -> log.info("No emitter founded"));

    }

    //subscribe - user가 alarm connect
    public SseEmitter connectAlarm(Integer userId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, sseEmitter);

        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));

        //name - event name
        try {
            log.info("send");
            sseEmitter.send(SseEmitter.event()
                    .id("id")
                    .name(ALARM_NAME)
                    .data("connect completed"));
        } catch (IOException e) {
            throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;
    }
}
