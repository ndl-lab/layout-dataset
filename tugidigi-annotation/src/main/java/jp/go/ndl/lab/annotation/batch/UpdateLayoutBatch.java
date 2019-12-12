package jp.go.ndl.lab.annotation.batch;


import jp.go.ndl.lab.annotation.Application;
import jp.go.ndl.lab.annotation.domain.*;
import jp.go.ndl.lab.annotation.infra.EsDataStore;
import jp.go.ndl.lab.annotation.service.ThumbService;
import jp.go.ndl.lab.annotation.utils.AbstractBatch;
import jp.go.ndl.lab.annotation.utils.PascalVoxToAnnotationObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component("update-layout")
public class UpdateLayoutBatch extends AbstractBatch {

    public static void main(String[] args) {
        Application.main("batch", "update-layout", "ignore\\input\\2544473\\xml");
    }


    @Autowired
    private EsDataStore<TargetImage> imageEsDataStore;
    @Autowired
    private EsDataStore<ImageType> annotationTypeEsDataStore;
    @Autowired
    private ThumbService thumbService;

    @Override
    public void run(String[] params) {
        File xmls = new File(params[0]);
        for (File xml : xmls.listFiles()) {
            TargetImage image = imageEsDataStore.get(xml.getName().replace(".xml", ".jpg"));
            if (image != null) {
                if (image.status != Status.ANNOTATED) {
                    image.annotations = PascalVoxToAnnotationObject.convert(xml);
                    imageEsDataStore.update(image,false);
                    ImageType type = annotationTypeEsDataStore.get(image.imageType);
                    try {
                        thumbService.createThumbnail(type, image);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                    log.info("{}は更新されました。", image.id);
                } else {
                    log.info("{}は人手で修正済みです。", image.id);
                }
            }else{
                log.info("{}は未登録です。", xml);
            }
        }
    }

}
