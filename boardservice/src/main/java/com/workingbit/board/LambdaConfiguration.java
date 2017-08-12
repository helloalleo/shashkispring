package com.workingbit.board;

import com.workingbit.board.common.AppConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Aleksey Popryaduhin on 13:04 09/08/2017.
 */
@EnableWebMvc
@Configuration
@ComponentScan(value = "com.workingbit.board", lazyInit = true)
public class LambdaConfiguration {

  @Bean
  public WebMvcConfigurer corsConfigurer(AppConstants appConstants) {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(appConstants.getClientUrl())
            .allowedMethods("GET", "POST", "PUT", "OPTIONS")
            .allowCredentials(false)
            .maxAge(3600);
      }
    };
  }
}
