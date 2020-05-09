package vnu.uet.mobilecourse.assistant.network.request;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserRequest {
    @FormUrlEncoded
    @POST("login/token.php")
    Call<JsonObject> login(@Field("username") String username, @Field("password") String password);
}
