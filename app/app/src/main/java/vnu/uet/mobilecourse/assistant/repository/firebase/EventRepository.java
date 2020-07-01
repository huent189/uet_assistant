package vnu.uet.mobilecourse.assistant.repository.firebase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import vnu.uet.mobilecourse.assistant.model.FinalExam;
import vnu.uet.mobilecourse.assistant.model.event.CourseSessionEvent;
import vnu.uet.mobilecourse.assistant.model.event.CourseSubmissionEvent;
import vnu.uet.mobilecourse.assistant.model.event.DailyEventList;
import vnu.uet.mobilecourse.assistant.model.event.EventComparator;
import vnu.uet.mobilecourse.assistant.model.event.IEvent;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseSession;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.repository.cache.DailyEventCache;
import vnu.uet.mobilecourse.assistant.repository.course.CourseRepository;
import vnu.uet.mobilecourse.assistant.repository.course.MaterialRepository;
import vnu.uet.mobilecourse.assistant.repository.course.PortalRepository;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.util.SessionConverter;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class EventRepository {

    private static EventRepository instance;

    private TodoRepository mTodoRepo;
    private CourseRepository mCourseRepo;
    private MaterialRepository mMaterialRepo;
    private DailyEventCache mCache;

    private EventRepository() {
        mTodoRepo = TodoRepository.getInstance();
        mCourseRepo = CourseRepository.getInstance();
        mMaterialRepo = MaterialRepository.getInstance();

        mCache = new DailyEventCache();
    }

    public static EventRepository getInstance() {
        if (instance == null) {
            instance = new EventRepository();
        }

        return instance;
    }

    public IStateLiveData<DailyEventList> getDailyEvent(Date date) {
        String dateInString = DateTimeUtils.DATE_FORMAT.format(date);

        // check in cache first
        if (mCache.containsKey(dateInString)) {
            return mCache.get(dateInString);
        }
        // if not in cache, query in database
        else {
            StateLiveData<List<Todo>> allTodo = mTodoRepo.getAllTodos();
            StateLiveData<List<CourseInfo>> allCourseInfo = mCourseRepo.getAllCourseInfos();
            StateLiveData<List<CourseSubmissionEvent>> submissionEvents = mMaterialRepo
                    .getDailyCourseSubmissionEvent(date);
            LiveData<List<FinalExam>> exams = new PortalRepository().getFinalExamByDay(date);

            IStateLiveData<DailyEventList> liveData =
                    new MergeDailyEventLiveData(date, allTodo, allCourseInfo, exams, submissionEvents);

            mCache.put(dateInString, liveData);
            return liveData;
        }
    }

    public static class MergeDailyEventLiveData extends StateMediatorLiveData<DailyEventList> {

        private List<Todo> todoList;
        private List<CourseSessionEvent> courseSessions;
        private List<CourseSubmissionEvent> submissionEvents;
        private List<FinalExam> finalExams;

        private boolean todoSuccess;
        private boolean courseSessionSuccess;
        private boolean submissionSuccess;
        private boolean examSuccess;

        private Date date;

        public MergeDailyEventLiveData(Date date, StateLiveData<List<Todo>> todoList,
                                       StateLiveData<List<CourseInfo>> courseInfo,
                                       LiveData<List<FinalExam>> exams,
                                       StateLiveData<List<CourseSubmissionEvent>> submissionEvents) {
            postLoading();

            this.date = date;

            addSource(exams, new Observer<List<FinalExam>>() {
                @Override
                public void onChanged(List<FinalExam> results) {
                    if (results == null) postLoading();
                    else {
                        examSuccess = true;
                        finalExams = results;

                        if (examSuccess && todoSuccess && courseSessionSuccess && submissionSuccess) {
                            DailyEventList dailyEventList = combineData();
                            postSuccess(dailyEventList);
                        }
                    }
                }
            });

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

                            if (examSuccess && todoSuccess && courseSessionSuccess && submissionSuccess) {
                                DailyEventList dailyEventList = combineData();
                                postSuccess(dailyEventList);
                            }
                    }
                }
            });

            addSource(submissionEvents, new Observer<StateModel<List<CourseSubmissionEvent>>>() {
                @Override
                public void onChanged(StateModel<List<CourseSubmissionEvent>> stateModel) {
                    switch (stateModel.getStatus()) {
                        case LOADING:
                            submissionSuccess = false;
                            postLoading();

                            break;

                        case ERROR:
                            submissionSuccess = false;
                            postError(stateModel.getError());

                            break;

                        case SUCCESS:
                            submissionSuccess = true;
                            setSubmissionEvents(stateModel.getData());

                            if (examSuccess && todoSuccess && courseSessionSuccess && submissionSuccess) {
                                DailyEventList dailyEventList = combineData();
                                postSuccess(dailyEventList);
                            }

                            break;
                    }
                }
            });

            addSource(courseInfo, new Observer<StateModel<List<CourseInfo>>>() {
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

                                    int selectedDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                                    if (session.getDayOfWeek() == selectedDayOfWeek) {
                                        CourseSessionEvent event = SessionConverter.toEvent(session, date);
                                        courseSessionEvents.add(event);
                                    }
                                }
                            });

                            setCourseSessions(courseSessionEvents);

                            if (examSuccess && todoSuccess && courseSessionSuccess && submissionSuccess) {
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

        private void setSubmissionEvents(List<CourseSubmissionEvent> submissionEvents) {
            this.submissionEvents = submissionEvents;
        }

        private DailyEventList combineData() {
            DailyEventList dailyEventList = new DailyEventList(date);

            dailyEventList.addAll(todoList);
            dailyEventList.addAll(courseSessions);
            dailyEventList.addAll(submissionEvents);
            dailyEventList.addAll(finalExams);

            return dailyEventList;
        }
    }
}
