package vnu.uet.mobilecourse.assistant;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;

public class ChatNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d("RECEIMESSAGE", "onMessageReceived: ");
        super.onMessageReceived(remoteMessage);
    }
}
