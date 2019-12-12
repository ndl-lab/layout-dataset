package jp.go.ndl.lab.annotation.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import jp.go.ndl.lab.annotation.Application;
import jp.go.ndl.lab.annotation.domain.ImageType;
import jp.go.ndl.lab.annotation.domain.TargetImage;
import jp.go.ndl.lab.annotation.infra.EsDataStore;
import jp.go.ndl.lab.annotation.infra.EsSearchQuery;
import jp.go.ndl.lab.annotation.infra.EsSearchResult;
import jp.go.ndl.lab.annotation.service.PathService;
import jp.go.ndl.lab.annotation.service.ThumbService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/image")
@Slf4j
@Profile(Application.MODE_WEB)
public class ImageController {

    @Autowired
    private EsDataStore<TargetImage> dataStore;

    @PostMapping(path = "/create")
    public TargetImage create(@RequestBody TargetImage image) throws Exception {
        log.info("{}", image);
        return dataStore.create(image, true);
    }

    @Autowired
    private EsDataStore<ImageType> typeStore;

    @Autowired
    private ThumbService thumbService;

    @PostMapping(path = "/update")
    public TargetImage update(@RequestBody TargetImage book) throws Exception {
        log.info("{}", book);
        ImageType type = typeStore.get(book.imageType);
        TargetImage img = dataStore.update(book, true);
        thumbService.asyncCreateThumbnail(type, img);
        return img;
    }

    @GetMapping(path = "/{id}")
    public TargetImage get(@PathVariable("id") String id) {
        log.info("{}", id);
        return dataStore.get(id);
    }

    @GetMapping(path = "/file/{name}")
    public HttpEntity<byte[]> getImage(@PathVariable("name") String name) throws IOException {
        TargetImage tg = dataStore.get(name);
        return returnImage(tg.toPath());
    }

    @GetMapping(path = "/thumb/{name}")
    public HttpEntity<byte[]> getThumb(@PathVariable("name") String name) throws IOException {
        TargetImage tg = dataStore.get(name);
        return returnImage(ps.thumbFile(tg));
    }

    private HttpEntity<byte[]> returnImage(Path p) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        if (!Files.exists(p)) {
            headers.setContentLength(0);
            log.error("file not found {}", p);
            return new HttpEntity<>(new byte[0], headers);
        }
        byte[] bt = null;
        try (InputStream is = Files.newInputStream(p)) {
            bt = IOUtils.toByteArray(is);
        }
        headers.setContentLength(bt.length);
        return new HttpEntity<>(bt, headers);
    }

    @Autowired
    private PathService ps;

    @GetMapping("search")
    public EsSearchResult<TargetImage> search(@RequestParam MultiValueMap<String, String> query) throws IOException {
        log.info("{}", query);
        SearchSourceBuilder ssb = EsSearchQuery.readQuery(query).createSearchSource();
        ssb.sort("name", SortOrder.ASC);

        EsSearchResult<TargetImage> r = dataStore.search(ssb);

        return r;
    }

    @PostMapping("search")
    public EsSearchResult<TargetImage> search(@RequestBody EsSearchQuery query) throws IOException {
        log.info("{}", query);
        return dataStore.search(query.createSearchSource());
    }

    @Autowired
    PathService pathService;

}
