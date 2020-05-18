package vnu.uet.mobilecourse.assistant.database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import vnu.uet.mobilecourse.assistant.model.*;

import java.util.List;

@Dao
public abstract class CoursesDAO {
    @Query("SELECT * FROM course ORDER BY id ASC")
    public abstract LiveData<List<Course>> getMyCourses();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertCourse(Course... course);
    @Query("SELECT * FROM Material WHERE weekId = :week_id")
    public abstract LiveData<List<Material>> getMaterialByWeek(int week_id);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertWeekInfo(WeeklyMaterial... materials);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertMaterial(List<Material> materials);
    public void insertCourseContent(int courseId, CourseContent... contents){
        for (CourseContent content: contents) {
            content.getWeekInfo().setCourseId(courseId);
            insertWeekInfo(content.getWeekInfo());
            content.getMaterials().forEach(material -> material.setWeekId(content.getWeekInfo().getId()));
            insertMaterial(content.getMaterials());
        }
    }
    @Transaction
    @Query("SELECT * FROM WeeklyMaterial WHERE courseId = :course_id")
    public abstract LiveData<List<CourseContent>> getCourseContent(int course_id);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertGrade(Grade... grades);
    @Query("select * from Grade where courseId = :courseId and type != \"course\" order by gradedDate DESC")
    public abstract LiveData<List<Grade>> getGrades(int courseId);
    @Query("select * from Grade where courseId = :courseId and type == \"course\"")
    public abstract LiveData<Grade> getTotalGrade(int courseId);
    //TODO: clear all table
}
