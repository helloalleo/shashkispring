package com.workingbit.share.common;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Created by Aleksey Popryaduhin on 19:04 26/09/2017.
 */
public class CorsFilterAdapter {

  private final String clientUrl;

  public CorsFilterAdapter(String clientUrl) {
    this.clientUrl = clientUrl;
  }

  public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(false);
    config.addAllowedOrigin(clientUrl);
    String[] headers = new String[] {"Content-Type", "Access-Control-Allow-Origin", "Access-Control-Allow-Headers", "Authorization", "X-Requested-With", "requestId", "Correlation-Id"};
    for (String header: headers) {
      config.addAllowedHeader(header);
    }
    String [] methods = new String[] {"GET", "POST", "PUT", "OPTIONS", "HEADER", "DELETE"};
    for (String method : methods) {
      config.addAllowedMethod(method);
    }
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }
}
