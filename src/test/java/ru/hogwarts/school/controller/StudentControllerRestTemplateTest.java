package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    public void clearDB() {
        studentRepository.deleteAll();
    }

    @Test
    void shouldCorrectlyGetStudentInfo() {
        //given
        Student student = new Student("Harry Potter", 20);
        student = studentRepository.save(student);

        //when
        ResponseEntity<Student> studentResponseEntity = restTemplate.getForEntity("http://localhost:" + port + "/student/" +
                        student.getId(),
                Student.class
        );

        //then
        assertNotNull(studentResponseEntity);
        assertEquals(studentResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));

        Student actualStudent = studentResponseEntity.getBody();
        assertEquals(actualStudent.getId(), student.getId());
        assertEquals(actualStudent.getName(), student.getName());
        assertEquals(actualStudent.getAge(), student.getAge());

    }

    @Test
    void shouldCorrectlyCreateStudent() {

        //give
        Student student = new Student("Harry Potter", 20);

        //when
        ResponseEntity<Student> studentResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/student",
                student,
                Student.class
        );

        //then
        assertNotNull(studentResponseEntity);
        assertEquals(studentResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));

        Student actualStudent = studentResponseEntity.getBody();
        assertNotNull(actualStudent.getId());
        assertEquals(actualStudent.getName(), student.getName());
        org.assertj.core.api.Assertions.assertThat(actualStudent.getAge()).isEqualTo(student.getAge());
    }

    @Test
    void shouldCorrectlyEditStudent() {
        // given
        Student student = new Student("Harry Poter", 20);
        student = studentRepository.save(student);

        Student studentForUpdate = new Student("Harry Potter", 20);

        // when
        HttpEntity<Student> entity = new HttpEntity<>(studentForUpdate);
        ResponseEntity<Student> studentResponseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/student/" + student.getId(),
                HttpMethod.PUT,
                entity,
                Student.class
        );

        // then
        assertNotNull(studentForUpdate);
        assertEquals(studentResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));

        Student actualStudent = studentResponseEntity.getBody();
        assertEquals(actualStudent.getId(), student.getId());
        assertEquals(actualStudent.getName(), studentForUpdate.getName());
        assertEquals(actualStudent.getAge(), studentForUpdate.getAge());


    }

    @Test
    void shouldCorrectlyDeleteStudent() {
        // given
        Student student = new Student("Harry Potter", 20);
        student = studentRepository.save(student);

        // when
        ResponseEntity<Void> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/student/" + student.getId(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );

        // then
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(200));
        Optional<Student> deletedStudent = studentRepository.findById(student.getId());
        assertTrue(deletedStudent.isEmpty());
    }

    @Test
    void shouldCorrectlyGetAllByAge() {
        // given
        Student student1 = new Student("name1", 20);
        Student student2 = new Student("name2", 25);
        Student student3 = new Student("name3", 20);

        student1 = studentRepository.save(student1);
        student2 = studentRepository.save(student2);
        student3 = studentRepository.save(student3);

        int age = 20;

        // when
        ResponseEntity<List<Student>> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/student?age=" + age,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<Student>>() {
                }
        );

        // then
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(200));

        List<Student> students = responseEntity.getBody();
        assertNotNull(students);
        assertEquals(students.size(), 2);

        assertTrue(students.stream()
                .allMatch(student -> student.getAge() == age));
    }

    @Test
    void shouldCorrectlyFindByAgeBetween() {
        // given
        Student student1 = new Student("name1", 20);
        Student student2 = new Student("name2", 25);
        Student student3 = new Student("name3", 30);

        student1 = studentRepository.save(student1);
        student2 = studentRepository.save(student2);
        student3 = studentRepository.save(student3);

        int fromAge = 22;
        int toAge = 28;

        // when
        ResponseEntity<List<Student>> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/student/findByAgeBetween?from=" + fromAge + "&to=" + toAge,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<Student>>() {
                }
        );

        // then
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(200));

        List<Student> students = responseEntity.getBody();
        assertNotNull(students);
        assertEquals(students.size(), 1);

        Student foundStudent = students.get(0);
        assertEquals(foundStudent.getId(), student2.getId());
        assertEquals(foundStudent.getName(), student2.getName());
        assertEquals(foundStudent.getAge(), student2.getAge());

        assertTrue(students.stream()
                .allMatch(student -> student.getAge() >= fromAge && student.getAge() <= toAge));
    }
}