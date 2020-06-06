package vnu.uet.mobilecourse.assistant.network.deserializer;

import com.google.gson.*;
import vnu.uet.mobilecourse.assistant.network.response.PageContentResponse;

import java.lang.reflect.Type;

public class PageContentDeseializer implements JsonDeserializer<PageContentResponse[]> {
    @Override
    public PageContentResponse[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement pages = json.getAsJsonObject().get("pages");
        PageContentResponse[] responses = new Gson().fromJson(pages, typeOfT);
        return responses;
    }
}
