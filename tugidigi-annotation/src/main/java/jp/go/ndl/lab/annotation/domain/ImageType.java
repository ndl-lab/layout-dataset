package jp.go.ndl.lab.annotation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jp.go.ndl.lab.annotation.infra.ESData;
import jp.go.ndl.lab.annotation.utils.ColorSchema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageType implements ESData {

    public String id;
    public Long version;
    public String name;
    public List<AnnotationType> types = new ArrayList<>();

    public Map<String, Color> colorMap() {
        List<String> typess = types.stream().filter(ts -> !ts.id.contains("overall")).map(ts -> ts.id).collect(Collectors.toList());
        Map<String, Color> colors = new HashMap<>();
        for (int i = 0; i < typess.size(); i++) {
            colors.put(typess.get(i), ColorSchema.getColor(i));
        }
        return colors;
    }

}
