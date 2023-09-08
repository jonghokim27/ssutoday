/**
 * @filename    : APIProvider.java
 * @description : Provider for making API requests
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.provider;

import kr.ac.ssu.ssutoday.provider.dto.APIRequestDto;
import kr.ac.ssu.ssutoday.provider.dto.APIResponseDto;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class APIProvider {

    /**
     * Send a GET request
     * @param apiRequestDto Request url, headers
     * @return Response body, headers (APIResponseDto)
     * @throws Exception thrown when API request failed
     * @author jonghokim27
     */
    @NotNull
    public APIResponseDto get(@NotNull APIRequestDto apiRequestDto) throws Exception {
        Headers requestHeaders = Headers.of(apiRequestDto.getHeaders());

        Request request = new Request.Builder()
                .url(apiRequestDto.getUrl())
                .headers(requestHeaders)
                .get()
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new Exception("Request was not successful.");
        }

        ResponseBody responseBody = response.body();
        Headers responseHeaders = response.headers();

        if (responseBody == null) {
            throw new Exception("Response body is null.");
        }

        String bodyText = responseBody.string();
        responseBody.close();

        return APIResponseDto.builder()
                .body(bodyText)
                .headers(responseHeaders.toMultimap())
                .build();
    }
}
