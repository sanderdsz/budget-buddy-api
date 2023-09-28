package com.asana.budgetbuddy.config;

import com.asana.budgetbuddy.service.BackupService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

//@Configuration
//@EnableScheduling
public class BackupSchedulerConfig {

    private final BackupService backupService;

    public BackupSchedulerConfig(BackupService backupService) {
        this.backupService = backupService;
    }

    @Scheduled(cron = "0 13 14 * * ?")
    public void scheduleBackup() {
        backupService.executeBackup();
    }
}
