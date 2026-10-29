package br.com.kauapereira.todolist.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@AllArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final OwnerInterceptor ownerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ownerInterceptor)
                .addPathPatterns("/api/**");
    }
}
