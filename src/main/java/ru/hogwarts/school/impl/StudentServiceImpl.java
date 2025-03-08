package ru.hogwarts.school.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.List;


@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    @Override
    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student findStudent(long id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public Student editeStudent(long id, Student student) {
        Student studentFromDb = studentRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);

        studentFromDb.setName(student.getName());
        studentFromDb.setAge(student.getAge());

        return studentRepository.save(studentFromDb);
    }

    @Override
    public void deleteStudent(long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public List<Student> getAllByAge(int age) {
        return studentRepository.findAll()
                .stream()
                .filter(it -> it.getAge() == age)
                .toList();
    }

//    @Override
//    public List<Student> findByAgeBetween(int from, int to) {
//        return studentRepository.findByAgeBetween(from, to);
//    }
}
