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
public class UserControllerIT extends TestCase {

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
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.get("/user/update/3")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("John Boyd", "john.boyd@gmail.com", "USER");
    }

    @Test
    public void testHome() throws Exception {
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/user/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Administrator", "admin", "ADMIN");
        assertThat(content).contains("User", "user", "USER");
        assertThat(content).contains("John Boyd", "john.boyd@gmail.com", "USER");
    }

    @Test
    public void testValidateOk() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.post("/user/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("username","User_004")
                        .param("password","Password444{}")
                        .param("fullname","User 004")
                        .param("role","Role of user 004")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/user/list"))
                .andExpect(status().isFound());

        //Check that id 5 have not been added
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/user/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Administrator", "admin", "ADMIN");
        assertThat(content).contains("User", "user", "USER");
        assertThat(content).contains("John Boyd", "john.boyd@gmail.com", "USER");
        assertThat(content).contains("User 004", "User_004", "Role of user 004");
    }


    @Test
    public void testValidateErrors() throws Exception {
        String stringOf126Chars = String.join("", Collections.nCopies(126, "x"));
        //update bidList
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.post("/user/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("username","")
                        .param("password","")
                        .param("fullname",stringOf126Chars)
                        .param("role","")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Username is mandatory");
        assertThat(content).contains("TOO_SHORT");
        assertThat(content).contains("Role is mandatory");
        assertThat(content).contains("Must be at most 125 characters in length");

        //Check that id 5 have not been updated
        result = this.mvc.perform(MockMvcRequestBuilders.get("/user/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).doesNotContain(stringOf126Chars);
    }

    @Test
    public void testUpdate() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.post("/user/update/3")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("username","jeff.john@gmail.com")
                        .param("password","Pwd123456{}")
                        .param("fullname","Jeff John")
                        .param("role","ROLE_OF_JEFF")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/user/list"))
                .andExpect(status().isFound());

        //Check that id 3 have been deleted
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/user/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Administrator", "admin", "ADMIN");
        assertThat(content).contains("User", "user", "USER");
        assertThat(content).doesNotContain("John Boyd", "john.boyd@gmail.com");
        assertThat(content).contains("Jeff John", "jeff.john@gmail.com", "ROLE_OF_JEFF");
    }

    @Test
    public void testUpdateError() throws Exception {
        String stringOf126Chars = String.join("", Collections.nCopies(126, "x"));
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.post("/user/update/3")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("username",stringOf126Chars)
                        .param("password",stringOf126Chars)
                        .param("fullname","")
                        .param("role",stringOf126Chars)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("FullName is mandatory");
        assertThat(content).contains("Must be at most 125 characters in length");

        //Check that id 3 have not been updated
        result = this.mvc.perform(MockMvcRequestBuilders.get("/user/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).contains("Administrator", "admin", "ADMIN");
        assertThat(content).contains("User", "user", "USER");
        assertThat(content).contains("John Boyd", "john.boyd@gmail.com");
        assertThat(content).doesNotContain(stringOf126Chars);
    }

    @Test
    public void testDelete() throws Exception {
        //delete
        this.mvc.perform(MockMvcRequestBuilders.get("/user/delete/3")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/user/list"))
                .andExpect(status().isFound());

        //Check that id 3 have been deleted
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/user/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Administrator", "admin", "ADMIN");
        assertThat(content).contains("User", "user", "USER");
        assertThat(content).doesNotContain("John Boyd", "john.boyd@gmail.com");
    }

    @Test
    public void testDeleteError() throws Exception {
        //Try to delete nonexistent
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.get("/user/delete/5")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Error !");
        assertThat(content).contains("Invalid user Id:5");
    }

    @Test
    public void testShowAddForm() throws Exception {
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/user/add")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Add New User");
    }

}