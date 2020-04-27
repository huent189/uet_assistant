package vnu.uet.mobilecourse.assistant.ui.network.request;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import vnu.uet.mobilecourse.assistant.ui.network.response.LoginResponse;

public interface UserRequest {
    @FormUrlEncoded
    @POST("login/token.php")
    Call<LoginResponse> login(@Field("username") String username, @Field("password") String password);
}
