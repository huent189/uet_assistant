package vnu.uet.mobilecourse.assistant.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import vnu.uet.mobilecourse.assistant.database.DAO.CoursesDAO;
import vnu.uet.mobilecourse.assistant.model.Grade;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.WeeklyMaterial;
import vnu.uet.mobilecourse.assistant.view.MyApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Course.class, Grade.class, WeeklyMaterial.class, Material.class}, version = 1, exportSchema = false)
public abstract class CoursesDatabase extends RoomDatabase {
    private static volatile CoursesDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public abstract CoursesDAO coursesDAO();
    public static CoursesDatabase getDatabase() {
        if (instance == null) {
            synchronized (CoursesDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(MyApplication.getInstance().getApplicationContext(),
                            CoursesDatabase.class, "courses_db")
                            .build();
                }
            }
        }
        return instance;
    }
}
