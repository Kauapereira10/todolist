package br.com.kauapereira.todolist.config;

import br.com.kauapereira.todolist.model.Owner;
import br.com.kauapereira.todolist.repository.OwnerRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class OwnerInterceptor implements HandlerInterceptor {

    public static final String OWNER_ATTRIBUTE = "owner";

    private final OwnerRepository ownerRepository;
    private final VisitorCookieFactory cookieFactory;
    private final CookieProperties cookieProperties;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        Optional<Owner> existing = readTokenFromCookie(request)
                .flatMap(ownerRepository::findByToken);

        Owner owner;
        if (existing.isPresent()) {
            owner = existing.get();
        } else {
            owner = ownerRepository.save(Owner.create());
            addOwnerCookie(response, owner);
        }

        request.setAttribute(OWNER_ATTRIBUTE, owner);
        return true;
    }

    private Optional<UUID> readTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookieProperties.name().equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .flatMap(this::parseUuid);
    }

    private Optional<UUID> parseUuid(String text) {
        try {
            return Optional.of(UUID.fromString(text));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private void addOwnerCookie(HttpServletResponse response, Owner owner) {
        ResponseCookie cookie = cookieFactory.create(owner.getToken().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}