package jp.go.ndl.lab.annotation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnnotationObject {

    public String id;
    public String name;
    public Bndbox bndbox;

}
