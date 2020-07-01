package vnu.uet.mobilecourse.assistant.repository.firebase.chatnoti;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String token;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {

        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        // TODO: update token to server

    }
}
