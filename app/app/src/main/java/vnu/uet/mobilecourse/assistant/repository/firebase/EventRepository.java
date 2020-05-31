package vnu.uet.mobilecourse.assistant.repository.firebase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.model.event.CourseSessionEvent;
import vnu.uet.mobilecourse.assistant.model.event.DailyEventList;
import vnu.uet.mobilecourse.assistant.model.event.EventComparator;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.repository.cache.DailyEventCache;
import vnu.uet.mobilecourse.assistant.repository.course.CourseRepository;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.util.SessionConverter;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class EventRepository {

    private static EventRepository instance;

    private TodoRepository todoRepo;
    private CourseRepository courseRepo;
    private DailyEventCache cache;

    public EventRepository() {
        todoRepo = TodoRepository.getInstance();
        courseRepo = CourseRepository.getInstance();

        cache = new DailyEventCache();
    }

    public static EventRepository getInstance() {
        if (instance == null) {
            instance = new EventRepository();
        }

        return instance;
    }

    public IStateLiveData<DailyEventList> getDailyEvent(Date date) {
        // check in cache first
        if (cache.containsKey(date)) {
            return cache.get(date);
        }
        // if not in cache, query in database
        else {
            IStateLiveData<DailyEventList> liveData =
                    new MergeDailyEventLiveData(date, todoRepo.getAllTodos(), courseRepo.getAllCourseInfos());

            cache.put(date, liveData);
            return liveData;
        }
    }

    public static class MergeDailyEventLiveData extends StateMediatorLiveData<DailyEventList> {

        private List<Todo> todoList;
        private List<CourseSessionEvent> courseSessions;
        private boolean todoSuccess;
        private boolean courseSessionSuccess;

        private Date date;

        public MergeDailyEventLiveData(Date date, StateLiveData<List<Todo>> todoList,
                                       StateLiveData<List<CourseInfo>> courseInfos) {
            postLoading();

            this.date = date;

            addSource(todoList, new Observer<StateModel<List<Todo>>>() {
                @Override
                public void onChanged(StateModel<List<Todo>> stateModel) {
                    switch (stateModel.getStatus()) {
                        case LOADING:
                            todoSuccess = false;
                            postLoading();
                            break;

                        case ERROR:
                            todoSuccess = false;
                            postError(stateModel.getError());
                            break;

                        case SUCCESS:
                            todoSuccess = true;

                            List<Todo> todoByDay = stateModel.getData().stream()
                                    .filter(todo -> DateTimeUtils.isSameDate(date, todo.getTime()))
                                    .sorted(new EventComparator())
                                    .collect(Collectors.toList());

                            setTodoList(todoByDay);

                            if (todoSuccess && courseSessionSuccess) {
                                DailyEventList dailyEventList = combineData();
                                postSuccess(dailyEventList);
                            }
                    }
                }
            });

            addSource(courseInfos, new Observer<StateModel<List<CourseInfo>>>() {
                @Override
                public void onChanged(StateModel<List<CourseInfo>> stateModel) {
                    switch (stateModel.getStatus()) {
                        case LOADING:
                            courseSessionSuccess = false;
                            postLoading();
                            break;

                        case ERROR:
                            courseSessionSuccess = false;
                            postError(stateModel.getError());
                            break;

                        case SUCCESS:
                            courseSessionSuccess = true;

                            List<CourseSessionEvent> courseSessionEvents = new ArrayList<>();

                            stateModel.getData().forEach(courseInfo -> {
                                for (CourseSession session : courseInfo.getSessions()) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(date);

                                    if (session.getDayOfWeek() == calendar.get(Calendar.DAY_OF_WEEK)) {
                                        CourseSessionEvent event = SessionConverter.toEvent(session, date);
                                        courseSessionEvents.add(event);
                                    }
                                }
                            });

                            setCourseSessions(courseSessionEvents);

                            if (todoSuccess && courseSessionSuccess) {
                                DailyEventList dailyEventList = combineData();
                                postSuccess(dailyEventList);
                            }
                    }
                }
            });
        }

        private void setTodoList(List<Todo> todoList) {
            this.todoList = todoList;
        }

        private void setCourseSessions(List<CourseSessionEvent> courseSessions) {
            this.courseSessions = courseSessions;
        }

        private DailyEventList combineData() {
            DailyEventList dailyEventList = new DailyEventList(date);

            dailyEventList.addAll(todoList);
            dailyEventList.addAll(courseSessions);

            return dailyEventList;
        }
    }
}
