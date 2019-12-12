package jp.go.ndl.lab.annotation.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.go.ndl.lab.annotation.Application;
import jp.go.ndl.lab.annotation.domain.ImageType;
import jp.go.ndl.lab.annotation.domain.TargetImage;
import jp.go.ndl.lab.annotation.infra.EsDataStore;
import jp.go.ndl.lab.annotation.service.ThumbService;
import jp.go.ndl.lab.annotation.utils.AbstractBatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component("add-type")
@Slf4j
public class AddType extends AbstractBatch {

    public static void main(String[] args) {
        Application.main(new String[]{"batch", "add-type","ignore\\type.json"});
    }

    @Autowired
    private EsDataStore<ImageType> typeEsDataStore;

    @Override
    public void run(String[] params) {
        try {
            ImageType type  = new ObjectMapper().readValue(new File(params[0]), ImageType.class);
            type = typeEsDataStore.create(type,true);
            log.info("タイプを更新しました：{}",type);
        } catch (IOException e) {
            log.error("多分JSONファイルに間違いがあります。");
        }
    }
}
