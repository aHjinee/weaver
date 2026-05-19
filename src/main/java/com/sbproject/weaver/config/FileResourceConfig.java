package com.sbproject.weaver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class FileResourceConfig implements WebMvcConfigurer {

    private final FileConfig fileConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = fileConfig.getUploadDir()
                .toAbsolutePath()
                .toUri()
                .toString();

        registry.addResourceHandler("/files/**")
                .addResourceLocations(uploadPath);
    }
}
