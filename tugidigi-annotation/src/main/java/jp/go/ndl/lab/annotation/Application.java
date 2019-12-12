package jp.go.ndl.lab.annotation;

import jp.go.ndl.lab.annotation.utils.AbstractBatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@SpringBootApplication
@Slf4j
@EnableScheduling
@EnableAsync
public class Application {

    public static final String MODE_BATCH = "batch";
    public static final String MODE_WEB = "web";

    public static void main(String... args) {
        if (args.length == 0) {
            log.error("起動モードの指定がありません");
            System.exit(-1);
        }

        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.setAdditionalProfiles(args[0]);
        switch (args[0]) {
            case MODE_WEB:
                springApplication.setWebApplicationType(WebApplicationType.SERVLET);
                springApplication.run(args);
                break;
            case MODE_BATCH:
                springApplication.setWebApplicationType(WebApplicationType.NONE);
                springApplication.setLogStartupInfo(false);
                try (ConfigurableApplicationContext ctx = springApplication.run(args)) {
                    try {
                        AbstractBatch batchService = ctx.getBean(args[1], AbstractBatch.class);
                        batchService.run(ArrayUtils.subarray(args, 2, args.length));
                    } catch (NoSuchBeanDefinitionException e) {
                        log.error("{}というバッチは存在しません", args[1]);
                    }
                } catch (Throwable t) {
                    log.error("バッチ実行中に予期せぬエラーが発生しました", t);
                    System.exit(-1);
                }
                System.exit(0);
                break;
            default:
                log.error("第一引数ERROR {}", args[0]);
                System.exit(-1);
        }
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(2);
        return taskScheduler;
    }
}
