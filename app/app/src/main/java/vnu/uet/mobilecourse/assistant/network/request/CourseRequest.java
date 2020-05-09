package vnu.uet.mobilecourse.assistant.network.request;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CourseRequest {
    @FormUrlEncoded
    @POST("webservice/rest/server.php?moodlewsrestformat=json&wsfunction=core_enrol_get_users_courses")
    Call<JsonObject> getMyCoures(@Field("userid") String userId);
}
