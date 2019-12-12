package jp.go.ndl.lab.annotation.service;

import jp.go.ndl.lab.annotation.domain.ImageBinder;
import jp.go.ndl.lab.annotation.domain.ImageType;
import jp.go.ndl.lab.annotation.domain.TargetImage;
import jp.go.ndl.lab.annotation.infra.EsDataStore;
import jp.go.ndl.lab.annotation.infra.EsDataStoreFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreBeans {

    @Autowired
    EsDataStoreFactory dsFactory;

    @Bean
    public EsDataStore<TargetImage> imageStore() {
        EsDataStore<TargetImage> video = dsFactory.build(TargetImage.class);
        return video;
    }

    @Bean
    public EsDataStore<ImageBinder> binderStore() {
        return dsFactory.build(ImageBinder.class);
    }

    @Bean
    public EsDataStore<ImageType> typeStore() {
        return dsFactory.build(ImageType.class);
    }

}
