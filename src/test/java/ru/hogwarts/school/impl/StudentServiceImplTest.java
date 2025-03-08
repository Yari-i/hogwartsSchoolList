package ru.hogwarts.school.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.impl.StudentServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    void shouldCorrectlyAddStudent() {

        Student student = new Student();
        student.setId(1);
        student.setName("Harry Potter");
        student.setAge(18);

        when(studentRepository.save(student)).thenReturn(student);

        Student savedStudent = studentService.addStudent(student);

        assertEquals(student, savedStudent);
    }

    @Test
    void shouldCorrectlyFindStudent() {
        long id = 1;
        Student student = new Student();
        student.setId(id);
        student.setName("Harry Potter");
        student.setAge(18);

        when(studentRepository.findById(id)).thenReturn(java.util.Optional.of(student));


        Student foundStudent = studentService.findStudent(id);


        assertEquals(student, foundStudent);
    }

    @Test
    void shouldCorrectlyEditeStudent() {
        long id = 1;
        Student student = new Student();
        student.setId(id);
        student.setName("Harry Potter");
        student.setAge(20);

        Student studentFromDb = new Student();
        studentFromDb.setId(id);
        studentFromDb.setName("Old Name");
        studentFromDb.setAge(22);

        when(studentRepository.findById(id)).thenReturn(Optional.of(studentFromDb));
        when(studentRepository.save(studentFromDb)).thenReturn(student);


        Student editedStudent = studentService.editeStudent(id, student);


        assertEquals(student.getName(), editedStudent.getName());
        assertEquals(student.getAge(), editedStudent.getAge());

        verify(studentRepository).save(studentFromDb);
    }

    @Test
    void shouldCorrectlyDeleteStudent() {
        long id = 1;

        studentService.deleteStudent(id);

        verify(studentRepository).deleteById(id);
    }

    @Test
    void shouldCorrectlyGetAllByAge() {

        int age = 20;
        List<Student> allStudents = new ArrayList<>();
        allStudents.add(new Student(1, "Harry Potter", 20));
        allStudents.add(new Student(2, "Ron Weasley", 22));
        allStudents.add(new Student(3, "Hermione Granger", 20));

        when(studentRepository.findAll()).thenReturn(allStudents);


        List<Student> studentsByAge = studentService.getAllByAge(age);


        assertEquals(2, studentsByAge.size());
        studentsByAge.forEach(student -> assertEquals(age, student.getAge()));
    }

    @Test
    void shouldCorrectlyFindByAgeBetween() {
        int fromAge = 18;
        int toAge = 22;

        Student student1 = new Student();
        student1.setAge(19);

        Student student2 = new Student();
        student2.setAge(22);

        Student student3 = new Student();
        student3.setAge(25);

        List<Student> expectedStudents = Arrays.asList(student1, student2);


        when(studentRepository.findByAgeBetween(fromAge, toAge)).thenReturn(expectedStudents);


        List<Student> actualStudents = studentService.findByAgeBetween(fromAge, toAge);


        assertEquals(expectedStudents, actualStudents);
    }
}