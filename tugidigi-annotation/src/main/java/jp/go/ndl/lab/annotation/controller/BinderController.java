package jp.go.ndl.lab.annotation.controller;

import jp.go.ndl.lab.annotation.Application;
import jp.go.ndl.lab.annotation.domain.ImageBinder;
import jp.go.ndl.lab.annotation.infra.EsDataStore;
import jp.go.ndl.lab.annotation.infra.EsFacet;
import jp.go.ndl.lab.annotation.infra.EsSearchQuery;
import jp.go.ndl.lab.annotation.infra.EsSearchResult;
import jp.go.ndl.lab.annotation.service.PathService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/binder")
@Slf4j
@Profile(Application.MODE_WEB)
public class BinderController {

    @Autowired
    private EsDataStore<ImageBinder> dataStore;

    @PostMapping(path = "/create")
    public ImageBinder create(@RequestBody ImageBinder image) throws Exception {
        log.info("{}", image);
        return dataStore.create(image, true);
    }

    @PostMapping(path = "/update")
    public ImageBinder update(@RequestBody ImageBinder book) throws Exception {
        log.info("{}", book);
        return dataStore.update(book, true);
    }

    @GetMapping(path = "/{id}")
    public ImageBinder get(@PathVariable("id") String id) {
        log.info("{}", id);
        return dataStore.get(id);
    }

    @Autowired
    private PathService ps;

    @GetMapping("search")
    public EsSearchResult<ImageBinder> search(@RequestParam MultiValueMap<String, String> query) throws IOException {
        log.info("{}", query);
        SearchSourceBuilder ssb = EsSearchQuery.readQuery(query).createSearchSource();

        TermsAggregationBuilder termFacetAgg = AggregationBuilders.terms("status");
        termFacetAgg.field("status");
        termFacetAgg.size(200);
        ssb.aggregation(termFacetAgg);

        termFacetAgg = AggregationBuilders.terms("tags");
        termFacetAgg.field("tags");
        termFacetAgg.size(200);
        ssb.aggregation(termFacetAgg);

        termFacetAgg = AggregationBuilders.terms("imageType");
        termFacetAgg.field("imageType");
        termFacetAgg.size(200);
        ssb.aggregation(termFacetAgg);

        termFacetAgg = AggregationBuilders.terms("holder");
        termFacetAgg.field("holder");
        termFacetAgg.size(200);
        ssb.aggregation(termFacetAgg);

        EsSearchResult<ImageBinder> r = dataStore.search(ssb);

        r.searchResponse.getAggregations().forEach(agg -> {
            Terms terms = (Terms) agg;
            String name = agg.getName();
            EsFacet facet = new EsFacet(name);
            for (Terms.Bucket b : terms.getBuckets()) {
                String key = b.getKeyAsString();
                long count = b.getDocCount();
                facet.addCount(key, count);
            }
            r.facets.put(name, facet);
        });

        return r;
    }

    @PostMapping("search")
    public EsSearchResult<ImageBinder> search(@RequestBody EsSearchQuery query) throws IOException {
        log.info("{}", query);
        return dataStore.search(query.createSearchSource());
    }

    @Autowired
    PathService pathService;

}
