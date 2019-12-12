package jp.go.ndl.lab.annotation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jp.go.ndl.lab.annotation.infra.ESData;
import lombok.Data;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TargetImage implements ESData {

    public Long version;
    public String id;
    public String binder;
    public String name;
    public String imageType;
    public List<String> tags;
    public ImageSize size = new ImageSize();
    public String annotator;

    public String imagePath;

    public Path toPath(){
        return Paths.get(imagePath);
    }

    public String note;

    public double bwThreshold = 0;

    public Status status;

    public List<AnnotationObject> annotations = new ArrayList<>();

    public void addTag(String tag) {
        if (tags == null) tags = new ArrayList<>();
        if (!tags.contains(tag)) tags.add(tag);
    }

}
