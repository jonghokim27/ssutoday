package kr.ac.ssu.ssutoday.service;

import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.repository.StudentRepository;
import kr.ac.ssu.ssutoday.service.dto.StudentLoginParamDto;
import kr.ac.ssu.ssutoday.service.dto.StudentLoginReturnDto;
import kr.ac.ssu.ssutoday.service.dto.StudentLogoutParamDto;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class StudentServiceTest {
    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentRepository studentRepository;

    Logger log = LoggerFactory.getLogger(StudentServiceTest.class);

    @Test
    public void userLogin(){
        StudentLoginParamDto studentLoginParamDto = StudentLoginParamDto.builder()
                        .id(20221488)
                        .name("김종호")
                        .major("cse")
                        .build();

        StudentLoginReturnDto studentLoginReturnDto = studentService.studentLogin(studentLoginParamDto);
        log.info(studentLoginReturnDto.getAccessToken());
        log.info(studentLoginReturnDto.getRefreshToken());
    }

    @Test
    public void userLogout(){
        Optional<Student> studentOptional = studentRepository.findById(20221488);
        StudentLogoutParamDto studentLogoutParamDto = StudentLogoutParamDto.builder()
                .student(studentOptional.get())
                .refreshToken("267097887c24d78cfc82d8424e22621da0c97ab10b5a9b9ebf2140144da9371f9c582acb7fff2be515d23f8b2bd8425bb3a7799b32b29b7de4be111800c716b7")
                .build();

        studentService.studentLogout(studentLogoutParamDto);
    }
}
