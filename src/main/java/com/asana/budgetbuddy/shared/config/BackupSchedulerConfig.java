package com.asana.budgetbuddy.shared.config;

import com.asana.budgetbuddy.shared.service.BackupService;
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
