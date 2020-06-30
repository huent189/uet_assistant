package vnu.uet.mobilecourse.assistant.Storage;

import android.net.Uri;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;


import vnu.uet.mobilecourse.assistant.database.DAO.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

interface IStorageInterface {

    /**
     * flow: upload file -> update message
     * @param groupId id of group chat
     * @param localPath path to local file
     * @param message message has been init at the time send file and this step only update path to
     *                attachment
     * @param memberIds
     * @return
     */
    IStateLiveData<String> uploadFileToGroupChat(String groupId, String localPath, Message_GroupChatSubCol message, String[] memberIds);

    /**
     *
     * @param Id id of user or group
     * @param localPath path to file in local
     * @return LiveData contains path to avatar on cloud
     */
    IStateLiveData<String> changeAvatar(String Id, String localPath);


    StorageReference getAvatar(String Id);

    /**
     * remove time stamp from file name
     * @param fileName full file name with unix timestamp
     * @return original file name
     */
    String denormalizeFileName (String fileName);

}

public class Storage implements IStorageInterface {
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
    public IStateLiveData<String> changeAvatar(String Id, String localPath) {
        IStateLiveData<String> changeAvatarState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));
        Uri fileURI = Uri.fromFile(new File(localPath));
        // delete old avatar
        StorageReference avatarRef = storage.child(Storage.AVATAR_DIR).child(Id).child(Storage.AVATAR_FILENAME);
        avatarRef.delete().addOnSuccessListener(aVoid -> { // delete old avatar
            avatarRef.putFile(fileURI).addOnCompleteListener(task -> { // upload new avatar
                if (task.isSuccessful()) {
                    changeAvatarState.postSuccess(avatarRef.getPath());
                } else {
                    changeAvatarState.postError(task.getException());
                }
            });
        });

        return changeAvatarState;
    }

    @Override
    public StorageReference getAvatar(String Id) {
        return storage.child(Storage.AVATAR_DIR).child(Id).child(Storage.AVATAR_FILENAME);
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
