package com.shooting-mq.http.pipeline.aspect;

import com.shooting-mq.http.pipeline.annotation.Publisher;
import com.shooting-mq.http.pipeline.dto.MessageDTO;
import com.shooting-mq.http.pipeline.handler.TaskScheduler;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@Aspect
@Order(1)
@Slf4j
public class PublisherAspect {
    @Autowired
    private TaskScheduler taskScheduler;

    @Pointcut("@annotation(com.shooting-mq.http.pipeline.annotation.Publisher)")
    public void PublisherAspect() {
    }

    @Around("PublisherAspect() && @annotation(publisher) ")
    public Object advice(ProceedingJoinPoint joinPoint, Publisher publisher) throws Throwable {
        Object result = joinPoint.proceed();
        var topicInfo = MessageDTO.builder()
                .uuid(UUID.randomUUID().toString())
                .time(new Date())
                .topic(publisher.topic())
                .info(result)
                .build();
        try {
            log.info(String.format("publisher advice begin %s", topicInfo.toJson()));
            //push queue
            taskScheduler.distribution(topicInfo);
        } catch (Exception e) {
            log.error(String.format("publisher advice error %s", topicInfo.toJson()), e);
        } finally {
            log.info(String.format("publisher advice end %s", topicInfo.toJson()));
        }
        return result;
    }
}
