package jp.go.ndl.lab.annotation.infra;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.search.SearchResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EsSearchResult<E> implements Iterable<E> {

    public EsSearchResult() {
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    public List<E> list = new ArrayList<>();
    public long hit;
    public int from;

    public Map<String, EsFacet> facets = new LinkedHashMap<>();

    @JsonIgnore
    public SearchResponse searchResponse;

    @Override
    public String toString() {
        return "SearchResult{" + "list=" + list + ", hit=" + hit + ", from=" + from + '}';
    }

}
