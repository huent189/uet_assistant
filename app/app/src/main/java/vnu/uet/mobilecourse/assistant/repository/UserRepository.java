package vnu.uet.mobilecourse.assistant.repository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vnu.uet.mobilecourse.assistant.exception.InvalidLoginException;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.model.Token;
import vnu.uet.mobilecourse.assistant.network.HTTPClient;
import vnu.uet.mobilecourse.assistant.network.request.UserRequest;
import vnu.uet.mobilecourse.assistant.network.response.LoginResponse;

public class UserRepository {
    private UserRequest userRequest;
    public StateLiveData<String> makeLoginRequest(String studentId, String password){
        final StateLiveData<String> liveLoginResponse = new StateLiveData<>();
        userRequest = HTTPClient.getCoursesClient().create(UserRequest.class);
        Call<LoginResponse> call = userRequest.login(studentId, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if(loginResponse.isSuccess()){
                    Token.setToken(loginResponse.getPrivateToken());
                    liveLoginResponse.postSuccess("Login successfully\n"+ loginResponse.toString());
                } else {
                    liveLoginResponse.postError(new InvalidLoginException(loginResponse.getErrorCode()));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable throwable) {
                liveLoginResponse.postError(new Exception(throwable.getMessage()));
            }
        });
        return liveLoginResponse;
    }
}
