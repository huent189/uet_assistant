package vnu.uet.mobilecourse.assistant.viewmodel;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.repository.course.UserRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.StudentRepository;
import vnu.uet.mobilecourse.assistant.storage.StorageAccess;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class MyProfileViewModel extends ViewModel {

    private StudentRepository studentRepo = StudentRepository.getInstance();

    private static final String STUDENT_ID = User.getInstance().getStudentId();

    public IStateLiveData<UserInfo> getUserInfo() {
        return studentRepo.getStudentById(STUDENT_ID);
    }

    public IStateLiveData<String> changeAvatarFromFile(Uri uri) {
        return new StorageAccess().changeAvatar(STUDENT_ID, uri);
    }

    public IStateLiveData<String> changeAvatarFromCamera(Bitmap photo) {
        return new StorageAccess().changeAvatarFromCamera(STUDENT_ID, photo);
    }

    public void signOut(){
        new UserRepository().signOut();
    }
}
