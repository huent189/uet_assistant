package vnu.uet.mobilecourse.assistant.repository.firebase.chatnoti;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.database.DAO.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.util.ChatRoomTracker;
import vnu.uet.mobilecourse.assistant.util.NotificationHelper;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.view.chat.ChatFragment;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public MyFirebaseMessagingService() {

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();

        String roomId = data.get("groupId");
        if (!isAlreadyInRoom(roomId)) {
            pushNotification(getApplicationContext(), data);
        }

        Log.d("OnMess: ", "onMessageReceived: " + remoteMessage.getData());
    }

    private boolean isAlreadyInRoom(String roomId) {
        String currentRoomId = ChatRoomTracker.getInstance().getCurrentRoom();

        return currentRoomId != null && currentRoomId.equals(roomId);
    }

    protected void pushNotification(Context context, Map<String, String> data) {
        String groupId = data.get("groupId");
        String groupName = data.get("groupName");
        String senderName = data.get("senderName");
        String content = data.get("content");

        String title = groupName;
        String desc = StringUtils.getLastSegment(senderName) + ": " + content;

        String notificationId = groupId + System.currentTimeMillis();

        Notification notification = NotificationHelper.getsInstance()
                .build(context, getClass().getName(), R.drawable.ic_chat_24dp,
                        title, desc, ChatFragment.class.getName());

        NotificationHelper.getsInstance().notify(context, notificationId, notification);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        updateToken(token);
    }

    public void updateToken(String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(FirebaseCollectionName.USER)
                .document(User.getInstance().getStudentId())
                .collection(FirebaseCollectionName.GROUP_CHAT)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<String> groupIds = queryDocumentSnapshots.getDocuments().stream().map(d -> d.toObject(GroupChat_UserSubCol.class).getId()).filter(Objects::nonNull).collect(Collectors.toList());
            WriteBatch batch  = db.batch();
            for (String groupId :
                    groupIds) {
                DocumentReference memberRef = db.collection(FirebaseCollectionName.GROUP_CHAT)
                        .document(groupId)
                        .collection(FirebaseCollectionName.MEMBER)
                        .document(User.getInstance().getStudentId());
                batch.update(memberRef, "token", token);
            }

            batch.commit();
        });
    }

    public void send(JsonObject payload){
        Client.getApiService().sendNotification(payload).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // TODO: Success
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // TODO: fail
            }
        });
    }

    public JsonObject buildNotificationPayload(Data data){
        String token = "dVPq05uZRpOu9pYaf5JIJK:APA91bFLTU6XvALk2TzZ55liRuTROZVOupf7UMtzLV8kWB4-tVZx6Mo0qleMvHCEu4kvZT5eyFQLA2XQ06oDJfyL82aZKwyjXa-fSHZS25bvFonr7QyOAarIq2vj4Dj1U6t60u3nrJnQ";
        JsonObject payload = new JsonObject();

        payload.addProperty("to", token);
        payload.add("data", data.toJSON());

        return payload;
    }
}
