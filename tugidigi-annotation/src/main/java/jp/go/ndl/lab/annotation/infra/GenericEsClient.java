package jp.go.ndl.lab.annotation.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GenericEsClient {

    public RestClient restClient;
    public RestHighLevelClient highClient;

    public GenericEsClient(@Value("${es.host}") String host, @Value("${es.port}") int port, @Value("${es.path}") String path) {
        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port, "http")).setRequestConfigCallback(c -> {
            return c.setConnectTimeout(5000).setSocketTimeout(60000);
        });
        if (StringUtils.isNotBlank(path)) {
            builder.setPathPrefix(path);
        }
        restClient = builder.build();
        highClient = new RestHighLevelClient(builder);
    }

    public String issueGet(String command) throws Exception {
        log.info("GET {}", command);
        Response r = restClient.performRequest(new Request("GET", "/" + command));
        String content = null;
        try (InputStream is = r.getEntity().getContent()) {
            content = IOUtils.toString(is, StandardCharsets.UTF_8);
        }
        return content;
    }

    public String issuePut(String command) throws Exception {
        log.info("PUT {}", command);
        Response r = restClient.performRequest(new Request(
                "PUT",
                "/" + command));
        String content = null;
        try (InputStream is = r.getEntity().getContent()) {
            content = IOUtils.toString(is, StandardCharsets.UTF_8);
        }
        return content;
    }

    public String issueDelete(String command) throws Exception {
        log.info("DELETE {}", command);
        Response r = restClient.performRequest(new Request(
                "DELETE",
                "/" + command));
        String content = null;
        try (InputStream is = r.getEntity().getContent()) {
            content = IOUtils.toString(is, StandardCharsets.UTF_8);
        }
        return content;
    }

    /*
    1行目にパスとメソッド、二行目以降にJSONを記入した命令ファイルでコマンドを実行する
     */
    public String issue(String method, String path, String json) throws Exception {
        HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);
        Request rq = new Request(method, "/" + path);
        rq.setEntity(entity);
        Response r = restClient.performRequest(rq);
        String content = null;
        try (InputStream is = r.getEntity().getContent()) {
            content = IOUtils.toString(is, StandardCharsets.UTF_8);
        }
        return content;
    }

    /*
    1行目にパスとメソッド、二行目以降にJSONを記入した命令ファイルでコマンドを実行する
     */
    public String issue(String method, String path, Path commandFile) throws Exception {
        List<String> file = Files.readAllLines(commandFile, StandardCharsets.UTF_8);
        HttpEntity entity = new NStringEntity(String.join("", file), ContentType.APPLICATION_JSON);
        Request rq = new Request(method, "/" + path);
        rq.setEntity(entity);
        Response r = restClient.performRequest(rq);
        String content = null;
        try (InputStream is = r.getEntity().getContent()) {
            content = IOUtils.toString(is, StandardCharsets.UTF_8);
        }
        return content;
    }

    /*
    1行目にパスとメソッド、二行目以降にJSONを記入した命令ファイルでコマンドを実行する
     */
    public String issue(Path commandFile) throws Exception {
        List<String> file = Files.readAllLines(commandFile, StandardCharsets.UTF_8);
        String[] order = file.get(0).split(" ");
        Response r = null;
        if (file.size() > 1) {
            file.remove(0);
            HttpEntity entity = new NStringEntity(String.join("", file), ContentType.APPLICATION_JSON);
            Request rq = new Request(order[0], "/" + order[1]);
            rq.setEntity(entity);
            r = restClient.performRequest(rq);
        } else {
            r = restClient.performRequest(new Request(order[0], "/" + order[1]));
        }
        String content = null;
        try (InputStream is = r.getEntity().getContent()) {
            content = IOUtils.toString(is, StandardCharsets.UTF_8);
        }
        return content;
    }

    public void deleteByQuery(String index, QueryBuilder queryBuilder) {
        try {
            String query = "{\"query\":" + org.elasticsearch.common.Strings.toString(queryBuilder, false, false) + "}";
            log.info("delete query: {}", query);
            HttpEntity entity = new NStringEntity(query, ContentType.APPLICATION_JSON);
            Request r = new Request("POST", "/" + index + "/_delete_by_query");
            r.setEntity(entity);
            restClient.performRequest(r);
        } catch (JsonProcessingException ex) {
            log.error("", ex);
        } catch (IOException ex) {
            log.error("", ex);
        }
    }

    public void deleteIndex(String index) throws IOException {
        DeleteRequest req = new DeleteRequest(index);
        highClient.delete(req, RequestOptions.DEFAULT);
    }

    public void close() throws IOException {
        restClient.close();
    }

}
