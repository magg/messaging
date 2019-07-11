package com.example.messaging.http;

import com.example.messaging.domain.StatusCode;
import com.sun.net.httpserver.Headers;

import java.util.Objects;

public final class ResponseEntity<T> {

    private final T body;
    private final Headers headers;
    private final StatusCode statusCode;


    public T getBody() {
        return body;
    }


    public Headers getHeaders() {
        return headers;
    }


    public StatusCode getStatusCode() {
        return statusCode;
    }

    public ResponseEntity(T body, Headers headers, StatusCode statusCode) {
        this.body = body;
        this.headers = headers;
        this.statusCode = statusCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResponseEntity)) return false;
        ResponseEntity<?> that = (ResponseEntity<?>) o;
        return getBody().equals(that.getBody()) &&
                getHeaders().equals(that.getHeaders()) &&
                getStatusCode() == that.getStatusCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBody(), getHeaders(), getStatusCode());
    }

    @Override
    public String toString() {
        return "ResponseEntity{" +
                "body=" + body +
                ", headers=" + headers +
                ", statusCode=" + statusCode +
                '}';
    }
}
