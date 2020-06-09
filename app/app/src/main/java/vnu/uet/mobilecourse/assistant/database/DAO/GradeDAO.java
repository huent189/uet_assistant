package vnu.uet.mobilecourse.assistant.database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import vnu.uet.mobilecourse.assistant.model.Grade;

import java.util.List;
@Dao
public interface GradeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertGrade(Grade... grades);
    @Query("select * from Grade where courseId = :courseId and type != \"course\" order by gradedDate DESC")
    public abstract LiveData<List<Grade>> getGrades(int courseId);
    @Query("select * from Grade where courseId = :courseId and type = \"course\"")
    public abstract LiveData<Grade> getTotalGrade(int courseId);
}
