package kr.ac.ssu.ssutoday.provider;

import kr.ac.ssu.ssutoday.service.StudentServiceTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;

    Logger log = LoggerFactory.getLogger(StudentServiceTest.class);

    @Test
    public void generateRefreshToken(){
        String refreshToken = tokenProvider.generateRandomHashToken();
        log.info(refreshToken);
    }
}
