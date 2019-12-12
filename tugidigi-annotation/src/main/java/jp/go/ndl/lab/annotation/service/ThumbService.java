package jp.go.ndl.lab.annotation.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.imageio.ImageIO;
import jp.go.ndl.lab.annotation.domain.ImageType;
import jp.go.ndl.lab.annotation.domain.TargetImage;
import jp.go.ndl.lab.annotation.utils.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ThumbService {

    @Autowired
    private PathService ps;

    @Async
    public void asyncCreateThumbnail(ImageType type, TargetImage image) {
        try {
            this.createThumbnail(type, image);
        } catch (Exception e) {
            log.info("", e);
        }
    }

    public Dimension createThumbnail(ImageType type, TargetImage image) throws Exception {
        BufferedImage img = ImageIO.read(image.toPath().toFile());
        Graphics2D g2 = img.createGraphics();
        final Map<String, Color> colorMap = type.colorMap();

        image.annotations.forEach(a -> {
            if (a.name.equals("1_overall")) {
                g2.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(4f));
                g2.setComposite(AlphaComposite.Src);
                g2.drawRect(a.bndbox.xmin, a.bndbox.ymin, a.bndbox.xmax - a.bndbox.xmin, a.bndbox.ymax - a.bndbox.ymin);
            } else {
                g2.setColor(colorMap.getOrDefault(a.name, Color.BLACK));
                g2.setComposite(AlphaComposite.SrcOver.derive(0.8f));
                g2.fillRect(a.bndbox.xmin, a.bndbox.ymin, a.bndbox.xmax - a.bndbox.xmin, a.bndbox.ymax - a.bndbox.ymin);
                g2.setStroke(new BasicStroke(1f));
                g2.setComposite(AlphaComposite.Src);
                g2.setColor(Color.BLACK);
                g2.drawRect(a.bndbox.xmin, a.bndbox.ymin, a.bndbox.xmax - a.bndbox.xmin, a.bndbox.ymax - a.bndbox.ymin);
            }
        });
        ps.thumbFile(image).toFile().getParentFile().mkdirs();
        BufferedImage scaled = ImageUtil.getFasterScaledInstance(img, 300, -1, true);
        ImageIO.write(scaled, "jpg", ps.thumbFile(image).toFile());
        img.flush();
        scaled.flush();
        return new Dimension(img.getWidth(), img.getHeight());
    }
}
