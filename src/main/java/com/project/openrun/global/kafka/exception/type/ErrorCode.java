package com.project.openrun.global.kafka.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    NOT_FOUND_DATA(HttpStatus.BAD_REQUEST,"데이터가 존재하지 않습니다. 사유 : {0}"),
    DUPLICATE_DATA(HttpStatus.BAD_REQUEST, "중복된 데이터 입니다. 사유 : {0}"),
    NOT_AUTHORIZATION(HttpStatus.BAD_REQUEST,"권한이 없습니다. 사유 : {0}"),
    INVALID_CONDITION(HttpStatus.BAD_REQUEST, "조건이 맞지 않습니다. 사유 : {0}"),

    WRONG_INPUT(HttpStatus.BAD_REQUEST, "잘못된 요청 입니다.");

    private final HttpStatus status;
    private final String messageTemplate;

    public String formatMessage(Object args) {
        return MessageFormat.format(this.messageTemplate, args);
    }

}
