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
    public final String canvasTermsAPIUrl = "https://canvas.ssu.ac.kr/learningx/api/v1/users/1/terms?include_invited_course_contained=true";
    public final String canvasCoursesAPIUrl = "https://canvas.ssu.ac.kr/learningx/api/v1/learn_activities/courses?term_ids[]=";
    public final String canvasToDosAPIUrl = "https://canvas.ssu.ac.kr/learningx/api/v1/learn_activities/to_dos?term_ids[]=";
    public final String publicS3Url = "https://r2.ssu.today/";
}
