package vnu.uet.mobilecourse.assistant.repository;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
                // hard code login firebase
                if(loginResponse.isSuccess()){
                    Token.setToken(loginResponse.getPrivateToken());
//                    liveLoginResponse.postSuccess("Login successfully\n"+ loginResponse.toString());
                    hardCodeFirebaseLogin(studentId+"@vnu.edu.vn", "abc123", liveLoginResponse);

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
}
