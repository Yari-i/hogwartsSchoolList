package ru.hogwarts.school.impl;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.HashMap;
import java.util.List;


@Service
public class StudentServiceImpl implements StudentService {

    private final HashMap<Long, Student> students = new HashMap<>();
    private long idCount = 0;

    @Override
    public Student addStudent(Student student) {
        student.setId(idCount++);
        students.put(student.getId(), student);
        return student;
    }

    @Override
    public Student findStudent(long id) {
        return students.get(id);
    }

    @Override
    public Student editeStudent(long id, Student student) {
        if (!students.containsKey(id)) {
            return null;
        }
        students.put(id, student);
        return student;
    }

    @Override
    public void deleteStudent(long id) {
        students.remove(id);
    }

    @Override
    public List<Student> getAllByAge(int age) {
        return students.values()
                .stream()
                .filter(it -> it.getAge() == age)
                .toList();
    }


}
