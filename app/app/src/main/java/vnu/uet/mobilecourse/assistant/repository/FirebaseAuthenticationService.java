package vnu.uet.mobilecourse.assistant.repository;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.util.Util;

import java.util.Map;

import vnu.uet.mobilecourse.assistant.BuildConfig;
import vnu.uet.mobilecourse.assistant.model.firebase.NotificationType;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.model.notification.AdminNotification;
import vnu.uet.mobilecourse.assistant.repository.firebase.FirebaseUserRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.NotificationRepository;
import vnu.uet.mobilecourse.assistant.util.CONST;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

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
                                // TODO: validate success
                                loginState.postSuccess("login Firebase succeeds");
                                // You can access the new user via result.getUser()
                                // Additional user info profile *not* available via:
                                // result.getAdditionalUserInfo().getProfile() == null
                                // You can check if the user is new or existing:
                                // result.getAdditionalUserInfo().isNewUser()
                                // TODO: check new user
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
        String id = email.replace(CONST.VNU_EMAIL_DOMAIN, CONST.EMPTY);
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

}