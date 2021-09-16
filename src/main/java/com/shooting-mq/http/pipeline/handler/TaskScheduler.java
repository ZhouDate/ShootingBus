package com.shooting-mq.http.pipeline.handler;

import com.shooting-mq.http.pipeline.annotation.Subscriber;
import com.shooting-mq.http.pipeline.dto.MessageDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TaskScheduler implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static Map<String, Map<String, String>> topicSubscribersMap = new HashMap<>();

    @SneakyThrows
    public void distribution(MessageDTO<Object> topicInfo) {
        if (topicInfo == null) {
            return;
        }
        var map = getSubscribers(topicInfo.getTopic());
        if (map == null) {
            return;
        }
        for (var key : map.keySet()) {
            try {
                log.info(String.format("taskScheduler distribution begin %s beanName %s ", topicInfo.toJson(), key));
                var service = applicationContext.getBean(key);
                var method = service.getClass().getMethod(map.get(key), topicInfo.getClass());
                method.invoke(service, topicInfo);
            } catch (Exception ex) {
                log.error(String.format("taskScheduler distribution error %s beanName %s ", topicInfo.toJson(), key), ex);
            } finally {
                log.info(String.format("taskScheduler distribution end %s beanName %s ", topicInfo.toJson(), key));
            }
        }
    }

    private Map<String, String> putTopicSubscribersMap(String topic, String beanName, String methodName) {
        Map<String, String> serviceMap = null;
        if (topicSubscribersMap.containsKey(topic)) {
            serviceMap = topicSubscribersMap.get(topic);
        } else {
            serviceMap = new HashMap<>();
            topicSubscribersMap.put(topic, serviceMap);
        }
        serviceMap.put(beanName, methodName);
        return serviceMap;
    }

    @SneakyThrows
    private Map<String, String> getSubscribers(String topic) {
        if (topicSubscribersMap.containsKey(topic)) {
            return topicSubscribersMap.get(topic);
        }
        Map<String, String> map = null;
        var beanMap = applicationContext.getBeansWithAnnotation(Service.class);
        if (beanMap == null) {
            return null;
        }
        for (var beanName : beanMap.keySet()) {
            var service = beanMap.get(beanName);
            for (var method : ClassUtils.getUserClass(service).getMethods()) {
                if (method == null) {
                    continue;
                }
                if (!method.isAnnotationPresent(Subscriber.class)) {
                    continue;
                }
                map = putTopicSubscribersMap(topic, beanName, method.getName());
            }
        }
        return map;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
