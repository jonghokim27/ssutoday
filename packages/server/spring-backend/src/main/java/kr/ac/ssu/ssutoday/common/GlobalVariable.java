/**
 * @filename    : GlobalVariable.java
 * @description : Project global variables definition
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.common;

import org.springframework.stereotype.Component;

@Component
public class GlobalVariable {
    /**
     * API URLs for Student authentication
     */
    public final String uSaintSSOUrl = "https://saint.ssu.ac.kr/webSSO/sso.jsp";
    public final String uSaintPortalUrl = "https://saint.ssu.ac.kr/webSSUMain/main_student.jsp";
}
