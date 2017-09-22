package com.workingbit.board.api.impl;

import com.workingbit.board.service.BaseServiceTest;
import com.workingbit.board.service.BoardBoxService;
import com.workingbit.share.domain.impl.BoardBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Aleksey Popryaduhin on 09:26 15/09/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BoardApiImplTest extends BaseServiceTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private BoardBoxService boardBoxService;

  private BoardBox board;

  @Before
  public void setUp() {
    this.board = boardBoxService.createBoard(getCreateBoardRequest());
  }

  public void tearDown() {
    boardBoxService.delete(board.getId());
  }

  @Test
  public void checkHealth_returnsTenants() throws Exception {
    mockMvc.perform(get("/board/" + board.getId()))
        .andExpect(content().contentType(APPLICATION_JSON_UTF8))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(equalTo(board.getId())));
//        .andExpect(jsonPath("$.tenants").value(Matchers.containsInAnyOrder(MAIN_TEST_TENANT_NAME, SECOND_TEST_TENANT_NAME)));
  }

}