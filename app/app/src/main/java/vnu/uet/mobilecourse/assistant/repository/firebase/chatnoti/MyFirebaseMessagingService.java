package vnu.uet.mobilecourse.assistant.repository.firebase.chatnoti;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vnu.uet.mobilecourse.assistant.database.DAO.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String token;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Log.d("OnMess: ", "onMessageReceived: " + remoteMessage.getData());


    }

    @Override
    public void onNewToken(@NonNull String token) {
        // TODO: update token to server
        updateToken(token);
    }

    private void updateToken(String token) {
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
}
