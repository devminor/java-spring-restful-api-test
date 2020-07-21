package uk.co.huntersix.spring.rest.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.huntersix.spring.rest.exception.EntityAlreadyExistException;
import uk.co.huntersix.spring.rest.exception.EntityNotFoundException;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonDataService personDataService;

    @Test
    public void shouldReturnPersonFromService() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(new Person("Mary", "Smith"));
        this.mockMvc.perform(get("/person/smith/mary"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("firstName").value("Mary"))
            .andExpect(jsonPath("lastName").value("Smith"));
    }

    @Test
    public void shouldReturnNotFoundWhenPersonNotFound() throws Exception {
        when(personDataService.findPerson(any(), any())).thenThrow(EntityNotFoundException.class);
        this.mockMvc.perform(get("/person/john/doe"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Entity not found"));
    }



    @Test
    public void shouldReturnMultipleResultWhileSearchBySurname() throws Exception {
        when(personDataService.findPerson("smith")).thenReturn(Arrays.asList(
                new Person("Marvel","Smith"),
                new Person("Mary","Smith"),
                new Person("Marian", "Smith")
        ));
        this.mockMvc.perform(get("/person/smith"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(jsonPath("$[0].firstName").value("Marvel"))
                .andExpect(jsonPath("$[0].lastName").value("Smith"))
                .andExpect(jsonPath("$[1].firstName").value("Mary"))
                .andExpect(jsonPath("$[1].lastName").value("Smith"))
                .andExpect(jsonPath("$[2].firstName").value("Marian"))
                .andExpect(jsonPath("$[2].lastName").value("Smith"));
    }

    @Test
    public void shouldReturnEmptyArrayForNoPeople() throws Exception {
        when(personDataService.findPerson(any())).thenReturn(Collections.emptyList());

        this.mockMvc.perform(get("/person/smith"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$",hasSize(0)));
    }

    @Test
    public void shouldReturnSingleResultWhileSearchBySurname() throws Exception {
        when(personDataService.findPerson("smith")).thenReturn(Collections.singletonList(
                new Person("Marvel", "Smith")
        ));
        this.mockMvc.perform(get("/person/smith"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("Marvel"))
                .andExpect(jsonPath("$[0].lastName").value("Smith"));
    }


    @Test
    public void shouldCreateNewPerson() throws Exception {
        String jsonBody = "{\"firstName\":\"Marvel\",\"lastName\":\"Bryn\"}";

        when(personDataService.addPerson(any())).thenReturn(new Person("Marvel", "Bryn"));

        this.mockMvc.perform(post("/person").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("firstName").value("Marvel"))
                .andExpect(jsonPath("lastName").value("Bryn"));

    }

    @Test
    public void shouldReturnBadRequestWhenPersonAlreadyExist() throws Exception {
        String jsonBody = "{\"firstName\":\"Marvel\",\"lastName\":\"Bryn\"}";
        when(personDataService.addPerson(any())).thenThrow(EntityAlreadyExistException.class);

        this.mockMvc.perform(post("/person").contentType(MediaType.APPLICATION_JSON_UTF8).content(jsonBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Entity already exists"));

    }

}