/**
 * @filename    : ScheduledJob.java
 * @description : Scheduled jobs configuration
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.common;

import kr.ac.ssu.ssutoday.service.LMSService;
import kr.ac.ssu.ssutoday.service.ReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledJob {
    private final ReserveService reserveService;
    private final LMSService lmsService;

    @Scheduled(cron="0 25 * * * *", zone="Asia/Seoul")
    public void reserveNotificationSend25(){
        reserveService.reserveNotificationSend();
    }

    @Scheduled(cron="0 55 * * * *", zone="Asia/Seoul")
    public void reserveNotificationSend55(){
        reserveService.reserveNotificationSend();
    }

    @Scheduled(cron="10 0 * * * *", zone="Asia/Seoul")
    public void verifyPhotoNotificationSend0(){ reserveService.verifyPhotoNotificationSend(); }

    @Scheduled(cron="10 30 * * * *", zone="Asia/Seoul")
    public void verifyPhotoNotificationSend30(){
        reserveService.verifyPhotoNotificationSend();
    }

    @Scheduled(cron="0 0 9 * * *", zone="Asia/Seoul")
    public void lmsNotificationSend(){
        lmsService.lmsNotificationSend();
    }
}
