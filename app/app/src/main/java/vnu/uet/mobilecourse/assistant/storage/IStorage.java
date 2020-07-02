package vnu.uet.mobilecourse.assistant.storage;

import android.net.Uri;

import com.google.firebase.storage.StorageReference;

import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public interface IStorage {

    /**
     *
     * @param groupId id of group chat
     * @param localPath path to local file
     * @return
     */
//    IStateLiveData<String> uploadFileToGroupChat(String groupId, String localPath);
    IStateLiveData<String> uploadFileToGroupChat(String groupId, Uri fileURI, Message_GroupChatSubCol message, String[] memberIds, String[] tokes, String groupName);

    /**
     *
     * @param Id id of user or group
     * @param localPath path to file in local
     * @return LiveData contains path to avatar on cloud
     */
    IStateLiveData<String> changeAvatar(String Id, Uri uri);

    String denormalizeFileName(String localPath);

    StorageReference getAvatar(String Id);

    IStateLiveData<String> downLoadFile(String cloudFileName);

}