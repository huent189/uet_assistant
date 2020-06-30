package vnu.uet.mobilecourse.assistant.repository.course;

import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.database.DAO.PortalDAO;
import vnu.uet.mobilecourse.assistant.model.FinalExam;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.network.PortalClient;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

public class PortalRepository {
    private PortalDAO dao;
    private List<FinalExam> updateExams = null;
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
            return old;
        }
        if(old == null || old.isEmpty()){
            updateExams =  newList;
        } else {
            updateExams = newList.stream().filter(exam -> !old.contains(exam)).collect(Collectors.toList());
        }
        dao.insertFinalExam( updateExams );
        return newList;
    }
    
    public IStateLiveData<List<FinalExam>> getFinalExams(){
        StateLiveData<List<FinalExam>> exams = new StateLiveData<>();
        exams.postLoading();
        new Thread(() -> {
            try {
                exams.postSuccess(syncFinalExams());
            } catch (ParseException e) {
                exams.postError(e);
            }
        }).start();
        return exams;
    }

    public List<FinalExam> getUpdateExams() {
        return updateExams;
    }
}
