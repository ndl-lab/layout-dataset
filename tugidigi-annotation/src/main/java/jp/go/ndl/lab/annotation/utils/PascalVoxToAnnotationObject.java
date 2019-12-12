package jp.go.ndl.lab.annotation.utils;

import jp.go.ndl.lab.annotation.domain.AnnotationObject;
import jp.go.ndl.lab.annotation.domain.Bndbox;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class PascalVoxToAnnotationObject {

public static List<AnnotationObject> convert(File xml){
    //Annotationの追加
    List<AnnotationObject> annotations = new ArrayList<>();
    try {
        Document d = Jsoup.parse(xml, "UTF-8");
        for (Element o : d.getElementsByTag("object")) {
            AnnotationObject ao = new AnnotationObject();
            ao.id = UUID.randomUUID().toString();
            ao.name = o.getElementsByTag("name").first().text();
            Element bnd = o.getElementsByTag("bndbox").first();
            ao.bndbox = new Bndbox(NumberUtils.toInt(bnd.getElementsByTag("xmin").first().text()), NumberUtils.toInt(bnd.getElementsByTag("ymin").first().text()), NumberUtils.toInt(bnd.getElementsByTag("xmax").first().text()), NumberUtils.toInt(bnd.getElementsByTag("ymax").first().text()));
            annotations.add(ao);
        }
    } catch (IOException ex) {
        log.error("XML parse error", ex);
    }
    return annotations;
}

}
