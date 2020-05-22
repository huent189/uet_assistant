package vnu.uet.mobilecourse.assistant.network.deserializer;

import com.google.gson.*;
import vnu.uet.mobilecourse.assistant.model.Grade;

import java.lang.reflect.Type;

public class CourseGradeDeserializer implements JsonDeserializer<Grade[]> {
    @Override
    public Grade[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Grade[] grades = null;
        Gson gson = new Gson();
        JsonElement raw = json.getAsJsonObject().get("usergrades");
        if(raw != null){
            raw = raw.getAsJsonArray().get(0);
            grades = gson.fromJson(raw.getAsJsonObject().get("gradeitems"), Grade[].class);
            int courseId = raw.getAsJsonObject().get("courseid").getAsInt();
            for (Grade g: grades ) {
                g.setCourseId(courseId);
                if(g.getGradedDate() == 0){
                    g.setUserGrade(-1.0);
                }
                if(g.getParentType().equals("course")){
                    g.setType("course");
                }
            }
        }

        return  grades;
    }
}
