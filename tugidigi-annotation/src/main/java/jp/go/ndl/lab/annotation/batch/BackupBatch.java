/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.go.ndl.lab.annotation.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.go.ndl.lab.annotation.Application;
import jp.go.ndl.lab.annotation.domain.ImageBinder;
import jp.go.ndl.lab.annotation.domain.TargetImage;
import jp.go.ndl.lab.annotation.infra.EsDataStore;
import jp.go.ndl.lab.annotation.utils.AbstractBatch;
import jp.go.ndl.lab.annotation.utils.RWUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component("backup")
public class BackupBatch extends AbstractBatch {

    public static void main(String[] args) {
        Application.main(Application.MODE_BATCH, "backup");
    }

    @Autowired
    private EsDataStore<TargetImage> imageEsDataStore;
    @Autowired
    private EsDataStore<ImageBinder> binderEsDataStore;

    @Override
    public void run(String[] params) {
        String today =  DateTimeFormatter.ofPattern("yyyyMMdd").format(ZonedDateTime.now());

        ObjectMapper om = new ObjectMapper();
        try (RWUtils.Writer writer = RWUtils.writer(Paths.get("backup", today, "image.jsonl"))) {
            imageEsDataStore.scroll(QueryBuilders.matchAllQuery(), b -> {
                try {
                    writer.writeLine(om.writeValueAsString(b));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception e) {
            log.error("", e);
        }
        try (RWUtils.Writer writer = RWUtils.writer(Paths.get("backup", today,"binder.jsonl"))) {
            binderEsDataStore.scroll(QueryBuilders.matchAllQuery(), b -> {
                try {
                    writer.writeLine(om.writeValueAsString(b));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
