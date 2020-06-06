package vnu.uet.mobilecourse.assistant.database.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import vnu.uet.mobilecourse.assistant.model.CourseOverview;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.WeeklyMaterial;

import java.util.List;
@Dao
public abstract class MaterialDAO {
    @Query("SELECT * FROM Material WHERE weekId = :week_id")
    public abstract LiveData<List<Material>> getMaterialByWeek(int week_id);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertWeekInfo(WeeklyMaterial... materials);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertMaterial(List<Material> materials);
    public void insertCourseContent(int courseId, CourseOverview... contents){
        for (CourseOverview content: contents) {
            content.getWeekInfo().setCourseId(courseId);
            insertWeekInfo(content.getWeekInfo());
            content.getMaterials().forEach(material -> material.setWeekId(content.getWeekInfo().getId()));
            insertMaterial(content.getMaterials());
        }
    }
    @Transaction
    @Query("SELECT * FROM WeeklyMaterial WHERE courseId = :course_id")
    public abstract LiveData<List<CourseOverview>> getCourseContent(int course_id);
    @Query("UPDATE Material SET completion = 1 WHERE id = :materialId")
    public abstract void updateMaterialCompletion(int materialId);
    @Query("UPDATE Material SET lastModified = :lastModified, description = :content WHERE id = :id ")
    public abstract void updatePageContent(String content, long lastModified, int id);
}
