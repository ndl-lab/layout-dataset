package jp.go.ndl.lab.annotation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import jp.go.ndl.lab.annotation.infra.ESData;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageBinder implements ESData {

    public String id;
    public Long version;
    public String name;
    public String imageType;
    public List<String> tags;
    public Status status;
    public String seeAlso;
    public int images;
    public String holder;
    public String imagePath;

    public List<Action> actions = new ArrayList<>();

    public Path toPath(){
        return Paths.get(this.imagePath);
    }

    public void addTag(String tag) {
        if (tags == null) tags = new ArrayList<>();
        if (!tags.contains(tag)) tags.add(tag);
    }

}
