package com.asana.budgetbuddy.shared.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BackupService {

    @Value("${MYSQL_HOST}")
    private String databaseHost;

    @Value("${MYSQL_PORT}")
    private String databasePort;

    @Value("${MYSQL_DATABASE}")
    private String databaseName;

    @Value("${MYSQL_USERNAME}")
    private String databaseUsername;

    @Value("${MYSQL_PASSWORD}")
    private String databasePassword;

    public void executeBackup() {
        String backupFileName = generateBackupFileName();

        String command = String.format(
                "mysqldump -h %s -P %s -u %s -p %s %s > %s",
                databaseHost,
                databasePort,
                databaseUsername,
                databasePassword,
                databaseName,
                backupFileName
        );

        try {
            String os = System.getProperty("os.name").toLowerCase();
            Process process;
            int exitCode;
            if (os.contains("win")) {
                process = new ProcessBuilder(
                        "cmd",
                        "/c",
                        "cd \"Program Files\\MySQL\\MySQL Server 8.0\\bin\"",
                        command
                ).start();
                exitCode = process.waitFor();
            } else {
                process = new ProcessBuilder("bash", "-c", command).start();
                exitCode = process.waitFor();
            }
            if (exitCode == 0) {
                System.out.println("Backup completed successfully.");
            } else {
                System.err.println("Backup failed.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String generateBackupFileName() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "backup_" + now.format(formatter) + ".sql";
    }
}
