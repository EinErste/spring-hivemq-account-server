package org.ein.erste.iot.account.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response <T> {
    private T res;
    private int code;
    private String message;

    public static <T> Response of(T res) {
        return new Response(res, 200, null);
    }

    public static <T> Response of(int code, String message) {
        return new Response(null, code, message);
    }
    public static class Error {
        private String message;
    }
}
