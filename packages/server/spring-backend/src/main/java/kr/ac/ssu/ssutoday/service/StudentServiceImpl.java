/**
 * @filename    : StudentServiceImpl.java
 * @description : Student Service Implementation
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service;

import io.jsonwebtoken.ExpiredJwtException;
import kr.ac.ssu.ssutoday.entity.RefreshToken;
import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.provider.TokenProvider;
import kr.ac.ssu.ssutoday.repository.RefreshTokenRepository;
import kr.ac.ssu.ssutoday.repository.StudentRepository;
import kr.ac.ssu.ssutoday.service.dto.*;
import kr.ac.ssu.ssutoday.vo.JWTPayloadVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {
    /**
     * DI
     */
    private final StudentRepository studentRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    /**
     * Student Login
     * @param studentLoginParamDto login params
     * @return login result (StudentLoginReturnDto)
     * @author jonghokim27
     */
    @NotNull
    @Override
    public StudentLoginReturnDto studentLogin(@NotNull StudentLoginParamDto studentLoginParamDto){
        Optional<Student> studentOptional = studentRepository.findById(studentLoginParamDto.getId());
        Student student;

        if(studentOptional.isEmpty()){
            student = studentLoginParamDto.toStudent();
        }
        else{
            student = studentOptional.get();
            student.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        }
        studentRepository.save(student);

        String accessToken = tokenProvider.generateAccessToken(studentLoginParamDto.toJWTPayloadVO());
        String refreshToken = tokenProvider.generateRandomHashToken();

        RefreshToken refreshTokenDB = RefreshToken.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .studentId(student.getId())
                .name(student.getName())
                .major(student.getMajor())
                .build();
        refreshTokenRepository.save(refreshTokenDB);

        return StudentLoginReturnDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .studentId(student.getId())
                .name(student.getName())
                .major(student.getMajor())
                .build();
    }

    /**
     * Student Logout
     * @param studentLogoutParamDto logout params
     * @author jonghokim27
     */
    @Override
    public void studentLogout(@NotNull StudentLogoutParamDto studentLogoutParamDto){
        refreshTokenRepository.deleteById(studentLogoutParamDto.getRefreshToken());
    }

    /**
     * Validate student
     * @param studentValidateParamDto validate params
     * @return validate result (ValidateStudentReturnDto)
     * @throws Exception thrown when validate student failed
     * @author jonghokim27
     */
    @NotNull
    @Override
    public StudentValidateReturnDto validateStudent(@NotNull StudentValidateParamDto studentValidateParamDto) throws Exception {
        JWTPayloadVo jwtPayloadVO;
        try {
            jwtPayloadVO = tokenProvider.validateAccessToken(studentValidateParamDto.getAccessToken());
        } catch(BadCredentialsException e){
            log.warn(e.getMessage(), e);
            throw new Exception("Invalid access token.");
        } catch(ExpiredJwtException e){
            log.debug(e.getMessage(), e);

            Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findById(studentValidateParamDto.getRefreshToken());
            if(refreshTokenOptional.isEmpty()){
                throw new Exception("Refresh token does not exist.");
            }

            RefreshToken refreshToken = refreshTokenOptional.get();
            if(!refreshToken.getAccessToken().equals(studentValidateParamDto.getAccessToken())){
                throw new Exception("Access token and refresh token does not match.");
            }

            Optional<Student> studentOptional = studentRepository.findById(refreshToken.getStudentId());
            if(studentOptional.isEmpty()){
                throw new Exception("User does not exist.");
            }

            Student student = studentOptional.get();
            String newAccessToken = tokenProvider.generateAccessToken(student.toJWTPayloadVO());
            String newRefreshToken = tokenProvider.generateRandomHashToken();

            RefreshToken newRefreshTokenDB = RefreshToken.builder()
                    .refreshToken(newRefreshToken)
                    .accessToken(newAccessToken)
                    .studentId(student.getId())
                    .name(student.getName())
                    .major(student.getMajor())
                    .build();

            refreshTokenRepository.save(newRefreshTokenDB);
            refreshTokenRepository.delete(refreshToken);

            return StudentValidateReturnDto.builder()
                    .student(student)
                    .accessToken(Optional.of(newAccessToken))
                    .refreshToken(Optional.ofNullable(newRefreshToken))
                    .build();
        }

        Optional<Student> student = studentRepository.findById(jwtPayloadVO.getStudentId());
        if(student.isEmpty()){
            throw new Exception("User does not exist.");
        }

        return StudentValidateReturnDto.builder()
                .student(student.get())
                .accessToken(Optional.empty())
                .refreshToken(Optional.empty())
                .build();
    }
}
