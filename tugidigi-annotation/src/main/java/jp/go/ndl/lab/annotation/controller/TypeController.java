package jp.go.ndl.lab.annotation.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jp.go.ndl.lab.annotation.Application;
import jp.go.ndl.lab.annotation.domain.ImageType;
import jp.go.ndl.lab.annotation.infra.EsDataStore;
import jp.go.ndl.lab.annotation.infra.EsSearchQuery;
import jp.go.ndl.lab.annotation.infra.EsSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/type")
@Slf4j
@Profile(Application.MODE_WEB)
public class TypeController {

    @Autowired
    private EsDataStore<ImageType> dataStore;

    @PostMapping(path = "/update")
    public ImageType update(@RequestBody ImageType project) throws Exception {
        log.info("{}", project);
        return dataStore.update(project, true);
    }

    @GetMapping(path = "/{id}")
    public ImageType get(@PathVariable("id") String id) {
        log.info("{}", id);
        return dataStore.get(id);
    }

    @PostMapping(path = "/delete")
    public void delete(@RequestParam("id") String id) {
        log.info("{}", id);
        dataStore.delete(id);
    }

    @GetMapping(path = "/all")
    public List<ImageType> all() {
        log.info("");
        return new ArrayList<>(dataStore.getAll().values());
    }

    @GetMapping("search")
    public EsSearchResult<ImageType> search(@RequestParam MultiValueMap<String, String> query) throws IOException {
        log.info("{}", query);
        return dataStore.search(EsSearchQuery.readQuery(query).createSearchSource());
    }

    @PostMapping("search")
    public EsSearchResult<ImageType> search(@RequestBody EsSearchQuery query) throws IOException {
        log.info("{}", query);
        return dataStore.search(query.createSearchSource());
    }
}
