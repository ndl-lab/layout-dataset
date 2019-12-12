package jp.go.ndl.lab.annotation.infra;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import jp.go.ndl.lab.annotation.Application;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BatchRunner {

    private String[] batchCommand;

    public BatchRunner(@Value("${command}") String convertCommand) {
        this.batchCommand = convertCommand.split("\\s");
    }

    public void run(String batchName, String... parameters) {
        try {
            log.info("run {} {}", batchName, parameters);
            File convertLog = FileUtils.getFile("log", "batch", batchName + "-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".log");
            String[] command = ArrayUtils.addAll(batchCommand, Application.MODE_BATCH, batchName);
            command = ArrayUtils.addAll(command, parameters);
            new ProcessBuilder(command)
                    .redirectError(ProcessBuilder.Redirect.to(convertLog))
                    .redirectOutput(ProcessBuilder.Redirect.to(convertLog))
                    .start();
        } catch (IOException ex) {
            log.error("予期せぬIOエラー", ex);
        }
    }

}
