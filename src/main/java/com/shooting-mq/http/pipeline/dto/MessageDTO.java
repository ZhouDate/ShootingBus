package com.shooting-mq.http.pipeline.dto;

import com.dashu.center.config.log.util.LogJsonUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonSerializable;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

@Builder
public class MessageDTO<T> {
    @Getter
    String topic;
    @Getter
    String uuid;
    @Getter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date time;
    @Getter
    T info;

    private String json;

    public String toJson() {
        if (StringUtils.isEmpty(json)) {
            json = LogJsonUtils.writeValueAsString(this);
        }
        return json;
    }
}
