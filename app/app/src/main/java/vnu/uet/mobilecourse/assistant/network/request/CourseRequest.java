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
    Call<JsonElement> getCourseContent(@Field("courseid") int courseId);
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=gradereport_user_get_grade_items")
    Call<JsonElement> getCourseGrade(@Field("courseid") String courseId, @Field("userid") String userId);
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=core_course_view_course&sectionnumber=0")
    Call<JsonElement> triggerCourseView(@Field("courseid") int courseId);
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
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=mod_label_get_labels_by_courses")
    Call<JsonElement> getLabelByCourses(@Field("courseids[]") int[] courseIds);
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=mod_resource_get_resources_by_courses")
    Call<JsonElement> getInternalResourceByCourses(@Field("courseids[]") int[] courseIds);
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=mod_assign_get_assignments")
    Call<JsonElement> getAssignmentByCourses(@Field("courseids[]") int[] courseIds);
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=mod_quiz_get_quizzes_by_courses")
    Call<JsonElement> getQuizzesByCourses(@Field("courseids[]") int[] courseIds);
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=mod_forum_get_discussion_posts&sortdirection=ASC")
    Call<JsonElement> getDiscussionDetail(@Field("discussionid") int dicussionId);
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=mod_forum_get_forums_by_courses")
    Call<JsonElement> getForumByCourses(@Field("courseids[]") int[] courseIds);
    @FormUrlEncoded
    @POST("webservice/rest/server.php?wsfunction=core_completion_update_activity_completion_status_manually")
    Call<JsonElement> updateMaterialCompletion(@Field("cmid") int materialId, @Field("completed") int isCompleted);
}
