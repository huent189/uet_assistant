package vnu.uet.mobilecourse.assistant.repository;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.util.Util;
import java.util.Map;
import vnu.uet.mobilecourse.assistant.BuildConfig;
import vnu.uet.mobilecourse.assistant.database.DAO.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;

import vnu.uet.mobilecourse.assistant.model.firebase.NotificationType;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.model.notification.AdminNotification;
import vnu.uet.mobilecourse.assistant.repository.firebase.FirebaseUserRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.NotificationRepository;
import vnu.uet.mobilecourse.assistant.util.StringConst;

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
                                // check new user
                                if (loginViaMail.getResult().getAdditionalUserInfo().isNewUser()) {
                                    createUser(email, loginState);
                                }

                                AuthResult result = loginViaMail.getResult();
                                if (result != null && result.getAdditionalUserInfo() != null) {
                                    AdditionalUserInfo userInfo = result.getAdditionalUserInfo();
                                    boolean isNewUser = userInfo.isNewUser();

                                    if (isNewUser) {
                                        // create new user profile document
                                        User user = createNewUserProfile(email);
                                        FirebaseUserRepository.getInstance().add(user);

                                        // welcome notification
                                        AdminNotification notification = generateWelcomeNotification();
                                        NotificationRepository.getInstance().add(notification);
                                    } else {
                                        Map<String, Object> profile = userInfo.getProfile();
                                    }
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

    private User createNewUserProfile(String email) {
        User user = new User();
        String id = email.replace(StringConst.VNU_EMAIL_DOMAIN, StringConst.EMPTY);
        user.setId(id);
        user.setAvatar(null);
        user.setNewNotifications(1);

        return user;
    }

    @SuppressLint("RestrictedApi")
    private AdminNotification generateWelcomeNotification() {
        AdminNotification notification = new AdminNotification();
        notification.setId(Util.autoId());
        notification.setTitle("Xin chào sinh viên!");
        notification.setDescription(
                "Khám phá những tính năng thú vị " +
                "và tối ưu hóa hiệu suất học tập " +
                "từ Trợ lý học tập Công nghệ."
        );
        notification.setNotifyTime(System.currentTimeMillis() / 1000);
        notification.setType(NotificationType.ADMIN);
        return notification;
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