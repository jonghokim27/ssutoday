/**
 * @filename    : SSOService.java
 * @description : SSO Service
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service;

import kr.ac.ssu.ssutoday.service.dto.*;
import kr.ac.ssu.ssutoday.exception.AuthFailedException;
import org.jetbrains.annotations.NotNull;

public interface SSOService {
    /**
     * Generate Token (for user)
     * @param ssoGenerateTokenParamDto generate params
     * @return generate result (SSOGenerateTokenReturnDto)
     * @throws Exception thrown when SSOClient with clientId does not exist
     * @author jonghokim27
     */
    @NotNull
    SSOGenerateTokenReturnDto ssoGenerateToken(@NotNull SSOGenerateTokenParamDto ssoGenerateTokenParamDto) throws Exception;

    /**
     * Validate Token (for server)
     * @param ssoValidateTokenParamDto validate params
     * @return validate result (SSOValidateTokenReturnDto)
     * @throws AuthFailedException thrown when failed to authenticate SSOClient
     * @throws Exception thrown when failed to verify token
     * @author jonghokim27
     */
    @NotNull
    SSOValidateTokenReturnDto ssoValidateToken(@NotNull SSOValidateTokenParamDto ssoValidateTokenParamDto) throws AuthFailedException, Exception;
}
