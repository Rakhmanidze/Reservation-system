package cz.cvut.fel.ear.semestralka.utill;

import cz.cvut.fel.ear.semestralka.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MembershipStatusScheduler {
    private static final Logger logger = LoggerFactory.getLogger(MembershipStatusScheduler.class);
    private MembershipService membershipService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void updateMembershipStatuses() {
        logger.info("Starting membership status update");
        membershipService.updateExpiredMemberships();
        logger.info("Membership statuses updated");
    }
}
