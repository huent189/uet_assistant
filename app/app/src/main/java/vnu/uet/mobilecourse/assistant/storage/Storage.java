package vnu.uet.mobilecourse.assistant.storage;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;


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
    public IStateLiveData<String> uploadFileToGroupChat(String groupId, String localPath) {
        return null;
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
        StorageReference reference = storage.child(Storage.AVATAR_DIR).child(Id).child(Storage.AVATAR_FILENAME);
        return reference;
    }
}
