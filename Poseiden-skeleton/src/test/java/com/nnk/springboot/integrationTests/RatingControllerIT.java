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
public class RatingControllerIT extends TestCase {

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
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.get("/rating/update/2")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Moodys rating 002", "SandP rating 002", "Fitch rating 002");
    }

    @Test
    public void testHome() throws Exception {
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/rating/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Moodys rating 001", "SandP rating 001", "Fitch rating 001");
        assertThat(content).contains("Moodys rating 002", "SandP rating 002", "Fitch rating 002");
        assertThat(content).contains("Moodys rating 003", "SandP rating 003", "Fitch rating 003");
        assertThat(content).contains("Moodys rating 004", "SandP rating 004", "Fitch rating 004");
    }
    @Test
    public void testValidateOk() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.post("/rating/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("moodysRating","Moodys rating 005")
                        .param("sandPRating","SandP rating 005")
                        .param("fitchRating","Fitch rating 005")
                        .param("orderNumber","5")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/rating/list"))
                .andExpect(status().isFound());

        //Check that id 5 have not been added
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/rating/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Moodys rating 001", "SandP rating 001", "Fitch rating 001");
        assertThat(content).contains("Moodys rating 002", "SandP rating 002", "Fitch rating 002");
        assertThat(content).contains("Moodys rating 003", "SandP rating 003", "Fitch rating 003");
        assertThat(content).contains("Moodys rating 004", "SandP rating 004", "Fitch rating 004");
        assertThat(content).contains("Moodys rating 005", "SandP rating 005", "Fitch rating 005");
    }


    @Test
    public void testValidateEmptyMoodys() throws Exception {
        //update bidList
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.post("/rating/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("moodysRating","")
                        .param("sandPRating","SandP rating 005")
                        .param("fitchRating","Fitch rating 005")
                        .param("orderNumber","5")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Moodys rating is mandatory");

        //Check that id 5 have not been updated
        result = this.mvc.perform(MockMvcRequestBuilders.get("/rating/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).doesNotContain("SandP rating 005", "Fitch rating 005");
    }

    @Test
    public void testValidateEmptySandP() throws Exception {
        //update bidList
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.post("/rating/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("moodysRating","Moodys rating 005")
                        .param("sandPRating","")
                        .param("fitchRating","Fitch rating 005")
                        .param("orderNumber","5")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("SandP rating is mandatory");

        //Check that id 5 have not been updated
        result = this.mvc.perform(MockMvcRequestBuilders.get("/rating/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).doesNotContain("Moodys rating 005", "Fitch rating 005");
    }

    @Test
    public void testValidateEmptyFitch() throws Exception {
        //update bidList
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.post("/rating/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("moodysRating","Moodys rating 005")
                        .param("sandPRating","SandP rating 005")
                        .param("fitchRating","")
                        .param("orderNumber","5")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Fitch rating is mandatory");

        //Check that id 5 have not been updated
        result = this.mvc.perform(MockMvcRequestBuilders.get("/rating/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).doesNotContain("SandP rating 005", "Moodys rating 005");
    }
    @Test
    public void testValidateNullOrderNumber() throws Exception {
        //update bidList
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.post("/rating/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("moodysRating","Moodys rating 005")
                        .param("sandPRating","SandP rating 005")
                        .param("fitchRating","Fitch rating 005")
                        .param("orderNumber","")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("must not be null");

        //Check that id 5 have not been updated
        result = this.mvc.perform(MockMvcRequestBuilders.get("/rating/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).doesNotContain("SandP rating 005", "Fitch rating 005");
    }

    @Test
    public void testUpdate() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.post("/rating/update/3")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("moodysRating","Moodys rating 159")
                        .param("sandPRating","SandP rating 275")
                        .param("fitchRating","Fitch rating 74")
                        .param("orderNumber","41")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/rating/list"))
                .andExpect(status().isFound());

        //Check that id 3 have been deleted
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/rating/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Moodys rating 001", "SandP rating 001", "Fitch rating 001");
        assertThat(content).contains("Moodys rating 002", "SandP rating 002", "Fitch rating 002");
        assertThat(content).doesNotContain("Moodys rating 003", "SandP rating 003", "Fitch rating 003");
        assertThat(content).contains("Moodys rating 159", "SandP rating 275", "Fitch rating 74", "41");
        assertThat(content).contains("Moodys rating 004", "SandP rating 004", "Fitch rating 004");
    }

    @Test
    public void testUpdateErrorOrderNumberOutOfRange() throws Exception {
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.post("/rating/update/3")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("moodysRating","Moodys rating 159")
                        .param("sandPRating","SandP rating 275")
                        .param("fitchRating","Fitch rating 74")
                        .param("orderNumber","128")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("must be less than 128");

        //Check that id 3 have not been updated
        result = this.mvc.perform(MockMvcRequestBuilders.get("/rating/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).contains("Moodys rating 001", "SandP rating 001", "Fitch rating 001");
        assertThat(content).contains("Moodys rating 002", "SandP rating 002", "Fitch rating 002");
        assertThat(content).contains("Moodys rating 003", "SandP rating 003", "Fitch rating 003");
        assertThat(content).contains("Moodys rating 004", "SandP rating 004", "Fitch rating 004");
    }

    @Test
    public void testDelete() throws Exception {
        //delete
        this.mvc.perform(MockMvcRequestBuilders.get("/rating/delete/3")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/rating/list"))
                .andExpect(status().isFound());

        //Check that id 3 have been deleted
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/rating/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Moodys rating 001", "SandP rating 001", "Fitch rating 001");
        assertThat(content).contains("Moodys rating 002", "SandP rating 002", "Fitch rating 002");
        assertThat(content).doesNotContain("Moodys rating 003", "SandP rating 003", "Fitch rating 003");
        assertThat(content).contains("Moodys rating 004", "SandP rating 004", "Fitch rating 004");
    }

    @Test
    public void testDeleteError() throws Exception {
        //Try to delete nonexistent
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.get("/rating/delete/5")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Error !");
        assertThat(content).contains("Invalid rating Id:5");
    }
}