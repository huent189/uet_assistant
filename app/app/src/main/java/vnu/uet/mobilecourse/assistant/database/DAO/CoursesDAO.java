package vnu.uet.mobilecourse.assistant.database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import vnu.uet.mobilecourse.assistant.model.Course;

import java.util.List;

@Dao
public interface CoursesDAO {
    @Query("SELECT * FROM course ORDER BY id ASC")
    LiveData<List<Course>> getMyCourses();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Course course);
    //TODO: clear all table
}
