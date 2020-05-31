package vnu.uet.mobilecourse.assistant.repository.firebase;

import java.util.List;

import vnu.uet.mobilecourse.assistant.database.DAO.NotificationDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

public class NotificationRepository {
    private static NotificationRepository instance;

    private StateLiveData<List<Notification_UserSubCol>> liveData;

    private NotificationDAO dao = new NotificationDAO();

    public NotificationRepository() {
        liveData = dao.readAll();
    }

    public static NotificationRepository getInstance() {
        if (instance == null) {
            instance = new NotificationRepository();
        }

        return instance;
    }

    public StateLiveData<List<Notification_UserSubCol>> getAllNotifications() {
        return liveData;
    }

    public IStateLiveData<Notification_UserSubCol> add(Notification_UserSubCol notification) {
        return dao.add(notification.getId(), notification);
    }

    public IStateLiveData<String> delete(Notification_UserSubCol notification) {
        return dao.delete(notification.getId());
    }
}
