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
public class RuleNameControllerIT extends TestCase {

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
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.get("/ruleName/update/2")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Rule name 002", "Description rule name 002", "{Json:002}", "Template 002", "Select * from RuleName_002", "Sqlpart 002");
    }

    @Test
    public void testHome() throws Exception {
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/ruleName/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Rule name 001", "Description rule name 001", "{Json:001}", "Template 001", "Select * from RuleName_001", "Sqlpart 001");
        assertThat(content).contains("Rule name 002", "Description rule name 002", "{Json:002}", "Template 002", "Select * from RuleName_002", "Sqlpart 002");
        assertThat(content).contains("Rule name 003", "Description rule name 003", "{Json:003}", "Template 003", "Select * from RuleName_003", "Sqlpart 003");
        assertThat(content).contains("Rule name 004", "Description rule name 004", "{Json:004}", "Template 004", "Select * from RuleName_004", "Sqlpart 004");
    }

    @Test
    public void testValidateOk() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.post("/ruleName/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("name","Rule name 005")
                        .param("description","Description rule name 005")
                        .param("json","{Json:005}")
                        .param("template","Template 005")
                        .param("sqlStr","Select * from RuleName_005")
                        .param("sqlPart","Sqlpart 005")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/ruleName/list"))
                .andExpect(status().isFound());

        //Check that id 5 have not been added
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/ruleName/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Rule name 001", "Description rule name 001", "{Json:001}", "Template 001", "Select * from RuleName_001", "Sqlpart 001");
        assertThat(content).contains("Rule name 002", "Description rule name 002", "{Json:002}", "Template 002", "Select * from RuleName_002", "Sqlpart 002");
        assertThat(content).contains("Rule name 003", "Description rule name 003", "{Json:003}", "Template 003", "Select * from RuleName_003", "Sqlpart 003");
        assertThat(content).contains("Rule name 004", "Description rule name 004", "{Json:004}", "Template 004", "Select * from RuleName_004", "Sqlpart 004");
        assertThat(content).contains("Rule name 005", "Description rule name 005", "{Json:005}", "Template 005", "Select * from RuleName_005", "Sqlpart 005");

    }

    @Test
    public void testValidateErrors() throws Exception {
        String stringOf126Chars = String.join("", Collections.nCopies(126, "x"));
        String stringOf513Chars = String.join("", Collections.nCopies(513, "x"));
        //update bidList
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.post("/ruleName/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("name","")
                        .param("description",stringOf126Chars)
                        .param("json","{Json:005}")
                        .param("template",stringOf513Chars)
                        .param("sqlStr","Select * from RuleName_005")
                        .param("sqlPart","Sqlpart 005")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Must be at most 125 characters in length");
        assertThat(content).contains("Must be at most 512 characters in length");

        //Check that id 5 have not been updated
        result = this.mvc.perform(MockMvcRequestBuilders.get("/ruleName/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).doesNotContain("{Json:005}", "elect * from RuleName_005", "Sqlpart 005");
    }

    @Test
    public void testUpdate() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.post("/ruleName/update/3")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("name","Rule name 164")
                        .param("description","Description rule name 1561")
                        .param("json","{Json:005}")
                        .param("template","Template 1158")
                        .param("sqlStr","Select * from RuleName_885")
                        .param("sqlPart","Sqlpart 20315")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/ruleName/list"))
                .andExpect(status().isFound());

        //Check that id 3 have been deleted
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/ruleName/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Rule name 001", "Description rule name 001", "{Json:001}", "Template 001", "Select * from RuleName_001", "Sqlpart 001");
        assertThat(content).contains("Rule name 002", "Description rule name 002", "{Json:002}", "Template 002", "Select * from RuleName_002", "Sqlpart 002");
        assertThat(content).doesNotContain("Rule name 003", "Description rule name 003", "{Json:003}", "Template 003", "Select * from RuleName_003", "Sqlpart 003");
        assertThat(content).contains("Rule name 164", "Description rule name 1561", "{Json:005}", "Template 1158", "Select * from RuleName_885", "Sqlpart 20315");
        assertThat(content).contains("Rule name 004", "Description rule name 004", "{Json:004}", "Template 004", "Select * from RuleName_004", "Sqlpart 004");

    }

    @Test
    public void testUpdateError() throws Exception {
        String stringOf126Chars = String.join("", Collections.nCopies(126, "x"));
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.post("/ruleName/update/3")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("name","Rule name 164")
                        .param("description","Description rule name 1561")
                        .param("json","{Json:005}")
                        .param("template","Template 1158")
                        .param("sqlStr",stringOf126Chars)
                        .param("sqlPart","Sqlpart 20315")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Must be at most 125 characters in length");

        //Check that id 3 have not been updated
        result = this.mvc.perform(MockMvcRequestBuilders.get("/ruleName/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).contains("Rule name 001", "Description rule name 001", "{Json:001}", "Template 001", "Select * from RuleName_001", "Sqlpart 001");
        assertThat(content).contains("Rule name 002", "Description rule name 002", "{Json:002}", "Template 002", "Select * from RuleName_002", "Sqlpart 002");
        assertThat(content).contains("Rule name 003", "Description rule name 003", "{Json:003}", "Template 003", "Select * from RuleName_003", "Sqlpart 003");
        assertThat(content).contains("Rule name 004", "Description rule name 004", "{Json:004}", "Template 004", "Select * from RuleName_004", "Sqlpart 004");

    }

    @Test
    public void testDelete() throws Exception {
        //delete
        this.mvc.perform(MockMvcRequestBuilders.get("/ruleName/delete/3")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/ruleName/list"))
                .andExpect(status().isFound());

        //Check that id 3 have been deleted
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/ruleName/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Rule name 001", "Description rule name 001", "{Json:001}", "Template 001", "Select * from RuleName_001", "Sqlpart 001");
        assertThat(content).contains("Rule name 002", "Description rule name 002", "{Json:002}", "Template 002", "Select * from RuleName_002", "Sqlpart 002");
        assertThat(content).doesNotContain("Rule name 003", "Description rule name 003", "{Json:003}", "Template 003", "Select * from RuleName_003", "Sqlpart 003");
        assertThat(content).contains("Rule name 004", "Description rule name 004", "{Json:004}", "Template 004", "Select * from RuleName_004", "Sqlpart 004");

    }

    @Test
    public void testDeleteError() throws Exception {
        //Try to delete nonexistent
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.get("/ruleName/delete/5")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Error !");
        assertThat(content).contains("Invalid ruleName Id:5");
    }

}