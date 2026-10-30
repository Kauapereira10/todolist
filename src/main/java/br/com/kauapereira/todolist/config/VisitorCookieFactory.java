package br.com.kauapereira.todolist.config;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class VisitorCookieFactory {

    private final CookieProperties properties;
    public VisitorCookieFactory(CookieProperties properties) {
        this.properties = properties;
    }

    public ResponseCookie create(String visitorId) {
        return ResponseCookie.from(properties.name(), visitorId)
                .secure(properties.secure())
                .httpOnly(properties.httpOnly())
                .sameSite(properties.sameSite())
                .maxAge(properties.maxAge())
                .path("/")
                .build();
    }

}
