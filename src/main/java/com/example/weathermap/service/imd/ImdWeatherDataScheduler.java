package com.example.weathermap.service.imd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "weather.imd.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class ImdWeatherDataScheduler {

    private static final Logger log = LoggerFactory.getLogger(ImdWeatherDataScheduler.class);

    private final ImdWeatherDataRefreshService refreshService;
    private final DailyDataFreshnessChecker dailyFreshnessChecker;

    public ImdWeatherDataScheduler(
            ImdWeatherDataRefreshService refreshService,
            DailyDataFreshnessChecker dailyFreshnessChecker
    ) {
        this.refreshService = refreshService;
        this.dailyFreshnessChecker = dailyFreshnessChecker;
    }

    @Scheduled(cron = "${weather.imd.scheduler.nowcast-cron:0 0 0/3 * * *}")
    public void scheduledNowcastRefresh() {
        log.info("Nowcast scheduler triggered");
        refreshService.refreshNowcastFromImd();
    }

    @Scheduled(cron = "${weather.imd.scheduler.daily-cron:0 0 9 * * *}")
    public void scheduledDailyRefresh() {
        log.info("Daily weather scheduler triggered (9 AM)");
        dailyFreshnessChecker.onDailyScheduleTrigger();
    }
}
