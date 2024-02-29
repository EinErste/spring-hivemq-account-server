package org.ein.erste.iot.account.settings;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@SuppressWarnings("NullableProblems")
public class SmartLocaleResolver extends AcceptHeaderLocaleResolver {
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String lang;
        if (request.getHeader("Accept-Language") == null || "".equals(request.getHeader("Accept-Language")))
            lang = "en";
        else
            lang = request.getHeader("Accept-Language");
        try {
            if (lang == null)
                return Locale.getDefault();
            if (lang.contains("_"))
                lang = lang.split("_")[0];
            List<Locale.LanguageRange> list = Locale.LanguageRange.parse(lang);
            Locale locale = Locale.lookup(list, WebMvcConfiguration.LOCALES);
            if (locale == null)
                return Locale.getDefault();
            return locale;
        } catch (IllegalArgumentException ex) {
            return Locale.getDefault();
        }
    }
}
