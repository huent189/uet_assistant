package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.room.*;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;
import vnu.uet.mobilecourse.assistant.model.CourseOverview;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.WeeklyMaterial;
import vnu.uet.mobilecourse.assistant.model.material.*;

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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertPageContent(List<PageContent> pageContents);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertExternalResourceContent(List<ExternalResourceContent> contents);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract  void insertMaterialContent(List<MaterialContent> contents);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract  void insertAssignments(List<AssignmentContent> contents);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract  void insertQuizzes(List<QuizNoGrade> contents);
    public  void insertInternalResource(List<InternalResourceContent> contents){
        insertInternalResourceParent(contents);
        for (int i = 0; i < contents.size(); i++) {
            int parentId = contents.get(i).getMaterialId();
            for (int j = 0; j < contents.get(i).getFiles().size(); j++) {
                contents.get(i).getFiles().get(j).setId(parentId * 100 + j);
                contents.get(i).getFiles().get(j).setParentId(parentId);
                insertInternalFiles(contents.get(i).getFiles());
            }
        }
    }
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract  void insertInternalResourceParent(List<InternalResourceContent> contents);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract  void insertInternalFiles(List<InternalFile> contents);
    public long getLastUpdateTime(Class classType){
        String tableName = classType.getSimpleName();
        Log.e("COURSE_DEBUG", "getLastUpdateTime: " + tableName );
        SimpleSQLiteQuery query = new SimpleSQLiteQuery("SELECT COALESCE(MAX(timeModified), -1) from " + tableName);
        return getMaxTimeModified(query);
    }
    @RawQuery()
    public abstract long getMaxTimeModified(SupportSQLiteQuery query);
    @Query("SELECT * from MaterialContent WHERE materialId = :id")
    public abstract LiveData<MaterialContent> getMaterialContent(int id);
    @Query("SELECT * from PageContent WHERE materialId = :id")
    public abstract LiveData<PageContent> getPageContent(int id);
    @Query("SELECT * from AssignmentContent WHERE materialId = :id")
    public abstract LiveData<AssignmentContent> getAssignment(int id);
    @Query("SELECT QuizNoGrade.*, Grade.userGrade as userGrade from QuizNoGrade, Grade WHERE QuizNoGrade.materialId = :id and Grade.materialId =:id")
    public abstract LiveData<QuizContent> getQuiz(int id);
    @Query("SELECT * from ExternalResourceContent WHERE materialId = :id")
    public abstract LiveData<ExternalResourceContent> getExternalResource(int id);
    public LiveData<InternalResourceContent> getInternalResource(int id){
        LiveData<InternalResourceContent> parent = getInternalResourceParent(id);
        LiveData<List<InternalFile>> childrent = getInternalFile(id);
        MediatorLiveData<InternalResourceContent> merger = new MediatorLiveData();
        merger.postValue(parent.getValue());
        merger.addSource(parent, internalResourceContent -> {
            InternalResourceContent old = merger.getValue();
            if(old != null) {
                internalResourceContent.setFiles(old.getFiles());
            }
            merger.postValue(internalResourceContent);

        });
        merger.addSource(childrent, internalFiles -> {
            InternalResourceContent old = merger.getValue();
            if(old == null){
                old = new InternalResourceContent();
            }
            old.setFiles(internalFiles);
            merger.postValue(old);
        });
        return merger;
    }
    @Query("SELECT * from InternalResourceContent WHERE materialId = :id")
    public abstract LiveData<InternalResourceContent> getInternalResourceParent(int id);
    @Query("SELECT * from InternalFile WHERE parentId = :id")
    public abstract LiveData<List<InternalFile>> getInternalFile(int id);
}
