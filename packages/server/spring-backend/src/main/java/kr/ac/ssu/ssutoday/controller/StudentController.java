/**
 * @filename    : StudentController.java
 * @description : REST API controller for /student
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.ac.ssu.ssutoday.common.CommonResponse;
import kr.ac.ssu.ssutoday.common.StatusCode;
import kr.ac.ssu.ssutoday.controller.dto.StudentLoginRequestDto;
import kr.ac.ssu.ssutoday.controller.dto.StudentUpdateXnApiTokenRequestDto;
import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.exception.APIRequestFailedException;
import kr.ac.ssu.ssutoday.exception.AuthFailedException;
import kr.ac.ssu.ssutoday.exception.HTMLParseFailedException;
import kr.ac.ssu.ssutoday.exception.UnsupportedMajorException;
import kr.ac.ssu.ssutoday.service.AuthService;
import kr.ac.ssu.ssutoday.service.StudentService;
import kr.ac.ssu.ssutoday.service.dto.StudentLoginReturnDto;
import kr.ac.ssu.ssutoday.service.dto.StudentUpdateXnApiTokenParamDto;
import kr.ac.ssu.ssutoday.service.dto.UsaintAuthReturnDto;
import kr.ac.ssu.ssutoday.util.HTTPRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
@Slf4j
public class StudentController {
    /**
     * DI
     */
    private final StudentService studentService;
    private final AuthService authService;
    private final HTTPRequestUtil httpRequestUtil;
    private final StatusCode statusCode;

    /**
     * Login (01)
     * /student/login
     * @param studentLoginRequestDto login request params
     * @return login result (StudentLoginResponseDto or null)
     * @author jonghokim27
     */
    @Nullable
    @PostMapping("/login")
    @ResponseBody
    public CommonResponse login(@NotNull @Valid @RequestBody StudentLoginRequestDto studentLoginRequestDto){
        UsaintAuthReturnDto usaintAuthReturnDto;
        try {
            usaintAuthReturnDto = authService.uSaintAuth(studentLoginRequestDto.toUsaintAuthParamDto());
        } catch (AuthFailedException e) {
            return new CommonResponse(statusCode.SSU4010, null, statusCode.SSU4010_MSG);
        } catch (APIRequestFailedException | HTMLParseFailedException e) {
            return new CommonResponse(statusCode.SSU5000, null, statusCode.SSU5000_MSG);
        } catch (UnsupportedMajorException e){
            return new CommonResponse(statusCode.SSU4011, null, statusCode.SSU4011_MSG);
        }

        StudentLoginReturnDto studentLoginReturnDto = studentService.studentLogin(usaintAuthReturnDto.toStudentLoginParamDto());

        return new CommonResponse(statusCode.SSU2010, studentLoginReturnDto.toStudentLoginResponseDto(), statusCode.SSU2010_MSG);
    }

    /**
     * Profile (02)
     * /student/profile
     * @return profile result (StudentProfileResponseDto)
     * @author jonghokim27
     */
    @NotNull
    @PostMapping("/profile")
    @ResponseBody
    public CommonResponse profile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) authentication.getPrincipal();

        return new CommonResponse(statusCode.SSU2020, student.toStudentProfileResponseDto(), statusCode.SSU2020_MSG);
    }

    /**
     * Logout (03)
     * /student/logout
     * @return null
     * @author jonghokim27
     */
    @Nullable
    @PostMapping("/logout")
    @ResponseBody
    public CommonResponse logout(@NotNull HttpServletRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) authentication.getPrincipal();

        Optional<String> refreshToken = httpRequestUtil.getRefreshToken(request);
        if(refreshToken.isEmpty()){
            return new CommonResponse(statusCode.SSU4000, null, statusCode.SSU4000_MSG);
        }

        studentService.studentLogout(student.toStudentLogoutParamDto(refreshToken.get()));

        return new CommonResponse(statusCode.SSU2030, null, statusCode.SSU2030_MSG);
    }

    /**
     * Update xnApiToken (19)
     * /student/updateXnApiToken
     * @return null
     * @author jonghokim27
     */
    @Nullable
    @PostMapping("/updateXnApiToken")
    @ResponseBody
    public CommonResponse updateXnApiToken(@NotNull @Valid @RequestBody StudentUpdateXnApiTokenRequestDto studentUpdateXnApiTokenRequestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) authentication.getPrincipal();

        StudentUpdateXnApiTokenParamDto studentUpdateXnApiTokenParamDto = studentUpdateXnApiTokenRequestDto.toStudentUpdateXnApiTokenParamDto(student);
        studentService.updateXnApiToken(studentUpdateXnApiTokenParamDto);

        return new CommonResponse(statusCode.SSU2190, null, statusCode.SSU2190_MSG);
    }



}
