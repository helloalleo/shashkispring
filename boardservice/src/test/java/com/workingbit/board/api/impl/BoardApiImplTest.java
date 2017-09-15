package com.workingbit.board.api.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Aleksey Popryaduhin on 09:26 15/09/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BoardApiImplTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void checkHealth_returnsTenants() throws Exception {
    mockMvc.perform(get("/board/1"))
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.statusCode").value(equalTo(200)));
//        .andExpect(jsonPath("$.tenants").value(Matchers.containsInAnyOrder(MAIN_TEST_TENANT_NAME, SECOND_TEST_TENANT_NAME)));
  }

}