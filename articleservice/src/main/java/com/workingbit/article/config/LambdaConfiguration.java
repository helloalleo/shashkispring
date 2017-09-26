package com.workingbit.article.config;

import com.rits.cloning.Cloner;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by Aleksey Popryaduhin on 13:04 09/08/2017.
 */
@EnableWebMvc
@Configuration
@ComponentScan(value = "com.workingbit.article", lazyInit = true)
public class LambdaConfiguration {

  @Bean
  public EmbeddedServletContainerCustomizer embeddedServletContainerCustomizer(AppProperties appProperties) {
    return container -> container.setContextPath(appProperties.getContextPath());
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public Cloner getCloner() {
    return new Cloner();
  }
}
