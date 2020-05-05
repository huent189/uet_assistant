package vnu.uet.mobilecourse.assistant.repository;

import android.util.Log;
import com.google.gson.JsonObject;
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
                hardCodeFirebaseLogin(studentId+"@vnu.edu.vn", "abc123", liveLoginResponse);
            }
            @Override
            public void onError(Exception e) {
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
}
