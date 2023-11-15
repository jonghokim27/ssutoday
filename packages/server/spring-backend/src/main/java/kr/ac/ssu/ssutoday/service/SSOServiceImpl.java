/**
 * @filename    : SSOServiceImpl.java
 * @description : SSO Service Implementation
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service;

import kr.ac.ssu.ssutoday.entity.SSOClient;
import kr.ac.ssu.ssutoday.entity.SSOToken;
import kr.ac.ssu.ssutoday.exception.AuthFailedException;
import kr.ac.ssu.ssutoday.provider.TokenProvider;
import kr.ac.ssu.ssutoday.repository.SSOClientRepository;
import kr.ac.ssu.ssutoday.repository.SSOTokenRepository;
import kr.ac.ssu.ssutoday.service.dto.SSOGenerateTokenParamDto;
import kr.ac.ssu.ssutoday.service.dto.SSOGenerateTokenReturnDto;
import kr.ac.ssu.ssutoday.service.dto.SSOValidateTokenParamDto;
import kr.ac.ssu.ssutoday.service.dto.SSOValidateTokenReturnDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SSOServiceImpl implements SSOService {
    /**
     * DI
     */
    private final TokenProvider tokenProvider;
    private final SSOClientRepository ssoClientRepository;
    private final SSOTokenRepository ssoTokenRepository;

    /**
     * Generate Token (for user)
     * @param ssoGenerateTokenParamDto generate params
     * @return generate result (SSOGenerateTokenReturnDto)
     * @throws Exception thrown when SSOClient with clientId does not exist
     * @author jonghokim27
     */
    @NotNull
    @Override
    public SSOGenerateTokenReturnDto ssoGenerateToken(@NotNull SSOGenerateTokenParamDto ssoGenerateTokenParamDto) throws Exception {
        Optional<SSOClient> ssoClientOptional = ssoClientRepository.findById(ssoGenerateTokenParamDto.getClientId());
        if(ssoClientOptional.isEmpty()){
            log.debug("Cannot find ssoClient with id {}", ssoGenerateTokenParamDto.getClientId());
            throw new Exception();
        }

        String ssoToken = tokenProvider.generateRandomHashToken(50);

        SSOToken ssoTokenDB = SSOToken.builder()
                .ssoToken(ssoToken)
                .clientId(ssoClientOptional.get().getId())
                .studentId(ssoGenerateTokenParamDto.getStudent().getId())
                .name(ssoGenerateTokenParamDto.getStudent().getName())
                .major(ssoGenerateTokenParamDto.getStudent().getMajor())
                .build();

        ssoTokenRepository.save(ssoTokenDB);

        return SSOGenerateTokenReturnDto.builder()
                .ssoToken(ssoToken)
                .callbackUrl(ssoClientOptional.get().getCallbackUrl())
                .build();
    }

    /**
     * Validate Token (for server)
     * @param ssoValidateTokenParamDto validate params
     * @return validate result (SSOValidateTokenReturnDto)
     * @throws AuthFailedException thrown when failed to authenticate SSOClient
     * @throws Exception thrown when failed to verify token
     * @author jonghokim27
     */
    @NotNull
    @Override
    public SSOValidateTokenReturnDto ssoValidateToken(@NotNull SSOValidateTokenParamDto ssoValidateTokenParamDto) throws AuthFailedException, Exception {
        Optional<SSOClient> ssoClientOptional = ssoClientRepository.findByIdAndSecret(ssoValidateTokenParamDto.getClientId(), ssoValidateTokenParamDto.getClientSecret());
        if(ssoClientOptional.isEmpty()){
            throw new AuthFailedException();
        }

        Optional<SSOToken> ssoTokenOptional = ssoTokenRepository.findById(ssoValidateTokenParamDto.getSsoToken());
        if(ssoTokenOptional.isEmpty() || !ssoTokenOptional.get().getClientId().equals(ssoClientOptional.get().getId())){
            throw new Exception();
        }

        ssoTokenRepository.delete(ssoTokenOptional.get());

        return SSOValidateTokenReturnDto.builder()
                .studentId(ssoTokenOptional.get().getStudentId())
                .name(ssoTokenOptional.get().getName())
                .major(ssoTokenOptional.get().getMajor())
                .build();
    }
}