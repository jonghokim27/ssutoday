package kr.ac.ssu.ssutoday.service;

import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.repository.StudentRepository;
import kr.ac.ssu.ssutoday.service.dto.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class DeviceServiceTest {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private StudentRepository studentRepository;

    Logger log = LoggerFactory.getLogger(StudentServiceTest.class);

    @Test
    public void registerDevice(){
        Student student = studentRepository.findById(20221488).get();
        DeviceRegisterParamDto deviceRegisterParamDto = DeviceRegisterParamDto.builder()
                .osType("iOS")
                .uuid("abcd-abcd")
                .pushToken("asdfasdfasdfsdaf")
                .student(student)
                .build();

        deviceService.deviceRegister(deviceRegisterParamDto);
    }

    @Test
    public void unregisterDevice() throws Exception {
        Student student = studentRepository.findById(20221488).get();
        DeviceUnregisterParamDto deviceUnregisterParamDto = DeviceUnregisterParamDto.builder()
                .osType("iOS")
                .uuid("abcd-abcd")
                .student(student)
                .build();

        deviceService.deviceUnregister(deviceUnregisterParamDto);
    }

}
