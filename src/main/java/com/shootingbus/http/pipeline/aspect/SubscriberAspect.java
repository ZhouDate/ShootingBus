package com.shooting-bus.http.pipeline.aspect;

import com.shooting-bus.http.pipeline.annotation.Subscriber;
import com.shooting-bus.http.pipeline.dto.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class SubscriberAspect {
    @Pointcut("@annotation(com.shooting-bus.http.pipeline.annotation.Subscriber)")
    private void SubscriberAspect() {
    }

    @Around("SubscriberAspect() && @annotation(subscriber)")
    public Object advice(ProceedingJoinPoint joinPoint, Subscriber subscriber) throws Throwable {
        Object result = null;
        MessageDTO<Object> msg = null;
        try {
            if (joinPoint.getArgs() == null && joinPoint.getArgs().length < 0) {
                throw new NoSuchMethodException();
            }
            msg = (MessageDTO<Object>) joinPoint.getArgs()[0];
            result = joinPoint.proceed(new Object[]{msg});
            log.info(String.format("subscriber advice begin %s ", msg.toJson()));
        } catch (Exception e) {
            log.error(String.format("subscriber advice error %s ", msg.toJson()), e);
        } finally {
            log.info(String.format("subscriber advice end %s ", msg.toJson()));
        }
        return result;
    }
}
