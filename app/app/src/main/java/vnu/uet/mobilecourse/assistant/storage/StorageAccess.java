package vnu.uet.mobilecourse.assistant.storage;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


import androidx.annotation.NonNull;
import vnu.uet.mobilecourse.assistant.database.DAO.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.database.DAO.UserDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.repository.firebase.chatnoti.Data;
import vnu.uet.mobilecourse.assistant.repository.firebase.chatnoti.MyFirebaseMessagingService;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class StorageAccess implements IStorage {
    public static final String AVATAR_DIR = "AVATAR";
    public static final String GROUP_DIR = "GROUP";
    public static final String AVATAR_FILENAME = "avatar.jpg";
    StorageReference storage = FirebaseStorage.getInstance().getReference();

    @Override
    public IStateLiveData<String> uploadFileToGroupChat(String groupId, Uri fileURI, Message_GroupChatSubCol message, String[] memberIds, String[] tokens, String groupName) {
        String fileName = normalizeFileName(fileURI);
        IStateLiveData<String> uploadFileState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));
        StorageReference fileRef = storage.child(StorageAccess.GROUP_DIR).child(groupId).child(fileName);
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
                            String simpleName = StringUtils.getLastSegment(message.getFromName(), 2);
                            batch.update(groupChatSubColRef, "lastMessage", simpleName + " đã gửi một tệp tin",
                                                "lastMessageTime", message.getTimestamp());
                }


                Data data = new Data(groupId, groupName, message.getFromName(), " đã gửi một tệp đính kèm");
                new MyFirebaseMessagingService().pushNoti(data, tokens);

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
        StorageReference avatarRef = storage.child(StorageAccess.AVATAR_DIR).child(Id).child(StorageAccess.AVATAR_FILENAME);
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

    public IStateLiveData<String> changeGroupAvatar(String Id, String[] memberIds, Uri fileURI) {
        IStateLiveData<String> changeAvatarState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));
        StorageReference avatarRef = storage.child(StorageAccess.AVATAR_DIR).child(Id).child(StorageAccess.AVATAR_FILENAME);
        avatarRef.putFile(fileURI).addOnCompleteListener(task -> { // upload new avatar
            if (task.isSuccessful()) {
                Map<String, Object> changes = new HashMap<>();
                changes.put("avatar", System.currentTimeMillis() / 1000);

                WriteBatch batch = FirebaseFirestore.getInstance().batch();

                for (String memberId : memberIds) {
                    DocumentReference memberDocRef = FirebaseFirestore.getInstance()
                            .collection(FirebaseCollectionName.USER)
                            .document(memberId)
                            .collection(FirebaseCollectionName.GROUP_CHAT)
                            .document(Id);

                    batch.update(memberDocRef, changes);
                }

                batch.commit();

                changeAvatarState.postSuccess(avatarRef.getPath());
            } else {
                changeAvatarState.postError(task.getException());
            }
        });

        return changeAvatarState;
    }

    public IStateLiveData<String> changeRoomAvatarFromCamera(String id, String[] memberIds, Bitmap photo) {
        IStateLiveData<String> changeAvatarState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

        StorageReference avatarRef = storage.child(StorageAccess.AVATAR_DIR).child(id).child(StorageAccess.AVATAR_FILENAME);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] b = stream.toByteArray();

        avatarRef.putBytes(b).addOnCompleteListener(task -> { // upload new avatar
            if (task.isSuccessful()) {
                Map<String, Object> changes = new HashMap<>();
                changes.put("avatar", System.currentTimeMillis() / 1000);

                WriteBatch batch = FirebaseFirestore.getInstance().batch();

                for (String memberId : memberIds) {
                    DocumentReference memberDocRef = FirebaseFirestore.getInstance()
                            .collection(FirebaseCollectionName.USER)
                            .document(memberId)
                            .collection(FirebaseCollectionName.GROUP_CHAT)
                            .document(id);

                    batch.update(memberDocRef, changes);
                }

                batch.commit();

                changeAvatarState.postSuccess(avatarRef.getPath());
            } else {
                changeAvatarState.postError(task.getException());
            }
        });

        return changeAvatarState;
    }

    @Override
    public StorageReference getAvatar(String Id) {
        StorageReference reference = storage.child(StorageAccess.AVATAR_DIR).child(Id).child(StorageAccess.AVATAR_FILENAME);
        return reference;
    }

    @Override
    public IStateLiveData<String> downLoadFile(String cloudPath) {
        StorageReference fileRef = storage.child(cloudPath.substring(1));
        File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileRef.getName());

        IStateLiveData<String> downloadFileState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

        fileRef.getFile(localFile).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                downloadFileState.postSuccess("Download file success");
            } else {
                downloadFileState.postError(task.getException());
            }
        });

        return downloadFileState;

    }

    @Override
    public String denormalizeFileName(String localPath) {
        return localPath.substring(10);
    }

    private String normalizeFileName(Uri fileURI) {
        long unixTime = System.currentTimeMillis() / 1000L;
        return Long.toString(unixTime) + fileURI.getLastPathSegment();
    }

    public IStateLiveData<String> changeAvatarFromCamera(String id, Bitmap photo) {
        IStateLiveData<String> changeAvatarState = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] b = stream.toByteArray();
        StorageReference avatarRef = storage.child(StorageAccess.AVATAR_DIR).child(id).child(StorageAccess.AVATAR_FILENAME);
        avatarRef.putBytes(b)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Map<String, Object> changes = new HashMap<>();
                    changes.put("avatar", System.currentTimeMillis() / 1000);
                    new UserDAO().update(id, changes);
                    changeAvatarState.postSuccess(avatarRef.getPath());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    changeAvatarState.postError(e);
                }
            });

        return changeAvatarState;
    }
}