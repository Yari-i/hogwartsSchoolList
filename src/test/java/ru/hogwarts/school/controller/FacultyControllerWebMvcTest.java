package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FacultyService facultyService;

    @MockitoBean
    private AvatarService avatarService;

    @Test
    void shouldCorrectlyGetFacultyInfo() throws Exception {
        // given
        long facultyId = 1L;
        Faculty faculty = new Faculty(" Gryffindor", "orange");


        when(facultyService.findFaculty(facultyId)).thenReturn(faculty);

        // when
        ResultActions perform = mockMvc.perform(get("/faculty/{id}", facultyId));

        // then
        perform
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()))
                .andDo(print());

    }

    @Test
    void shouldCorrectlyCreateFaculty() throws Exception {
        // given
        long facultyId = 1L;
        Faculty faculty = new Faculty("Gryffindor", "orange");
        Faculty savedFaculty = new Faculty("Gryffindor", "orange");
        savedFaculty.setId(facultyId);

        when(facultyService.addFaculty(faculty)).thenReturn(savedFaculty);

        // when
        ResultActions perform = mockMvc.perform(post("/faculty")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(faculty)));

        // then
        perform
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedFaculty.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(savedFaculty.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(savedFaculty.getColor()))
                .andDo(print());
    }

    @Test
    void shouldCorrectlyEditFaculty() throws Exception {
        // given
        long facultyId = 1L;
        Faculty faculty = new Faculty("Gryffindor", "orange");

        when(facultyService.editFaculty(facultyId, faculty)).thenReturn(faculty);

        //when
        ResultActions perform = mockMvc.perform(put("/faculty/{id}", facultyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(faculty)));

        //then
        perform
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()))
                .andDo(print());


    }

    @Test
    void shouldCorrectlyDeleteFaculty() throws Exception {
        // given
        long facultyId = 1L;
        Faculty faculty = new Faculty("Gryffindor", "orange");

        doNothing().when(facultyService).deleteFaculty(facultyId);

        // when
        ResultActions perform = mockMvc.perform(delete("/faculty/{id}", facultyId));

        // then
        perform
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldCorrectlyGetAllByColor() throws Exception { // given
        String color = "orange";

        List<Faculty> faculties = List.of(
                new Faculty("Gryffindor", "orange"),
                new Faculty("Slytherin", "green")
        );

        when(facultyService.getAllByColor(color)).thenReturn(faculties);

        // when
        ResultActions perform = mockMvc.perform(get("/faculty").param("color", String.valueOf(color)));

        // then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(faculties.size()))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[1].name").value("Slytherin"))
                .andDo(print());
    }

    @Test
    void shouldCorrectlyFindByNameIgnoreCaseOrIgnoreColorCase() throws Exception {
        // given
        String query = "Gryffindor";

        List<Faculty> faculties = List.of(
                new Faculty("Gryffindor", "orange")
        );

        when(facultyService.findByNameIgnoreCaseOrColorIgnoreCase(query, query)).thenReturn(faculties);

        // when
        ResultActions perform = mockMvc.perform(get("/faculty/findByNameIgnoreCaseOrIgnoreColorCase")
                .param("query", query));

        // then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(faculties.size()))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[0].color").value("orange"))
                .andDo(print());
    }

}