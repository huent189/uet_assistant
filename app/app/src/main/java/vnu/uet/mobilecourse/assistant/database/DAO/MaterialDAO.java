package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.room.*;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;
import vnu.uet.mobilecourse.assistant.database.querymodel.Submission;
import vnu.uet.mobilecourse.assistant.model.CourseOverview;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.WeeklyMaterial;
import vnu.uet.mobilecourse.assistant.model.material.*;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class MaterialDAO {
    @Query("SELECT * FROM Material WHERE weekId = :week_id")
    public abstract LiveData<List<Material>> getMaterialByWeek(int week_id);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertWeekInfo(WeeklyMaterial... materials);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertMaterial(List<Material> materials);
    public ArrayList<Material> insertMaterial(int courseId, CourseOverview... contents){
        ArrayList<Material> updateList = new ArrayList<>();
        for (CourseOverview content: contents) {
            content.getWeekInfo().setCourseId(courseId);
            insertWeekInfo(content.getWeekInfo());
            content.getMaterials().forEach(material -> material.setWeekId(content.getWeekInfo().getId()));
            insertMaterial(content.getMaterials());
            updateList.addAll(content.getMaterials());
        }
        return updateList;
    }
    @Query("SELECT COALESCE(MAX(Material.lastModified), -1) from Material, WeeklyMaterial WHERE courseId = :courseId and Material.weekId = WeeklyMaterial.id")
    public abstract long getLastMaterialUpdateTimeByCourse(int courseId);
    @Transaction
    @Query("SELECT * FROM WeeklyMaterial WHERE courseId = :course_id")
    public abstract LiveData<List<CourseOverview>> getCourseContent(int course_id);
    @Query("UPDATE Material SET completion = :completion WHERE id = :materialId")
    public abstract void updateCompletion(int materialId, int completion);
    public void updateMaterialCompletion(int materialId, int completion){
        updateCompletion(materialId, completion);
        int courseId = getCourseIdFromMaterial(materialId);
        double progress = calculateProgress(courseId);
        Log.d("COURSE_DEBUG", "updateMaterialCompletion: " + progress);
        updateProgress(courseId, progress);
    }
    @Query("SELECT courseId from WeeklyMaterial, Material where Material.id = :materialId and WeeklyMaterial.id = Material.weekId")
    protected abstract int getCourseIdFromMaterial(int materialId);
    @Query("UPDATE Course SET progress = :newProgress WHERE id = :id")
    protected abstract void updateProgress(int id, double newProgress);
    @Query("select (sum(Material.completion) * 100.0/ COUNT(Material.completion)) from Material, WeeklyMaterial where WeeklyMaterial.courseId = :courseId and WeeklyMaterial.id= Material.weekId and completion != -1")
    public abstract double calculateProgress(int courseId);
    @Query("SELECT completion from Material WHERE id = :materialId")
    public abstract int getMaterialCompletion(int materialId);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertPageContent(List<PageContent> pageContents);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertExternalResourceContent(List<ExternalResourceContent> contents);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract  void insertMaterialContent(List<MaterialContent> contents);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract  void insertAssignments(List<AssignmentContent> contents);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertQuizzes(List<QuizNoGrade> contents);
    public  void insertInternalResource(List<InternalResourceContent> contents){
        insertInternalResourceParent(contents);
        for (InternalResourceContent content : contents) {
            int parentId = content.getMaterialId();
            for (int j = 0; j < content.getFiles().size(); j++) {
                content.getFiles().get(j).setId(parentId * 100 + j);
                content.getFiles().get(j).setParentId(parentId);
                insertInternalFiles(content.getFiles());
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
        LiveData<List<InternalFile>> children = getInternalFile(id);
        MediatorLiveData<InternalResourceContent> merger = new MediatorLiveData<>();
        merger.setValue(new InternalResourceContent());
        merger.addSource(parent, internalResourceContent -> {
            InternalResourceContent old = merger.getValue();
            if(old != null) {
                internalResourceContent.setFiles(old.getFiles());
            }
            merger.postValue(internalResourceContent);

        });
        merger.addSource(children, internalFiles -> {
            InternalResourceContent old = merger.getValue();
            if(old != null){
                old.setFiles(internalFiles);
            }
            merger.postValue(old);
        });
        return merger;
    }
    @Query("SELECT * from InternalResourceContent WHERE materialId = :id")
    public abstract LiveData<InternalResourceContent> getInternalResourceParent(int id);
    @Query("SELECT * from InternalFile WHERE parentId = :id")
    public abstract LiveData<List<InternalFile>> getInternalFile(int id);
    @Query("SELECT Course.id as courseId, Course.code as courseName, Material.type, Material.completion as isCompleted, materialId," +
        " AssignmentContent.name as materialName, startDate as startTime, deadline as endTime" +
        " from Course, Material, AssignmentContent " +
        "WHERE Course.id = AssignmentContent.courseId and Material.id = AssignmentContent.materialId " +
        "and ((startDate >= :startTime and startDate < :endTime) or (deadline >= :startTime and deadline < :endTime))")
    public abstract List<Submission> getAssignmentInRange(long startTime, long endTime);
    @Query("SELECT Course.id as courseId, Course.code as courseName, Material.type, Material.completion as isCompleted, materialId," +
            " QuizNoGrade.name as materialName, timeOpen as startTime, timeClose as endTime" +
            " from Course, Material, QuizNoGrade " +
            "WHERE Course.id = QuizNoGrade.courseId and Material.id = QuizNoGrade.materialId " +
            "and ((timeOpen >= :startTime and timeOpen < :endTime) or (timeClose >= :startTime and timeClose < :endTime))")
    public abstract List<Submission> getQuizInRange(long startTime, long endTime);
    @Query("SELECT Course.id as courseId, Course.code as courseName, Material.type, Material.completion as isCompleted, materialId," +
            " QuizNoGrade.name as materialName, timeOpen as startTime, timeClose as endTime" +
            " from Course, Material, QuizNoGrade " +
            "WHERE Course.id = QuizNoGrade.courseId and Material.id = QuizNoGrade.materialId " +
            "and QuizNoGrade.id in (:ids)")
    public abstract List<Submission> getQuizSubmissions(List<Integer> ids);
    @Query("SELECT Course.id as courseId, Course.code as courseName, Material.type, Material.completion as isCompleted, materialId," +
            " AssignmentContent.name as materialName, startDate as startTime, deadline as endTime" +
            " from Course, Material, AssignmentContent " +
            "WHERE Course.id = AssignmentContent.courseId and Material.id = AssignmentContent.materialId " +
            "and AssignmentContent.id in (:ids)")
    public abstract List<Submission> getAssignmentSubmissions(List<Integer> ids);
    @Query("SELECT id from QuizNoGrade")
    public abstract List<Integer> getAllQuizId();
}
