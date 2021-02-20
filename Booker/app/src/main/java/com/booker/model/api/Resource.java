package com.booker.model.api;

import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class Resource<T> {

    @NonNull
    private Status mStatus;

    @Nullable
    private T mData;

    @Nullable
    private String mMessage;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        mStatus = status;
        mData = data;
        mMessage = message;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg);
    }

    public enum Status {SUCCESS, ERROR, LOADING}

}

