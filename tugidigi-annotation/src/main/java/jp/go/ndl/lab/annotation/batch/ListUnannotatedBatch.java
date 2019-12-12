package jp.go.ndl.lab.annotation.batch;

import jp.go.ndl.lab.annotation.Application;
import jp.go.ndl.lab.annotation.domain.ImageBinder;
import jp.go.ndl.lab.annotation.domain.Status;
import jp.go.ndl.lab.annotation.domain.TargetImage;
import jp.go.ndl.lab.annotation.infra.EsDataStore;
import jp.go.ndl.lab.annotation.utils.AbstractBatch;
import jp.go.ndl.lab.annotation.utils.RWUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;


@Slf4j
@Component("list-unfinished")
public class ListUnannotatedBatch extends AbstractBatch {

    public static void main(String[] args) {
        Application.main("batch", "list-unfinished", "out.txt");
    }


    @Autowired
    private EsDataStore<TargetImage> imageEsDataStore;
    @Autowired
    private EsDataStore<ImageBinder> binderEsDataStore;

    @Override
    public void run(String[] params) {
        log.info("write to {}", params[0]);
        try (RWUtils.Writer writer = RWUtils.writer(Paths.get(params[0]))) {
            binderEsDataStore.scroll(QueryBuilders.matchAllQuery(), b -> {
                if (b.status != Status.CHECKED && StringUtils.isEmpty(b.holder)) {
                    try {
                        writer.writeLine(b.id);
                    } catch (IOException e) {
                        log.error("", e);
                    }
                }
            });
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
