package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Student;

import java.util.List;

public interface StudentService {
    Student addStudent(Student student);

    Student findStudent(long id);

    Student editeStudent(long id, Student student);

    void deleteStudent(long id);

    List<Student> getAllByAge(int age);
}
