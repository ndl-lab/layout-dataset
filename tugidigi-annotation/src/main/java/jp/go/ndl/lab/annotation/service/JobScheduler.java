package jp.go.ndl.lab.annotation.service;

import jp.go.ndl.lab.annotation.Application;
import jp.go.ndl.lab.annotation.batch.BackupBatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Profile({Application.MODE_WEB})
@Component
public class JobScheduler {

    public JobScheduler() {
        log.info("init job scheduler");
    }

    @Autowired
    private BackupBatch backupBatch;

    @Scheduled(cron = "0 0 1 * * *")
    public void run() {
        log.info("start backup");
        backupBatch.run(new String[0]);
        log.info("done");
    }
}
