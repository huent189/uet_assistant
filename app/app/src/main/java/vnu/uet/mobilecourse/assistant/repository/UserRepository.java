package vnu.uet.mobilecourse.assistant.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vnu.uet.mobilecourse.assistant.SharedPreferencesManager;
import vnu.uet.mobilecourse.assistant.exception.InvalidLoginException;
import vnu.uet.mobilecourse.assistant.exception.NoConnectivityException;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.network.HTTPClient;
import vnu.uet.mobilecourse.assistant.network.request.UserRequest;
import vnu.uet.mobilecourse.assistant.network.response.CoursesResponseCallback;
import vnu.uet.mobilecourse.assistant.network.response.LoginResponse;
import vnu.uet.mobilecourse.assistant.util.CONST;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class UserRepository {
    public StateLiveData<String> makeLoginRequest(String studentId, String password){
        clearSession();
        final StateLiveData<String> liveLoginResponse = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));
        UserRequest userRequest = HTTPClient.getInstance().request(UserRequest.class);
        Call<JsonElement> call = userRequest.login(studentId, password);
        Log.d("LOGIN", "param: " + studentId + " " + password);
        call.enqueue(new CoursesResponseCallback<LoginResponse>(LoginResponse.class) {
            @Override
            public void onSucess(LoginResponse response) {
                User.getInstance().setToken(response.getToken());
                User.getInstance().setEmail(studentId + CONST.VNU_EMAIL_DOMAIN);
                userRequest.getUserId().enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonElement userId = response.body().get("userid");
                        if(userId != null){
                            User.getInstance().setUserId(userId.getAsString());
                            new FirebaseAuthenticationService().sendLinkLoginToMail(User.getInstance().getEmail(), liveLoginResponse);
                        }

                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable throwable) {
                        liveLoginResponse.postError(new Exception(throwable));
                    }
                });
//                hardCodeFirebaseLogin(studentId+"@vnu.edu.vn", "abc123", liveLoginResponse);

            }
            @Override
            public void onError(Call<JsonElement> call, Exception e) {
                liveLoginResponse.postError(e);
            }
        });
        return liveLoginResponse;
    }

    private void hardCodeFirebaseLogin(String email, String password, StateLiveData<String> liveLoginResponse){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final int[] a = {0};
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> loginFirebase) {
                if (loginFirebase.isSuccessful()){
                    liveLoginResponse.postSuccess("Login successfully\n");
                } else {
                    liveLoginResponse.postError(loginFirebase.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                liveLoginResponse.postError(e);
            }
        });
    }

    private void clearSession(){
        SharedPreferencesManager.clearAll();
        FirebaseAuth.getInstance().signOut();
    }

    public StateLiveData<String> isLoggedIn() {
        final StateLiveData<String> loginState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));
        if (User.getInstance().getToken() == null) {
            loginState.postError(new Exception("require login"));
            return loginState;
        }
        HTTPClient.getInstance().request(UserRequest.class).getUserId().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body().get("errorcode") == null) {
                    Log.d("COURSES_DEBUG", response.toString());
                    loginState.postSuccess("login successfully");
                } else {
                    Log.d("COURSES_DEBUG", "login failed");
                    loginState.postError(new InvalidLoginException());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                if(throwable instanceof NoConnectivityException){
                    loginState.postSuccess("login successfully");
                    Log.d("COURSE_DEBUG", "onFailure: no internet");
                } else {
                    loginState.postError(new Exception(throwable));
                }

            }
        });
        return loginState;
    }
    public StateLiveData<String> resendVerificationMail() {
        final StateLiveData<String> liveLoginResponse = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

        new FirebaseAuthenticationService().sendLinkLoginToMail(User.getInstance().getEmail(), liveLoginResponse);

        return liveLoginResponse;
    }
}
