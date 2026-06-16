package com.lsd.rate_limiter.scheduler;

import com.lsd.rate_limiter.models.Requests;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class CleanupScheduler {
    private final Requests model;

    public CleanupScheduler(Requests model) {
        this.model = model;
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void runPeriodicTask() {
        long fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000);

        model.getUsers().entrySet().removeIf(entry ->
                entry.getValue().getLastSeenAt() < fiveMinutesAgo
        );
    }
}
