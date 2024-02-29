package org.ein.erste.iot.account.utils.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicException extends RuntimeException{

    private int code;
    public BasicException(int code, String message) {
        super(message);
        this.code = code;
    }
}
