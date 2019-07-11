package com.example.messaging.domain;

public class ErrorResponse {

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    private ErrorResponse(Builder builder) {
        code = builder.code;
        message = builder.message;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(ErrorResponse copy) {
        Builder builder = new Builder();
        builder.code = copy.getCode();
        builder.message = copy.getMessage();
        return builder;
    }

    public static final class Builder {
        private int code;
        private String message;

        private Builder() {
        }

        public Builder code(int val) {
            code = val;
            return this;
        }

        public Builder message(String val) {
            message = val;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(this);
        }
    }
}