package vnu.uet.mobilecourse.assistant.database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteQuery;
import vnu.uet.mobilecourse.assistant.database.querymodel.IdNamePair;
import vnu.uet.mobilecourse.assistant.model.Course;

import java.util.List;

@Dao
public abstract class CoursesDAO {
    @Query("SELECT * FROM course ORDER BY id ASC")
    public abstract LiveData<List<Course>> getMyCourses();
    @Query("SELECT id FROM course ORDER BY id ASC")
    public abstract int[] getCourseId();
    public void insertCourse(Course... course){
        if(numRow() != course.length){
            deleteAll();
//            rawQueryExceute(new SimpleSQLiteQuery("PRAGMA foreign_keys = OFF"));
            insert(course);
//            rawQueryExceute(new SimpleSQLiteQuery("PRAGMA foreign_keys = ON"));
        } else {
            for(Course c: course){
                updateLastAccessTime(c.getId(), c.getLastAccessTime());
                updateProgress(c.getId(), c.getProgress());
            }
        }
    }
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Course... course);
    @Query("UPDATE Course SET lastAccessTime =:accessTime WHERE id = :id and lastAccessTime < :accessTime")
    public abstract void updateLastAccessTime(int id, long accessTime);
    @Query("UPDATE Course SET progress =:progress WHERE id = :id")
    public abstract void updateProgress(int id, double progress);
    @Query("SELECT count(*) FROM Course")
    public abstract int numRow();
    @RawQuery
    public abstract int rawQueryExceute(SupportSQLiteQuery query);
    @Query("DELETE FROM Course")
    public abstract void deleteAll();
    @Query("SELECT id, title as name FROM course")
    public abstract List<IdNamePair> getAllCourseName();
    @Query("SELECT progress FROM COURSE WHERE id = :courseId")
    public abstract LiveData<Double> getProgress(int courseId);
    //TODO: clear all table
}
