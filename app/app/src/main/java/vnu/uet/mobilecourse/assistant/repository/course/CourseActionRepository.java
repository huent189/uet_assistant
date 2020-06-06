package vnu.uet.mobilecourse.assistant.repository.course;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.model.material.CourseConstant;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.network.HTTPClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;

public class CourseActionRepository {
    private CourseRequest sender = HTTPClient.getInstance().request(CourseRequest.class);
    private CoursesDatabase database = CoursesDatabase.getDatabase();
    public void triggerCourseView(int courseId){
        CoursesDatabase.databaseWriteExecutor.execute(()->{
            database.coursesDAO().updateLastAccessTime(courseId, System.currentTimeMillis()/1000);
        });
        sender.triggerCourseView(courseId)
                  .enqueue(new Callback<JsonElement>() {
                      @Override
                      public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                          JsonObject jsonResponse = response.body().getAsJsonObject();
                          JsonElement status = jsonResponse.get("status");
                          if(status == null || status.getAsBoolean()){
                              // TODO: handle trigger error
                          }
                      }

                      @Override
                      public void onFailure(Call<JsonElement> call, Throwable throwable) {
                          // TODO: handle connectivity error
                      }
                  });
    }
    public void triggerMaterialCompletion(Material material){
        CoursesDatabase.databaseWriteExecutor.execute(()->{
            database.materialDAO().updateMaterialCompletion(material.getId());
        });
        switch (material.getType()){
            case CourseConstant.MaterialType.PAGE:
                //TODO: trigger page view
                break;
            case CourseConstant.MaterialType.RESOURSE:
                //TODO: trigger resource view
                break;
        }
    }
}
