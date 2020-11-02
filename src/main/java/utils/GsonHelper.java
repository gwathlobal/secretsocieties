package utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import models.helper.RoleSet;

import java.lang.reflect.Type;

public class GsonHelper {
    private static Gson gson;

    private GsonHelper() {}

    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();

            gsonBuilder.registerTypeAdapter(new TypeToken<RoleSet>() {}.getType(), RoleSet.getJsonSerializer());
            gsonBuilder.registerTypeAdapter(RoleSet.class, RoleSet.getJsonDeserializer());

            gson = gsonBuilder.create();
        }
        return gson;
    }

}
