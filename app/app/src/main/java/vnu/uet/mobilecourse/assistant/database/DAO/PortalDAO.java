package vnu.uet.mobilecourse.assistant.database.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import vnu.uet.mobilecourse.assistant.model.FinalExam;

import java.util.List;

@Dao
public interface PortalDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertFinalExam(List<FinalExam> exams);
    @Query("SELECT * from FinalExam")
    public List<FinalExam> getAllFinalExams();
}
