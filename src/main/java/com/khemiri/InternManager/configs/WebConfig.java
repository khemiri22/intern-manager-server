package com.khemiri.InternManager.configs;
import com.khemiri.InternManager.interceptors.IdentityVerificationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${upload.path.intern}")
    private String uploadInternPath;
    @Autowired
    private IdentityVerificationInterceptor identityVerificationInterceptor;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/uploads/**")
                        .addResourceLocations("file:"+uploadInternPath);
            }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(identityVerificationInterceptor)
                .addPathPatterns("/api/uploads/**");
    }
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api", c -> true);
    }
}





