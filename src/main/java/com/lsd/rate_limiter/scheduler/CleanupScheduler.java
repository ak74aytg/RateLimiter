package com.lsd.rate_limiter.scheduler;

import com.lsd.rate_limiter.models.Requests;
import com.lsd.rate_limiter.models.UserRequestState;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

@Component
public class CleanupScheduler {
    private final Requests model;

    public CleanupScheduler(Requests model) {
        this.model = model;
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void runPeriodicTask() {
        long milliseconds = System.currentTimeMillis();
        Iterator<Map.Entry<String, UserRequestState>> iterator = model.getUsers().entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, UserRequestState> entry = iterator.next();

            if (entry.getValue().getLastSeenAt() == milliseconds - 5 * 60 * 1000) {
                iterator.remove();
            }
        }
    }
}
