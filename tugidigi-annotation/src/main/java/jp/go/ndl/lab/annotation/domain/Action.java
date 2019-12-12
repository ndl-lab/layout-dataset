package jp.go.ndl.lab.annotation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Action {

    private Status type;
    private String note;
    private String user;
    private Date start;
    private Date end;

    public Action() {
    }

}
