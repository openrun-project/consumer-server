package com.project.openrun.global.kafka.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExceptionResponseDto {

    private final String status;
    private final String message;

}