/**
 * @filename    : LMSService.java
 * @description : LMS Service
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service;

public interface LMSService {
    /**
     * Send LMS notification (scheduled job)
     * @author jonghokim27
     */
    void lmsNotificationSend();
}
