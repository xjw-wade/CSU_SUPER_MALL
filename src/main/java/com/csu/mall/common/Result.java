package com.csu.mall.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","fieldHandler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {
    private int status;
    private String message;
    private T data;

    @JsonIgnore
    public boolean isSuccess() {
        return this.status == ResultCode.SUCCESS.getCode();
    }

    public static <T> Result<T> createForSuccess() {
        return new Result<T>(ResultCode.SUCCESS.getCode());
    }

    public static <T> Result<T> createForSuccessMessage(String message) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message);
    }

    public static <T> Result<T> createForSuccess(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), data);
    }

    public static <T> Result<T> createForSuccess(String message, T data) {
        return new Result<T>(ResultCode.SUCCESS.getCode(), message, data);
    }

    public static <T> Result<T> createForError() {
        return new Result<T>(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMsg());
    }

    public static <T> Result<T> createForError(String message) {
        return new Result<T>(ResultCode.ERROR.getCode(), message);
    }

    public static <T> Result<T> createForError(int code, String message) {
        return new Result<T>(code, message);
    }

    private Result(int status) {
        this.status = status;
    }
    private Result(int status, String message) {
        this.status = status;
        this.message = message;
    }
    private Result(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
    private Result(int status, T data) {
        this.status = status;
        this.data = data;
    }
}
