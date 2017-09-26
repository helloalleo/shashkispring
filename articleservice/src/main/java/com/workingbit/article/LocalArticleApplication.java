package com.workingbit.article;

import com.workingbit.article.config.LambdaConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * Created by Aleksey Popryaduhin on 13:04 09/08/2017.
 */
@Profile("dev")
@SpringBootApplication
public class LocalArticleApplication {

  public static void main(String[] args) {
    SpringApplication application = new SpringApplication(LambdaConfiguration.class);
//    application.setWebEnvironment(false);
//    application.setBannerMode(Banner.Mode.OFF);
    AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
    appContext.setParent(application.run());
    appContext.refresh();
    appContext.start();
//    while (true) {
//      try {
//        TimeUnit.SECONDS.sleep(5);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//    }

//    SpringApplication.run(WikiApplication.class, args);
  }
}
