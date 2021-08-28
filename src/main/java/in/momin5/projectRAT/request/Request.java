package in.momin5.projectRAT.request;

import java.io.File;

public interface Request {

    void init() throws Exception;
    String getMessage();
    File getFile();


}
