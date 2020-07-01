package vnu.uet.mobilecourse.assistant.repository.course;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.exception.HostIsNotReachable;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.network.CourseClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;
import vnu.uet.mobilecourse.assistant.network.response.CoursesResponseCallback;
import vnu.uet.mobilecourse.assistant.work.courses.CourseActionSynchronization;

public class CourseActionRepository {
    private CourseRequest sender = CourseClient.getInstance().request(CourseRequest.class);
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
        updateMaterialCompletion(material.getId(), 1);
    }

    public void triggerMaterialUnCompletion(Material material){
        updateMaterialCompletion(material.getId(), 0);
    }

    private void updateMaterialCompletion(int materialId, int isCompleted){
        CoursesDatabase.databaseWriteExecutor.execute(()->{
            database.materialDAO().updateMaterialCompletion(materialId, isCompleted);
        });
        sender.updateMaterialCompletion(materialId, isCompleted).enqueue(new CoursesResponseCallback<JsonElement>(JsonElement.class) {
            @Override
            public void onSuccess(JsonElement response) {
            }

            @Override
            public void onError(Call<JsonElement> call, Throwable throwable) {
                super.onError(call, throwable);
                if(throwable instanceof HostIsNotReachable){
                    CourseActionSynchronization.scheduleUpdateMaterialCompletion(materialId);
                }

            }
        });
    }
}
