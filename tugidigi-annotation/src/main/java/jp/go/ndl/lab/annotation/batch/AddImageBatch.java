package jp.go.ndl.lab.annotation.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.go.ndl.lab.annotation.Application;
import jp.go.ndl.lab.annotation.domain.*;
import jp.go.ndl.lab.annotation.infra.EsDataStore;
import jp.go.ndl.lab.annotation.service.ThumbService;
import jp.go.ndl.lab.annotation.utils.AbstractBatch;
import jp.go.ndl.lab.annotation.utils.PascalVoxToAnnotationObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;

@Component("add-image")
@Slf4j
public class AddImageBatch extends AbstractBatch {

    public static void main(String[] args) {
        Application.main(new String[]{"batch", "add-image", "kotenseki", "ignore\\input", "true"});
    }


    @Autowired
    private ThumbService ts;

    @Autowired
    private EsDataStore<ImageBinder> binderEsDataStore;
    @Autowired
    private EsDataStore<TargetImage> imageEsDataStore;
    @Autowired
    private EsDataStore<ImageType> typeEsDataStore;

    @Autowired
    private ThumbService thumbService;

    @Override
    public void run(String[] params) {
        ImageType type = typeEsDataStore.get(params[0]);
        if (type == null) {
            log.error("タイプ:{}は存在しません", params[1]);
            return;
        }
        File baseDir = new File(params[1]);

        boolean forceUpdate = params.length >= 3 && Boolean.parseBoolean(params[2]);
        ObjectMapper om = new ObjectMapper();


        for (File dir : baseDir.listFiles()) {
            log.info("start {}", dir);
            try {
                ImageBinder binder = om.readValue(FileUtils.getFile(dir, "binder.json"), ImageBinder.class);
                if (StringUtils.isBlank(binder.id)) {
                    log.error("{}のbinder.jsonにIDがありません", dir);
                    continue;
                }
                ImageBinder current = binderEsDataStore.get(binder.id);
                if (current != null && !forceUpdate) {
                    log.info("ID:{}は登録済みです", current.id);
                }
                log.info("{}",binder);

                binder.imageType = type.id;
                binder.status = Status.WORKING;

                int count = 0;
                for (File image : binder.toPath().toFile().listFiles()) {
                    count++;
                    TargetImage img = new TargetImage();
                    img.id = img.name = image.getName();
                    img.binder = binder.id;
                    img.imageType = type.id;
                    img.imagePath = image.getPath();

                    File xml = FileUtils.getFile(dir, "xml", image.getName().replace(".jpg", ".xml"));
                    if (xml.exists()) {
                        //Annotationの追加
                        img.annotations = PascalVoxToAnnotationObject.convert(xml);
                        img.status = Status.ESTIMATED;
                    } else {
                        log.info("{}についてXMLが見つかりません", image);
                        img.status = Status.CREATED;
                    }
                    Dimension d = thumbService.createThumbnail(type, img);
                    img.size = new ImageSize(d.width, d.height, 3);
                    imageEsDataStore.create(img, false);
                }
                binder.images = count;

                binderEsDataStore.create(binder, false);
            } catch (Exception e) {
                log.error("JSONファイルが不正です@" + dir, e);
            }
        }
    }
}
