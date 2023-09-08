/**
 * @filename    : ScheduledJob.java
 * @description : Scheduled jobs configuration
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.common;

import kr.ac.ssu.ssutoday.service.ReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledJob {
    private final ReserveService reserveService;

    @Scheduled(cron="0 25 * * * *", zone="Asia/Seoul")
    public void reserveNotificationSend25(){
        reserveService.reserveNotificationSend();
    }

    @Scheduled(cron="0 55 * * * *", zone="Asia/Seoul")
    public void reserveNotificationSend55(){
        reserveService.reserveNotificationSend();
    }
}
