package jp.go.ndl.lab.annotation.service;

import java.io.IOException;
import java.nio.file.Path;
import jp.go.ndl.lab.annotation.domain.TargetImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PathService {

    public Path thumbPath;

    public PathService(
            @Value("${thumbPath}") Resource thumbPath
    ) {
        try {
            this.thumbPath = thumbPath.getFile().toPath();
        } catch (IOException ex) {
            log.error("予期せぬエラー", ex);
        }
    }

    public Path thumbFile(TargetImage image) {
        return this.thumbPath.resolve(image.binder).resolve(image.id);
    }


}
