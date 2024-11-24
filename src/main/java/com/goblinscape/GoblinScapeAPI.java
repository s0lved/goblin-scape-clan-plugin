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

    private ArrayList<GoblinScapePlayerData> parseData(JsonArray j)
    {
        ArrayList<GoblinScapePlayerData> l = new ArrayList<>();
        for (JsonElement jsonElement : j)
        {
            JsonObject jObj = jsonElement.getAsJsonObject();
            if (!jObj.get("name").getAsString().equals(plugin.getPlayerName())) {
                GoblinScapePlayerData p = new GoblinScapePlayerData(jObj.get("name").getAsString(), jObj.get("x").getAsInt(),
                        jObj.get("y").getAsInt(), jObj.get("plane").getAsInt(),
                        jObj.get("title").getAsString(), jObj.get("world").getAsInt());
                l.add(p);
            }
        }
        return l;
    }
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
                            plugin.setPlayerData(parseData(j));
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
}
