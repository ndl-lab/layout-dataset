package jp.go.ndl.lab.annotation.utils;

import java.io.File;
import java.util.UUID;
import org.apache.commons.io.FileUtils;

public class TempUtils {

    public static File createTempFile() {
        File tempDir = FileUtils.getFile("temp");
        tempDir.mkdirs();
        return FileUtils.getFile(tempDir, UUID.randomUUID().toString());
    }

}
