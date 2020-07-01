package vnu.uet.mobilecourse.assistant.database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import vnu.uet.mobilecourse.assistant.model.FinalExam;

import java.util.List;

@Dao
public interface PortalDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFinalExam(List<FinalExam> exams);
    @Query("SELECT * from FinalExam")
    List<FinalExam> getAllFinalExams();
    @Query("SELECT * from FinalExam")
    LiveData<List<FinalExam>> getAllFinalExamsAsync();
    @Query("SELECT * FROM FinalExam WHERE classCode = :code")
    FinalExam getExamByCourseCode(String code);
    @Query("SELECT * FROM FinalExam WHERE examTime >= :start and examTime <= :end")
    LiveData<List<FinalExam>> getExamByTime(long start, long end);
}
