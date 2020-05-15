package vnu.uet.mobilecourse.assistant.network.deserializer;

import com.google.gson.*;
import vnu.uet.mobilecourse.assistant.model.CourseContent;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.WeeklyMaterial;

import java.lang.reflect.Type;
import java.util.LinkedList;

public class CourseContentDeserializer implements JsonDeserializer<CourseContent[]> {
    @Override
    public CourseContent[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        CourseContent[] response = new CourseContent[json.getAsJsonArray().size()];
        int i = 0;
        for (JsonElement weeklyElement: json.getAsJsonArray()) {
            WeeklyMaterial weeklyMaterial = gson.fromJson(weeklyElement, WeeklyMaterial.class);
            JsonArray materialJsonList = weeklyElement.getAsJsonObject().get("modules").getAsJsonArray();
            LinkedList<Material> materials = new LinkedList<>();
            for(JsonElement materialElement : materialJsonList){
                Material child =  gson.fromJson(materialElement, Material.class);
                child.setWeekId(weeklyMaterial.getId());
                JsonElement contents = materialElement.getAsJsonObject().get("contents");
                if(contents != null){
                    JsonObject firstContent = contents.getAsJsonArray().get(0).getAsJsonObject();
                    child.setLastModified(Long.parseLong(firstContent.get("timemodified").getAsString()));
                    child.setFileName(firstContent.get("filename").getAsString());
                    child.setFileUrl(firstContent.get("fileurl").getAsString());
                }
                materials.addLast(child);
            }
            response[i++] = new CourseContent(weeklyMaterial, materials);
        }
        return response;
    }
}