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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
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
public class BidListControllerIT extends TestCase {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }


    @Test
    public void showUpdateForm() throws Exception {
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.get("/bidList/update/2")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Account_002", "BID_TYPE_002", "2.22");
    }

    @Test
    public void testHome() throws Exception {
        //Show Bid List
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/bidList/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Account_001", "BID_TYPE_001", "1.11");
        assertThat(content).contains("Account_002", "BID_TYPE_002", "2.22");
        assertThat(content).contains("Account_003", "BID_TYPE_003", "3.33");
        assertThat(content).contains("Account_004", "BID_TYPE_004", "4.44");
    }

    @Test
    public void testValidateOk() throws Exception {
        //update bidList
        this.mvc.perform(MockMvcRequestBuilders.post("/bidList/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("account","Account_005")
                        .param("type","BID_TYPE_005")
                        .param("bidQuantity","5.55")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/bidList/list"))
                .andExpect(status().isFound());

        //Check that id 5 have not been added
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/bidList/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Account_001", "BID_TYPE_001", "1.11");
        assertThat(content).contains("Account_002", "BID_TYPE_002", "2.22");
        assertThat(content).contains("Account_003", "BID_TYPE_003", "3.33");
        assertThat(content).contains("Account_004", "BID_TYPE_004", "4.44");
        assertThat(content).contains("Account_005", "BID_TYPE_005", "5.55");
    }

    @Test
    public void testValidateEmptyBidQuantity() throws Exception {
        //update bidList
        this.mvc.perform(MockMvcRequestBuilders.post("/bidList/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("account","Account_005")
                        .param("type","BID_TYPE_005")
                        .param("bidQuantity","")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/bidList/list"))
                .andExpect(status().isFound());

        //Check that id 5 have not been added
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/bidList/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Account_001", "BID_TYPE_001", "1.11");
        assertThat(content).contains("Account_002", "BID_TYPE_002", "2.22");
        assertThat(content).contains("Account_003", "BID_TYPE_003", "3.33");
        assertThat(content).contains("Account_004", "BID_TYPE_004", "4.44");
        assertThat(content).contains("Account_005", "BID_TYPE_005", "0.0");
    }

    @Test
    public void testValidateInvalidAccount() throws Exception {
        //update bidList
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.post("/bidList/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("account","")
                        .param("type","BID_TYPE_005")
                        .param("bidQuantity","5.55")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Account is mandatory");
        assertThat(content).doesNotContain("Type is mandatory");

        //Check that id 5 have not been updated
        result = this.mvc.perform(MockMvcRequestBuilders.get("/bidList/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).doesNotContain("BID_TYPE_005", "5.55");
    }

    @Test
    public void testValidateInvalidType() throws Exception {
        //update bidList
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.post("/bidList/validate")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("account","Account_005")
                        .param("type","")
                        .param("bidQuantity","5.55")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).doesNotContain("Account is mandatory");
        assertThat(content).contains("Type is mandatory");

        //Check that id 5 have not been updated
        result = this.mvc.perform(MockMvcRequestBuilders.get("/bidList/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).doesNotContain("Account_005", "5.55");
    }

    @Test
    public void testUpdateBid() throws Exception {
        //update bidList
        this.mvc.perform(MockMvcRequestBuilders.post("/bidList/update/3")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("account","Account_333")
                        .param("type","Type_333")
                        .param("bidQuantity","0.03")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/bidList/list"))
                .andExpect(status().isFound());

        //Check that id 3 have been deleted
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/bidList/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Account_001", "BID_TYPE_001", "1.11");
        assertThat(content).contains("Account_002", "BID_TYPE_002", "2.22");
        assertThat(content).doesNotContain("Account_003", "BID_TYPE_003", "3.33");
        assertThat(content).contains("Account_333", "Type_333", "0.03");
        assertThat(content).contains("Account_004", "BID_TYPE_004", "4.44");
    }

    @Test
    public void testUpdateBidError() throws Exception {
        //update bidList
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.post("/bidList/update/3")
                        .with(Client.johnBoyd())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("account","")
                        .param("type","")
                        .param("bidQuantity","")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Account is mandatory");
        assertThat(content).contains("Type is mandatory");

        //Check that id 3 have not been updated
        result = this.mvc.perform(MockMvcRequestBuilders.get("/bidList/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).contains("Account_001", "BID_TYPE_001", "1.11");
        assertThat(content).contains("Account_002", "BID_TYPE_002", "2.22");
        assertThat(content).contains("Account_003", "BID_TYPE_003", "3.33");
        assertThat(content).contains("Account_004", "BID_TYPE_004", "4.44");
    }

    @Test
    public void testDeleteBid() throws Exception {
        //delete bidList
        this.mvc.perform(MockMvcRequestBuilders.get("/bidList/delete/3")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/bidList/list"))
                .andExpect(status().isFound());

        //Check that id 3 have been deleted
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/bidList/list")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Account_001", "BID_TYPE_001", "1.11");
        assertThat(content).contains("Account_002", "BID_TYPE_002", "2.22");
        assertThat(content).doesNotContain("Account_003", "BID_TYPE_003", "3.33");
        assertThat(content).contains("Account_004", "BID_TYPE_004", "4.44");
    }

    @Test
    public void testDeleteBidError() throws Exception {
        //Try to delete nonexistent bid list
        MvcResult result =  this.mvc.perform(MockMvcRequestBuilders.get("/bidList/delete/5")
                        .with(Client.johnBoyd())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Error !");
        assertThat(content).contains("Invalid bidList Id:5");
    }
}