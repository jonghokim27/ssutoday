/**
 * @filename    : StudentService.java
 * @description : Student Service
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service;

import kr.ac.ssu.ssutoday.service.dto.*;
import org.jetbrains.annotations.NotNull;

public interface StudentService {
    /**
     * Student Login
     * @param studentLoginParamDto login params
     * @return login result (StudentLoginReturnDto)
     * @author jonghokim27
     */
    @NotNull
    StudentLoginReturnDto studentLogin(@NotNull StudentLoginParamDto studentLoginParamDto);

    /**
     * Student Logout
     * @param studentLogoutParamDto logout params
     * @author jonghokim27
     */
    void studentLogout(@NotNull StudentLogoutParamDto studentLogoutParamDto);

    /**
     * Validate student
     * @param studentValidateParamDto validate params
     * @return validate result (ValidateStudentReturnDto)
     * @throws Exception thrown when validate student failed
     * @author jonghokim27
     */
    @NotNull
    StudentValidateReturnDto validateStudent(@NotNull StudentValidateParamDto studentValidateParamDto) throws Exception;
}
