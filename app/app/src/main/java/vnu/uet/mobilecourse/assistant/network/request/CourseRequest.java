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
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=gradereport_user_get_grade_items")
    Call<JsonElement> getCourseGrade(@Field("courseid") String courseId, @Field("userid") String userId);
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=core_course_view_course&sectionnumber=0")
    Call<JsonElement> triggerCourseView(@Field("courseid") int courseId);
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=mod_forum_get_discussion_post")
    Call<JsonElement> getDicussionPost(@Field("postid") int postId);
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=core_course_get_course_module")
    Call<JsonElement> getModuleContent(@Field("cmid") int moduleId);
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=mod_page_get_pages_by_courses")
    Call<JsonElement> getPagesByCourses(@Field("courseids[]") int[] courseIds);
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=mod_url_get_urls_by_courses")
    Call<JsonElement> getURLsByCourses(@Field("courseids[]") int[] courseIds);
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=mod_forum_get_forum_discussions")
    Call<JsonElement> getDiscussionByForum(@Field("forumid") int forumId);
}
