package com.nnk.springboot.integrationTests;

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

import java.util.Collections;

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
public class TradeControllerIT extends TestCase {

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
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.get("/trade/update/2")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Account 002", "Type 002", "200.02");
    }

    @Test
    public void testHome() throws Exception {
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/trade/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Account 001", "Type 001", "100.01");
        assertThat(content).contains("Account 002", "Type 002", "200.02");
        assertThat(content).contains("Account 003", "Type 003", "300.03");
        assertThat(content).contains("Account 004", "Type 004", "400.04");
        }

    @Test
    public void testValidateOk() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.post("/trade/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("account","Account 005")
                        .param("type","Type 005")
                        .param("buyQuantity","500.05")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/trade/list"))
                .andExpect(status().isFound());

        //Check that id 5 have not been added
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/trade/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Account 001", "Type 001", "100.01");
        assertThat(content).contains("Account 002", "Type 002", "200.02");
        assertThat(content).contains("Account 003", "Type 003", "300.03");
        assertThat(content).contains("Account 004", "Type 004", "400.04");
        assertThat(content).contains("Account 005", "Type 005", "500.05");
    }

    @Test
    public void testValidateErrors() throws Exception {
        String stringOf31Chars = String.join("", Collections.nCopies(31, "x"));
        //update bidList
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.post("/trade/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("account","")
                        .param("type",stringOf31Chars)
                        .param("buyQuantity","-0.01")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Account is mandatory");
        assertThat(content).contains("Must be at most 30 characters in length");
        assertThat(content).contains("Must be positive");

        //Check that id 5 have not been updated
        result = this.mvc.perform(MockMvcRequestBuilders.get("/trade/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).doesNotContain(stringOf31Chars,"-0.01");
    }

    @Test
    public void testUpdate() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.post("/trade/update/3")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("account","Account 04561")
                        .param("type","Type 06156")
                        .param("buyQuantity","111.255")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/trade/list"))
                .andExpect(status().isFound());

        //Check that id 3 have been deleted
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/trade/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Account 001", "Type 001", "100.01");
        assertThat(content).contains("Account 002", "Type 002", "200.02");
        assertThat(content).doesNotContain("Account 003", "Type 003", "300.03");
        assertThat(content).contains("Account 04561", "Type 06156", "111.255");
        assertThat(content).contains("Account 004", "Type 004", "400.04");
    }

    @Test
    public void testUpdateError() throws Exception {
        String stringOf31Chars = String.join("", Collections.nCopies(31, "x"));
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.post("/trade/update/3")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("account",stringOf31Chars)
                        .param("type","")
                        .param("buyQuantity","-156")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Type is mandatory");
        assertThat(content).contains("Must be at most 30 characters in length");
        assertThat(content).contains("Must be positive");

        //Check that id 3 have not been updated
        result = this.mvc.perform(MockMvcRequestBuilders.get("/trade/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).contains("Account 001", "Type 001", "100.01");
        assertThat(content).contains("Account 002", "Type 002", "200.02");
        assertThat(content).contains("Account 003", "Type 003", "300.03");
        assertThat(content).contains("Account 004", "Type 004", "400.04");
        assertThat(content).doesNotContain(stringOf31Chars, "-156");
    }

    @Test
    public void testDelete() throws Exception {
        //delete
        this.mvc.perform(MockMvcRequestBuilders.get("/trade/delete/3")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/trade/list"))
                .andExpect(status().isFound());

        //Check that id 3 have been deleted
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/trade/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Account 001", "Type 001", "100.01");
        assertThat(content).contains("Account 002", "Type 002", "200.02");
        assertThat(content).doesNotContain("Account 003", "Type 003", "300.03");
        assertThat(content).contains("Account 004", "Type 004", "400.04");
    }

    @Test
    public void testDeleteError() throws Exception {
        //Try to delete nonexistent
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.get("/trade/delete/5")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Error !");
        assertThat(content).contains("Invalid trade Id:5");
    }

    @Test
    public void testShowAddForm() throws Exception {
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/trade/add")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Add New Trade");
    }

}