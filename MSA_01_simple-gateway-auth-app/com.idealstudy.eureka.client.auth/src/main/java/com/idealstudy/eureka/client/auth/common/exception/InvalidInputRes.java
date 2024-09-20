package com.idealstudy.eureka.client.auth.common.exception;

public record InvalidInputRes(
    String field,
    String message
) {

}