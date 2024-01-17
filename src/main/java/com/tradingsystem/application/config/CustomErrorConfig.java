package com.tradingsystem.application.config;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Configuration
public class CustomErrorConfig {

    @Bean
    public DefaultErrorAttributes errorAttributes() {
        return new CustomErrorAttributes();
    }

    private static class CustomErrorAttributes extends DefaultErrorAttributes {

        @Override
        public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
            Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

            // Customize the error response as needed
            errorAttributes.remove("trace"); // Remove the stack trace

            return errorAttributes;
        }
    }
}
