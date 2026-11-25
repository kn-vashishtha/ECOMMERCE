package org.bootcamp.ecommerce.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.repository.BlacklistedTokenRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {

    private final BlacklistedTokenRepo blacklistedTokenRepo;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void purgeExpiredTokens() {
        log.info("Running scheduled task: Purging expired blacklisted tokens...");
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        long tokensDeleted = blacklistedTokenRepo.deleteByBlacklistedAtBefore(twentyFourHoursAgo);
        if (tokensDeleted > 0) {
            log.info("Successfully purged {} expired token(s) from the blacklist.", tokensDeleted);
        } else {
            log.info("No expired blacklisted tokens found to purge.");
        }
    }
}