package vnu.uet.mobilecourse.assistant.storage;

import android.net.Uri;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import vnu.uet.mobilecourse.assistant.database.DAO.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.database.DAO.UserDAO;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class Storage implements IStorage {
    public static final String AVATAR_DIR = "AVATAR";
    public static final String GROUP_DIR = "GROUP";
    public static final String AVATAR_FILENAME = "avatar.jpg";
    StorageReference storage = FirebaseStorage.getInstance().getReference();

    @Override
    public IStateLiveData<String> uploadFileToGroupChat(String groupId, String localPath, Message_GroupChatSubCol message, String[] memberIds) {
        Uri fileURI = Uri.fromFile(new File(localPath));
        String fileName = normalizeFileName(fileURI);
        IStateLiveData<String> uploadFileState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));
        StorageReference fileRef = storage.child(Storage.GROUP_DIR).child(groupId).child(fileName);
        fileRef.putFile(fileURI).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                // update message
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                WriteBatch batch = db.batch();
                // update message to group:

                // set path to attachment
                message.setAttachmentPath(fileRef.getPath());
                batch.set(db.collection(FirebaseCollectionName.GROUP_CHAT).document(groupId).collection(FirebaseCollectionName.MESSAGE).document(message.getId()), message);

                for (String memberId :
                        memberIds) {
                            DocumentReference groupChatSubColRef = db.collection(FirebaseCollectionName.USER)
                            .document(memberId)
                            .collection(FirebaseCollectionName.GROUP_CHAT)
                            .document(groupId);
                            batch.update(groupChatSubColRef, "lastMessage", message.getFromName() + " has sent an attachment",
                                                "lastMessageTime", message.getTimestamp());
                }

                batch.commit().addOnSuccessListener(aVoid -> {
                    uploadFileState.postSuccess(fileRef.getPath());
                }).addOnFailureListener(e -> {
                    uploadFileState.postError(e);
                });
            } else {
                uploadFileState.postError(task.getException());
            }
        });

        return uploadFileState;
    }

    @Override
    public IStateLiveData<String> changeAvatar(String Id, Uri fileURI) {
        IStateLiveData<String> changeAvatarState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));
        StorageReference avatarRef = storage.child(Storage.AVATAR_DIR).child(Id).child(Storage.AVATAR_FILENAME);
            avatarRef.putFile(fileURI).addOnCompleteListener(task -> { // upload new avatar
                if (task.isSuccessful()) {
                    Map<String, Object> changes = new HashMap<>();
                    changes.put("avatar", System.currentTimeMillis() / 1000);
                    new UserDAO().update(Id, changes);
                    changeAvatarState.postSuccess(avatarRef.getPath());
                } else {
                    changeAvatarState.postError(task.getException());
                }
            });

        return changeAvatarState;
    }

    @Override
    public StorageReference getAvatar(String Id) {
        StorageReference reference = storage.child(Storage.AVATAR_DIR).child(Id).child(Storage.AVATAR_FILENAME);
        return reference;
    }

    @Override
    public String denormalizeFileName(String localPath) {
        return localPath.substring(10);
    }

    private String normalizeFileName(Uri fileURI) {
        long unixTime = System.currentTimeMillis() / 1000L;
        return Long.toString(unixTime) + fileURI.getLastPathSegment();
    }

}
