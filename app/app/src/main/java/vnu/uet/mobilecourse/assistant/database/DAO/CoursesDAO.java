package vnu.uet.mobilecourse.assistant.database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import vnu.uet.mobilecourse.assistant.model.*;

import java.util.List;

@Dao
public interface CoursesDAO {
    @Query("SELECT * FROM course ORDER BY id ASC")
    LiveData<List<Course>> getMyCourses();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourse(Course... course);
    @Query("UPDATE Course SET lastAccessTime =:accessTime WHERE id = :id")
    void updateLastAccessTime(int id, long accessTime);
    //TODO: clear all table
}
