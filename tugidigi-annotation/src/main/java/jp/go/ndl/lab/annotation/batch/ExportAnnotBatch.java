package jp.go.ndl.lab.annotation.batch;

import jp.go.ndl.lab.annotation.Application;
import jp.go.ndl.lab.annotation.domain.ImageBinder;
import jp.go.ndl.lab.annotation.domain.Status;
import jp.go.ndl.lab.annotation.domain.TargetImage;
import jp.go.ndl.lab.annotation.infra.EsDataStore;
import jp.go.ndl.lab.annotation.utils.AbstractBatch;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component("export")
public class ExportAnnotBatch extends AbstractBatch {
    public static void main(String[] args) {
        Application.main("batch", "export", "all", "ignore\\output");
    }

    @Autowired
    private EsDataStore<TargetImage> imageEsDataStore;
    @Autowired
    private EsDataStore<ImageBinder> binderEsDataStore;

    @Override
    public void run(String[] params) {

        String type = params[0];
        Path output = Paths.get(params[1]);

        try {
            Files.createDirectories(output);

            final TemplateEngine templateEngine = new TemplateEngine();
            ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
            resolver.setTemplateMode("XML");
            resolver.setPrefix("templates/");
            resolver.setSuffix(".xml");
            templateEngine.setTemplateResolver(resolver);


            imageEsDataStore.scroll(QueryBuilders.matchAllQuery(), b -> {
                if ((b.imageType.equals(type) || type.equals("all")) && b.status == Status.ANNOTATED) {
                    log.info("export {}", b.id);
                    try {
                        Path file = output.resolve(b.id.replace(".jpg", ".xml"));
                        final Context ctx = new Context();
                        ctx.setVariable("i", b);
                        final Writer writer = new FileWriter(file.toString());
                        templateEngine.process("voc", ctx, writer);
                        writer.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
