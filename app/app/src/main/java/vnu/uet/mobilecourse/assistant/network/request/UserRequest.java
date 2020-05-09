package vnu.uet.mobilecourse.assistant.network.request;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserRequest {
    @FormUrlEncoded
    @POST("login/token.php?service=moodle_mobile_app")
    Call<JsonElement> login(@Field("username") String username, @Field("password") String password);
    @POST("webservice/rest/server.php?wsfunction=core_webservice_get_site_info")
    Call<JsonObject> getUserId();
}
