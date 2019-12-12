package jp.go.ndl.lab.annotation.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EsDataStoreFactory {

    private String host;
    private int port;
    private String path;

    public EsDataStoreFactory(@Value("${es.host}") String host, @Value("${es.port}") int port, @Value("${es.path}") String path) {
        this.host = host;
        this.port = port;
        this.path = path;
    }

    public EsDataStore build(Class clazz) {
        return new EsDataStore(host, port, path, "ja_" + clazz.getSimpleName().toLowerCase(), clazz);
    }

}
