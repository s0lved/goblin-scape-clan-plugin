package com.goblinscape;
import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import javax.inject.Inject;
@Slf4j
public class GoblinScapeAPI {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    @Inject
    private OkHttpClient okHttpClient;

    @Inject
    private Gson gson;

    @Inject
    private GoblinScapePlugin plugin;


    protected void makePostRequest(Object temp)
    {
        try
        {
            Request r = new Request.Builder()
                    .url(plugin.getPostEndpoint())
                    .addHeader("Authorization", plugin.getSharedKey())
                    .post(RequestBody.create(JSON, gson.toJson(temp)))
                    .build();

            okHttpClient.newCall(r).enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    plugin.setPostError(true);
                }

                @Override
                public void onResponse(Call call, Response response)
                {
                    if (response.isSuccessful())
                    {
                        try {
                            JsonArray j = gson.fromJson(response.body().string(), JsonArray.class);
                            log.debug(j.toString());
                            plugin.setPostError(false);
                            response.close();
                        }
                        catch (IOException | JsonSyntaxException e)
                        {
                            plugin.setGetError(true);
                            log.error(e.getMessage());
                        }
                    }
                    else
                    {
                        log.error("Post request unsuccessful");
                        plugin.setPostError(true);
                    }
                    response.close();
                }
            });
        }
        catch (IllegalArgumentException e)
        {
            log.error("Bad URL given: " + e.getLocalizedMessage());
            plugin.setPostError(true);
        }
    }

    protected void makeMessageRequest(Object msg)
    {
        try
        {
            Request req = new Request.Builder()
                    .url(plugin.getMessageEndpoint())
                    .addHeader("Authorization", plugin.getSharedKey())
                    .post(RequestBody.create(JSON, gson.toJson(msg)))
                    .build();

            okHttpClient.newCall(req).enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    plugin.setMsgError(true);
                }

                @Override
                public void onResponse(Call call, Response response)
                {
                    if (response.isSuccessful())
                    {
                        try {
                            JsonArray j = gson.fromJson(response.body().string(), JsonArray.class);
                            log.debug(j.toString());
                            plugin.setMsgError(false);
                            response.close();
                        }
                        catch (IOException | JsonSyntaxException e)
                        {
                            plugin.setGetError(true);
                            log.error(e.getMessage());
                        }
                    }
                    else
                    {
                        log.error("Message request unsuccessful");
                        plugin.setMsgError(true);
                    }
                    response.close();
                }
            });
        }
        catch (IllegalArgumentException e)
        {
            log.error("Bad URL given: " + e.getLocalizedMessage());
            plugin.setMsgError(true);
        }
    }

    protected void sendOnlinePlayers(JsonObject payload)
    {
        try
        {
            Request req = new Request.Builder()
                    .url(plugin.getOnlineEndpoint())
                    .addHeader("Authorization", plugin.getSharedKey())
                    .post(RequestBody.create(JSON, gson.toJson(payload)))
                    .build();

            okHttpClient.newCall(req).enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    log.error("Failed to send online players: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response)
                {
                    if (response.isSuccessful())
                    {
                        log.info("Online players updated successfully.");
                    }
                    else
                    {
                        log.error("Failed to update online players. HTTP Code: " + response.code());
                    }
                    response.close();
                }
            });
        }
        catch (Exception e)
        {
            log.error("Error sending online players: " + e.getMessage());
        }
    }


    protected void sendAllMembers(JsonObject payload)
    {
        try
        {
            Request req = new Request.Builder()
                    .url(plugin.getMemberEndpoint())
                    .addHeader("Authorization", plugin.getSharedKey())
                    .post(RequestBody.create(JSON, gson.toJson(payload)))
                    .build();

            okHttpClient.newCall(req).enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    log.error("Failed to send members: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response)
                {
                    if (response.isSuccessful())
                    {
                        log.info("Members updated successfully.");
                    }
                    else
                    {
                        log.error("Failed to update members. HTTP Code: " + response.code());
                    }
                    response.close();
                }
            });
        }
        catch (Exception e)
        {
            log.error("Error sending members: " + e.getMessage());
        }
    }
}

