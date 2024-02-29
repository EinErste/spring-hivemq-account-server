package org.ein.erste.iot.account.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Random;

public class Utils {
    public static JsonMapper mapper = createJsonMapper();

    public static JsonMapper createJsonMapper() {
        var mapper = new JsonMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    public static void writeObjectToResponse(Object object, HttpServletResponse response) throws IOException {
        Assert.notNull(object, "object can not be null");

        response.getWriter().write(new ObjectMapper().writeValueAsString(object));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public static String getRandomString(int length) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder res = new StringBuilder();
        Random rnd = new Random();
        while (res.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            res.append(SALTCHARS.charAt(index));
        }
        return res.toString();
    }


}
