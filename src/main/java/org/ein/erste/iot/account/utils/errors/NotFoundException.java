package org.ein.erste.iot.account.utils.errors;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotFoundException extends BasicException{

    public NotFoundException(String message) {
        super(HttpServletResponse.SC_NOT_FOUND, message);
    }

    public NotFoundException() {
        super(HttpServletResponse.SC_NOT_FOUND,"error.not.found");
    }
}
