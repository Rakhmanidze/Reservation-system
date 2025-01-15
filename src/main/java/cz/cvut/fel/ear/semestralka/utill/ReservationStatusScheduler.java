package cz.cvut.fel.ear.semestralka.utill;

import cz.cvut.fel.ear.semestralka.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class ReservationStatusScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ReservationStatusScheduler.class);
    private final ReservationService reservationStatusUpdateService;

    @Scheduled(cron = "0 * * * * ?")
    public void updateReservationStatuses() {
        logger.info("Starting reservation status update");
        reservationStatusUpdateService.updateExpiredReservations();
        logger.info("Updated reservation statuses");
    }
}
