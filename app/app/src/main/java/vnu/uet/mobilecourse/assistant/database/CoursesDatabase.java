package vnu.uet.mobilecourse.assistant.database;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import vnu.uet.mobilecourse.assistant.database.DAO.CoursesDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.GradeDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.MaterialDAO;
import vnu.uet.mobilecourse.assistant.model.*;
import vnu.uet.mobilecourse.assistant.view.MyApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Course.class, Grade.class, WeeklyMaterial.class, Material.class, AssignmentContent.class,
                    ExternalResourceContent.class, InternalFile.class, InternalResourceContent.class,
                    MaterialContent.class, PageContent.class, QuizContent.class},
        version = 2, exportSchema = true)
public abstract class CoursesDatabase extends RoomDatabase {
    private static volatile CoursesDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public abstract CoursesDAO coursesDAO();
    public abstract GradeDAO gradeDAO();
    public abstract MaterialDAO materialDAO();
    public static CoursesDatabase getDatabase() {
        if (instance == null) {
            synchronized (CoursesDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(MyApplication.getInstance().getApplicationContext(),
                            CoursesDatabase.class, "courses_db")
                            .addMigrations(new Migration(1, 2) {
                                @Override
                                public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
                                    supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `AssignmentContent` (`deadline` INTEGER NOT NULL, `startDate` INTEGER NOT NULL, `maximumGrade` INTEGER NOT NULL, `isCompleted` INTEGER NOT NULL, `maxAttemptAllowed` INTEGER NOT NULL, `id` INTEGER NOT NULL, `materialId` INTEGER NOT NULL, `courseId` INTEGER NOT NULL, `name` TEXT, `intro` TEXT, `timeModified` INTEGER NOT NULL, PRIMARY KEY(`id`))");
                                    supportSQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS `index_AssignmentContent_materialId` ON `AssignmentContent` (`materialId`)");
                                    supportSQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS `index_AssignmentContent_courseId` ON `AssignmentContent` (`courseId`)");
                                    supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `ExternalResourceContent` (`fileUrl` TEXT, `revision` INTEGER NOT NULL, `id` INTEGER NOT NULL, `materialId` INTEGER NOT NULL, `courseId` INTEGER NOT NULL, `name` TEXT, `intro` TEXT, `timeModified` INTEGER NOT NULL, PRIMARY KEY(`id`))");
                                    supportSQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS `index_ExternalResourceContent_materialId` ON `ExternalResourceContent` (`materialId`)");
                                    supportSQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS `index_ExternalResourceContent_courseId` ON `ExternalResourceContent` (`courseId`)");
                                    supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `InternalFile` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `parentId` INTEGER NOT NULL, `fileName` TEXT, `fileUrl` TEXT, `mimeType` TEXT, `fileSize` INTEGER NOT NULL)");
                                    supportSQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS `index_InternalFile_parentId` ON `InternalFile` (`parentId`)");
                                    supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `InternalResourceContent` (`revision` INTEGER NOT NULL, `id` INTEGER NOT NULL, `materialId` INTEGER NOT NULL, `courseId` INTEGER NOT NULL, `name` TEXT, `intro` TEXT, `timeModified` INTEGER NOT NULL, PRIMARY KEY(`id`))");
                                    supportSQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS `index_InternalResourceContent_materialId` ON `InternalResourceContent` (`materialId`)");
                                    supportSQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS `index_InternalResourceContent_courseId` ON `InternalResourceContent` (`courseId`)");
                                    supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `MaterialContent` (`id` INTEGER NOT NULL, `materialId` INTEGER NOT NULL, `courseId` INTEGER NOT NULL, `name` TEXT, `intro` TEXT, `timeModified` INTEGER NOT NULL, PRIMARY KEY(`id`))");
                                    supportSQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS `index_MaterialContent_materialId` ON `MaterialContent` (`materialId`)");
                                    supportSQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS `index_MaterialContent_courseId` ON `MaterialContent` (`courseId`)");
                                    supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `PageContent` (`revision` INTEGER NOT NULL, `id` INTEGER NOT NULL, `materialId` INTEGER NOT NULL, `courseId` INTEGER NOT NULL, `name` TEXT, `intro` TEXT, `timeModified` INTEGER NOT NULL, PRIMARY KEY(`id`))");
                                    supportSQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS `index_PageContent_materialId` ON `PageContent` (`materialId`)");
                                    supportSQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS `index_PageContent_courseId` ON `PageContent` (`courseId`)");
                                    supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `QuizContent` (`timeOpen` INTEGER NOT NULL, `timeClose` INTEGER NOT NULL, `timeLimit` INTEGER NOT NULL, `maximumAttemp` INTEGER NOT NULL, `maximumGrade` INTEGER NOT NULL, `id` INTEGER NOT NULL, `materialId` INTEGER NOT NULL, `courseId` INTEGER NOT NULL, `name` TEXT, `intro` TEXT, `timeModified` INTEGER NOT NULL, PRIMARY KEY(`id`))");
                                    supportSQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS `index_QuizContent_materialId` ON `QuizContent` (`materialId`)");
                                    supportSQLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS `index_QuizContent_courseId` ON `QuizContent` (`courseId`)");
                                }
                            })
                            .build();
                }
            }
        }
        return instance;
    }
}
