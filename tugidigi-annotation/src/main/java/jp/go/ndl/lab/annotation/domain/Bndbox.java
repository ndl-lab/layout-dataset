package jp.go.ndl.lab.annotation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bndbox {

    public Bndbox(int xmin, int ymin, int xmax, int ymax) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }

    public Bndbox() {
    }

    public int xmin;
    public int ymin;
    public int xmax;
    public int ymax;

}
