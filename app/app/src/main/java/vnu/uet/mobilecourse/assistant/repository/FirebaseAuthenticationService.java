package vnu.uet.mobilecourse.assistant.repository;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import vnu.uet.mobilecourse.assistant.BuildConfig;
import vnu.uet.mobilecourse.assistant.database.DAO.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class FirebaseAuthenticationService {

    private FirebaseAuth mAuth;
    private ActionCodeSettings actionCodeSettings;

    public FirebaseAuthenticationService() {
        initFirebaseAuthentication();
    }

    private void initFirebaseAuthentication() {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("en");

        actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl("https://uet-assistant-d048c.firebaseapp.com/") // This is deeplink, not dynamiclink
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(BuildConfig.APPLICATION_ID, false, null)
                        .build();
    }


    public StateLiveData<String> sendLinkLoginToMail(String email, StateLiveData<String> validationState) {
        mAuth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> sendEmail) {
                        if (sendEmail.isSuccessful()) {
                            // TODO: sent success
                            validationState.postSuccess("Login Firebase Success");
                        } else {
                            // TODO sent fail
                            Exception exception = sendEmail.getException();
                            assert exception != null;
                            validationState.postError(exception);
                        }
                    }
                });

        return validationState;
    }

    public StateLiveData<String> signInWithEmailLink(String email, String emailLink) {
        StateLiveData<String> loginState = new StateLiveData<>();
        // Confirm the link is a sign-in with email link.
        if (mAuth.isSignInWithEmailLink(emailLink)) {

            // The client SDK will parse the code from the link for you.
            mAuth.signInWithEmailLink(email, emailLink)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> loginViaMail) {
                            if (loginViaMail.isSuccessful()) {
                                // TODO: check new user
                                if (loginViaMail.getResult().getAdditionalUserInfo().isNewUser()) {
                                    createUser(email, loginState);
                                }
                            } else {
                                // TODO: error
                                Exception exception = loginViaMail.getException();
                                assert exception != null;
                                loginState.postError(exception);
                            }
                        }
                    });
        }
        return loginState;
    }

    public static boolean isFirebaseLoggedIn (){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private StateLiveData<String> createUser(String email, StateLiveData loginState) {
        String studentId = email.substring(0,8);
        FirebaseFirestore.getInstance().collection(FirebaseCollectionName.USER_INFO).document(studentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> fbUserInfo) {
                if (fbUserInfo.isSuccessful()) {
                    UserInfo userInfo = fbUserInfo.getResult().toObject(UserInfo.class);
                    User user = new User();
                    user.setAvatar("");
                    user.setId(studentId);
                    user.setName(userInfo.getName());

                    FirebaseFirestore.getInstance().collection(FirebaseCollectionName.USER).document(user.getId()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loginState.postSuccess("create user success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loginState.postError(e);
                        }
                    });
                } else {
                    loginState.postError(fbUserInfo.getException());
                }
            }
        });

        return loginState;
    }

}