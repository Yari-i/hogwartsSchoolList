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
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private AvatarService avatarService;


    @Test
    void shouldCorrectlyGetStudentInfo() throws Exception {
        // given
        long studentId = 1L;
        Student student = new Student("Harry Potter", 20);


        when(studentService.findStudent(studentId)).thenReturn(student);

        // when
        ResultActions perform = mockMvc.perform(get("/student/{id}", studentId));

        // then
        perform
                .andExpect(jsonPath("$.name").value(student.getName()))
                .andExpect(jsonPath("$.age").value(student.getAge()))
                .andDo(print());

    }

    @Test
    void shouldCorrectlyCreateStudent() throws Exception {
        // given
        long studentId = 1L;
        Student student = new Student("Harry Potter", 20);
        Student savedStudent = new Student("Harry Potter", 20);
        savedStudent.setId(studentId);

        when(studentService.addStudent(student)).thenReturn(savedStudent);

        // when
        ResultActions perform = mockMvc.perform(post("/student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)));

        // then
        perform
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedStudent.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(savedStudent.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(savedStudent.getAge()))
                .andDo(print());
    }


    @Test
    void shouldCorrectlyEditStudent() throws Exception {
        // given
        long studentId = 1L;
        Student student = new Student("Harry Potter", 20);

        when(studentService.editeStudent(studentId, student)).thenReturn(student);

        //when
        ResultActions perform = mockMvc.perform(put("/student/{id}", studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)));

        //then
        perform
                .andExpect(jsonPath("$.name").value(student.getName()))
                .andExpect(jsonPath("$.age").value(student.getAge()))
                .andDo(print());


    }

    @Test
    void shouldCorrectlyDeleteStudent() throws Exception {
        // given
        long studentId = 1L;
        Student student = new Student("Harry Potter", 20);

        doNothing().when(studentService).deleteStudent(studentId);

        // when
        ResultActions perform = mockMvc.perform(delete("/student/{id}", studentId));

        // then
        perform
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldCorrectlyGetAllByAge() throws Exception {
        // given
        int age = 20;

        List<Student> students = List.of(
                new Student("Harry Potter", 20),
                new Student("Ron Weasley", 20)
        );

        when(studentService.getAllByAge(age)).thenReturn(students);

        // when
        ResultActions perform = mockMvc.perform(get("/student").param("age", String.valueOf(age)));

        // then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(students.size()))
                .andExpect(jsonPath("$[0].name").value("Harry Potter"))
                .andExpect(jsonPath("$[1].name").value("Ron Weasley"))
                .andDo(print());
    }

    @Test
    void shouldCorrectlyFindByAgeBetween() throws Exception {
        // given
        int from = 15;
        int to = 25;

        List<Student> students = List.of(
                new Student("Harry Potter", 20),
                new Student("Ron Weasley", 22)
        );

        when(studentService.findByAgeBetween(from, to)).thenReturn(students);

        // when
        ResultActions perform = mockMvc.perform(get("/student/findByAgeBetween")
                .param("from", String.valueOf(from))
                .param("to", String.valueOf(to)));

        // then
        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(students.size()))
                .andExpect(jsonPath("$[0].name").value("Harry Potter"))
                .andExpect(jsonPath("$[1].name").value("Ron Weasley"))
                .andDo(print());
    }
}