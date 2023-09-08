/**
 * @filename    : AuthService.java
 * @description : Authentication Service
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service;

import kr.ac.ssu.ssutoday.exception.APIRequestFailedException;
import kr.ac.ssu.ssutoday.exception.AuthFailedException;
import kr.ac.ssu.ssutoday.exception.HTMLParseFailedException;
import kr.ac.ssu.ssutoday.exception.UnsupportedMajorException;
import kr.ac.ssu.ssutoday.service.dto.UsaintAuthParamDto;
import kr.ac.ssu.ssutoday.service.dto.UsaintAuthReturnDto;
import org.jetbrains.annotations.NotNull;
public interface AuthService {
    /**
     * Authentication via uSaint
     * @param usaintAuthParamDto auth params
     * @return auth result (UsaintAuthReturnDto)
     * @throws APIRequestFailedException thrown when API request to uSaint SSO or Portal failed
     * @throws AuthFailedException thrown when student authentication failed
     * @author jonghokim27
     */
    @NotNull
    UsaintAuthReturnDto uSaintAuth(@NotNull UsaintAuthParamDto usaintAuthParamDto) throws APIRequestFailedException, AuthFailedException, HTMLParseFailedException, UnsupportedMajorException;
}
