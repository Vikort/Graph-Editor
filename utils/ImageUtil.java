package utils;

import java.io.InputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageUtil {

    // sourcePath: /icon/add.png
    public static ImageView getImage(String sourcePath) {
        InputStream input = null;
        try {
            Class<?> c = ImageUtil.class;
            input = c.getResourceAsStream(sourcePath);
            Image img = new Image(input);
            ImageView imageView = new ImageView(img);
            imageView.setFitHeight(25);
            imageView.setFitWidth(25);
            return imageView;
        } finally {
            closeQuietly(input);
        }

    }

    private static void closeQuietly(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (Exception e) {

        }
    }
}
