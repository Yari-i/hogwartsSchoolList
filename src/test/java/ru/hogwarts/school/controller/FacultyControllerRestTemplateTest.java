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
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    @BeforeEach
    public void clearDB() {
        facultyRepository.deleteAll();
    }

    @Test
    void shouldCorrectlyGetFacultyInfo() {
        //given
        Faculty faculty = new Faculty("Gryffindor", "orange");
        faculty = facultyRepository.save(faculty);

        //when
        ResponseEntity<Faculty> facultyResponseEntity = restTemplate.getForEntity("http://localhost:" + port + "/faculty/" +
                        faculty.getId(),
                Faculty.class
        );

        //then
        assertNotNull(facultyResponseEntity);
        assertEquals(facultyResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));

        Faculty actualFaculty = facultyResponseEntity.getBody();
        assertEquals(actualFaculty.getId(), faculty.getId());
        assertEquals(actualFaculty.getName(), faculty.getName());
        assertEquals(actualFaculty.getColor(), faculty.getColor());

    }

    @Test
    void shouldCorrectlyCreateFaculty() {

        //give
        Faculty faculty = new Faculty("Gryffindor", "orange");

        //when
        ResponseEntity<Faculty> facultyResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/faculty",
                faculty,
                Faculty.class
        );

        //then
        assertNotNull(facultyResponseEntity);
        assertEquals(facultyResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));

        Faculty actualFaculty = facultyResponseEntity.getBody();
        assertNotNull(actualFaculty.getId());
        assertEquals(actualFaculty.getName(), faculty.getName());
        org.assertj.core.api.Assertions.assertThat(actualFaculty.getColor()).isEqualTo(faculty.getColor());
    }

    @Test
    void shouldCorrectlyEditFaculty() {
        // given
        Faculty faculty = new Faculty("name", "color");
        faculty = facultyRepository.save(faculty);

        Faculty facultyForUpdate = new Faculty("newName", "newColor");

        // when
        HttpEntity<Faculty> entity = new HttpEntity<>(facultyForUpdate);
        ResponseEntity<Faculty> facultyResponseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/" + faculty.getId(),
                HttpMethod.PUT,
                entity,
                Faculty.class
        );

        // then
        assertNotNull(facultyResponseEntity);
        assertEquals(facultyResponseEntity.getStatusCode(), HttpStatusCode.valueOf(200));

        Faculty actualFaculty = facultyResponseEntity.getBody();
        assertEquals(actualFaculty.getId(), faculty.getId());
        assertEquals(actualFaculty.getName(), facultyForUpdate.getName());
        assertEquals(actualFaculty.getColor(), facultyForUpdate.getColor());


    }

    @Test
    void shouldCorrectlyDeleteFaculty() {
        // given
        Faculty faculty = new Faculty("name", "color");
        faculty = facultyRepository.save(faculty);

        // when
        ResponseEntity<Void> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/" + faculty.getId(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );

        // then
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(200));
        Optional<Faculty> deletedFaculty = facultyRepository.findById(faculty.getId());
        assertTrue(deletedFaculty.isEmpty());
    }

    @Test
    void shouldCorrectlyGetAllFacultiesByColor() {
        // given
        Faculty faculty1 = new Faculty("name1", "red");
        Faculty faculty2 = new Faculty("name2", "green");
        Faculty faculty3 = new Faculty("name3", "red");

        faculty1 = facultyRepository.save(faculty1);
        faculty2 = facultyRepository.save(faculty2);
        faculty3 = facultyRepository.save(faculty3);

        String color = "red";

        // when
        ResponseEntity<List<Faculty>> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/faculty?color=" + color,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<Faculty>>() {
                }
        );

        // then
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(200));

        List<Faculty> faculties = responseEntity.getBody();
        assertNotNull(faculties);
        assertEquals(faculties.size(), 2);

        assertTrue(faculties.stream()
                .allMatch(faculty -> faculty.getColor().equals(color)));
    }

    @Test
    void shouldFindFacultiesByNameOrColorIgnoreCase() {
        // given
        Faculty faculty1 = new Faculty("Gryffindor", "Red");
        Faculty faculty2 = new Faculty("Slytherin", "green");
        Faculty faculty3 = new Faculty("Hufflepuff", "Yellow");

        faculty1 = facultyRepository.save(faculty1);
        faculty2 = facultyRepository.save(faculty2);
        faculty3 = facultyRepository.save(faculty3);

        String query = "gReeN";

        // when
        ResponseEntity<List<Faculty>> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/findByNameIgnoreCaseOrIgnoreColorCase?query=" + query,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<Faculty>>() {
                }
        );

        // then
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(200));

        List<Faculty> faculties = responseEntity.getBody();
        assertNotNull(faculties);
        assertEquals(faculties.size(), 1);

        Faculty foundFaculty = faculties.get(0);
        assertEquals(foundFaculty.getId(), faculty2.getId());
        assertEquals(foundFaculty.getName(), faculty2.getName());
        assertEquals(foundFaculty.getColor(), faculty2.getColor());
    }
}