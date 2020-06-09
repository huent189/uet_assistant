package vnu.uet.mobilecourse.assistant.network.deserializer;

import com.google.gson.*;
import vnu.uet.mobilecourse.assistant.model.material.PageContent;
import vnu.uet.mobilecourse.assistant.util.StringUtils;

import java.lang.reflect.Type;

public class PageContentDeseializer implements JsonDeserializer<PageContent[]> {
    @Override
    public PageContent[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray pages = json.getAsJsonArray();
        Gson gson = new Gson();
        int size = pages.size();
        PageContent[] pageContents = new PageContent[size];
        for (int i = 0; i < size; i++){
            pageContents[i] = gson.fromJson(pages.get(i), PageContent.class);
            String newIntro = StringUtils.mergePageContent(pageContents[i].getIntro(), pages.get(i).getAsJsonObject().get("content").getAsString());
            pageContents[i].setIntro(newIntro);
        }
        return pageContents;
    }
}
