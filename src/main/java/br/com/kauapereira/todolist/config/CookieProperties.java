package br.com.kauapereira.todolist.config;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.cookie")
public record CookieProperties(

        @NotBlank
        String name,

        @DefaultValue("true")
        boolean secure,

        @DefaultValue("true")
        boolean httpOnly,

        @NotBlank
        @Pattern(regexp = "Strict|Lax|None", message = "sameSite must be Strict, Lax or None")
        String sameSite,

        @NotNull
        Duration maxAge
) {

    @AssertTrue(message = "sameSite=None requires secure=true")
    public boolean isSameSiteConsistentWithSecure() {
        return !"None".equals(sameSite) || secure;
    }
}
