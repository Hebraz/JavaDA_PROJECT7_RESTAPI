package com.nnk.springboot.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnk.springboot.integrationTests.util.Client;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
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
public class CurveControllerIT extends TestCase {

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
    public void showUpdateForm() throws Exception {
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.get("/curvePoint/update/2")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("22", "2.02", "2000.0002");
    }

    @Test
    public void testHome() throws Exception {
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/curvePoint/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("11", "1.01", "1000.0001");
        assertThat(content).contains("22", "2.02", "2000.0002");
        assertThat(content).contains("11", "3.03", "3000.0003");
        assertThat(content).contains("11", "4.04", "4000.0004");
    }
    @Test
    public void testValidateOk() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.post("/curvePoint/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("curveId","26")
                        .param("term","0.74")
                        .param("value","4891.25")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/curvePoint/list"))
                .andExpect(status().isFound());

        //Check that id 5 have not been added
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/curvePoint/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("11", "1.01", "1000.0001");
        assertThat(content).contains("22", "2.02", "2000.0002");
        assertThat(content).contains("33", "3.03", "3000.0003");
        assertThat(content).contains("44", "4.04", "4000.0004");
        assertThat(content).contains("26", "0.74", "4891.25");
    }


    @Test
    public void testValidateNullCurveId() throws Exception {
        //update bidList
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.post("/curvePoint/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("curveId", "")
                        .param("term","0.74")
                        .param("value","4891.25")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("must not be null");

        //Check that id 5 have not been updated
        result = this.mvc.perform(MockMvcRequestBuilders.get("/curvePoint/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).doesNotContain("0.74", "4891.25");
    }

    @Test
    public void testUpdate() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.post("/curvePoint/update/3")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("curveId","26")
                        .param("term","0.74")
                        .param("value","4891.25")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/curvePoint/list"))
                .andExpect(status().isFound());

        //Check that id 3 have been deleted
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/curvePoint/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("11", "1.01", "1000.0001");
        assertThat(content).contains("22", "2.02", "2000.0002");
        assertThat(content).doesNotContain("33", "3.03", "3000.0003");
        assertThat(content).contains("26", "0.74", "4891.25");
        assertThat(content).contains("44", "4.04", "4000.0004");
        assertThat(content).contains("26", "0.74", "4891.25");
    }

    @Test
    public void testUpdateError() throws Exception {
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.post("/curvePoint/update/3")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("curveId", "")
                        .param("term","0.74")
                        .param("value","4891.25")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("must not be null");

        //Check that id 3 have not been updated
        result = this.mvc.perform(MockMvcRequestBuilders.get("/curvePoint/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).contains("11", "1.01", "1000.0001");
        assertThat(content).contains("22", "2.02", "2000.0002");
        assertThat(content).contains("33", "3.03", "3000.0003");
        assertThat(content).contains("44", "4.04", "4000.0004");
    }

    @Test
    public void testDelete() throws Exception {
        //delete bidList
        this.mvc.perform(MockMvcRequestBuilders.get("/curvePoint/delete/3")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/curvePoint/list"))
                .andExpect(status().isFound());

        //Check that id 3 have been deleted
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/curvePoint/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("11", "1.01", "1000.0001");
        assertThat(content).contains("22", "2.02", "2000.0002");
        assertThat(content).doesNotContain("33", "3.03", "3000.0003");
        assertThat(content).contains("44", "4.04", "4000.0004");
    }
    @Test
    public void testDeleteError() throws Exception {
        //Try to delete nonexistent bid list
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.get("/curvePoint/delete/5")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Error !");
        assertThat(content).contains("Invalid curvePoint Id:5");
    }
}