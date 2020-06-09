package vnu.uet.mobilecourse.assistant.network.deserializer;

import com.google.gson.*;
import vnu.uet.mobilecourse.assistant.model.material.AssignmentContent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.function.Consumer;

public class AssignmentDeserializer implements JsonDeserializer<AssignmentContent[]> {
    @Override
    public AssignmentContent[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ArrayList<AssignmentContent> assigns = new ArrayList<>();
        Gson gson = new Gson();
        json.getAsJsonObject().get("courses").getAsJsonArray().forEach(new Consumer<JsonElement>() {
            @Override
            public void accept(JsonElement jsonElement) {
                JsonObject course = jsonElement.getAsJsonObject();
                course.get("assignments").getAsJsonArray().forEach(new Consumer<JsonElement>() {
                    @Override
                    public void accept(JsonElement jsonElement) {
                        assigns.add(gson.fromJson(jsonElement, AssignmentContent.class));
                    }
                });

            }
        });
        return assigns.toArray(new AssignmentContent[assigns.size()]);
    }
}
