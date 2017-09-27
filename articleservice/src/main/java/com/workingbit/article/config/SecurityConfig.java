package com.workingbit.article.config;

import com.workingbit.share.common.CorsFilterAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

@EnableWebSecurity
@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final AppProperties appProperties;

  @Autowired
  public SecurityConfig(AppProperties appProperties) {
    this.appProperties = appProperties;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    System.out.println("SPRING SECURITY CONFIGURE");
    http
//        .addFilterBefore(corsFilter(), SessionManagementFilter.class) //adds your custom CorsFilter
        .addFilterBefore(new HelloFilter(), SessionManagementFilter.class)
        .authorizeRequests()
        .antMatchers("/**")
        .permitAll()
        .and()
        .csrf()
        .disable();
  }

  private CorsFilter corsFilter() {
    return new CorsFilterAdapter(
        appProperties.getClientUrls(),
        appProperties.getCorsHeaders(),
        appProperties.getCorsMethods())
        .corsFilter();
  }

  public class HelloFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
      System.out.println("INIT HELLO");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      System.out.println("*****************************");
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      Enumeration<String> headerNames = httpRequest.getHeaderNames();

      if (headerNames != null) {
        while (headerNames.hasMoreElements()) {
          String name = headerNames.nextElement();
          System.out.println("Header: " + name + " " + httpRequest.getHeader(name));
        }
      }

      String origin = httpRequest.getHeader("Origin");
      System.out.println(origin);
      UriComponentsBuilder urlBuilder;
        // Build more efficiently if we can: we only need scheme, host, port for origin comparison
        urlBuilder = UriComponentsBuilder.newInstance().
            scheme(httpRequest.getScheme()).
            host(httpRequest.getServerName()).
            port(httpRequest.getServerPort());
      UriComponents actualUrl = urlBuilder.build();
      UriComponents originUrl = UriComponentsBuilder.fromOriginHeader(origin).build();

      System.out.println("ActualUrl: " + actualUrl);
      System.out.println("OriginUrl: " + originUrl);
      System.out.println("*****************************");
      //doFilter
      chain.doFilter(httpRequest, response);
    }

    @Override
    public void destroy() {
      System.out.println("DESTROY HELLO");
    }
  }
}