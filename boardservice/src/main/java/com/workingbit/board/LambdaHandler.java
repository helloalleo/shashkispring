package com.workingbit.board;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.workingbit.board.config.LambdaConfiguration;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class LambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {
    private static SpringLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    private static SpringLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> getHandler() {
        if (null == handler) {
            try {
                SpringApplication application = new SpringApplication(LambdaConfiguration.class);
                application.setWebEnvironment(true);
                application.setBannerMode(Banner.Mode.OFF);
                ConfigurableWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
                appContext.setParent(application.run());

                handler = SpringLambdaContainerHandler.getAwsProxyHandler(appContext);
                handler.activateSpringProfiles("production");
            } catch (ContainerInitializationException e) {
                e.printStackTrace();
            }
        }
        return handler;
    }

    public AwsProxyResponse handleRequest(AwsProxyRequest awsProxyRequest, Context context) {
        return getHandler().proxy(awsProxyRequest, context);
    }
}