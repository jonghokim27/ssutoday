/**
 * @filename    : MaskUtil.java
 * @description : Private information masking Util
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class MaskUtil {

    /**
     * Mask name
     * @param name original name
     * @return masked name
     */
    @NotNull
    public String maskName(@NotNull String name) {
        String middleMask = "";

        // 이름이 외자 또는 4자 이상인 경우 분기
        if(name.length() > 2){
            middleMask = name.substring(1, name.length()-1);
        } else {
            middleMask = name.substring(1, name.length());
        }

        // 마스킹 변수 선언(*)
        String masking = "";

        // 가운데 글자 마스킹 하기위한 증감값
        for(int i = 0; i < middleMask.length(); i++){
            masking += "*";
        }

        // 이름이 외자 또는 4자 이상인 경우 분기
        if(name.length() > 2){
            name = name.substring(0,1)
                    + middleMask.replace(middleMask, masking)
                    + name.substring(name.length()-1, name.length());
        } else {
            name = name.substring(0,1)
                    + middleMask.replace(middleMask, masking);
        }

        return name;
    }

    /**
     * Mask student id
     * @param studentId student id
     * @return masked student id
     */
    @NotNull
    public String maskStudentId(@NotNull Integer studentId){
        String studentIdStr = studentId.toString();
        return studentIdStr.substring(6, 8);
    }
}
