package vnu.uet.mobilecourse.assistant.repository;

import android.util.Log;
import com.google.gson.JsonObject;
import retrofit2.Call;
import vnu.uet.mobilecourse.assistant.SharedPreferencesManager;
import vnu.uet.mobilecourse.assistant.network.HTTPClient;
import vnu.uet.mobilecourse.assistant.network.request.UserRequest;
import vnu.uet.mobilecourse.assistant.network.response.CoursesResponseCallback;
import vnu.uet.mobilecourse.assistant.network.response.LoginResponse;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

public class UserRepository {
    public StateLiveData<String> makeLoginRequest(String studentId, String password){
        SharedPreferencesManager.clearAll();
        final StateLiveData<String> liveLoginResponse = new StateLiveData<>();
        UserRequest userRequest = HTTPClient.getCoursesClient().create(UserRequest.class);
        Call<JsonObject> call = userRequest.login(studentId, password);
        Log.d("LOGIN", "param: " + studentId + " " + password);
        call.enqueue(new CoursesResponseCallback<LoginResponse>(LoginResponse.class) {
            @Override
            public void onSucess(LoginResponse response) {
                SharedPreferencesManager.setString(SharedPreferencesManager.TOKEN, response.getToken());
                SharedPreferencesManager.setString(SharedPreferencesManager.REGISTER_EMAIL, studentId + "@vnu.edu.vn");
                liveLoginResponse.postSuccess("Login successfully\n");
            }
            @Override
            public void onError(Exception e) {
                liveLoginResponse.postError(e);
            }
        });
        return liveLoginResponse;
    }
}
