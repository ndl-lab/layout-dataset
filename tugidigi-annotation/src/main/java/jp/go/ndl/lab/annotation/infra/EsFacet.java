package jp.go.ndl.lab.annotation.infra;

import java.util.LinkedHashMap;
import java.util.Map;

public class EsFacet {

    public EsFacet() {
    }

    public EsFacet(String field) {
        this.field = field;
    }

    public String field;
    public Map<String, Long> counts = new LinkedHashMap<>();

    public void addCount(String key, long count) {
        Long current = counts.get(key);
        if (current == null) {
            current = 0L;
        }
        counts.put(key, (current + count));
    }

}
