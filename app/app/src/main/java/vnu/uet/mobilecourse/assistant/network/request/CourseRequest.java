package vnu.uet.mobilecourse.assistant.network.request;

import com.google.gson.JsonElement;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface CourseRequest {
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=core_enrol_get_users_courses")
    Call<JsonElement> getMyCoures(@Field("userid") String userId);
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=core_course_get_contents")
    Call<JsonElement> getCourseContent(@Field("courseid") String courseId);
}
