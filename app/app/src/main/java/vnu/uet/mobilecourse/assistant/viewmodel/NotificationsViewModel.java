package vnu.uet.mobilecourse.assistant.viewmodel;

import androidx.lifecycle.ViewModel;

import java.util.List;

import vnu.uet.mobilecourse.assistant.model.notification.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.repository.firebase.NotificationRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class NotificationsViewModel extends ViewModel {

    private NotificationRepository notifyRepo = NotificationRepository.getInstance();

    public IStateLiveData<List<Notification_UserSubCol>> getNotifications() {
        return notifyRepo.getAllNotifications();
    }

    public IStateLiveData<String> deleteNotification(Notification_UserSubCol notification) {
        return notifyRepo.delete(notification);
    }
}