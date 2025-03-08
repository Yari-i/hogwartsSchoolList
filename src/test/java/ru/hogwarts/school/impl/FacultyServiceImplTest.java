package ru.hogwarts.school.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacultyServiceImplTest {

    @Mock
    private FacultyRepository facultyRepository;

    @InjectMocks
    private FacultyServiceImpl facultyService;


    @Test
    void shouldCorrectlyAddFaculty() {
        Faculty faculty = new Faculty();
        faculty.setId(1);
        faculty.setName("Gryffindor");
        faculty.setColor("Green");

        when(facultyRepository.save(faculty)).thenReturn(faculty);

        Faculty savedFaculty = facultyService.addFaculty(faculty);

        assertEquals(faculty, savedFaculty);
    }

    @Test
    void shouldCorrectlyFindFaculty() {
        long id = 1;
        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName("Gryffindor");
        faculty.setColor("Green");

        when(facultyRepository.findById(id)).thenReturn(java.util.Optional.of(faculty));


        Faculty foundFaculty = facultyService.findFaculty(id);


        assertEquals(faculty, foundFaculty);
    }

    @Test
    void shouldCorrectlyEditFaculty() {
        long id = 1;
        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName("Gryffindor");
        faculty.setColor("Green");

        Faculty facultyFromDb = new Faculty();
        facultyFromDb.setId(id);
        facultyFromDb.setName("Old Name");
        facultyFromDb.setColor("Old color");

        when(facultyRepository.findById(id)).thenReturn(Optional.of(facultyFromDb));
        when(facultyRepository.save(facultyFromDb)).thenReturn(faculty);


        Faculty editedFaculty = facultyService.editFaculty(id, faculty);


        assertEquals(faculty.getName(), editedFaculty.getName());
        assertEquals(faculty.getColor(), editedFaculty.getColor());

        verify(facultyRepository).save(facultyFromDb);
    }

    @Test
    void shouldCorrectlyDeleteFaculty() {
        long id = 1;

        facultyService.deleteFaculty(id);

        verify(facultyRepository).deleteById(id);
    }

    @Test
    void shouldCorrectlyGetAllByColor() {
        String color = "Green";
        List<Faculty> allFaculty = new ArrayList<>();
        allFaculty.add(new Faculty(1, "Gryffindor", "Green"));
        allFaculty.add(new Faculty(2, "Slytherin", "Green"));
        allFaculty.add(new Faculty(3, "Hufflepuff", "Orange"));

        when(facultyRepository.findAll()).thenReturn(allFaculty);


        List<Faculty> facultiesByColor = facultyService.getAllByColor(color);


        assertEquals(2, facultiesByColor.size());
        facultiesByColor.forEach(student -> assertEquals(color, student.getColor()));

    }
}