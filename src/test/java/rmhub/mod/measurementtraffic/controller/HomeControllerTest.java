package rmhub.mod.measurementtraffic.controller;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest(HomeController.class)
public class HomeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testRedirectHomeToSwagger() throws Exception {
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/"))
        .andExpect(MockMvcResultMatchers.redirectedUrl("/swagger-ui.html"))
        .andReturn()
        .getResponse();
    Assertions.assertThat(response.getStatus()).isEqualTo(302);
  }
}
