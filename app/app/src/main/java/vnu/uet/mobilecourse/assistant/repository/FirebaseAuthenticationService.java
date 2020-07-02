package vnu.uet.mobilecourse.assistant.repository;

import android.annotation.SuppressLint;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.util.Util;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.BuildConfig;
import vnu.uet.mobilecourse.assistant.model.notification.NotificationType;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.model.notification.AdminNotification;
import vnu.uet.mobilecourse.assistant.repository.firebase.FirebaseUserRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.NotificationRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.StudentRepository;
import vnu.uet.mobilecourse.assistant.util.StringConst;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
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
                        .setUrl("http://uet-assisstant.ddns.net/") // This is deeplink, not dynamiclink
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
                            // sent success
                            validationState.postSuccess("Login Firebase Success");
                        } else {
                            // sent fail
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

//                                        // update firebase user profile for quick use
//                                        updateFirebaseUser(email, loginState);

                                    }

                                    updateUserProfile(email, new Runnable() {
                                        @Override
                                        public void run() {
                                            loginState.postSuccess("login Firebase succeeds");
                                        }
                                    });

                                } else {
                                    loginState.postError(new Exception("Null Auth Result or Null User Info."));
                                }

                            } else {
                                // error
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
//        user.setAvatar(null);
        user.setNewNotifications(1);

        return user;
    }

    private void updateUserProfile(String email, Runnable onSuccessCallback) {
        String id = email.replace(StringConst.VNU_EMAIL_DOMAIN, StringConst.EMPTY);

        vnu.uet.mobilecourse.assistant.model.User user = vnu.uet.mobilecourse.assistant.model.User.getInstance();

        IStateLiveData<UserInfo> userInfoLiveData = StudentRepository.getInstance().getStudentById(id);

        userInfoLiveData.observeForever(new Observer<StateModel<UserInfo>>() {
            @Override
            public void onChanged(StateModel<UserInfo> stateModel) {
                if (stateModel.getStatus() == StateStatus.SUCCESS) {
                    UserInfo userInfo = stateModel.getData();
                    user.setName(userInfo.getName());
                    user.setDob(userInfo.getDOB());
                    user.setUetClass(userInfo.getUetClass());

                    onSuccessCallback.run();

                    userInfoLiveData.removeObserver(this);
                }
            }
        });

    }

    private void updateFirebaseUser(String email, StateLiveData<String> loginState) {
        String id = email.replace(StringConst.VNU_EMAIL_DOMAIN, StringConst.EMPTY);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.updateEmail(email);

            IStateLiveData<UserInfo> userInfo = StudentRepository.getInstance().getStudentById(id);

            userInfo.observeForever(new Observer<StateModel<UserInfo>>() {
                @Override
                public void onChanged(StateModel<UserInfo> stateModel) {
                    if (stateModel.getStatus() == StateStatus.SUCCESS) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(stateModel.getData().getName())
                                .build();

                        firebaseUser.updateProfile(profileUpdates);

                        loginState.postSuccess("login Firebase succeeds");

                        userInfo.removeObserver(this);
                    }
                }
            });
        }
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

    public static boolean isFirebaseLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }


}