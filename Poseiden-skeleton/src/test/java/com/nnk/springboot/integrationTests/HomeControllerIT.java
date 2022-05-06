package com.nnk.springboot.integrationTests;

import com.nnk.springboot.integrationTests.util.Client;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/schema.sql", "/data.sql"}) //RELOAD database before each test
public class HomeControllerIT extends TestCase {

        @Autowired
        private MockMvc mvc;
        @Autowired
        private WebApplicationContext context;
        @Before
        public void setup() {
            mvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .apply(springSecurity())
                    .build();
        }

    @Test
    public void testhome() throws Exception {
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("HOME PAGE");
    }

    @Test
    public void adminHomeAuthorizedForAdmin() throws Exception {

            this.mvc.perform(MockMvcRequestBuilders.get("/admin/home")
                        .with(Client.admin())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/bidList/list"));
    }

    @Test
    public void adminHomeDeniedForUser() throws Exception {

        this.mvc.perform(MockMvcRequestBuilders.get("/admin/home")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}