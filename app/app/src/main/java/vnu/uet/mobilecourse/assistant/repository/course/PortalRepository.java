package vnu.uet.mobilecourse.assistant.repository.course;

import androidx.lifecycle.LiveData;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.database.DAO.PortalDAO;
import vnu.uet.mobilecourse.assistant.exception.SQLiteRecordNotFound;
import vnu.uet.mobilecourse.assistant.model.FinalExam;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.network.PortalClient;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PortalRepository {
    private static final int DAY_DURATION_MILLIS = 24 * 60 *60 * 1000;
    private PortalDAO dao;
    public PortalRepository() {
        dao = CoursesDatabase.getDatabase().portalDAO();
    }
    public List<FinalExam> syncFinalExams() throws ParseException {
        List<FinalExam> old = dao.getAllFinalExams();
        List<FinalExam> newList = null;
        try {
             newList = PortalClient.getFinalExamSchedule(User.getInstance().getStudentId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(newList == null){
            return null;
        }
        if(old == null || old.isEmpty()){
            dao.insertFinalExam(newList);
            return null;
        } else {
            List<FinalExam> updateExams = newList.stream().filter(exam -> !old.contains(exam)).collect(Collectors.toList());
            dao.insertFinalExam(updateExams);
            return updateExams;
        }
    }
    public IStateLiveData<FinalExam> getFinalExamByCourse(String courseCode){
        StateLiveData<FinalExam> exam = new StateLiveData<>();
        exam.postLoading();
        new Thread(() -> {
            FinalExam model = dao.getExamByCourseCode(courseCode);
            if(model == null){
                exam.postError(new SQLiteRecordNotFound("final exam " + courseCode));
            } else {
                exam.postSuccess(model);
            }
        }).start();
        return exam;
    }

    public LiveData<List<FinalExam>> getFinalExamByDay(Date date) {
        new Thread(() -> {
            try {
                syncFinalExams();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }).start();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.clear();
        calendar.set(year, month, day);
        long startTime = calendar.getTimeInMillis();
        long endTime = startTime + DAY_DURATION_MILLIS;
        return dao.getExamByTime(startTime, endTime);
    }
}
