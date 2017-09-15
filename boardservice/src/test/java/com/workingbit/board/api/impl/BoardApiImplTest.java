package com.workingbit.board.api.impl;

import io.github.robwin.swagger.test.SwaggerAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

/**
 * Created by Aleksey Popryaduhin on 09:26 15/09/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BoardApiImplTest {

  @LocalServerPort
  private int randomPort;

  @Test
  public void validateThatImplementationSatisfiesConsumerSpecification() {
    File designFirstSwagger = new File(BoardApiImplTest.class.getResource("/boardservice-swagger.yaml").getFile());
    SwaggerAssertions.assertThat("http://localhost:" + randomPort + "/v1/v2/api-docs")
        .satisfiesContract(designFirstSwagger.getAbsolutePath());
  }

}