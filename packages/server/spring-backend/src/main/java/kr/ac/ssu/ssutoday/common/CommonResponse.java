/**
 * @filename    : CommonResponse.java
 * @description : Common REST API response template for controllers
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.common;

import lombok.Data;

@Data
public class CommonResponse {
    private String statusCode;
    private Object data;
    private String message;

    public CommonResponse(String statusCode, Object data, String message){
        this.statusCode = statusCode;
        this.data = data;
        this.message = message;
    }
}