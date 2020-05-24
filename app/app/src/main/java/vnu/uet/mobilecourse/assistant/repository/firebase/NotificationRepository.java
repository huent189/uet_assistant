package vnu.uet.mobilecourse.assistant.repository.firebase;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.database.DAO.NotificationDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.TodoDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.TodoListDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.Notification_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.model.todo.TodoComparator;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.DeepTodoListsStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

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

    public IStateLiveData<Notification_UserSubCol> add(Notification_UserSubCol noti) {
        return dao.add(noti.getId(), noti);
    }
}
