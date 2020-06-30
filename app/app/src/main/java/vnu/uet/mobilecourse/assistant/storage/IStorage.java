package vnu.uet.mobilecourse.assistant.storage;

import com.google.firebase.storage.StorageReference;

import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public interface IStorage {

    /**
     *
     * @param groupId id of group chat
     * @param localPath path to local file
     * @return
     */
    IStateLiveData<String> uploadFileToGroupChat(String groupId, String localPath);

    /**
     *
     * @param Id id of user or group
     * @param localPath path to file in local
     * @return LiveData contains path to avatar on cloud
     */
    IStateLiveData<String> changeAvatar(String Id, String localPath);


    StorageReference getAvatar(String Id);

}